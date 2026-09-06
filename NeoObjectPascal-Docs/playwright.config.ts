import { defineConfig } from '@playwright/test'

// Smoke tests run against the built site served by `vitepress preview`.
export default defineConfig({
  testDir: './tests',
  timeout: 30_000,
  fullyParallel: true,
  use: {
    baseURL: 'http://localhost:4173'
  },
  webServer: {
    command: 'npm run docs:preview -- --port 4173',
    url: 'http://localhost:4173',
    reuseExistingServer: !process.env.CI,
    timeout: 60_000
  }
})
