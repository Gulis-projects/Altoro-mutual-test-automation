package com.demoqa.driver;

import com.demoqa.config.ConfigReader;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.safari.SafariDriver;

import java.time.Duration;

/**
 * DriverManager — Creates, provides, and destroys the WebDriver instance.
 *
 * WHY THREADLOCAL?
 * ThreadLocal means each thread gets its OWN separate WebDriver instance.
 * Right now you run tests one at a time — so this doesn't matter yet.
 * But when you run tests in PARALLEL (multiple tests at once), without
 * ThreadLocal they would all share one browser and crash each other.
 * Using ThreadLocal from day one is the senior pattern — it makes your
 * framework parallel-ready without any future refactoring.
 *
 * THE PATTERN:
 * 1. Test starts  → call initDriver()  → browser opens
 * 2. Test runs    → call getDriver()   → get the browser to interact with
 * 3. Test ends    → call quitDriver()  → browser closes, memory freed
 */
public class DriverManager {

    // ThreadLocal holds a separate WebDriver for each thread
    // "private static" means one shared storage container for the whole app
    private static final ThreadLocal<WebDriver> driver = new ThreadLocal<>();

    // Private constructor — nobody should create a DriverManager object
    // All methods are static, called directly: DriverManager.getDriver()
    private DriverManager() {}

    /**
     * Creates and configures the WebDriver based on config.properties.
     * Call this at the START of each test (in @BeforeScenario).
     */
    public static void initDriver() {
        String browser = ConfigReader.get("browser").toLowerCase();
        boolean headless = ConfigReader.getBoolean("headless");
        int pageLoadTimeout = ConfigReader.getInt("page.load.timeout");
        int implicitWait = ConfigReader.getInt("implicit.wait");

        WebDriver webDriver;

        switch (browser) {
            case "chrome" -> {
                WebDriverManager.chromedriver().setup();
                ChromeOptions options = new ChromeOptions();
                if (headless) options.addArguments("--headless=new");
                options.addArguments("--window-size=1280,800");
                options.addArguments("--disable-extensions");
                options.addArguments("--no-sandbox");
                options.addArguments("--disable-dev-shm-usage");
                // Accept confirm/alert dialogs automatically so form-submission prompts
                // (e.g. the Altoro Mutual transfer confirmation) don't block tests.
                options.setUnhandledPromptBehaviour(
                        org.openqa.selenium.UnexpectedAlertBehaviour.ACCEPT);
                webDriver = new ChromeDriver(options);
            }
            case "firefox" -> {
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions options = new FirefoxOptions();
                if (headless) options.addArguments("--headless");
                options.setUnhandledPromptBehaviour(
                        org.openqa.selenium.UnexpectedAlertBehaviour.ACCEPT);
                webDriver = new FirefoxDriver(options);
            }
            case "edge" -> {
                WebDriverManager.edgedriver().setup();
                EdgeOptions options = new EdgeOptions();
                if (headless) options.addArguments("--headless=new");
                options.setUnhandledPromptBehaviour(
                        org.openqa.selenium.UnexpectedAlertBehaviour.ACCEPT);
                webDriver = new EdgeDriver(options);
            }
            case "safari" -> {
                // SafariDriver is bundled with macOS — no WebDriverManager needed.
                // Prerequisite: Safari → Develop → Allow Remote Automation
                webDriver = new SafariDriver();
            }
            default -> throw new RuntimeException(
                    "Browser '" + browser + "' is not supported. " +
                            "Use: chrome, firefox, edge, or safari in config.properties"
            );
        }

        // Set timeouts — how long Selenium waits before giving up
        webDriver.manage().timeouts()
                .pageLoadTimeout(Duration.ofSeconds(pageLoadTimeout));
        webDriver.manage().timeouts()
                .implicitlyWait(Duration.ofSeconds(implicitWait));

        // Maximize the browser window
        webDriver.manage().window().maximize();

        // Store the driver in ThreadLocal for this thread
        driver.set(webDriver);
    }

    /**
     * Returns the WebDriver for the current thread.
     * Call this anywhere you need to interact with the browser.
     * Example: DriverManager.getDriver().findElement(...)
     */
    public static WebDriver getDriver() {
        if (driver.get() == null) {
            throw new RuntimeException(
                    "WebDriver is null — did you forget to call initDriver() first?"
            );
        }
        return driver.get();
    }

    /**
     * Closes the browser and cleans up memory.
     * Call this at the END of each test (in @AfterScenario).
     * ALWAYS call this — if you don't, browser processes pile up
     * and slow down or crash your machine.
     */
    public static void quitDriver() {
        if (driver.get() != null) {
            driver.get().quit();  // closes browser
            driver.remove();      // removes from ThreadLocal — prevents memory leak
        }
    }
}