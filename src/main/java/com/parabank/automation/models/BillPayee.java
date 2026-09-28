package com.parabank.automation.models;

/**
 * Domain model representing a third-party payee in Bill Pay workflow.
 */
public record BillPayee(
        String name,
        String address,
        String city,
        String state,
        String zipCode,
        String phoneNumber,
        String accountNumber,
        String verifyAccount,
        double amount
) {
    public static BillPayee sampleElectricUtility(double amount, String accountNumber) {
        return new BillPayee(
                "Metro Energy Corp",
                "450 Power Grid Way",
                "Metro",
                "NY",
                "10002",
                "2125559876",
                accountNumber,
                accountNumber,
                amount
        );
    }
}
