package com.demoqa.stepdefs;

import com.demoqa.TestContext;
import com.demoqa.driver.DriverManager;
import com.demoqa.utils.ScreenshotUtil;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;

/**
 * Hooks — Runs setup and teardown code around every Cucumber scenario.
 *
 * @Before runs BEFORE each scenario — opens the browser
 * @After  runs AFTER  each scenario — closes the browser,
 *                                     takes screenshot if failed
 *
 * WHY THIS IS IMPORTANT:
 * Without hooks, you would have to open and close the browser
 * in every single step definition. That is repetitive and error-prone.
 * Hooks handle it once, automatically, for every scenario.
 *
 * This is the class that connects DriverManager and ScreenshotUtil
 * to the actual test execution lifecycle.
 */
public class Hooks {

    // TestContext is injected by Cucumber — same instance shared
    // across all step definition classes in the same scenario
    private final TestContext context;

    public Hooks(TestContext context) {
        this.context = context;
    }

    /**
     * Runs BEFORE every scenario.
     * Opens a fresh browser session.
     */
    @Before
    public void setUp(Scenario scenario) {
        // Store scenario name in context so other classes can use it
        // (e.g. for screenshot filenames)
        context.scenarioName = scenario.getName();
        System.out.println("\n─────────────────────────────────────");
        System.out.println("Starting scenario: " + scenario.getName());
        System.out.println("─────────────────────────────────────");

        // Initialize the WebDriver — opens the browser
        DriverManager.initDriver();
        // Page objects need the driver, so create them after initDriver()
        context.init();
    }

    /**
     * Runs AFTER every scenario.
     * Takes a screenshot if the scenario failed.
     * Always closes the browser — even if the test crashed.
     */
    @After
    public void tearDown(Scenario scenario) {
        try {
            if (scenario.isFailed()) {
                System.out.println("Scenario FAILED — capturing screenshot");

                // Capture screenshot and attach to Cucumber report
                String screenshotPath = ScreenshotUtil.capture(scenario.getName());

                // Also embed screenshot directly in Cucumber HTML report
                if (!screenshotPath.isEmpty()) {
                    scenario.attach(
                            java.nio.file.Files.readAllBytes(
                                    java.nio.file.Paths.get(screenshotPath)
                            ),
                            "image/png",
                            scenario.getName()
                    );
                }

                System.out.println("Screenshot saved: " + screenshotPath);
            }
        } catch (Exception e) {
            System.err.println("Hooks: error during teardown — " + e.getMessage());
        } finally {
            // ALWAYS quit the driver — the finally block runs even if
            // an exception was thrown above
            DriverManager.quitDriver();
            System.out.println("Browser closed. Scenario: "
                    + (scenario.isFailed() ? "FAILED" : "PASSED"));
        }
    }
}