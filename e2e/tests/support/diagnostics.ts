import { expect, Page, TestInfo } from '@playwright/test';

// Shared browser diagnostics used by every journey in this suite.
//
// We watch the browser for the failure signals that matter during a rollout:
// uncaught JS errors, console errors, dropped connections, and 5xx responses.
// The dashboard and quiz specs both attach these and gate on them, so the probe
// behaves identically whichever UI it is pointed at.
export interface Diagnostics {
  consoleErrors: string[];
  pageErrors: string[];
  serverErrors: string[];
  failedRequests: string[];
}

export function watch(page: Page): Diagnostics {
  const d: Diagnostics = { consoleErrors: [], pageErrors: [], serverErrors: [], failedRequests: [] };

  page.on('console', (msg) => {
    if (msg.type() === 'error') d.consoleErrors.push(msg.text());
  });
  page.on('pageerror', (err) => d.pageErrors.push(err.message));
  page.on('response', (resp) => {
    if (resp.status() >= 500) {
      d.serverErrors.push(`${resp.status()} ${resp.request().method()} ${resp.url()}`);
    }
  });
  page.on('requestfailed', (req) => {
    const reason = req.failure()?.errorText ?? 'failed';
    // ERR_ABORTED is the browser cancelling in-flight requests on navigation — not downtime.
    if (reason !== 'net::ERR_ABORTED') {
      d.failedRequests.push(`${reason} ${req.method()} ${req.url()}`);
    }
  });

  return d;
}

// Attach everything we saw to the report and echo a summary to the console.
export async function reportDiagnostics(testInfo: TestInfo, d: Diagnostics): Promise<void> {
  await testInfo.attach('diagnostics.json', {
    body: JSON.stringify(d, null, 2),
    contentType: 'application/json',
  });

  const problems = [
    ...d.serverErrors.map((e) => `  [5xx]      ${e}`),
    ...d.failedRequests.map((e) => `  [dropped]  ${e}`),
    ...d.pageErrors.map((e) => `  [pageerr]  ${e}`),
    ...d.consoleErrors.map((e) => `  [console]  ${e}`),
  ];
  if (problems.length > 0) {
    // eslint-disable-next-line no-console
    console.log(`\n!!! ${problems.length} problem(s) observed during the journey:\n${problems.join('\n')}\n`);
  }
}

// Zero-downtime gate: a single 5xx or dropped connection during the run is a failure.
// Soft assertions so one run reports *every* problem category at once instead of
// stopping at the first — the continuous runner logs them and resumes.
export function assertNoServerProblems(d: Diagnostics): void {
  expect.soft(
    d.serverErrors,
    `Server (5xx) responses occurred during the journey:\n${d.serverErrors.join('\n')}`,
  ).toHaveLength(0);
  expect.soft(
    d.failedRequests,
    `Requests were dropped during the journey:\n${d.failedRequests.join('\n')}`,
  ).toHaveLength(0);
  expect.soft(
    d.pageErrors,
    `Uncaught page errors occurred during the journey:\n${d.pageErrors.join('\n')}`,
  ).toHaveLength(0);
}
