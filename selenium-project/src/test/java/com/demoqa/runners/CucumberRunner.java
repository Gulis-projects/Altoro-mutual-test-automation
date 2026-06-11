package com.demoqa.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

/**
 * CucumberRunner — Entry point for the test suite.
 *
 * Parallel execution:
 *   @DataProvider(parallel=true) runs each Cucumber scenario in its own
 *   thread. DriverManager uses ThreadLocal<WebDriver> so every thread
 *   gets an isolated browser session — no cross-thread interference.
 *
 * Cross-browser via TestNG XML:
 *   testng-cross-browser.xml passes a "browser" parameter to @BeforeTest,
 *   which sets it as a system property before any scenario starts.
 *   ConfigReader checks System.getProperty() first, so the XML value
 *   takes precedence over config.properties.
 *
 * Tag filtering:
 *   mvn test -Dcucumber.filter.tags="@smoke"
 *   mvn test -Dcucumber.filter.tags="@regression"
 *   mvn test -Dcucumber.filter.tags="@security"
 */
@CucumberOptions(
        features = "src/test/resources/features",
        glue     = {"com.demoqa.stepdefs", "com.demoqa"},
        plugin   = {
                "pretty",
                "html:reports/cucumber-report.html",
                "json:reports/cucumber-report.json"
        },
        monochrome = true
)
public class CucumberRunner extends AbstractTestNGCucumberTests {

    /**
     * Reads the browser name from the TestNG XML parameter and makes it
     * available to ConfigReader (and therefore DriverManager) via a
     * system property. Defaults to "chrome" when running outside a
     * cross-browser XML (e.g. plain `mvn test`).
     */
    @BeforeTest
    @Parameters("browser")
    public void setBrowser(@Optional("chrome") String browser) {
        System.setProperty("browser", browser);
    }

    /**
     * Runs each Cucumber scenario sequentially by default.
     *
     * Why sequential: all scenarios use the same demo credentials (jsmith).
     * Running them in parallel invalidates each other's server sessions on
     * demo.testfire.net because the app only maintains one session per user.
     *
     * Cross-browser parallelism (Chrome vs Firefox vs Edge) is handled at the
     * TestNG-suite level via testng-cross-browser.xml (-Pcross-browser).
     */
    @Override
    @DataProvider(parallel = false)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}
