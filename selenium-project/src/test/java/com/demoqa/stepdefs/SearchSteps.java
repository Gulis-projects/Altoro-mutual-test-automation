package com.demoqa.stepdefs;

import com.demoqa.TestContext;
import com.demoqa.pages.SearchPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;

public class SearchSteps {

    private final SearchPage searchPage;

    public SearchSteps(TestContext context) {
        this.searchPage = context.searchPage;
    }

    @Given("I am on the Altoro Mutual search page")
    public void iAmOnTheAltoroMutualSearchPage() {
        searchPage.open();
        searchPage.assertOnSearchPage();
    }

    @When("I search for {string}")
    public void iSearchFor(String term) {
        searchPage.searchFor(term);
    }

    @Then("I should see search results")
    public void iShouldSeeSearchResults() {
        Assert.assertTrue(searchPage.getResultCount() > 0,
                "Expected search results but found none");
    }

    @Then("I should see no results message")
    public void iShouldSeeNoResultsMessage() {
        searchPage.assertNoResultsFound();
    }
}
