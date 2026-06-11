Feature: User Authentication
  As a registered Altoro Mutual customer
  I want to securely log in and out of online banking
  So that I can access my accounts and perform transactions

  Background:
    Given I am on the Altoro Mutual login page

  # ── Smoke ──────────────────────────────────────────────────────────

  @smoke @login
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
  Scenario Outline: Login is rejected for unrecognised users
    When I enter username "<username>" and password "<password>"
    And I click the login button
    Then I should see a login error message
    And I should remain on the login page

    Examples: unrecognised accounts
      | username    | password    |
      | unknownuser | Demo1234    |
      | jsmith      | badpassword |

  # ── Security (run separately — behaviour on demo.testfire.net is intentionally vulnerable) ──

  @security @login
  Scenario: SQL injection bypasses authentication (known vulnerability)
    When I enter username "' OR '1'='1" and password "Demo1234"
    And I click the login button
    Then I should be redirected to the account summary page

  @security @login
  Scenario: Valid username with empty password logs in (auth bypass vulnerability)
    When I enter username "jsmith" and password ""
    And I click the login button
    Then I should be redirected to the account summary page

  @security @login
  Scenario Outline: Non-SQL injection payloads fail login
    When I enter username "<payload>" and password "Demo1234"
    And I click the login button
    Then I should remain on the login page

    Examples: attack vectors
      | payload                        |
      | <script>alert(1)</script>      |
      | admin'--                       |

  # ── Session management ─────────────────────────────────────────────

  @regression @login
  Scenario: User can log out successfully
    When I enter username "jsmith" and password "Demo1234"
    And I click the login button
    Then I should be redirected to the account summary page
    When I click the sign off link
    Then I should be returned to the login page

  @regression @login
  Scenario: Session is terminated after logout
    When I enter username "jsmith" and password "Demo1234"
    And I click the login button
    Then I should be redirected to the account summary page
    When I click the sign off link
    Then I should be returned to the login page
    When I navigate directly to a protected page without logging in
    Then I should be redirected back to the login page

  @regression @login
  Scenario: Accessing a protected page without logging in redirects to login
    When I navigate directly to a protected page without logging in
    Then I should be redirected back to the login page
