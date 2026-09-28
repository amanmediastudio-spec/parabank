package com.parabank.automation.context;

/**
 * Enumeration of shared scenario state keys used across Cucumber step definitions.
 */
public enum ContextKey {
    PRIMARY_ACCOUNT_ID,
    SECONDARY_ACCOUNT_ID,
    NEW_SAVINGS_ACCOUNT_ID,
    NEW_CHECKING_ACCOUNT_ID,
    LOAN_ACCOUNT_ID,
    TRANSFER_AMOUNT,
    SOURCE_INITIAL_BALANCE,
    TARGET_INITIAL_BALANCE,
    TOTAL_PORTFOLIO_BALANCE,
    TRANSACTION_ID,
    TRANSACTION_AMOUNT,
    TRANSACTION_DATE,
    BILL_PAYEE,
    BILL_AMOUNT,
    CUSTOMER_PROFILE,
    LAST_ERROR_MESSAGE,
    LOAN_STATUS
}
