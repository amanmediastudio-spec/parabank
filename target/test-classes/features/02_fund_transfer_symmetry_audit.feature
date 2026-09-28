@FundTransfer @Regression @RequiresAuth
Feature: Fund Transfer Symmetry and Audit
  As a banking customer
  I want to transfer funds between two accounts
  So that debit/credit symmetry is strictly maintained and transactions are audited

  Scenario: Execute internal funds transfer and audit debit-credit consistency
    Given the user logs into Parabank with credentials "HellBound" and "HellBound"
    And the user ensures at least two active accounts exist, capturing source and target account IDs
    And the user records initial balances for both source and target accounts
    When the user transfers "50.00" from the source account to the target account
    Then a transfer confirmation message should be displayed with amount "50.00"
    When the user navigates to Accounts Overview
    Then the source account balance should be decreased by exactly "50.00"
    And the target account balance should be increased by exactly "50.00"
    When the user views the transaction history of the source account
    Then a debit transaction of "50.00" should be recorded
    When the user views the transaction history of the target account
    Then a credit transaction of "50.00" should be recorded
