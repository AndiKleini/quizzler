import { test, expect, Page, TestInfo } from '@playwright/test';

// --- Configuration ---------------------------------------------------------
// The journey enters on the quizzler UI and is carried across to the payment UI
// (and back) by the application's own redirects, so only the entry URL and the
// seeded session id need configuring.
const QUIZZLER_UI = process.env.QUIZZLER_UI_BASE_URL ?? 'http://localhost:4200';
const SESSION_ID = process.env.QUIZZLER_SESSION_ID ?? '11111111-2222-3333-4444-555555555555';
// Upper bound on questions so a broken "Next" can never loop forever.
const MAX_QUESTIONS = 25;

// --- Diagnostics: make any problem visible ---------------------------------
// We watch the browser for the failure signals that matter during a rollout:
// uncaught JS errors, console errors, dropped connections, and 5xx responses.
interface Diagnostics {
  consoleErrors: string[];
  pageErrors: string[];
  serverErrors: string[];
  failedRequests: string[];
}

function watch(page: Page): Diagnostics {
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

// Fail loudly (with the captured URL) if the app routed itself to its error page.
async function ensureNotErrorPage(page: Page): Promise<void> {
  const onErrorRoute = page.url().includes('/error');
  const errorTextVisible = await page
    .getByText('Something went wrong')
    .isVisible()
    .catch(() => false);
  if (onErrorRoute || errorTextVisible) {
    throw new Error(`Application routed to its error page (url: ${page.url()})`);
  }
}

// Attach everything we saw to the report and echo a summary to the console.
async function reportDiagnostics(testInfo: TestInfo, d: Diagnostics): Promise<void> {
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

// --- The journey -----------------------------------------------------------
test('quiz journey: buy → pay → answer → finish (stays up end-to-end)', async ({ page }, testInfo) => {
  const diag = watch(page);

  try {
    await test.step('Open the seeded quiz session', async () => {
      await page.goto(`${QUIZZLER_UI}/quiz-session/${SESSION_ID}`, { waitUntil: 'domcontentloaded' });
      await ensureNotErrorPage(page);
      await expect(
        page.getByRole('button', { name: 'Buy Now' }),
        'Quiz session did not load a "Buy Now" — is quizzler-db seeded and quizzler-api up?',
      ).toBeVisible();
    });

    await test.step('Buy the quiz attempt', async () => {
      await page.getByRole('button', { name: 'Buy Now' }).click();
      await expect(page.getByRole('button', { name: 'Start payment' })).toBeVisible();
      await ensureNotErrorPage(page);
    });

    await test.step('Start payment (redirects to the payment UI)', async () => {
      await page.getByRole('button', { name: 'Start payment' }).click();
      await expect(page.getByRole('button', { name: 'Confirm' })).toBeVisible();
      await ensureNotErrorPage(page);
    });

    await test.step('Confirm payment (redirects back to quizzler, starts the attempt)', async () => {
      await page.getByRole('button', { name: 'Confirm' }).click();
      // quizzler polls the confirmation, then routes to the first question.
      await expect(page.getByRole('button', { name: 'Submit' })).toBeVisible({ timeout: 30_000 });
      await ensureNotErrorPage(page);
    });

    await test.step('Answer every question through to the end', async () => {
      const submit = page.getByRole('button', { name: 'Submit' });
      const next = page.getByRole('button', { name: 'Next' });
      const finished = page.getByText('Congratulations');

      for (let i = 0; i < MAX_QUESTIONS; i++) {
        // Each loop starts on either a question (Submit) or the final page (Congratulations).
        await expect(submit.or(finished).first()).toBeVisible();
        if (await finished.isVisible()) break;

        await page.locator('input[type="radio"]').first().check();
        await submit.click();

        // Evaluation reveals the "Next" button.
        await expect(next).toBeVisible();
        await ensureNotErrorPage(page);
        await next.click();
      }
    });

    await test.step('Reach the end of the quiz', async () => {
      await expect(page.getByText('Congratulations')).toBeVisible();
      await ensureNotErrorPage(page);
    });
  } finally {
    // Always surface what we observed, pass or fail.
    await reportDiagnostics(testInfo, diag);
  }

  // Zero-downtime gate: a single 5xx or dropped connection during the run is a failure.
  // Soft assertions so one run reports *every* problem category at once instead of
  // stopping at the first — the continuous runner logs them and resumes.
  expect.soft(
    diag.serverErrors,
    `Server (5xx) responses occurred during the journey:\n${diag.serverErrors.join('\n')}`,
  ).toHaveLength(0);
  expect.soft(
    diag.failedRequests,
    `Requests were dropped during the journey:\n${diag.failedRequests.join('\n')}`,
  ).toHaveLength(0);
  expect.soft(
    diag.pageErrors,
    `Uncaught page errors occurred during the journey:\n${diag.pageErrors.join('\n')}`,
  ).toHaveLength(0);
});
