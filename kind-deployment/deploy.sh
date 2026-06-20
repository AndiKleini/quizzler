#!/usr/bin/env bash
#
# Build the six app images, copy them into the KIND cluster, and (re)deploy the
# whole quizzler system. Idempotent: re-run it after changing code to roll the
# difference out.
#
# Each run produces a unique, content-distinguishing image tag (git SHA +
# timestamp). Because the tag changes, `kubectl apply` sees a real diff in the
# Deployment's image field and rolls the pods automatically — no `rollout
# restart` nudge is needed. The manifests carry a `:kind` placeholder that is
# rewritten to the run's tag at apply time.
#
# Usage:
#   ./deploy.sh              build images, load them, apply manifests
#   ./deploy.sh --no-build   skip the build, reuse the most recently built tag
#
set -euo pipefail

CLUSTER="quizzler"
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT="$(dirname "$SCRIPT_DIR")"

BUILD=1
[[ "${1:-}" == "--no-build" ]] && BUILD=0

# image-name  build-context  [extra docker build args...]
IMAGES=(
  "quizzler-api|${ROOT}/api/quizzler|"
  "payment-api|${ROOT}/api/payment|"
  "dashboard-api|${ROOT}/api/dashboard|"
  "quizzler-ui|${ROOT}/ui/quizzler|--build-arg NG_CONFIGURATION=kind"
  "payment-ui|${ROOT}/ui/payment|--build-arg NG_CONFIGURATION=kind"
  "dashboard-ui|${ROOT}/ui/dashboard|--build-arg NG_CONFIGURATION=kind"
)

if [[ "${BUILD}" == "1" ]]; then
  # Unique per run: <git-sha>-<timestamp>, or nogit-<timestamp> outside a repo.
  TAG="$(git -C "${ROOT}" rev-parse --short HEAD 2>/dev/null || echo nogit)-$(date +%Y%m%d%H%M%S)"
else
  # Reuse the newest tag already built for the first image.
  TAG="$(docker images quizzler-api --format '{{.Tag}}' | grep -v '<none>' | head -n1 || true)"
  [[ -z "${TAG}" ]] && { echo "ERROR: --no-build but no existing quizzler-api image to reuse" >&2; exit 1; }
  echo "==> Reusing existing tag '${TAG}'"
fi

echo "==> Ensuring KIND cluster '${CLUSTER}' exists"
if ! kind get clusters | grep -qx "${CLUSTER}"; then
  kind create cluster --config "${SCRIPT_DIR}/kind-cluster.yaml"
else
  echo "    cluster '${CLUSTER}' already present"
fi

# SQL Server (dashboard-db) aborts at startup on hybrid Intel CPUs (P-cores with
# hyperthreading + E-cores without): its NUMA setup asserts that the logical-
# processor count is a clean multiple of the physical-core count, which it isn't.
# Kubernetes has no simple per-pod cpuset, so pin the whole kind node container to
# a homogeneous CPU set; every pod then inherits a uniform processor/core ratio.
# Default is the i7-12700H P-cores (0-11). Override for your host's P-cores, or set
# MSSQL_CPUSET="" to skip (e.g. on a non-hybrid CPU where it is unnecessary).
MSSQL_CPUSET="${MSSQL_CPUSET-0-11}"
if [[ -n "${MSSQL_CPUSET}" ]]; then
  echo "==> Pinning kind node CPUs to '${MSSQL_CPUSET}' (homogeneous topology for SQL Server)"
  docker update --cpuset-cpus "${MSSQL_CPUSET}" "${CLUSTER}-control-plane" >/dev/null \
    || echo "    WARN: could not set cpuset on ${CLUSTER}-control-plane (continuing)"
fi

if [[ "${BUILD}" == "1" ]]; then
  echo "==> Building images (tag ${TAG})"
  for entry in "${IMAGES[@]}"; do
    IFS='|' read -r name context args <<<"${entry}"
    echo "    docker build ${args} -t ${name}:${TAG} ${context}"
    # shellcheck disable=SC2086
    docker build ${args} -t "${name}:${TAG}" "${context}"
  done
fi

echo "==> Loading images into the cluster"
for entry in "${IMAGES[@]}"; do
  IFS='|' read -r name _ _ <<<"${entry}"
  echo "    kind load docker-image ${name}:${TAG}"
  kind load docker-image "${name}:${TAG}" --name "${CLUSTER}"
done

echo "==> Installing ingress-nginx (kind provider)"
kubectl apply -f "${SCRIPT_DIR}/ingress-nginx.yaml"
echo "    waiting for the ingress controller to be ready"
kubectl -n ingress-nginx wait --for=condition=Available deployment/ingress-nginx-controller --timeout=180s
# The admission webhook must have endpoints before our Ingress objects apply.
kubectl -n ingress-nginx wait --for=condition=Ready pod \
  --selector=app.kubernetes.io/component=controller --timeout=180s

echo "==> Applying manifests (image tag -> ${TAG}; deploys only the difference)"
# Namespaces first so the namespaced resources below have somewhere to land.
kubectl apply \
  -f "${SCRIPT_DIR}/quizzler/00-namespace.yaml" \
  -f "${SCRIPT_DIR}/payment/00-namespace.yaml" \
  -f "${SCRIPT_DIR}/dashboard/00-namespace.yaml" \
  -f "${SCRIPT_DIR}/messaging/00-namespace.yaml"
# Everything else, with the :kind image placeholder rewritten to this run's tag.
# A changed image field is what triggers the rollout — hence no `rollout restart`.
for ns in quizzler payment dashboard; do
  for f in "${SCRIPT_DIR}/${ns}"/[1-9]*.yaml; do
    sed "s|\(image: [^[:space:]]*\):kind|\1:${TAG}|" "${f}"
    echo "---"
  done
done | kubectl apply -f -

# Need to run this separately because there is a parsing proble with nodePort number when this is streamed via cli
# applied together with the other manifests (error: "Service "rabbitmq" is invalid: spec.ports[0].nodePort: Invalid value: 30000: provided port is not in the valid range. The range of valid ports is 30000-32767")
kubectl apply -f "${SCRIPT_DIR}/messaging/10-rabbitmq-messaging.yaml"

echo "==> Waiting for rollouts"
kubectl -n quizzler rollout status deployment/quizzler-db   --timeout=180s
kubectl -n quizzler rollout status deployment/quizzler-api  --timeout=180s
kubectl -n quizzler rollout status deployment/quizzler-ui   --timeout=180s
kubectl -n payment  rollout status deployment/payment-db    --timeout=180s
kubectl -n payment  rollout status deployment/payment-api   --timeout=180s
kubectl -n payment  rollout status deployment/payment-ui    --timeout=180s
# SQL Server takes longer to come up than Postgres, so allow extra time.
kubectl -n dashboard rollout status deployment/dashboard-db  --timeout=300s
kubectl -n dashboard rollout status deployment/dashboard-api --timeout=180s
kubectl -n dashboard rollout status deployment/dashboard-ui  --timeout=180s

cat <<'EOF'

==> Done.

Access (add these to /etc/hosts if your resolver does not map *.localhost to 127.0.0.1):
    127.0.0.1  quizzler.localhost api.quizzler.localhost payment.localhost api.payment.localhost dashboard.localhost api.dashboard.localhost

    quizzler  UI   -> http://quizzler.localhost
    quizzler  API  -> http://api.quizzler.localhost
    payment   UI   -> http://payment.localhost
    payment   API  -> http://api.payment.localhost
    dashboard UI   -> http://dashboard.localhost
    dashboard API  -> http://api.dashboard.localhost
EOF
