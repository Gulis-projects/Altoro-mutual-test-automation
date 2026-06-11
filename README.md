# Altoro Mutual — Automated Test Suite

End-to-end test coverage for [demo.testfire.net](https://demo.testfire.net), an intentionally vulnerable banking demo app. This repo hosts two production-grade automation frameworks side by side.

| | Playwright (TypeScript) | Selenium (Java / Cucumber) |
|---|---|---|
| **Language** | TypeScript | Java 17 |
| **Framework** | Playwright Test | Selenium WebDriver 4 + Cucumber BDD |
| **Test runner** | Playwright Test | TestNG via Maven Surefire |
| **Pattern** | Page Object Model | Page Object Model + DI via PicoContainer |
| **Parallelism** | Built-in, per-file | ThreadLocal WebDriver, per-scenario |
| **Reporting** | HTML (built-in) | Cucumber HTML + JSON |
| **CI** | GitHub Actions | GitHub Actions |
| **Credentials** | `.env` via dotenv | `.env` via dotenv-java |

---

## Target Application

**Altoro Mutual** (`https://demo.testfire.net`) is a deliberately vulnerable banking demo.

| Credential | Value |
|---|---|
| Username | `jsmith` |
| Password | `Demo1234` |

> **Security note:** The app contains intentional SQL injection, XSS, and session-management vulnerabilities. Tests in both frameworks document these as known bugs.

---

## Playwright

### Prerequisites

- Node.js ≥ 18
- npm ≥ 9

### Setup

```bash
cd playwright
npm install
npx playwright install --with-deps
```

Create a `.env` file from the example:

```bash
cp .env.example .env
# Edit .env if you need non-default credentials
```

### Run tests

```bash
# All tests, all browsers
npm test

# Single browser
npm run test:chromium
npm run test:firefox
npm run test:webkit

# Headed (visible browser)
npm run test:headed

# Open HTML report after a run
npm run report
```

### Project structure

```
playwright/
├── .env.example            # Credential template
├── playwright.config.ts    # Browsers, baseURL, retries, reporters
├── pages/
│   ├── LoginPage.ts        # Login page actions & assertions
│   ├── AccountPage.ts      # Account summary page
│   └── TransferPage.ts     # Fund transfer page
└── tests/
    ├── login.spec.ts       # Auth flow + SQL injection documentation
    ├── account.spec.ts     # Account summary post-login
    └── transfer.spec.ts    # Fund transfer happy path + validation
```

### Key design decisions

- **Page Object Model**: every page has its own class; tests never call `page.locator()` directly.
- **dotenv**: credentials are loaded from `.env` at startup; `playwright.config.ts` falls back to public demo defaults so tests work out of the box.
- **CI-friendly headless**: `headless` is `true` when `CI=true` (set automatically by GitHub Actions), `false` locally.
- **Parallel execution**: `fullyParallel: true` in config; each test file runs in its own worker.
- **Retries**: 2 retries on CI, 0 locally — avoids flakiness in pipelines without hiding real bugs locally.

---

## Selenium (Java / Cucumber BDD)

### Prerequisites

- Java 17+
- Maven 3.8+
- Chrome, Firefox, or Edge installed

### Setup

```bash
cd selenium-project
cp .env.example .env
# Edit .env if you need non-default credentials
```

Maven downloads all dependencies automatically on first run.

### Run tests

```bash
# Default: all scenarios, Chrome, headed
mvn test

# Smoke tests only
mvn test -Dcucumber.filter.tags="@smoke"

# Regression suite
mvn test -Dcucumber.filter.tags="@regression"

# Security tests
mvn test -Dcucumber.filter.tags="@security"

# Specific feature area
mvn test -Dcucumber.filter.tags="@login"
mvn test -Dcucumber.filter.tags="@transfer"
mvn test -Dcucumber.filter.tags="@account"

# Headless (CI mode)
mvn test -Dheadless=true

# Different browser
mvn test -Dbrowser=firefox
mvn test -Dbrowser=edge

# Cross-browser parallel (Chrome + Safari + Edge)
mvn test -Pcross-browser
```

Reports are written to `reports/cucumber-report.html` and `reports/cucumber-report.json`.  
Screenshots on failure are saved to `screenshots/`.

### Project structure

```
selenium-project/
├── .env.example
├── config/
│   └── config.properties       # Non-sensitive settings (URL, timeouts, browser)
├── testng.xml                  # Default single-browser suite
├── testng-cross-browser.xml    # Parallel cross-browser suite
├── src/
│   ├── main/java/com/demoqa/
│   │   ├── config/
│   │   │   └── ConfigReader.java     # Reads .env → system props → config.properties
│   │   ├── driver/
│   │   │   └── DriverManager.java    # ThreadLocal WebDriver lifecycle
│   │   ├── pages/
│   │   │   ├── BasePage.java         # Shared Selenium actions (click, type, wait)
│   │   │   ├── LoginPage.java
│   │   │   ├── AccountPage.java
│   │   │   ├── TransferPage.java
│   │   │   └── SearchPage.java
│   │   └── utils/
│   │       ├── ScreenshotUtil.java
│   │       └── WaitUtil.java
│   └── test/
│       ├── java/com/demoqa/
│       │   ├── TestContext.java           # Cucumber DI hub (page objects + shared state)
│       │   ├── runners/
│       │   │   └── CucumberRunner.java
│       │   └── stepdefs/
│       │       ├── Hooks.java             # @Before / @After (driver init, screenshots)
│       │       ├── LoginSteps.java
│       │       ├── AccountSteps.java
│       │       ├── TransferSteps.java
│       │       └── SearchSteps.java
│       └── resources/features/
│           ├── login.feature
│           ├── account.feature
│           ├── transfer.feature
│           └── search.feature
```

### Key design decisions

- **BDD with Cucumber**: feature files are written in Gherkin and readable by non-engineers; step definitions map directly to page object calls.
- **TestContext + PicoContainer DI**: one `TestContext` instance is created per scenario and injected into every step definition class, letting steps share page objects and data across class boundaries.
- **ThreadLocal WebDriver**: `DriverManager` stores the driver in `ThreadLocal<WebDriver>` so parallel scenarios never share browser state.
- **ConfigReader priority chain**: JVM system property → `.env` file (via `dotenv-java`) → `config.properties`. CI injects secrets via environment variables without touching config files.
- **Hooks**: `@Before` opens a fresh browser; `@After` captures a screenshot on failure (embedded in the Cucumber report) and always quits the driver.

---

## Known Security Vulnerabilities

`demo.testfire.net` is intentionally vulnerable. The following are **documented by tests**, not hidden:

| Vulnerability | Where documented |
|---|---|
| SQL injection bypasses login | `playwright/tests/login.spec.ts` — `SQL injection bypasses login` |
| SQL injection bypasses login | `selenium-project/.../login.feature` — `Login form rejects injection payloads` |
| No server-side account ownership validation | Transfer tests can move funds between any accounts |

These tests show that the vulnerability is reproducible automatically — the same evidence required in a security regression report.

---

## CI / CD

| Workflow | Trigger | Matrix |
|---|---|---|
| `.github/workflows/playwright.yml` | Push / PR on `playwright/**` | Chromium, Firefox, WebKit |
| `.github/workflows/selenium.yml` | Push / PR on `selenium-project/**` | Chrome, Firefox (headless) |

Credentials are stored as GitHub Actions repository secrets (`APP_USERNAME`, `APP_PASSWORD`). No credentials appear in workflow files or committed config.

---

## Framework comparison

| Concern | Playwright | Selenium + Cucumber |
|---|---|---|
| **Setup time** | ~2 min (`npm install`) | ~5 min (Maven resolves dependencies) |
| **Browser management** | Built-in, zero config | WebDriverManager auto-downloads drivers |
| **Auto-wait** | Built-in on every action | Explicit `WebDriverWait` required |
| **Flakiness** | Very low (smart waits) | Moderate (needs careful wait strategy) |
| **Parallelism** | File-level, no extra config | Scenario-level via TestNG `@DataProvider(parallel=true)` |
| **BDD readability** | Low (code-first) | High (Gherkin readable by PO/BA) |
| **Debugging** | Trace viewer, video, timeline | Screenshots on failure, Cucumber HTML report |
| **Cross-browser** | 3 engines (Chromium, Firefox, WebKit) | Chrome, Firefox, Edge, Safari |
| **Language ecosystem** | TypeScript / JavaScript | Java (strong in enterprise/banking) |
| **Best for** | Fast feedback, modern web apps | BDD with stakeholders, enterprise Java teams |
