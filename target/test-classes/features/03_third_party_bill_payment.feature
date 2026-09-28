@BillPay @Regression @RequiresAuth
Feature: Third-Party Bill Payment Processing
  As a Parabank customer
  I want to pay a utility bill to a third party
  So that the funds are deducted and the ledger records the transaction accurately

  Scenario: Successfully process third-party bill payment and verify ledger deduction
    Given the user logs into Parabank with credentials "HellBound" and "HellBound"
    And the user records the available balance of the active checking account
    When the user pays a utility bill with the following details:
      | Payee Name       | Address            | City   | State | Zip Code | Phone Number | Account Number | Amount |
      | Apex Energy Grid | 742 Evergreen Terr | Metro  | NY    | 10001    | 555-019-2834 | 99887766       | 45.50  |
    Then the bill payment confirmation should display payee "Apex Energy Grid" and amount "45.50"
    When the user navigates to Accounts Overview
    Then the checking account balance should be reduced by "45.50"
    When the user navigates to the checking account transaction history
    Then a bill payment transaction to "Apex Energy Grid" for "45.50" should be present
