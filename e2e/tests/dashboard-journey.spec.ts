import { test, expect, Page, Locator } from '@playwright/test';
import { watch, reportDiagnostics, assertNoServerProblems } from './support/diagnostics';

// --- Configuration ---------------------------------------------------------
// The dashboard UI is a separate SPA (its own ingress host / compose port). It
// reads a single seeded session by id from dashboard-api.
//
//   compose: http://localhost:4202   KIND: http://dashboard.localhost
//
// The default id is one of the rows inserted by
// api/dashboard/insert-session-dashboard-data.sql — run that seed first, or
// point DASHBOARD_ID at a session the dashboard has already received events for.
const DASHBOARD_UI = process.env.DASHBOARD_UI_BASE_URL ?? 'http://localhost:4202';
const DASHBOARD_ID = process.env.DASHBOARD_ID ?? 'session-001';

// Read the numeric value out of a "<label>: <value>" stat row.
function statValue(page: Page, label: string): Locator {
  return page.locator('.stat-row', { hasText: label }).locator('.stat-value');
}

async function readInt(locator: Locator): Promise<number> {
  const text = (await locator.innerText()).replace(/[^\d-]/g, '');
  return Number.parseInt(text, 10);
}

// --- Happy path: a seeded session renders its stats ------------------------
test('dashboard journey: a seeded session renders its quiz and payment stats', async ({ page }, testInfo) => {
  const diag = watch(page);

  try {
    await test.step('Open the seeded session dashboard', async () => {
      await page.goto(`${DASHBOARD_UI}/dashboard/${DASHBOARD_ID}`, { waitUntil: 'domcontentloaded' });

      // The loading and error states are mutually exclusive with the content.
      await expect(
        page.getByText('Error Loading Dashboard'),
        `Dashboard reported an error for "${DASHBOARD_ID}" — is dashboard-api up and the session seeded ` +
          '(api/dashboard/insert-session-dashboard-data.sql)?',
      ).toHaveCount(0);
      await expect(page.getByRole('heading', { name: 'Session Dashboard' })).toBeVisible();
      await expect(page.getByText(`Session ID: ${DASHBOARD_ID}`)).toBeVisible();
    });

    await test.step('Both stat cards are present', async () => {
      await expect(page.getByRole('heading', { name: 'Quiz Performance' })).toBeVisible();
      await expect(page.getByRole('heading', { name: 'Payment Information' })).toBeVisible();
    });

    await test.step('Quiz stats are internally consistent', async () => {
      const questions = await readInt(statValue(page, 'Total Questions:'));
      const answered = await readInt(statValue(page, 'Answered:'));
      const correct = await readInt(statValue(page, 'Correct Answers:'));
      const wrong = await readInt(statValue(page, 'Wrong Answers:'));

      // These are real values served from the DB via dashboard-api, not placeholders.
      expect(Number.isNaN(questions), 'Total Questions should render a number').toBe(false);
      // The UI derives "Answered" as correct + wrong — verify the rendered figures agree.
      expect(answered).toBe(correct + wrong);
      expect(questions).toBeGreaterThanOrEqual(answered);

      // Accuracy is shown as a whole percentage in range.
      const accuracy = await readInt(page.locator('.accuracy-value'));
      expect(accuracy).toBeGreaterThanOrEqual(0);
      expect(accuracy).toBeLessThanOrEqual(100);
      if (answered > 0) {
        expect(accuracy).toBe(Math.round((correct / answered) * 100));
      }
    });

    await test.step('Payment info renders a currency amount', async () => {
      await expect(statValue(page, 'Number of Payments:')).toBeVisible();
      // "$<n>.<nn>" — two decimal places from formatCurrency().
      await expect(statValue(page, 'Total Payment Amount:')).toHaveText(/^\$\d+\.\d{2}$/);
    });

    await test.step('The wisdom development chart renders', async () => {
      // The chart either plots time-zone bars or shows its own "no data" state,
      // but the component must mount without throwing.
      await expect(page.locator('app-wisdom-chart')).toBeVisible();
    });
  } finally {
    await reportDiagnostics(testInfo, diag);
  }

  assertNoServerProblems(diag);
});

// --- Error path: an unknown session fails gracefully -----------------------
// dashboard-api returns 404 for an unknown id; the SPA must show its error state
// rather than crash — and a 404 must not be mistaken for downtime (no 5xx, no
// uncaught JS error).
test('dashboard journey: an unknown session shows a graceful error', async ({ page }, testInfo) => {
  const diag = watch(page);
  const unknownId = `does-not-exist-${Date.now()}`;

  try {
    await page.goto(`${DASHBOARD_UI}/dashboard/${unknownId}`, { waitUntil: 'domcontentloaded' });

    await expect(page.getByRole('heading', { name: 'Error Loading Dashboard' })).toBeVisible();
    await expect(page.getByText('Unable to load dashboard data.')).toBeVisible();
    // The stats content must not have rendered for a missing session.
    await expect(page.getByRole('heading', { name: 'Session Dashboard' })).toHaveCount(0);
  } finally {
    await reportDiagnostics(testInfo, diag);
  }

  // The 404 is handled in-app, so it must not surface as a server outage.
  assertNoServerProblems(diag);
});
