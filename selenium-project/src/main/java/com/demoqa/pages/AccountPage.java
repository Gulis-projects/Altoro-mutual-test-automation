package com.demoqa.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import java.util.List;

/**
 * AccountPage — Page Object for Altoro Mutual account summary page.
 * URL: http://demo.testfire.net/bank/main.jsp
 *
 * This page shows after successful login.
 * It displays:
 *   - Welcome message with username
 *   - List of accounts with balances
 *   - Navigation to individual account details
 *
 * WHY THIS IS REALISTIC FOR YOUR RESUME:
 * Account summary pages are the heart of every banking application.
 * At SWIFT you tested payment tracking systems — this is the same concept.
 * Accounts have balances, transactions flow through them, data must be
 * accurate. This is exactly what financial services QA is about.
 */
public class AccountPage extends BasePage {

    // ── URL ──────────────────────────────────────────────────────────
    private static final String PAGE_PATH = "/bank/main.jsp";

    // ── Locators ─────────────────────────────────────────────────────
    private static final By WELCOME_MESSAGE    = By.cssSelector("h1");
    private static final By ACCOUNT_TABLE      = By.cssSelector("#onlineForm table");
    private static final By ACCOUNT_LINKS      = By.cssSelector("#onlineForm table a");
    private static final By ACCOUNT_ROWS       = By.cssSelector("#onlineForm table tr");
    private static final By NAV_MY_ACCOUNT     = By.linkText("MY ACCOUNT");
    private static final By NAV_TRANSFER       = By.linkText("Transfer Funds");
    private static final By NAV_SIGN_OFF       = By.linkText("Sign Off");

    // ── Actions ──────────────────────────────────────────────────────

    public void open() {
        navigateTo(PAGE_PATH);
    }

    /**
     * Click on a specific account link by account number.
     * Example: clickAccount("800000") navigates to that account detail.
     */
    public void clickAccount(String accountNumber) {
        click(By.linkText(accountNumber));
    }

    /**
     * Navigate to Transfer Funds page via the left navigation.
     */
    public void goToTransferFunds() {
        click(NAV_TRANSFER);
    }

    /**
     * Get the welcome message text shown after login.
     * Example: "Hello John Smith, ..."
     */
    public String getWelcomeMessage() {
        return getText(WELCOME_MESSAGE);
    }

    /**
     * Get all account numbers displayed on the page.
     * Returns a list so tests can verify specific accounts exist.
     */
    public List<String> getAccountNumbers() {
        List<WebElement> links = driver.findElements(ACCOUNT_LINKS);
        return links.stream()
                .map(WebElement::getText)
                .filter(text -> !text.isBlank())
                .toList();
    }

    /**
     * Get the number of accounts displayed.
     * Useful for verifying a user has the expected number of accounts.
     */
    public int getAccountCount() {
        return getAccountNumbers().size();
    }

    // ── Assertions ───────────────────────────────────────────────────

    /**
     * Assert user landed on the account summary page after login.
     */
    public void assertOnAccountSummaryPage() {
        waitForUrlToContain("/bank/main.jsp");
        assertVisible(ACCOUNT_TABLE);
    }

    /**
     * Assert the welcome message contains the expected username.
     * Example: assertWelcomeMessageContains("jsmith")
     */
    public void assertWelcomeMessageContains(String username) {
        assertTextContains(WELCOME_MESSAGE, username);
    }

    /**
     * Assert that at least one account is displayed.
     * A logged-in user should always have at least one account.
     */
    public void assertAccountsAreDisplayed() {
        assertVisible(ACCOUNT_LINKS);
        Assert.assertTrue(getAccountCount() > 0,
                "Expected at least one account but found none");
    }

    public void assertAccountExists(String accountNumber) {
        List<String> accounts = getAccountNumbers();
        Assert.assertTrue(accounts.contains(accountNumber),
                "Expected account '" + accountNumber + "' but found: " + accounts);
    }

    /**
     * Assert the Transfer Funds navigation link is visible.
     * Verifies the authenticated navigation menu is fully loaded.
     */
    public void assertTransferLinkVisible() {
        assertVisible(NAV_TRANSFER);
    }
}
