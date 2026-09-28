@BillPay @Validation @Boundary @Regression @RequiresAuth
Feature: Bill Pay Validation and Boundary Testing
  As a banking platform
  I want to reject malformed bill payments with field-level validations
  So that funds are not improperly transferred

  Scenario: Validate field error handling on account mismatch and missing inputs
    Given the user logs into Parabank with credentials "HellBound" and "HellBound"
    When the user navigates to Bill Pay
    And attempts to submit bill payment with empty required fields
    Then field validation error messages should be displayed for required inputs
    When the user enters payee details with mismatched account numbers:
      | Payee Name  | Address      | City  | State | Zip Code | Phone Number | Account Number | Verify Account | Amount |
      | Water Works | 100 River Rd | Metro | NY    | 10001    | 555-111-2222 | 12345          | 54321          | 75.00  |
    And clicks Send Payment
    Then a validation error should state that account numbers do not match
