@ProfileManagement @Security @Regression @RequiresAuth
Feature: Customer Profile Persistence Audit
  As a Parabank customer
  I want to update my contact information
  So that the changes are safely persisted across browser restarts and re-authentication

  Scenario: Update contact info and verify persistence after relogin
    Given the user logs into Parabank with credentials "HellBound" and "HellBound"
    When the user navigates to Update Contact Info
    And updates the profile with new contact information:
      | Street          | City       | State | Zip Code | Phone Number |
      | 777 Quantum Way | Cyberville | WA    | 98052    | 425-555-0144 |
    Then the profile update confirmation message should be displayed
    When the user logs out of Parabank
    And logs back in with credentials "HellBound" and "HellBound"
    And the user navigates to Update Contact Info
    Then the persisted street address should be "777 Quantum Way"
    And the persisted city should be "Cyberville"
    And the persisted state should be "WA"
    And the persisted zip code should be "98052"
    And the persisted phone number should be "425-555-0144"
