import { defineConfig, devices } from '@playwright/test';

export default defineConfig({
  testDir: './specs',
  timeout: 30_000,
  expect: { timeout: 5_000 },
  fullyParallel: false,
  workers: 1,
  // Bewust geen retries: anders verdwijnt de flakiness uit beeld en dat is
  // precies wat je in dit project wilt zien.
  retries: 0,
  reporter: [['list'], ['json', { outputFile: 'test-results/report.json' }],
    ['html', { open: 'never', outputFolder: 'playwright-report' }]],
  use: {
    baseURL: process.env.BASE_URL ?? 'http://localhost:3004',
    trace: 'retain-on-failure',
    screenshot: 'only-on-failure',
    locale: 'nl-NL',
    timezoneId: 'Europe/Amsterdam'
  },
  projects: [
    { name: 'chromium', use: { ...devices['Desktop Chrome'] } }
  ]
});
