@AccountLifecycle @Regression @RequiresAuth
Feature: Account Lifecycle and Balance Integrity
  As an authenticated Parabank customer
  I want to open a new savings account and transfer initial funds
  So that I can verify accounting ledger integrity and transaction activity

  Scenario: Successfully open new savings account and verify ledger balance integrity
    Given the user logs into Parabank with credentials "HellBound" and "HellBound"
    And the user navigates to Accounts Overview and records the primary account ID and initial balance
    When the user navigates to Open New Account
    And the user opens a new "SAVINGS" account with funds transferred from the primary account
    Then a new account number is generated and captured
    When the user navigates to Accounts Overview
    Then the new savings account should appear in the portfolio with the initial deposit
    And the primary account balance should be debited by the initial deposit
    And the portfolio total balance should equal the mathematical sum of all accounts
    When the user opens the transaction details of the newly created savings account
    Then the account details should display account type "SAVINGS" and initial deposit transaction
