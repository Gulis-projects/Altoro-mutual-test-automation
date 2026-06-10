package com.demoqa.utils;

import com.demoqa.driver.DriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * WaitUtil — Extra wait helpers for edge cases.
 *
 * WHY THIS EXISTS:
 * BasePage handles 95% of waits through waitForVisibility()
 * and waitForClickability(). But some situations need special handling:
 * - Waiting for a page to fully load (JavaScript complete)
 * - Waiting for an element to DISAPPEAR (loading spinners)
 * - Waiting for an alert dialog to appear
 *
 * These are less common but critical when you need them.
 * Keeping them in a separate utility class keeps BasePage clean.
 */
public class WaitUtil {

    private WaitUtil() {}

    /**
     * Wait for the page to fully load.
     * Checks JavaScript's document.readyState == "complete"
     * Useful after navigation or form submission.
     *
     * @param timeoutSeconds how long to wait before giving up
     */
    public static void waitForPageLoad(int timeoutSeconds) {
        WebDriver driver = DriverManager.getDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        wait.until(webDriver ->
                ((JavascriptExecutor) webDriver)
                        .executeScript("return document.readyState")
                        .equals("complete")
        );
    }

    /**
     * Wait for a loading spinner or overlay to disappear.
     * Very common in financial apps — after you click Transfer,
     * a spinner shows while the transaction processes.
     * You must wait for it to vanish before asserting anything.
     *
     * @param locator        the spinner/overlay element
     * @param timeoutSeconds how long to wait
     */
    public static void waitForElementToDisappear(By locator, int timeoutSeconds) {
        WebDriver driver = DriverManager.getDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    /**
     * Wait for a browser alert dialog to appear, then return its text.
     * Example: after clicking Delete, a confirm() alert pops up.
     *
     * @param timeoutSeconds how long to wait
     * @return the text message shown in the alert
     */
    public static String waitForAlertAndGetText(int timeoutSeconds) {
        WebDriver driver = DriverManager.getDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        wait.until(ExpectedConditions.alertIsPresent());
        return driver.switchTo().alert().getText();
    }

    /**
     * Accept (click OK on) a browser alert dialog.
     */
    public static void acceptAlert(int timeoutSeconds) {
        WebDriver driver = DriverManager.getDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        wait.until(ExpectedConditions.alertIsPresent());
        driver.switchTo().alert().accept();
    }
}