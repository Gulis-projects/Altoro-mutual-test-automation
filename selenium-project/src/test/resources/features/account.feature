# account.feature — BDD scenarios for account summary page

Feature: Account Summary
  As a logged in Altoro Mutual customer
  I want to view my account summary
  So that I can see my accounts and navigate to account details

  Background:
    Given I am logged in as "jsmith" with password "Demo1234"

  @smoke @account
  Scenario: Account summary page displays after login
    Then I should be on the account summary page
    And I should see at least one account listed

  @smoke @account
  Scenario: Welcome message shows correct username
    Then the welcome message should contain "John Smith"

  @regression @account
  Scenario: User can navigate to transfer funds page
    When I click on Transfer Funds
    Then I should be on the transfer funds page

  @regression @account
  Scenario: Account links are clickable
    Then I should see account links displayed on the page