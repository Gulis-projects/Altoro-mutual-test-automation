package com.demoqa.stepdefs;

import com.demoqa.TestContext;
import com.demoqa.pages.AccountPage;
import com.demoqa.pages.LoginPage;
import com.demoqa.pages.TransferPage;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class AccountSteps {

    private final LoginPage loginPage;
    private final AccountPage accountPage;
    private final TransferPage transferPage;

    public AccountSteps (TestContext context){
        this.accountPage= context.accountPage;
        this.loginPage = context.loginPage;
        this.transferPage = context.transferPage;
    }
    @Given("I am logged in as {string} with password {string}")
    public void iAmLoggedInAsWithPassword(String username, String password) {
        loginPage.open();
        loginPage.login(username,password);

    }

    @Then("I should be on the account summary page")
    public void iShouldBeOnTheAccountSummaryPage() {
        accountPage.assertOnAccountSummaryPage();
    }

    @And("I should see at least one account listed")
    public void iShouldSeeAtLeastOneAccountListed() {
        accountPage.assertAccountsAreDisplayed();
    }

    @Then("the welcome message should contain {string}")
    public void theWelcomeMessageShouldContain(String expectedText) {
        accountPage.assertWelcomeMessageContains(expectedText);
    }

    @When("I click on Transfer Funds")
    public void iClickOnTransferFunds() {
        accountPage.goToTransferFunds();
    }

    @Then("I should be on the transfer funds page")
    public void iShouldBeOnTheTransferFundsPage() {
        transferPage.assertOnTransferPage();

    }

    @Then("I should see account links displayed on the page")
    public void iShouldSeeAccountLinksDisplayedOnThePage() {
        accountPage.assertAccountsAreDisplayed();
    }
}
