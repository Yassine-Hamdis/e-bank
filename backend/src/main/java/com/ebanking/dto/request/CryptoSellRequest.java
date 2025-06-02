package com.ebanking.dto.request;

import javax.validation.constraints.*;
import java.math.BigDecimal;

/**
 * DTO for cryptocurrency sell requests
 */
public class CryptoSellRequest {

    @NotBlank(message = "Crypto type is required")
    @Pattern(regexp = "BTC|ETH|USDT|BNB", message = "Crypto type must be one of: BTC, ETH, USDT, BNB")
    private String cryptoType;

    @NotNull(message = "Exchange rate is required")
    @DecimalMin(value = "0.00000001", message = "Exchange rate must be greater than 0")
    @Digits(integer = 12, fraction = 8, message = "Exchange rate format is invalid")
    private BigDecimal exchangeRate;

    @NotBlank(message = "Wallet address is required")
    @Size(max = 100, message = "Wallet address must not exceed 100 characters")
    private String walletAddress;

    @NotNull(message = "Crypto amount is required")
    @DecimalMin(value = "0.00000001", message = "Crypto amount must be greater than 0")
    @Digits(integer = 12, fraction = 8, message = "Crypto amount format is invalid")
    private BigDecimal cryptoAmount;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    private String type = "CRYPTO_SELL";

    @DecimalMin(value = "0.00", message = "Network fee cannot be negative")
    @Digits(integer = 10, fraction = 8, message = "Network fee format is invalid")
    private BigDecimal networkFee = BigDecimal.ZERO;

    @DecimalMin(value = "0.00", message = "Platform fee cannot be negative")
    @Digits(integer = 10, fraction = 2, message = "Platform fee format is invalid")
    private BigDecimal platformFee = BigDecimal.ZERO;

    // Constructors
    public CryptoSellRequest() {}

    public CryptoSellRequest(String cryptoType, BigDecimal exchangeRate, String walletAddress, 
                            BigDecimal cryptoAmount, String description) {
        this.cryptoType = cryptoType;
        this.exchangeRate = exchangeRate;
        this.walletAddress = walletAddress;
        this.cryptoAmount = cryptoAmount;
        this.description = description;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    // Calculate fiat amount from crypto amount and exchange rate
    public BigDecimal calculateFiatAmount() {
        if (cryptoAmount != null && exchangeRate != null) {
            return cryptoAmount.multiply(exchangeRate);
        }
        return BigDecimal.ZERO;
    }

    @Override
    public String toString() {
        return "CryptoSellRequest{" +
                "cryptoType='" + cryptoType + '\'' +
                ", exchangeRate=" + exchangeRate +
                ", walletAddress='" + walletAddress + '\'' +
                ", cryptoAmount=" + cryptoAmount +
                ", description='" + description + '\'' +
                ", type='" + type + '\'' +
                ", networkFee=" + networkFee +
                ", platformFee=" + platformFee +
                '}';
    }
}
