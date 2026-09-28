@LoanApplication @LoanApproval @Regression @RequiresAuth
Feature: Loan Application Approval
  As an eligible Parabank customer
  I want to apply for a small personal loan with appropriate down payment
  So that my loan is approved and a new loan account is generated

  Scenario: Successfully apply for a loan and verify loan account creation
    Given the user logs into Parabank with credentials "HellBound" and "HellBound"
    And the user identifies an eligible account with sufficient funds for down payment
    When the user applies for a loan with amount "1000.00" and down payment "100.00"
    Then the loan request should be processed with status "Approved"
    And a new loan account number should be generated
    When the user navigates to Accounts Overview
    Then the new loan account should be present in the accounts table
