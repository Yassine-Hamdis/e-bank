package com.ebanking.entity;

/**
 * Enum representing different statuses of crypto wallets
 */
public enum WalletStatus {
    ACTIVE("Active"),
    INACTIVE("Inactive"),
    SUSPENDED("Suspended"),
    BLOCKED("Blocked");

    private final String displayName;

    WalletStatus(String displayName) {
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
