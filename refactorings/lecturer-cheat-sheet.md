# Instructor Cheat Sheet

Handy commands for running the live demo on the single-node
[KIND](https://kind.sigs.k8s.io/) cluster (see [`../kind-deployment/README.md`](../kind-deployment/README.md)).

## Inspect the images loaded into the KIND node

List the images in the node's containerd store from **inside** the node
container (our node is `quizzler-control-plane`):

```bash
docker exec -it <node-name> crictl images
```

## List the images actually in use, with their pods

Show every pod in the cluster next to the image(s) its containers run — the
images currently in use, not just what's cached on the node:

```bash
kubectl get pods -A -o custom-columns='NAMESPACE:.metadata.namespace,POD:.metadata.name,IMAGES:.spec.containers[*].image'
```

## Run the e2e journeys endlessly (zero-downtime probe)

Loop the quiz and dashboard journeys against the KIND ingress hosts (Ctrl-C to
stop; failures are appended to `e2e/errors.log` and the loop keeps going):

```bash
QUIZZLER_UI_BASE_URL=http://quizzler.localhost \
  DASHBOARD_UI_BASE_URL=http://dashboard.localhost \
  npm run loop
```

## Zero-downtime deployment steps for Loading Spinner on the move

Roll out the images in the exact sequence below:

```bash
./roll-api.sh kind.mlos.step.1 payment-api
./roll-api.sh kind.mlos.step.2 quizzler-api
./roll-api.sh kind.mlos.step.3 payment-api
```

## Reset quizzler and payment api and dashboard-api

```bash
./roll-api.sh 51ccf8f-20260703191213 payment-api
./roll-api.sh 51ccf8f-20260703191213 quizzler-api
./roll-api.sh 51ccf8f-20260703191213 dashboard-api
```

## Zero-downtime deployment steps for Graph of Wisdom

Roll out the images in the exact sequence below:

```bash
./roll-api.sh kind.gow.step.1 dashboard-api
./roll-api.sh kind.gow.step.2 dashboard-api
./roll-api.sh kind.gow.step.3 dashboard-api
./roll-api.sh kind.gow.step.4 dashboard-api
```
