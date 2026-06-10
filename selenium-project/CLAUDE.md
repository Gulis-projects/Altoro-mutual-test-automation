# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
# Run the full test suite
mvn test

# Run only smoke tests
mvn test -Dcucumber.filter.tags="@smoke"

# Run only regression tests
mvn test -Dcucumber.filter.tags="@regression"

# Run a specific feature tag (e.g. login, account, transfer, search, security)
mvn test -Dcucumber.filter.tags="@login"

# Run headless (CI mode)
mvn test -Dheadless=true

# Run on a different browser
mvn test -Dbrowser=firefox

# Combine filters
mvn test -Dcucumber.filter.tags="@smoke" -Dheadless=true -Dbrowser=chrome
```

Reports are written to `reports/cucumber-report.html` and `reports/cucumber-report.json`. Screenshots on failure are saved to `screenshots/`.

## Architecture

**Test target:** Altoro Mutual demo banking app at `http://demo.testfire.net`. Credentials: `jsmith` / `Demo1234` (public demo credentials stored in `config/config.properties`).

**Layer flow:**

```
.feature files (Gherkin)
  → step definitions (stepdefs/)
    → page objects (pages/)
      → BasePage (Selenium primitives)
        → DriverManager (ThreadLocal WebDriver)
```

**`TestContext`** is the Cucumber DI hub — created once per scenario and injected into every step definition class that declares it in its constructor. All page objects live here. Any data that must cross step class boundaries (e.g. `transferAmount`, `scenarioName`) is stored as public fields on `TestContext`.

**`BasePage`** owns all Selenium interactions (`click`, `type`, `getText`, `waitForVisibility`, etc.). Page objects never call `driver.findElement()` directly — they always go through `BasePage` methods. This keeps waits and interactions consistent.

**`DriverManager`** uses `ThreadLocal<WebDriver>` so the framework is parallel-safe from the start. Lifecycle: `initDriver()` in `@Before`, `quitDriver()` in `@After` (always in `finally`).

**`login()` vs credential entry distinction:** `LoginPage.login(username, password)` enters credentials AND clicks submit — used by `AccountSteps`/`TransferSteps` Background steps to set up a logged-in state. `LoginPage.enterUsername()` + `enterPassword()` enter credentials only — used by `LoginSteps` when the feature file controls when the button is clicked. Do not collapse these back into a single method; the distinction prevents double-clicks in scenarios that explicitly step through the button click.

**Tag strategy:**
- `@smoke` — critical path, runs on every commit
- `@regression` — full suite, runs before release
- `@security` — input validation / injection tests (subset of `@regression`)
- Feature tags (`@login`, `@account`, `@transfer`, `@search`) for targeted runs

## Known Incomplete Code

`TransferSteps.java` has several unimplemented step methods (empty bodies or syntax errors — `transferPage.` with nothing after it). The corresponding `TransferPage` page object methods for account selection, amount entry, submit, and result assertions need to be implemented before the `@transfer` scenarios will pass.

## Configuration

All runtime settings are in `config/config.properties`. Browser (`chrome`/`firefox`/`edge`), headless mode, and timeout values can be overridden at the command line with `-D` flags as shown above — Maven Surefire passes them as system properties which `ConfigReader` reads via `System.getProperty` fallback through `Properties`.
