package com.ebanking.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * CryptoTransaction entity extending Transaction
 * Represents cryptocurrency buy/sell transactions
 */
@Entity
@DiscriminatorValue("CRYPTO")
public class CryptoTransaction extends Transaction implements Serializable {

    @Column(name = "crypto_type", nullable = true, length = 10)
    private String cryptoType; // BTC, ETH, USDT, etc.

    @Column(name = "exchange_rate", precision = 20, scale = 8, nullable = true)
    private BigDecimal exchangeRate; // Exchange rate at time of transaction

    @Column(name = "wallet_address", nullable = true, length = 100)
    private String walletAddress;

    @Column(name = "crypto_amount", precision = 20, scale = 8)
    private BigDecimal cryptoAmount; // Amount of crypto bought/sold

    @Column(name = "network_fee", precision = 15, scale = 8)
    private BigDecimal networkFee = BigDecimal.ZERO; // Blockchain network fee

    @Column(name = "platform_fee", precision = 15, scale = 2)
    private BigDecimal platformFee = BigDecimal.ZERO; // Platform transaction fee

    // Constructors
    public CryptoTransaction() {
        super();
    }

    public CryptoTransaction(String transactionId, BigDecimal amount, TransactionType type,
                           String description, String cryptoType, BigDecimal exchangeRate,
                           String walletAddress) {
        super(transactionId, amount, type, description);
        this.cryptoType = cryptoType;
        this.exchangeRate = exchangeRate;
        this.walletAddress = walletAddress;

        // Calculate crypto amount based on fiat amount and exchange rate
        if (amount != null && exchangeRate != null && exchangeRate.compareTo(BigDecimal.ZERO) > 0) {
            this.cryptoAmount = amount.divide(exchangeRate, 8, BigDecimal.ROUND_HALF_UP);
        }
    }

    // Getters and Setters
    public String getCryptoType() {
        return cryptoType;
    }

    public void setCryptoType(String cryptoType) {
        this.cryptoType = cryptoType;
    }

    public BigDecimal getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(BigDecimal exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public String getWalletAddress() {
        return walletAddress;
    }

    public void setWalletAddress(String walletAddress) {
        this.walletAddress = walletAddress;
    }

    public BigDecimal getCryptoAmount() {
        return cryptoAmount;
    }

    public void setCryptoAmount(BigDecimal cryptoAmount) {
        this.cryptoAmount = cryptoAmount;
    }

    public BigDecimal getNetworkFee() {
        return networkFee;
    }

    public void setNetworkFee(BigDecimal networkFee) {
        this.networkFee = networkFee != null ? networkFee : BigDecimal.ZERO;
    }

    public BigDecimal getPlatformFee() {
        return platformFee;
    }

    public void setPlatformFee(BigDecimal platformFee) {
        this.platformFee = platformFee != null ? platformFee : BigDecimal.ZERO;
    }

    @Override
    public String toString() {
        return "CryptoTransaction{" +
                "cryptoType='" + cryptoType + '\'' +
                ", exchangeRate=" + exchangeRate +
                ", walletAddress='" + walletAddress + '\'' +
                ", cryptoAmount=" + cryptoAmount +
                ", networkFee=" + networkFee +
                ", platformFee=" + platformFee +
                ", transactionId='" + getTransactionId() + '\'' +
                ", amount=" + getAmount() +
                ", type=" + getType() +
                ", status=" + getStatus() +
                '}';
    }
}
