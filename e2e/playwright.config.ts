import { defineConfig, devices } from '@playwright/test';

// Entry point of the journey. Defaults to the docker-compose URL; override for KIND:
//   QUIZZLER_UI_BASE_URL=http://quizzler.localhost npx playwright test
const quizzlerUiBaseUrl = process.env.QUIZZLER_UI_BASE_URL ?? 'http://localhost:4200';

export default defineConfig({
  testDir: './tests',
  // The journey crosses two UIs and waits on the payment confirmation poll.
  timeout: 120_000,
  expect: { timeout: 15_000 },
  fullyParallel: false,
  // No retries: this probe is meant to surface any blip during a rollout, not hide it.
  retries: 0,
  // `list` prints progress live; `html` keeps screenshots/video/trace for failures.
  reporter: [['list'], ['html', { open: 'never' }]],
  use: {
    baseURL: quizzlerUiBaseUrl,
    actionTimeout: 15_000,
    navigationTimeout: 30_000,
    // Make failures inspectable.
    trace: 'retain-on-failure',
    screenshot: 'only-on-failure',
    video: 'retain-on-failure',
  },
  projects: [
    { name: 'chromium', use: { ...devices['Desktop Chrome'] } },
  ],
});
