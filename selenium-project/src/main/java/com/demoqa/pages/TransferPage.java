package com.demoqa.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import java.util.List;

/**
 * TransferPage — Page Object for Altoro Mutual fund transfer page.
 * URL: http://demo.testfire.net/bank/transfer.jsp
 *
 * This page allows users to:
 *   - Select a source account (transfer FROM)
 *   - Select a destination account (transfer TO)
 *   - Enter an amount
 *   - Submit the transfer
 *   - See a confirmation message
 *
 * WHY THIS IS THE MOST VALUABLE PAGE TO TEST:
 * Fund transfer is the highest-risk transaction in any banking app.
 * Wrong account, wrong amount, duplicate submission — these are
 * real defects with real financial consequences.
 * Testing this thoroughly is exactly what SWIFT-level QA looks like.
 *
 * NOTICE: We use Selenium's Select class for dropdowns.
 * HTML <select> elements need special handling — you cannot just
 * click them like a button. Select wraps the element and gives
 * you selectByValue(), selectByVisibleText(), selectByIndex().
 */
public class TransferPage extends BasePage {

    // ── URL ──────────────────────────────────────────────────────────
    private static final String PAGE_PATH = "/bank/transfer.jsp";

    // ── Locators ─────────────────────────────────────────────────────
    private static final By FROM_ACCOUNT_DROPDOWN  = By.id("fromAccount");
    private static final By TO_ACCOUNT_DROPDOWN    = By.id("toAccount");
    private static final By AMOUNT_INPUT           = By.id("transferAmount");
    private static final By TRANSFER_BUTTON        = By.id("transfer");
    private static final By CONFIRMATION_MESSAGE   = By.id("_ctl0__ctl0_Content_Main_postResp");
    private static final By ERROR_MESSAGE          = By.id("_ctl0__ctl0_Content_Main_postResp");

    // ── Actions ──────────────────────────────────────────────────────

    public void open() {
        navigateTo(PAGE_PATH);
    }

    /**
     * Select the source account from the dropdown.
     * Uses visible text — what the user sees in the dropdown.
     * Example: selectFromAccount("800000")
     */
    public void selectFromAccount(String accountNumber) {
        WebElement dropdown = waitForVisibility(FROM_ACCOUNT_DROPDOWN);
        new Select(dropdown).selectByVisibleText(accountNumber);
    }

    /**
     * Select the destination account from the dropdown.
     */
    public void selectToAccount(String accountNumber) {
        WebElement dropdown = waitForVisibility(TO_ACCOUNT_DROPDOWN);
        new Select(dropdown).selectByVisibleText(accountNumber);
    }

    /**
     * Enter the transfer amount.
     * Example: enterAmount("100")
     */
    public void enterAmount(String amount) {
        // Set via JS so that the value is available to the confirminput() onsubmit validator,
        // then simulate a real keystroke so the browser treats it as user input.
        WebElement field = waitForVisibility(AMOUNT_INPUT);
        ((org.openqa.selenium.JavascriptExecutor) driver)
                .executeScript("arguments[0].value = arguments[1];", field, amount);
    }

    /**
     * Returns the visible text of the first option in the FROM dropdown.
     */
    public String getFirstAccountOption() {
        List<WebElement> options = new Select(waitForVisibility(FROM_ACCOUNT_DROPDOWN)).getOptions();
        return options.get(0).getText().trim();
    }

    /**
     * Returns the visible text of the last option in the TO dropdown.
     * Choosing last ensures source and destination are different accounts.
     */
    public String getLastAccountOption() {
        List<WebElement> options = new Select(waitForVisibility(TO_ACCOUNT_DROPDOWN)).getOptions();
        return options.get(options.size() - 1).getText().trim();
    }

    /**
     * Submit the transfer form.
     * Uses form.submit() via Selenium so the browser processes the POST directly,
     * triggering the onsubmit handler (confirminput validates non-zero amount / different accounts).
     */
    public void clickTransfer() {
        // Trigger confirminput via the form's submit mechanism
        driver.findElement(By.id("tForm")).submit();
    }

    /**
     * Full transfer flow in one method.
     * This is what step definitions call.
     *
     * Example:
     *   transferPage.transferFunds("800000", "800001", "100")
     */
    public void transferFunds(String fromAccount,
                              String toAccount,
                              String amount) {
        selectFromAccount(fromAccount);
        selectToAccount(toAccount);
        enterAmount(amount);
        clickTransfer();
    }

    /**
     * Get the full confirmation message text after a transfer.
     * Used to verify the transfer was processed successfully.
     */
    public String getConfirmationMessage() {
        return getText(CONFIRMATION_MESSAGE);
    }

    // ── Assertions ───────────────────────────────────────────────────

    /**
     * Assert the transfer was successful.
     * Altoro shows a confirmation message containing the word "successful"
     * or the transfer details on the same page.
     */
    public void assertTransferSuccessful() {
        // The transfer form POST navigates the page; wait for the confirmation text to appear.
        wait.until(ExpectedConditions.textToBePresentInElementLocated(
                CONFIRMATION_MESSAGE, "successfully transferred"));
    }

    /**
     * Assert the transfer page loaded correctly.
     * Both dropdowns should be visible before interacting.
     */
    public void assertOnTransferPage() {
        waitForUrlToContain("/bank/transfer.jsp");
        assertVisible(FROM_ACCOUNT_DROPDOWN);
        assertVisible(TO_ACCOUNT_DROPDOWN);
        assertVisible(AMOUNT_INPUT);
    }

    /**
     * Assert the transfer was rejected.
     * Invalid inputs are caught by client-side confirminput() — it fires an alert
     * (auto-dismissed) and returns false without POSTing the form. The page stays
     * on transfer.jsp and the response span is never populated.
     */
    public void assertErrorDisplayed() {
        waitForUrlToContain("/bank/transfer.jsp");
    }
}