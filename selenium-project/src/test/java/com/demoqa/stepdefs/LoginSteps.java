package com.demoqa.stepdefs;

import com.demoqa.TestContext;
import com.demoqa.pages.LoginPage;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class LoginSteps {

    private final LoginPage loginPage;

    public LoginSteps(TestContext context) {
        this.loginPage = context.loginPage;
    }

    @Given("I am on the Altoro Mutual login page")
    public void iAmOnTheAltoroMutualLoginPage() {
        loginPage.open();
    }

    @When("I enter username {string} and password {string}")
    public void iEnterUsernameAndPassword(String username, String password) {
        loginPage.enterUsername(username);
        loginPage.enterPassword(password);
    }

    @And("I click the login button")
    public void iClickTheLoginButton() {
        loginPage.clickLogin();
    }

    @Then("I should be redirected to the account summary page")
    public void iShouldBeRedirectedToTheAccountSummaryPage() {
        loginPage.assertLoginSuccessful();
    }

    @And("I should see a welcome message containing {string}")
    public void iShouldSeeAWelcomeMessageContaining(String expectedText) {
        loginPage.assertWelcomeMessageContains(expectedText);
    }

    @Then("I should see a login error message")
    public void iShouldSeeALoginErrorMessage() {
        loginPage.assertLoginFailed();
    }

    @And("I should remain on the login page")
    public void iShouldRemainOnTheLoginPage() {
        loginPage.assertOnLoginPage();
    }

    @When("I click the sign off link")
    public void iClickTheSignOffLink() {
        loginPage.logout();
    }

    @Then("I should be returned to the login page")
    public void iShouldBeReturnedToTheLoginPage() {
        loginPage.assertLoggedOut();
    }

    @When("I navigate directly to a protected page without logging in")
    public void iNavigateDirectlyToAProtectedPage() {
        loginPage.navigateToProtectedPage();
    }

    @Then("I should be redirected back to the login page")
    public void iShouldBeRedirectedBackToTheLoginPage() {
        loginPage.assertOnLoginPage();
    }
}
