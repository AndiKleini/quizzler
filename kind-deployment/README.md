# KIND deployment

Runs the whole quizzler system on a single-node [KIND](https://kind.sigs.k8s.io/)
cluster behind an **nginx ingress**, split into capability bundles:

| Capability | Namespace | Pods (replicas) |
|------------|-----------|-----------------|
| quizzler   | `quizzler`  | `quizzler-ui` (2), `quizzler-api` (2), `quizzler-db` (1, PostgreSQL) |
| payment    | `payment`   | `payment-ui` (2), `payment-api` (2), `payment-db` (1, PostgreSQL) |
| dashboard  | `dashboard` | `dashboard-ui` (2), `dashboard-api` (2, .NET), `dashboard-db` (1, SQL Server) |
| messaging  | `messaging` | `rabbitmq` (1) |

Every pod runs **two replicas except the database pods**, which are single
instances bound to a `ReadWriteOnce` PVC. The `dashboard-db` runs Microsoft SQL
Server (≈2 GiB RAM) instead of PostgreSQL; `dashboard-api` applies its EF Core
migrations on startup, so the schema is created on the fresh instance with no
manual step.

> **Hybrid CPUs and SQL Server.** On Intel hybrid CPUs (P-cores with
> hyperthreading + E-cores without), SQL Server aborts at startup with a fatal
> `sosnumap.cpp` assert because the logical-processor count is not a clean
> multiple of the physical-core count (the `/proc/.../fdinfo: Permission denied`
> lines in the log are just the crash-dump collector, not the cause). `deploy.sh`
> works around this by pinning the kind node container to a homogeneous CPU set
> via `docker update --cpuset-cpus` (`MSSQL_CPUSET`, default `0-11` = the
> i7-12700H P-cores). Override it for your host's P-cores, or set `MSSQL_CPUSET=""`
> to skip on a non-hybrid CPU. The same applies to docker-compose, where the
> `dashboard-db` service sets `cpuset: "0-11"`.

## Prerequisites

- `docker`, `kind`, `kubectl` on the PATH.
- Host ports **80** and **443** free (the cluster maps them into the node).
- `*.localhost` resolving to `127.0.0.1`. Browsers do this automatically; for
  `curl`/CLI add to `/etc/hosts`:
  ```
  127.0.0.1  quizzler.localhost api.quizzler.localhost payment.localhost api.payment.localhost dashboard.localhost api.dashboard.localhost
  ```

## Deploy

```bash
cd kind-deployment
./deploy.sh
```

The script:
1. creates the `quizzler` KIND cluster (`kind-cluster.yaml`) if absent,
2. builds the six images under a unique per-run tag `<git-sha>-<timestamp>`
   (UIs with `--build-arg NG_CONFIGURATION=kind` so the ingress host URLs are
   baked in),
3. `kind load`s the images into the node,
4. installs the vendored `ingress-nginx.yaml` (kind provider, controller v1.11.3),
5. rewrites the manifests' `:kind` image placeholder to the run's tag and
   `kubectl apply`s all capability bundles — applying only the difference,
6. waits for every rollout.

Because the image tag changes each run, `apply` sees a real diff in the
Deployment's image field and rolls the pods automatically — no `rollout restart`
is needed. Re-run `./deploy.sh` after a code change, or `./deploy.sh --no-build`
to reuse the most recently built tag (reload + re-apply only).

## Rolling updates (cluster stays up)

Once the cluster is running, roll a new version of the **app services** (UIs and
APIs) without recreating the cluster or touching the databases, RabbitMQ or the
ingress:

```bash
cd kind-deployment
./rolling-update.sh                       # build + roll all six app services
./rolling-update.sh quizzler-api          # roll only the named service(s)
./rolling-update.sh quizzler-ui payment-ui dashboard-ui
./rolling-update.sh --no-build quizzler-api   # reuse newest built tag, just roll
```

Valid service names: `quizzler-api` `payment-api` `dashboard-api` `quizzler-ui`
`payment-ui` `dashboard-ui`.

The script builds + `kind load`s the selected images under a fresh per-run tag
and triggers the rollout with `kubectl set image` (so only the app Deployments
change). It **refuses to run if the cluster is absent** — unlike `deploy.sh`, it
never creates infrastructure; the cluster is meant to stay available across
rollouts.

Availability is guaranteed by the Deployments' rolling-update strategy
(`maxUnavailable: 0`, `maxSurge: 1`): a new pod must pass its readiness probe
before an old one is removed, so each service keeps serving throughout. The DBs,
RabbitMQ and ingress are never restarted. Use `deploy.sh` instead when you also
need to (re)apply infrastructure or config/manifest changes beyond the image.

## Access

| URL | Routes to |
|-----|-----------|
| http://quizzler.localhost | `quizzler-ui` |
| http://api.quizzler.localhost | `quizzler-api` |
| http://payment.localhost | `payment-ui` |
| http://api.payment.localhost | `payment-api` |
| http://dashboard.localhost | `dashboard-ui` |
| http://api.dashboard.localhost | `dashboard-api` |

## Connecting to the databases

Both PostgreSQL instances are exposed on the host as `NodePort` services mapped
in through the cluster (so no `port-forward` process is needed):

| Database | Engine | Host | Port | DB / user / password |
|----------|--------|------|------|----------------------|
| quizzler-db | PostgreSQL | localhost | 5432 | quizzler / quizzler / quizzler |
| payment-db  | PostgreSQL | localhost | 5433 | payment / payment / payment |
| dashboard-db | SQL Server | localhost | 1433 | dashboard / sa / `Dboard123!` |

Point DBeaver / pgAdmin / `psql` at e.g. `postgresql://quizzler:quizzler@localhost:5432/quizzler`.
For `dashboard-db`, point Azure Data Studio / `sqlcmd` at `localhost,1433` (user `sa`,
password `Dboard123!`, trust the server certificate).

> The host-port mappings (30432→5432, 30433→5433, 30434→1433) live in `kind-cluster.yaml`
> and are applied at cluster creation. If the cluster already exists you must
> recreate it (`kind delete cluster --name quizzler` then `./deploy.sh`) for the
> new ports to take effect. Ensure host ports 5432/5433/1433 are free (stop the
> docker-compose stack or a local Postgres/SQL Server first). In-cluster apps are
> unaffected — they keep reaching the DBs via the ClusterIP service DNS.

For a one-off connection without exposing ports you can instead use:

```bash
kubectl -n quizzler  port-forward svc/quizzler-db  5432:5432
kubectl -n payment   port-forward svc/payment-db   5433:5432
kubectl -n dashboard port-forward svc/dashboard-db 1433:1433
```

## Routing model

`ingress-nginx` listens on node port 80 (mapped to host 80) and routes by host:

```
quizzler.localhost       -> quizzler-ui:80
api.quizzler.localhost   -> quizzler-api:8080
payment.localhost        -> payment-ui:80
api.payment.localhost    -> payment-api:8081
dashboard.localhost      -> dashboard-ui:80
api.dashboard.localhost  -> dashboard-api:8080
```

Browser-facing URLs are the ingress hosts above; server-to-server calls use
in-cluster service DNS:

- `quizzler-api` → `payment-api.payment.svc.cluster.local:8081` (create payment)
- `payment-api` → `quizzler-api.quizzler.svc.cluster.local:8080` (success webhook)
- the post-payment **browser** redirect uses `http://quizzler.localhost`
- `dashboard-api` consumes quizzler notification events from
  `rabbitmq.messaging.svc.cluster.local:5672`

CORS on each API is set to its UI's ingress host: the Spring APIs via
`CORS_ALLOWED_ORIGINS`, the .NET `dashboard-api` via `Cors__AllowedOrigins`.

## Seeding

`quizzler-api`'s existing `SeedDataInitializer` seeds `quizzler-db` on first
startup; it is idempotent (no-op once data exists). With two `quizzler-api`
replicas starting together there is a brief seed race — the pod that loses the
unique-id insert restarts once, then sees the data already present and starts
cleanly. The seed therefore converges without manual steps.

## Teardown

```bash
kind delete cluster --name quizzler
```

## Relationship to docker-compose

The same Dockerfiles back both setups. `docker-compose` builds the default
(`production`/localhost) UI configuration and exposes everything on localhost;
KIND builds the `kind` UI configuration and routes through the ingress. The
backend images are identical — CORS and cross-service URLs are environment-driven.
