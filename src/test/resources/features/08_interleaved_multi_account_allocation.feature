@ComplexFlow @MultiAccount @Regression @RequiresAuth
Feature: Interleaved Multi-Account Fund Allocation
  As a Parabank customer
  I want to allocate funds sequentially across multiple subsidiary accounts
  So that ledger integrity and conservation of total funds are preserved

  Scenario: Sequentially fund multiple accounts and audit total conservation of funds
    Given the user logs into Parabank with credentials "HellBound" and "HellBound"
    And the user opens a secondary "CHECKING" account funded from primary account
    And the user opens a third "SAVINGS" account funded from primary account
    And the user records balances of primary, secondary, and tertiary accounts
    When the user transfers "25.00" from primary account to secondary account
    And the user transfers "10.00" from secondary account to tertiary account
    When the user navigates to Accounts Overview
    Then the primary account net balance should decrease by "25.00"
    And the secondary account net balance should increase by "15.00"
    And the tertiary account net balance should increase by "10.00"
    And the overall total portfolio balance should remain preserved across all internal transfers
