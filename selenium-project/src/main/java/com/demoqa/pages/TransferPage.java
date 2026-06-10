package com.demoqa.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
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
    private static final By TRANSFER_BUTTON        = By.cssSelector("input[value='Transfer Money']");
    private static final By CONFIRMATION_MESSAGE   = By.id("transferForm");
    private static final By ERROR_MESSAGE          = By.cssSelector(".error");

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
        type(AMOUNT_INPUT, amount);
    }

    /**
     * Returns the visible text of the first option in the FROM dropdown.
     * Used by step definitions that select accounts by position rather than value.
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
     * Click the Transfer Money button.
     */
    public void clickTransfer() {
        click(TRANSFER_BUTTON);
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
        assertTextContains(CONFIRMATION_MESSAGE, "successful");
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
     * Assert an error message is shown.
     * Example: transferring more than the available balance.
     */
    public void assertErrorDisplayed() {
        assertVisible(ERROR_MESSAGE);
    }
}