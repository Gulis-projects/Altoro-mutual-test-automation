package com.demoqa.pages;

import com.demoqa.driver.DriverManager;
import com.demoqa.config.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.time.Duration;

/**
 * BasePage — Parent class for all Page Objects.
 *
 * WHY THIS EXISTS:
 * Every page in the app needs the same basic actions —
 * click something, type something, wait for something, get text.
 * Instead of writing these methods in EVERY page class,
 * we write them ONCE here.
 *
 * Every page object extends BasePage:
 *   public class LoginPage extends BasePage { ... }
 *
 * This means LoginPage automatically inherits ALL methods here.
 * This is the OOP principle called INHERITANCE.
 *
 * DESIGN PRINCIPLE — Why tests never call driver.findElement() directly:
 * If Selenium changes its API, or you want to add logging/screenshots
 * to every click, you change it in ONE place here — not in 50 test files.
 */
public class BasePage {

    // Every page object has access to the driver and a wait object
    protected WebDriver driver;
    protected WebDriverWait wait;

    /**
     * Constructor — runs when any page object is created.
     * Gets the driver from DriverManager (never creates a new one).
     * Sets up WebDriverWait using the timeout from config.
     */
    public BasePage() {
        this.driver = DriverManager.getDriver();
        this.wait = new WebDriverWait(
                driver,
                Duration.ofSeconds(ConfigReader.getInt("explicit.wait"))
        );
    }

    // ─────────────────────────────────────────────────────────────────
    // NAVIGATION
    // ─────────────────────────────────────────────────────────────────

    /**
     * Navigate to the base URL + a path.
     * Example: navigateTo("/login") goes to https://demoqa.com/login
     */
    protected void navigateTo(String path) {
        driver.get(ConfigReader.get("base.url") + path);
    }

    /**
     * Get the current page URL.
     * Used in assertions: assert the URL contains "/profile"
     */
    protected String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    /**
     * Get the current page title.
     */
    protected String getPageTitle() {
        return driver.getTitle();
    }

    // ─────────────────────────────────────────────────────────────────
    // WAITING — Explicit waits before interacting
    // WHY: Pages load at different speeds. Without waiting,
    // Selenium clicks before the element exists and throws an error.
    // Explicit wait checks repeatedly until the condition is true
    // or the timeout is reached.
    // ─────────────────────────────────────────────────────────────────

    /**
     * Wait until an element is visible on the page, then return it.
     * This is the most commonly used wait in the framework.
     */
    protected WebElement waitForVisibility(By locator) {
        return wait.until(
                ExpectedConditions.visibilityOfElementLocated(locator)
        );
    }

    /**
     * Wait until an element is clickable (visible + enabled).
     * Use this before clicking buttons or links.
     */
    protected WebElement waitForClickability(By locator) {
        return wait.until(
                ExpectedConditions.elementToBeClickable(locator)
        );
    }

    /**
     * Wait until an element is present in the DOM.
     * The element might not be visible yet — just present in the HTML.
     */
    protected WebElement waitForPresence(By locator) {
        return wait.until(
                ExpectedConditions.presenceOfElementLocated(locator)
        );
    }

    /**
     * Wait until the URL contains a specific fragment.
     * Example: waitForUrlToContain("/profile")
     * Used after login to confirm redirect happened.
     */
    protected void waitForUrlToContain(String fragment) {
        wait.until(ExpectedConditions.urlContains(fragment));
    }

    // ─────────────────────────────────────────────────────────────────
    // INTERACTIONS — All user actions go through these methods
    // ─────────────────────────────────────────────────────────────────

    /**
     * Click an element. Waits for it to be clickable first.
     */
    protected void click(By locator) {
        waitForClickability(locator).click();
    }

    /**
     * Clear a field and type text into it.
     * Waits for visibility first.
     */
    protected void type(By locator, String text) {
        WebElement element = waitForVisibility(locator);
        element.clear();
        element.sendKeys(text);
    }

    /**
     * Get the visible text of an element.
     * Example: getText(By.id("username-value")) returns "gulistan"
     */
    protected String getText(By locator) {
        return waitForVisibility(locator).getText();
    }

    /**
     * Get the value attribute of an input field.
     * Example: getInputValue(By.id("userName")) returns what was typed
     */
    protected String getInputValue(By locator) {
        return waitForVisibility(locator).getAttribute("value");
    }

    /**
     * Check if an element is currently displayed on the page.
     * Returns true/false — used for conditional logic in page objects.
     */
    protected boolean isDisplayed(By locator) {
        try {
            return driver.findElement(locator).isDisplayed();
        } catch (Exception e) {
            // Element not found at all — definitely not displayed
            return false;
        }
    }

    /**
     * Scroll an element into view using JavaScript.
     * Useful for elements below the fold that Selenium can't click.
     */
    protected void scrollIntoView(By locator) {
        WebElement element = driver.findElement(locator);
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView(true);", element
        );
    }

    /**
     * Click using JavaScript instead of Selenium's normal click.
     * Use when a normal click fails due to overlapping elements
     * or animation covering the button.
     */
    protected void jsClick(By locator) {
        WebElement element = driver.findElement(locator);
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].click();", element
        );
    }

    // ─────────────────────────────────────────────────────────────────
    // ASSERTIONS — Page-level checks
    // ─────────────────────────────────────────────────────────────────

    /**
     * Assert the current URL contains a given fragment.
     * Throws AssertionError with a clear message if it fails.
     */
    protected void assertUrlContains(String fragment) {
        String currentUrl = getCurrentUrl();
        Assert.assertTrue(currentUrl.contains(fragment),
                "Expected URL to contain '" + fragment + "' but was: " + currentUrl);
    }

    protected void assertVisible(By locator) {
        Assert.assertTrue(isDisplayed(locator),
                "Expected element to be visible: " + locator);
    }

    protected void assertTextContains(By locator, String expectedText) {
        String actualText = getText(locator);
        Assert.assertTrue(actualText.contains(expectedText),
                "Expected text '" + expectedText + "' but found: '" + actualText + "'");
    }
}
