@LoanApplication @LoanDenial @Regression @RequiresAuth
Feature: Loan Application Risk Denial
  As a risk assessment module in Parabank
  When an applicant requests an exorbitant loan with minimal collateral
  Then the loan should be denied and no portfolio accounts created

  Scenario: Reject excessive loan request and assert balance conservation
    Given the user logs into Parabank with credentials "HellBound" and "HellBound"
    And the user notes the primary account number and current balance
    When the user applies for an excessive loan of "1000000.00" with down payment "10.00"
    Then the loan request status should be "Denied"
    And the system should display an error message stating insufficient funds or down payment
    When the user navigates to Accounts Overview
    Then no new loan account should have been added to the customer portfolio
    And the primary account balance should remain completely unchanged
