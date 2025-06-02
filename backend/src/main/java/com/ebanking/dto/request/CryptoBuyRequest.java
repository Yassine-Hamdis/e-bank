package com.ebanking.dto.request;

import javax.validation.constraints.*;
import java.math.BigDecimal;

/**
 * DTO for cryptocurrency buy requests
 */
public class CryptoBuyRequest {

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

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "10.00", message = "Minimum purchase amount is 10.00 MAD")
    @DecimalMax(value = "50000.00", message = "Maximum purchase amount is 50,000.00 MAD")
    @Digits(integer = 8, fraction = 2, message = "Amount format is invalid")
    private BigDecimal amount;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    private String type = "CRYPTO_BUY";

    @DecimalMin(value = "0.00", message = "Network fee cannot be negative")
    @Digits(integer = 10, fraction = 8, message = "Network fee format is invalid")
    private BigDecimal networkFee = BigDecimal.ZERO;

    @DecimalMin(value = "0.00", message = "Platform fee cannot be negative")
    @Digits(integer = 10, fraction = 2, message = "Platform fee format is invalid")
    private BigDecimal platformFee = BigDecimal.ZERO;

    // Constructors
    public CryptoBuyRequest() {}

    public CryptoBuyRequest(String cryptoType, BigDecimal exchangeRate, String walletAddress, 
                           BigDecimal amount, String description) {
        this.cryptoType = cryptoType;
        this.exchangeRate = exchangeRate;
        this.walletAddress = walletAddress;
        this.amount = amount;
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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
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

    @Override
    public String toString() {
        return "CryptoBuyRequest{" +
                "cryptoType='" + cryptoType + '\'' +
                ", exchangeRate=" + exchangeRate +
                ", walletAddress='" + walletAddress + '\'' +
                ", amount=" + amount +
                ", description='" + description + '\'' +
                ", type='" + type + '\'' +
                ", networkFee=" + networkFee +
                ", platformFee=" + platformFee +
                '}';
    }
}
