package com.demoqa.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;

/**
 * LoginPage — Page Object for Altoro Mutual login page.
 * URL: http://demo.testfire.net/login.jsp
 *
 * WHAT THIS CLASS DOES:
 * Represents everything on the login page.
 * - Locators: how to FIND each element on the page
 * - Actions: what a USER can DO on this page
 * - Assertions: what we VERIFY after actions
 *
 * WHAT THIS CLASS DOES NOT DO:
 * It never touches Cucumber, TestNG, or test logic.
 * It just models the page. Tests call its methods.
 * That separation is the entire point of Page Object Model.
 */
public class LoginPage extends BasePage {

    // ─────────────────────────────────────────────────────────────────
    // URL
    // ─────────────────────────────────────────────────────────────────
    private static final String PAGE_PATH = "/login.jsp";

    // ─────────────────────────────────────────────────────────────────
    // LOCATORS
    // All selectors defined here as constants — never inline in methods.
    //
    // WHY By.name and By.id?
    // Altoro Mutual uses name attributes on its form fields.
    // By.id is fastest and most reliable when available.
    // By.name is the next best option.
    // We NEVER use XPath here unless absolutely necessary —
    // XPath breaks when the HTML structure changes.
    // ─────────────────────────────────────────────────────────────────
    private static final By USERNAME_INPUT    = By.name("uid");
    private static final By PASSWORD_INPUT    = By.name("passw");
    private static final By LOGIN_BUTTON      = By.name("btnSubmit");
    private static final By ERROR_MESSAGE     = By.id("_ctl0__ctl0_Content_Main_message");
    private static final By LOGOUT_LINK       = By.linkText("Sign Off");
    private static final By WELCOME_MESSAGE   = By.cssSelector("h1");
    private static final By MY_ACCOUNT_LINK   = By.linkText("MY ACCOUNT");

    // ─────────────────────────────────────────────────────────────────
    // ACTIONS
    // ─────────────────────────────────────────────────────────────────

    /**
     * Navigate directly to the login page.
     * Called at the start of every login-related test.
     */
    public void open() {
        navigateTo(PAGE_PATH);
    }

    /**
     * Type into the username field.
     */
    public void enterUsername(String username) {
        type(USERNAME_INPUT, username);
    }

    /**
     * Type into the password field.
     */
    public void enterPassword(String password) {
        type(PASSWORD_INPUT, password);
    }

    /**
     * Click the Login button.
     */
    public void clickLogin() {
        click(LOGIN_BUTTON);
    }

    /**
     * Full login flow in one method.
     * This is what most step definitions will call.
     * Example: loginPage.login("jsmith", "Demo1234")
     */
    public void login(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLogin();
        dismissPopupIfPresent();
    }

    // Altoro Mutual shows a credit-offer popup after login; dismiss it if it's a JS alert.
    private void dismissPopupIfPresent() {
        try {
            driver.switchTo().alert().accept();
        } catch (NoAlertPresentException e) {
            // No JS alert — inline popup handled by page content, nothing to dismiss
        }
    }

    /**
     * Click Sign Off to log out.
     * Only visible when the user is already logged in.
     */
    public void logout() {
        click(LOGOUT_LINK);
    }

    /**
     * Navigate directly to a page that requires authentication.
     * Used to verify unauthenticated requests are redirected to login.
     */
    public void navigateToProtectedPage() {
        navigateTo("/bank/main.jsp");
    }

    // ─────────────────────────────────────────────────────────────────
    // ASSERTIONS
    // These methods verify outcomes — called from step definitions.
    // They use TestNG's Assert class for clear failure messages.
    // ─────────────────────────────────────────────────────────────────

    /**
     * Assert login was successful.
     * After login, the URL changes to /bank/main.jsp
     * and "My Account" link appears in the navigation.
     */
    public void assertLoginSuccessful() {
        waitForUrlToContain("/bank/main.jsp");
        assertVisible(MY_ACCOUNT_LINK);
    }

    public void assertOnLoginPage() {
        waitForUrlToContain("/login.jsp");
        assertVisible(LOGIN_BUTTON);
    }

    public void assertWelcomeMessageContains(String expectedText) {
        assertTextContains(WELCOME_MESSAGE, expectedText);
    }

    /**
     * Assert login was rejected (browser stays on login page).
     * Some invalid inputs (blank username/password) don't populate the error span,
     * so we verify URL only — not the specific error message text.
     */
    public void assertLoginFailed() {
        waitForUrlToContain("/login.jsp");
    }

    /**
     * Assert user is logged out.
     * After Sign Off, Altoro Mutual may redirect to index.jsp or login.jsp — the exact
     * URL varies. We assert that the Sign Off link is gone instead of matching a URL.
     * Cookies are cleared so any stale session token is removed before a re-login attempt.
     */
    public void assertLoggedOut() {
        wait.until(d -> !isDisplayed(LOGOUT_LINK));
        driver.manage().deleteAllCookies();
    }

    /**
     * Assert that a protected page was served without requiring authentication.
     * Documents Altoro Mutual's missing auth enforcement vulnerability.
     */
    public void assertUnauthenticatedPageServed() {
        assertUrlContains("/bank/main.jsp");
    }

    /**
     * Returns true if the user is currently logged in.
     * Useful for conditional logic in hooks (e.g. clean up after test).
     */
    public boolean isLoggedIn() {
        return isDisplayed(LOGOUT_LINK);
    }
}