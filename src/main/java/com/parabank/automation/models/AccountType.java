package com.parabank.automation.models;

public enum AccountType {
    CHECKING("0", "CHECKING"),
    SAVINGS("1", "SAVINGS");

    private final String value;
    private final String displayName;

    AccountType(String value, String displayName) {
        this.value = value;
        this.displayName = displayName;
    }

    public String getValue() {
        return value;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static AccountType fromString(String text) {
        for (AccountType type : AccountType.values()) {
            if (type.name().equalsIgnoreCase(text) || type.displayName.equalsIgnoreCase(text)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown AccountType: " + text);
    }
}
