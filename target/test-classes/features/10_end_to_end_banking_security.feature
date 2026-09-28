@EndToEnd @Security @SessionControl @Regression @RequiresAuth
Feature: End-to-End Banking Workflow and Session Security
  As a security-conscious Parabank customer
  I want to execute a composite banking lifecycle and ensure session termination closes all access
  So that deep links to secure pages cannot be accessed after logout

  Scenario: Execute composite banking workflow and verify post-logout session isolation
    Given the user logs into Parabank with credentials "HellBound" and "HellBound"
    When the user navigates to Open New Account
    And the user opens a new "SAVINGS" account with funds transferred from the primary account
    Then a new account number is generated and captured
    When the user transfers "60.00" from the primary account to the newly created account
    Then a transfer confirmation message should be displayed with amount "60.00"
    When the user navigates to Accounts Overview
    Then the newly created account balance should equal "160.00"
    When the user logs out of Parabank
    And attempts direct deep-link access to secure URL "overview.htm"
    Then the user should see the login form indicating session termination
