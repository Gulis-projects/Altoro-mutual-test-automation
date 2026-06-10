package com.demoqa;

import com.demoqa.pages.LoginPage;
import com.demoqa.pages.AccountPage;
import com.demoqa.pages.SearchPage;
import com.demoqa.pages.TransferPage;

/**
 * TestContext — Shares page objects and data across step definition classes.
 *
 * WHY THIS EXISTS — this is critical to understand:
 *
 * In Cucumber, each step definition class is a SEPARATE Java class.
 * LoginSteps.java handles login steps.
 * TransferSteps.java handles transfer steps.
 *
 * But a scenario like this spans BOTH classes:
 *   Given I am logged in          ← LoginSteps
 *   When I transfer $100          ← TransferSteps
 *   Then my balance shows $900    ← AccountSteps
 *
 * How does TransferSteps know the user is logged in?
 * How does AccountSteps know what amount was transferred?
 * They need to SHARE state.
 *
 * TestContext is that shared state. Cucumber creates ONE instance
 * and injects it into every step definition class that needs it.
 * This is called DEPENDENCY INJECTION — a senior pattern.
 *
 * HOW TO USE:
 * Any step def class that needs shared state adds this constructor:
 *   public LoginSteps(TestContext context) {
 *       this.context = context;
 *   }
 */
public class TestContext {

    // ── Page Objects ─────────────────────────────────────────────────
    // Created once here, shared across all step definition classes.
    // Step defs never call new LoginPage() themselves.
    public LoginPage loginPage;
    public AccountPage accountPage;
    public TransferPage transferPage;
    public SearchPage searchPage;

    // ── Shared test data ─────────────────────────────────────────────
    // Store values that need to pass between steps.
    // Example: store the transfer amount in one step, verify it in another.
    public String transferAmount;
    public String accountNumber;
    public String scenarioName;
    public String selectedFromAccount;
    public String selectedToAccount;

    /**
     * Empty constructor — PicoContainer calls this before @Before, so the
     * driver isn't up yet. Page objects are created in init() instead.
     */
    public TestContext() {}

    /**
     * Called from Hooks.setUp() after DriverManager.initDriver() so that
     * BasePage constructors can safely call DriverManager.getDriver().
     */
    public void init() {
        loginPage    = new LoginPage();
        accountPage  = new AccountPage();
        transferPage = new TransferPage();
        searchPage   = new SearchPage();
    }
}
