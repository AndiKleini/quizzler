#!/usr/bin/env bash
#
# Roll a SINGLE, already-built API image into an ALREADY-RUNNING KIND cluster.
#
# Unlike rolling-update.sh (which builds a fresh per-run tag for one or more
# services), this script takes an *existing* image tag and rolls exactly one API
# Deployment to it. Use it to promote an image you built elsewhere — e.g. from the
# refactor-direct-dependency-step-1/2/3 branches — onto the running cluster.
#
# The image `<api-name>:<tag>` must already exist in your local Docker. The script
# loads it into the KIND node and switches the Deployment's container image with
# `kubectl set image`; the Deployment's RollingUpdate strategy (maxUnavailable=0,
# maxSurge=1) keeps the API available throughout — a new pod must pass its
# readiness probe before an old one is removed. Databases, RabbitMQ, the ingress
# and every other service are left untouched.
#
# Usage:
#   ./roll-api.sh <image-tag> <api-name>
#
# Examples:
#   ./roll-api.sh refactor-step-1 quizzler-api
#   ./roll-api.sh 51ccf8f-20260703 payment-api
#
# Valid API names: quizzler-api  payment-api  dashboard-api
set -euo pipefail

CLUSTER="quizzler"

# api-name -> namespace (container name == deployment name == service name).
declare -A API_NAMESPACE=(
  [quizzler-api]=quizzler
  [payment-api]=payment
  [dashboard-api]=dashboard
)

valid_apis() { echo "${!API_NAMESPACE[@]}"; }

usage() {
  cat >&2 <<EOF
Usage: ./roll-api.sh <image-tag> <api-name>
  <image-tag>  tag of an already-built image, e.g. refactor-step-1
  <api-name>   one of: $(valid_apis)
EOF
}

# --- Parse and validate the two positional args. ------------------------------
if [[ $# -ne 2 ]]; then
  echo "ERROR: expected exactly 2 arguments, got $#." >&2
  usage
  exit 2
fi

TAG="$1"
API="$2"

if [[ -z "${TAG}" ]]; then
  echo "ERROR: image tag must not be empty." >&2
  usage
  exit 2
fi

if [[ -z "${API_NAMESPACE[$API]:-}" ]]; then
  echo "ERROR: unknown API '${API}'. Valid: $(valid_apis)" >&2
  exit 2
fi

NAMESPACE="${API_NAMESPACE[$API]}"
IMAGE="${API}:${TAG}"

# --- The cluster must already be up; this script never creates it. ------------
echo "==> Checking KIND cluster '${CLUSTER}' is running"
if ! kind get clusters | grep -qx "${CLUSTER}"; then
  echo "ERROR: cluster '${CLUSTER}' not found. Bring it up first with ./deploy.sh" >&2
  exit 1
fi

# --- The image must already exist locally (this script does not build). -------
echo "==> Checking image '${IMAGE}' exists in local Docker"
if ! docker image inspect "${IMAGE}" >/dev/null 2>&1; then
  echo "ERROR: image '${IMAGE}' not found locally. Build/tag it first, e.g.:" >&2
  echo "         docker build -t ${IMAGE} ../api/${API#*-}" >&2
  exit 1
fi

# --- Load the image into the KIND node so IfNotPresent can pick it up. --------
echo "==> Loading '${IMAGE}' into cluster '${CLUSTER}'"
kind load docker-image "${IMAGE}" --name "${CLUSTER}"

# --- Trigger the rolling update on just this Deployment. ----------------------
# `kubectl set image` changes only the container image; the DBs, RabbitMQ and
# ingress are never touched. maxUnavailable=0 keeps the API serving throughout.
echo "==> Rolling deployment/${API} (namespace ${NAMESPACE}) to image ${IMAGE}"
kubectl -n "${NAMESPACE}" set image "deployment/${API}" "${API}=${IMAGE}"

echo "==> Waiting for the rollout to complete"
kubectl -n "${NAMESPACE}" rollout status "deployment/${API}" --timeout=180s

echo
echo "==> Done. Rolled ${API} -> ${IMAGE}."
echo "    The cluster and infrastructure (databases, RabbitMQ, ingress) were left running."
