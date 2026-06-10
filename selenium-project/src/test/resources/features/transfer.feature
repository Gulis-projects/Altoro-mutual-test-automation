Feature: Fund Transfer
  As a logged in Altoro Mutual customer
  I want to transfer funds between my accounts
  So that I can manage my money effectively

  Background:
    Given I am logged in as "jsmith" with password "Demo1234"
    And I am on the transfer funds page

  # ── Smoke ──────────────────────────────────────────────────────────

  @smoke @transfer
  Scenario: Transfer page loads with required fields
    Then I should see the from account dropdown
    And I should see the to account dropdown
    And I should see the amount input field

  @smoke @transfer
  Scenario: Successful fund transfer between accounts
    When I select the first account as the source
    And I select the second account as the destination
    And I enter a transfer amount of "100"
    And I click the transfer button
    Then I should see a transfer confirmation message

  # ── Validation ─────────────────────────────────────────────────────

  @regression @transfer
  Scenario Outline: Transfer is rejected for invalid amounts
    When I select the first account as the source
    And I select the second account as the destination
    And I enter a transfer amount of "<amount>"
    And I click the transfer button
    Then I should see a transfer error message

    Examples: invalid amounts
      | amount | reason          |
      | 0      | zero amount     |
      |        | empty amount    |
      | -100   | negative amount |
      | abc    | non-numeric     |
