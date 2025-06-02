package com.ebanking.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * DTO for cryptocurrency buy from main account response
 */
public class CryptoBuyFromMainResponse {

    private String transactionId;
    private String cryptoType;
    private BigDecimal madAmount;
    private BigDecimal usdAmount;
    private BigDecimal cryptoAmount;
    private BigDecimal exchangeRate; // Crypto price in USD
    private BigDecimal madToUsdRate; // MAD to USD conversion rate
    private BigDecimal platformFee;
    private String status;
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime transactionDate;

    // Updated balances
    private BigDecimal newMainAccountBalance;
    private Map<String, BigDecimal> updatedCryptoBalances;
    private String walletAddress;

    // Rate information
    private BigDecimal currentRate;
    private String rateSource;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime rateTimestamp;

    // Constructors
    public CryptoBuyFromMainResponse() {}

    public CryptoBuyFromMainResponse(String transactionId, String cryptoType, BigDecimal madAmount, 
                                   BigDecimal cryptoAmount, BigDecimal exchangeRate, String status) {
        this.transactionId = transactionId;
        this.cryptoType = cryptoType;
        this.madAmount = madAmount;
        this.cryptoAmount = cryptoAmount;
        this.exchangeRate = exchangeRate;
        this.status = status;
        this.transactionDate = LocalDateTime.now();
    }

    // Getters and Setters
    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getCryptoType() {
        return cryptoType;
    }

    public void setCryptoType(String cryptoType) {
        this.cryptoType = cryptoType;
    }

    public BigDecimal getMadAmount() {
        return madAmount;
    }

    public void setMadAmount(BigDecimal madAmount) {
        this.madAmount = madAmount;
    }

    public BigDecimal getUsdAmount() {
        return usdAmount;
    }

    public void setUsdAmount(BigDecimal usdAmount) {
        this.usdAmount = usdAmount;
    }

    public BigDecimal getCryptoAmount() {
        return cryptoAmount;
    }

    public void setCryptoAmount(BigDecimal cryptoAmount) {
        this.cryptoAmount = cryptoAmount;
    }

    public BigDecimal getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(BigDecimal exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public BigDecimal getMadToUsdRate() {
        return madToUsdRate;
    }

    public void setMadToUsdRate(BigDecimal madToUsdRate) {
        this.madToUsdRate = madToUsdRate;
    }

    public BigDecimal getPlatformFee() {
        return platformFee;
    }

    public void setPlatformFee(BigDecimal platformFee) {
        this.platformFee = platformFee;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    public BigDecimal getNewMainAccountBalance() {
        return newMainAccountBalance;
    }

    public void setNewMainAccountBalance(BigDecimal newMainAccountBalance) {
        this.newMainAccountBalance = newMainAccountBalance;
    }

    public Map<String, BigDecimal> getUpdatedCryptoBalances() {
        return updatedCryptoBalances;
    }

    public void setUpdatedCryptoBalances(Map<String, BigDecimal> updatedCryptoBalances) {
        this.updatedCryptoBalances = updatedCryptoBalances;
    }

    public String getWalletAddress() {
        return walletAddress;
    }

    public void setWalletAddress(String walletAddress) {
        this.walletAddress = walletAddress;
    }

    public BigDecimal getCurrentRate() {
        return currentRate;
    }

    public void setCurrentRate(BigDecimal currentRate) {
        this.currentRate = currentRate;
    }

    public String getRateSource() {
        return rateSource;
    }

    public void setRateSource(String rateSource) {
        this.rateSource = rateSource;
    }

    public LocalDateTime getRateTimestamp() {
        return rateTimestamp;
    }

    public void setRateTimestamp(LocalDateTime rateTimestamp) {
        this.rateTimestamp = rateTimestamp;
    }

    @Override
    public String toString() {
        return "CryptoBuyFromMainResponse{" +
                "transactionId='" + transactionId + '\'' +
                ", cryptoType='" + cryptoType + '\'' +
                ", madAmount=" + madAmount +
                ", usdAmount=" + usdAmount +
                ", cryptoAmount=" + cryptoAmount +
                ", exchangeRate=" + exchangeRate +
                ", madToUsdRate=" + madToUsdRate +
                ", status='" + status + '\'' +
                ", newMainAccountBalance=" + newMainAccountBalance +
                ", walletAddress='" + walletAddress + '\'' +
                '}';
    }
}
