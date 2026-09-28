package com.parabank.automation.models;

/**
 * Immutable domain model representing a Parabank Customer Profile.
 */
public record CustomerProfile(
        String firstName,
        String lastName,
        String address,
        String city,
        String state,
        String zipCode,
        String phoneNumber,
        String ssn,
        String username,
        String password
) {
    public static CustomerProfile defaultProfile() {
        return new CustomerProfile(
                "Hell",
                "Bound",
                "100 Cyber Blvd",
                "Metro",
                "NY",
                "10001",
                "2125550199",
                "123-45-6789",
                "HellBound",
                "HellBound"
        );
    }
}
