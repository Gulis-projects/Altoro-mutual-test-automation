# Altoro Banking QA Framework

Production-grade UI automation framework for the [Altoro Mutual](http://demo.testfire.net) demo banking application, built as a portfolio project demonstrating senior-level Java and Cucumber BDD practices.

**Stack:** Java 17 · Selenium WebDriver 4 · Cucumber 7 · TestNG · ExtentReports · WebDriverManager

---

## Prerequisites

| Tool | Version |
|---|---|
| Java | 17+ |
| Maven | 3.8+ |
| Chrome | latest (managed automatically) |
| Safari | macOS only — enable via **Develop → Allow Remote Automation** |
| Edge | latest (managed automatically) |

---

## Running Tests

### Single browser

```bash
mvn test                          # Chrome (default)
mvn test -Psafari                 # Safari (macOS only)
mvn test -Pedge                   # Edge
mvn test -Dheadless=true          # Chrome headless (CI mode)
```

### Cross-browser parallel

Runs Chrome, Safari, and Edge simultaneously using TestNG parallel suite:

```bash
mvn test -Pcross-browser
```

### Tag filtering

```bash
mvn test -Dcucumber.filter.tags="@smoke"
mvn test -Dcucumber.filter.tags="@regression"
mvn test -Dcucumber.filter.tags="@security"
mvn test -Dcucumber.filter.tags="@login"
mvn test -Dcucumber.filter.tags="@transfer"
```

---

## Reports

| Artifact | Path |
|---|---|
| Cucumber HTML report | `reports/cucumber-report.html` |
| Cucumber JSON report | `reports/cucumber-report.json` |
| Failure screenshots | `screenshots/` |

---

## Architecture

```
src/
├── main/java/com/demoqa/
│   ├── config/        ConfigReader       — typed access to config.properties; system properties take precedence
│   ├── driver/        DriverManager      — ThreadLocal<WebDriver>; supports Chrome, Safari, Edge, Firefox
│   ├── pages/         BasePage + POMs    — all Selenium interactions centralised in BasePage
│   └── utils/         ScreenshotUtil     — failure screenshots; WaitUtil — edge-case waits
└── test/java/com/demoqa/
    ├── TestContext.java                  — Cucumber PicoContainer DI hub; one instance per scenario
    ├── runners/CucumberRunner.java       — @DataProvider(parallel=true); @BeforeTest browser injection
    └── stepdefs/                         — Hooks (lifecycle) + feature step definitions
config/config.properties                  — base URL, browser, timeouts, credentials
testng.xml                                — default single-browser parallel suite
testng-cross-browser.xml                  — Chrome + Safari + Edge parallel suite
```

**Key design decisions:**

- `ThreadLocal<WebDriver>` in `DriverManager` makes every scenario thread-safe with zero locking — required for `@DataProvider(parallel=true)`.
- `TestContext` is injected by PicoContainer into every step definition class that declares it in its constructor. Cross-step state (selected accounts, transfer amounts) lives here.
- `BasePage` uses `TestNG Assert` (not Java `assert`) so failures always produce meaningful messages and cannot be silenced with `-da`.
- `ConfigReader.get()` checks `System.getProperty()` before the `.properties` file, allowing TestNG XML parameters and Maven `-D` flags to override config without touching files.
- `LoginPage.login()` (credentials + click) is kept separate from `enterUsername()`/`enterPassword()` to avoid double-clicks in scenarios that step through the button explicitly.

---

## Test Coverage

| Feature | Smoke | Regression | Security | Total scenarios |
|---|---|---|---|---|
| Login / Auth | 2 | 9 | 4 | 15 |
| Account Summary | 2 | 2 | — | 4 |
| Fund Transfer | 2 | 4 | — | 6 |
| Site Search | 1 | 2 | — | 3 |

Auth coverage includes: valid login, invalid password, unrecognised credentials (Scenario Outline), whitespace-only input, SQL injection and XSS payloads (Scenario Outline), logout, re-login after logout, and unauthenticated redirect guard.

Transfer coverage includes: page load, successful transfer, and a Scenario Outline covering zero, empty, negative, and non-numeric amounts.

---

## CI / CD

GitHub Actions runs the smoke suite on Chrome and Edge on every push and pull request. Safari is run on macOS-hosted runners and is marked `continue-on-error` due to hosted runner availability.

See [`.github/workflows/ci.yml`](.github/workflows/ci.yml).
