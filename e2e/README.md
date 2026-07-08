# End-to-end journeys (Playwright)

Browser journeys that walk the system end-to-end:

- **quiz journey** (`quiz-journey.spec.ts`) —
  **buy quiz → confirm payment → answer all questions → reach the end** — across
  the quizzler and payment UIs, following the application's own redirects.
- **dashboard journey** (`dashboard-journey.spec.ts`) — opens the dashboard UI
  for a seeded session and checks its quiz/payment stats render and are
  internally consistent, plus that an **unknown session fails gracefully** (the
  in-app 404 error state, not a crash).

They double as a **zero-downtime probe**: run them on a loop while you roll a
deployment, and any error is made visible (failed step with screenshot/video/
trace, plus attached diagnostics listing 5xx responses, dropped connections,
uncaught JS errors and console errors). A single 5xx or dropped request during
a run fails the test. The shared diagnostics live in `tests/support/diagnostics.ts`.

## Install

```bash
cd e2e
npm install
npx playwright install chromium
```

## Run

Against **docker-compose** (default, `http://localhost:4200`):

```bash
npm test
```

Against **KIND** (host-based ingress):

```bash
QUIZZLER_UI_BASE_URL=http://quizzler.localhost \
DASHBOARD_UI_BASE_URL=http://dashboard.localhost npm test
```

Run a single journey:

```bash
npx playwright test quiz-journey        # quiz only
npx playwright test dashboard-journey   # dashboard only
```

> The dashboard happy-path test reads a seeded session. Seed it first with
> `api/dashboard/insert-session-dashboard-data.sql` (its default id `session-old-001`
> is one of those rows), or point `DASHBOARD_ID` at a session the dashboard has
> already received events for. The unknown-session test needs no seed.

Useful variants:

```bash
npm run test:headed     # watch it drive the browser
npm run test:ui         # Playwright UI mode (time-travel debugging)
npm run report          # open the HTML report after a run
```

### Configuration

| Env var | Default | Meaning |
|---------|---------|---------|
| `QUIZZLER_UI_BASE_URL` | `http://localhost:4200` | Quiz journey entry URL (the payment UI is reached via redirect). |
| `QUIZZLER_SESSION_ID`  | `11111111-2222-3333-4444-555555555555` | Seeded quiz session id. |
| `DASHBOARD_UI_BASE_URL` | `http://localhost:4202` | Dashboard UI base URL. |
| `DASHBOARD_ID`         | `session-001` | Seeded dashboard session id to open. |

## Zero-downtime demonstration

Run the journey continuously in one terminal. **A failing run does not stop the
loop** — its full output is appended to `errors.log` and the probe resumes:

```bash
# compose
npm run loop
# or KIND
QUIZZLER_UI_BASE_URL=http://quizzler.localhost npm run loop
```

…and trigger a rolling update in another:

```bash
kubectl -n quizzler rollout restart deployment/quizzler-api deployment/quizzler-ui
kubectl -n payment  rollout restart deployment/payment-api  deployment/payment-ui
```

The runner prints a live `runs / passed / failed` tally and keeps going through
any blip. Inspect what broke afterwards:

```bash
cat errors.log          # every failed run, timestamped, with its output
```

Each failing run still reports **all** problems it saw (5xx, dropped requests,
uncaught errors) in one go, because the final gates are soft assertions.

Bounded variants: `ITERATIONS=100 npm run loop` (stop after 100 runs), or
`npm run soak` (200 sequential runs via Playwright's `--repeat-each`).

## Where failures show up

- **Console**: the `list` reporter prints each step and a `!!! N problem(s)…`
  summary of anything observed.
- **HTML report** (`npm run report`): screenshot, video and a Playwright trace
  for the failed run, plus the attached `diagnostics.json`.
