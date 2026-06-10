Feature: User Authentication
  As a registered Altoro Mutual customer
  I want to securely log in and out of online banking
  So that I can access my accounts and perform transactions

  Background:
    Given I am on the Altoro Mutual login page

  # ── Smoke ──────────────────────────────────────────────────────────

  @smoke @login @wip
  Scenario: Successful login with valid credentials
    When I enter username "jsmith" and password "Demo1234"
    And I click the login button
    Then I should be redirected to the account summary page
    And I should see a welcome message containing "Smith"

  @smoke @login
  Scenario: Login fails with invalid password
    When I enter username "jsmith" and password "wrongpassword"
    And I click the login button
    Then I should see a login error message
    And I should remain on the login page

  # ── Invalid credentials ────────────────────────────────────────────

  @regression @login
  Scenario Outline: Login is rejected for unrecognised credential combinations
    When I enter username "<username>" and password "<password>"
    And I click the login button
    Then I should see a login error message
    And I should remain on the login page

    Examples: unrecognised accounts
      | username    | password |
      | unknownuser | Demo1234 |
      | jsmith      |          |
      |             | Demo1234 |
      |             |          |

  @regression @login
  Scenario: Login fails with whitespace-only credentials
    When I enter username " " and password " "
    And I click the login button
    Then I should see a login error message
    And I should remain on the login page

  # ── Security ───────────────────────────────────────────────────────

  @regression @security @login
  Scenario Outline: Login form rejects injection payloads
    When I enter username "<payload>" and password "Demo1234"
    And I click the login button
    Then I should see a login error message
    And I should remain on the login page

    Examples: attack vectors
      | payload                        |
      | ' OR '1'='1                    |
      | <script>alert(1)</script>      |
      | admin'--                       |
      | " OR ""="                      |

  # ── Session management ─────────────────────────────────────────────

  @regression @login
  Scenario: User can log out successfully
    When I enter username "jsmith" and password "Demo1234"
    And I click the login button
    Then I should be redirected to the account summary page
    When I click the sign off link
    Then I should be returned to the login page

  @regression @login
  Scenario: Re-login after logout succeeds
    When I enter username "jsmith" and password "Demo1234"
    And I click the login button
    Then I should be redirected to the account summary page
    When I click the sign off link
    Then I should be returned to the login page
    When I enter username "jsmith" and password "Demo1234"
    And I click the login button
    Then I should be redirected to the account summary page

  @regression @login
  Scenario: Accessing a protected page without logging in redirects to login
    When I navigate directly to a protected page without logging in
    Then I should be redirected back to the login page
