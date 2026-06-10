package com.demoqa.utils;

import com.demoqa.driver.DriverManager;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * ScreenshotUtil — Captures and saves screenshots on test failure.
 *
 * WHY THIS EXISTS:
 * When a test fails in CI at 2am, nobody is watching the screen.
 * A screenshot captures exactly what the browser showed at the
 * moment of failure — the most valuable debugging tool you have.
 *
 * HOW IT WORKS:
 * Selenium's TakesScreenshot interface captures the browser as a
 * byte array. We write those bytes to a PNG file in /screenshots.
 * The Hooks class calls this automatically after every failed scenario.
 */
public class ScreenshotUtil {

    // Folder where screenshots are saved
    private static final String SCREENSHOTS_DIR = "screenshots";

    // Date format used in filenames: 2026-04-14_10-30-45
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

    // Private constructor — utility class, never instantiated
    private ScreenshotUtil() {}

    /**
     * Captures a screenshot and saves it to the screenshots folder.
     *
     * @param scenarioName The name of the failing scenario
     *                     (used in the filename so you know which test failed)
     * @return The full file path of the saved screenshot,
     *         or empty string if capture failed
     */
    public static String capture(String scenarioName) {
        WebDriver driver = DriverManager.getDriver();

        if (driver == null) {
            System.err.println("ScreenshotUtil: driver is null, cannot capture screenshot");
            return "";
        }

        try {
            // Step 1 — Create the screenshots directory if it does not exist
            Path screenshotsPath = Paths.get(SCREENSHOTS_DIR);
            Files.createDirectories(screenshotsPath);

            // Step 2 — Build a clean filename from the scenario name + timestamp
            // Replace spaces and special characters so the filename is safe
            String cleanName = scenarioName
                    .replaceAll("[^a-zA-Z0-9]", "_")
                    .toLowerCase();
            String timestamp = LocalDateTime.now().format(FORMATTER);
            String fileName = cleanName + "_" + timestamp + ".png";
            String fullPath = SCREENSHOTS_DIR + File.separator + fileName;

            // Step 3 — Tell Selenium to take the screenshot as a byte array
            byte[] screenshotBytes = ((TakesScreenshot) driver)
                    .getScreenshotAs(OutputType.BYTES);

            // Step 4 — Write the bytes to a file
            Files.write(Paths.get(fullPath), screenshotBytes);

            System.out.println("Screenshot saved: " + fullPath);
            return fullPath;

        } catch (IOException e) {
            System.err.println("ScreenshotUtil: failed to save screenshot — " + e.getMessage());
            return "";
        }
    }
}
