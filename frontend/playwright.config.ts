import { defineConfig, devices } from '@playwright/test';

export default defineConfig({
  testDir: './tests/e2e',
  fullyParallel: false,
  timeout: 30_000,
  expect: { timeout: 8_000 },
  reporter: [['list'], ['html', { outputFolder: '../docs/e2e-report', open: 'never' }]],
  use: {
    baseURL: process.env.FINX_URL ?? 'http://localhost:5173',
    trace: 'retain-on-failure',
    screenshot: 'only-on-failure',
    video: 'on'
  },
  projects: [{ name: 'chromium', use: { ...devices['Desktop Chrome'] } }]
});
