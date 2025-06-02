package com.ebanking.entity;

/**
 * Enum representing different statuses of transactions
 */
public enum TransactionStatus {
    PENDING("Pending"),
    VERIFIED("Verified"),
    REJECTED("Rejected"),
    COMPLETED("Completed"),
    FAILED("Failed"),
    CANCELLED("Cancelled");

    private final String displayName;

    TransactionStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
