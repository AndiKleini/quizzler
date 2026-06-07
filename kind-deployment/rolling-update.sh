#!/usr/bin/env bash
#
# Roll a new version of the application services (UIs and APIs) into an
# ALREADY-RUNNING KIND cluster, without touching the cluster, the databases,
# RabbitMQ or the ingress controller. The system stays available for the whole
# duration: each Deployment uses a RollingUpdate strategy with maxUnavailable=0,
# so a new pod must pass its readiness probe before an old one is removed.
#
# Difference from deploy.sh: deploy.sh is the full bring-up (it may create the
# cluster and applies every manifest incl. infrastructure). This script only
# rebuilds + reloads the app images and rolls the app Deployments. It refuses to
# run if the cluster is absent — the cluster is meant to stay up across rollouts.
#
# Each run produces a unique, content-distinguishing image tag (git SHA +
# timestamp), so `kubectl set image` sees a real change and triggers a rollout.
#
# Usage:
#   ./rolling-update.sh                       build + roll all four app services
#   ./rolling-update.sh quizzler-api          build + roll only the named service(s)
#   ./rolling-update.sh quizzler-ui payment-ui
#   ./rolling-update.sh --no-build [service…] reuse the newest built tag, just roll
#
# Valid service names: quizzler-api  payment-api  quizzler-ui  payment-ui
set -euo pipefail

CLUSTER="quizzler"
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT="$(dirname "$SCRIPT_DIR")"

# service-name | namespace | build-context | [extra docker build args...]
ALL_SERVICES=(
  "quizzler-api|quizzler|${ROOT}/api/quizzler|"
  "payment-api|payment|${ROOT}/api/payment|"
  "quizzler-ui|quizzler|${ROOT}/ui/quizzler|--build-arg NG_CONFIGURATION=kind"
  "payment-ui|payment|${ROOT}/ui/payment|--build-arg NG_CONFIGURATION=kind"
)

valid_service() {
  local name="$1" entry
  for entry in "${ALL_SERVICES[@]}"; do
    [[ "${entry%%|*}" == "${name}" ]] && return 0
  done
  return 1
}

# --- Parse args: optional --no-build flag plus an optional list of services ----
BUILD=1
REQUESTED=()
for arg in "$@"; do
  case "${arg}" in
    --no-build) BUILD=0 ;;
    -*) echo "ERROR: unknown flag '${arg}'" >&2; exit 2 ;;
    *)
      if ! valid_service "${arg}"; then
        echo "ERROR: unknown service '${arg}'. Valid: quizzler-api payment-api quizzler-ui payment-ui" >&2
        exit 2
      fi
      REQUESTED+=("${arg}")
      ;;
  esac
done

# Build the working set (selected services, or all of them by default).
SELECTED=()
for entry in "${ALL_SERVICES[@]}"; do
  name="${entry%%|*}"
  if [[ ${#REQUESTED[@]} -eq 0 ]]; then
    SELECTED+=("${entry}")
  else
    for want in "${REQUESTED[@]}"; do
      [[ "${want}" == "${name}" ]] && SELECTED+=("${entry}")
    done
  fi
done

# --- The cluster must already be up; this script never creates it. ------------
echo "==> Checking KIND cluster '${CLUSTER}' is running"
if ! kind get clusters | grep -qx "${CLUSTER}"; then
  echo "ERROR: cluster '${CLUSTER}' not found. Bring it up first with ./deploy.sh" >&2
  exit 1
fi

# --- Pick the image tag for this rollout. -------------------------------------
if [[ "${BUILD}" == "1" ]]; then
  TAG="$(git -C "${ROOT}" rev-parse --short HEAD 2>/dev/null || echo nogit)-$(date +%Y%m%d%H%M%S)"
else
  TAG="$(docker images "${SELECTED[0]%%|*}" --format '{{.Tag}}' | grep -v '<none>' | head -n1 || true)"
  [[ -z "${TAG}" ]] && { echo "ERROR: --no-build but no existing image to reuse" >&2; exit 1; }
  echo "==> Reusing existing tag '${TAG}'"
fi

# --- Build + load the selected images. ----------------------------------------
if [[ "${BUILD}" == "1" ]]; then
  echo "==> Building images (tag ${TAG})"
  for entry in "${SELECTED[@]}"; do
    IFS='|' read -r name _ context args <<<"${entry}"
    echo "    docker build ${args} -t ${name}:${TAG} ${context}"
    # shellcheck disable=SC2086
    docker build ${args} -t "${name}:${TAG}" "${context}"
  done
fi

echo "==> Loading images into the cluster"
for entry in "${SELECTED[@]}"; do
  name="${entry%%|*}"
  echo "    kind load docker-image ${name}:${TAG}"
  kind load docker-image "${name}:${TAG}" --name "${CLUSTER}"
done

# --- Trigger the rolling update on each selected Deployment. -------------------
# `kubectl set image` changes only the container image, so the DBs, RabbitMQ and
# ingress are never touched. The Deployment's RollingUpdate strategy
# (maxUnavailable=0) keeps the service available throughout.
echo "==> Rolling out (image -> ${TAG})"
for entry in "${SELECTED[@]}"; do
  IFS='|' read -r name namespace _ _ <<<"${entry}"
  echo "    kubectl -n ${namespace} set image deployment/${name} ${name}=${name}:${TAG}"
  kubectl -n "${namespace}" set image "deployment/${name}" "${name}=${name}:${TAG}"
done

echo "==> Waiting for rollouts to complete"
for entry in "${SELECTED[@]}"; do
  IFS='|' read -r name namespace _ _ <<<"${entry}"
  kubectl -n "${namespace}" rollout status "deployment/${name}" --timeout=180s
done

echo
echo "==> Done. Rolled: ${SELECTED[*]%%|*}"
echo "    The cluster and infrastructure (databases, RabbitMQ, ingress) were left running."
