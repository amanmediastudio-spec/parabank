@TransactionSearch @Regression @RequiresAuth
Feature: Multi-Criteria Transaction Search
  As a Parabank customer
  I want to search my past transactions across multiple criteria
  So that I can verify transaction records by ID, Date, and Amount

  Scenario: Find transactions across amount and transaction ID criteria
    Given the user logs into Parabank with credentials "HellBound" and "HellBound"
    And the user executes a unique transfer of "27.50" to generate an identifiable transaction
    When the user navigates to Find Transactions
    And searches for transactions with amount "27.50"
    Then the search results should contain the transaction with amount "27.50"
    When the user captures the transaction ID from the results
    And searches for the transaction by its exact ID
    Then the search results should contain the matching transaction ID
