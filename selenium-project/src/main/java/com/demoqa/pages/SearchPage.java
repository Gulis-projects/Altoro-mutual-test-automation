package com.demoqa.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import java.util.List;

/**
 * SearchPage — Page Object for Altoro Mutual site search.
 * URL: http://demo.testfire.net/search.jsp
 *
 * Tests the search functionality — a common feature in
 * enterprise financial apps for finding content, accounts,
 * or transactions quickly.
 */
public class SearchPage extends BasePage {

    // ── URL ──────────────────────────────────────────────────────────
    private static final String PAGE_PATH = "/search.jsp";

    // ── Locators ─────────────────────────────────────────────────────
    private static final By SEARCH_INPUT    = By.name("query");
    private static final By SEARCH_BUTTON   = By.cssSelector("input[value='Go']");
    private static final By SEARCH_RESULTS  = By.cssSelector(".search-results");
    private static final By RESULT_ITEMS    = By.cssSelector(".search-results p");
    private static final By NO_RESULTS_MSG  = By.cssSelector(".search-results");

    // ── Actions ──────────────────────────────────────────────────────

    public void open() {
        navigateTo(PAGE_PATH);
    }

    public void enterSearchTerm(String term) {
        type(SEARCH_INPUT, term);
    }

    public void clickSearch() {
        click(SEARCH_BUTTON);
    }

    /**
     * Full search flow in one method.
     */
    public void searchFor(String term) {
        enterSearchTerm(term);
        clickSearch();
    }

    /**
     * Get all search result texts as a list.
     */
    public List<String> getSearchResults() {
        List<WebElement> results = driver.findElements(RESULT_ITEMS);
        return results.stream()
                .map(WebElement::getText)
                .filter(text -> !text.isBlank())
                .toList();
    }

    public int getResultCount() {
        return getSearchResults().size();
    }

    // ── Assertions ───────────────────────────────────────────────────

    public void assertResultsContain(String keyword) {
        List<String> results = getSearchResults();
        boolean found = results.stream()
                .anyMatch(r -> r.toLowerCase().contains(keyword.toLowerCase()));
        Assert.assertTrue(found,
                "Expected search results to contain '" + keyword + "' but got: " + results);
    }

    public void assertNoResultsFound() {
        assertTextContains(NO_RESULTS_MSG, "No results");
    }

    public void assertOnSearchPage() {
        waitForUrlToContain("/search.jsp");
        assertVisible(SEARCH_INPUT);
    }
}