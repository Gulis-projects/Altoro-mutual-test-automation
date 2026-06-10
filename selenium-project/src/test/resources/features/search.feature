# search.feature — BDD scenarios for site search functionality

Feature: Site Search
  As an Altoro Mutual website visitor
  I want to search the website
  So that I can find relevant banking information quickly

  Background:
    Given I am on the Altoro Mutual search page


  Scenario: Search returns results for valid keyword
    When I search for "bank"
    Then I should see search results


  Scenario: Search with no matching term shows no results
    When I search for "xyznotexist12345"
    Then I should see no results message


  Scenario: Search for account related content
    When I search for "account"
    Then I should see search results