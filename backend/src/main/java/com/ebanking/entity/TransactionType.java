package com.ebanking.entity;

/**
 * Enum representing different types of transactions
 */
public enum TransactionType {
    TRANSFER("Transfer"),
    DEPOSIT("Deposit"),
    WITHDRAWAL("Withdrawal"),
    PAYMENT("Payment"),
    REFUND("Refund"),
    FEE("Fee"),
    INTEREST("Interest"),
    MOBILE_RECHARGE("Mobile Recharge"),
    CRYPTO_BUY("Crypto Buy"),
    CRYPTO_SELL("Crypto Sell"),
    CRYPTO_TRANSFER("Crypto Transfer");

    private final String displayName;

    TransactionType(String displayName) {
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
