package com.demoqa.stepdefs;

import com.demoqa.TestContext;
import com.demoqa.pages.AccountPage;
import com.demoqa.pages.LoginPage;
import com.demoqa.pages.TransferPage;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class TransferSteps {

    private final TestContext context;
    private final TransferPage transferPage;

    public TransferSteps(TestContext context) {
        this.context = context;
        this.transferPage = context.transferPage;
    }

    @And("I am on the transfer funds page")
    public void iAmOnTheTransferFundsPage() {
        transferPage.open();
    }

    @Then("I should see the from account dropdown")
    public void iShouldSeeTheFromAccountDropdown() {
        transferPage.assertOnTransferPage();
    }

    @And("I should see the to account dropdown")
    public void iShouldSeeTheToAccountDropdown() {
        transferPage.assertOnTransferPage();
    }

    @And("I should see the amount input field")
    public void iShouldSeeTheAmountInputField() {
        transferPage.assertOnTransferPage();
    }

    @When("I select the first account as the source")
    public void iSelectTheFirstAccountAsTheSource() {
        String account = transferPage.getFirstAccountOption();
        transferPage.selectFromAccount(account);
        context.selectedFromAccount = account;
    }

    @And("I select the second account as the destination")
    public void iSelectTheSecondAccountAsTheDestination() {
        String account = transferPage.getLastAccountOption();
        transferPage.selectToAccount(account);
        context.selectedToAccount = account;
    }

    @And("I enter a transfer amount of {string}")
    public void iEnterATransferAmountOf(String amount) {
        transferPage.enterAmount(amount);
        context.transferAmount = amount;
    }

    @And("I click the transfer button")
    public void iClickTheTransferButton() {
        transferPage.clickTransfer();
    }

    @Then("I should see a transfer confirmation message")
    public void iShouldSeeATransferConfirmationMessage() {
        transferPage.assertTransferSuccessful();
    }

    @Then("I should see a transfer error message")
    public void iShouldSeeATransferErrorMessage() {
        transferPage.assertErrorDisplayed();
    }
}
