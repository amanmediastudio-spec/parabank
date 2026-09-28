package com.parabank.automation.models;

/**
 * Domain model representing a Loan Request.
 */
public record LoanRequest(
        double amount,
        double downPayment,
        String fromAccountId
) {
}
