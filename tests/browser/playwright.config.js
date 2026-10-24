const { defineConfig, devices } = require("@playwright/test");

module.exports = defineConfig({
  testDir: ".",
  testMatch: "*.spec.js",
  use: { baseURL: "http://localhost:8080" },
  projects: [
    { name: "desktop", use: { ...devices["Desktop Chrome"] } },
    { name: "mobile", use: { ...devices["Pixel 7"] } }
  ],
  webServer: {
    command: "jbang --fresh site.electrostatic:electrostatic-cli:0.0.3 serve --base-url=http://localhost:8080",
    cwd: "../..",
    url: "http://localhost:8080",
    reuseExistingServer: !process.env.CI,
    timeout: 180000
  }
});
