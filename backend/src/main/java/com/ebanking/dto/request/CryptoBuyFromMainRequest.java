package com.ebanking.dto.request;

import javax.validation.constraints.*;
import java.math.BigDecimal;

/**
 * DTO for cryptocurrency buy from main account requests
 */
public class CryptoBuyFromMainRequest {

    @NotBlank(message = "Crypto type is required")
    @Pattern(regexp = "BTC|ETH|USDT|BNB", message = "Crypto type must be one of: BTC, ETH, USDT, BNB")
    private String cryptoType;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "10.00", message = "Minimum purchase amount is 10.00 MAD")
    @DecimalMax(value = "50000.00", message = "Maximum purchase amount is 50,000.00 MAD")
    @Digits(integer = 8, fraction = 2, message = "Amount format is invalid")
    private BigDecimal amount;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @DecimalMin(value = "0.00", message = "Platform fee cannot be negative")
    @Digits(integer = 10, fraction = 2, message = "Platform fee format is invalid")
    private BigDecimal platformFee = BigDecimal.ZERO;

    private boolean useRealTimeRate = true; // Flag to use real-time rates from Binance

    // Constructors
    public CryptoBuyFromMainRequest() {}

    public CryptoBuyFromMainRequest(String cryptoType, BigDecimal amount, String description) {
        this.cryptoType = cryptoType;
        this.amount = amount;
        this.description = description;
    }

    public CryptoBuyFromMainRequest(String cryptoType, BigDecimal amount, String description, 
                                   BigDecimal platformFee, boolean useRealTimeRate) {
        this.cryptoType = cryptoType;
        this.amount = amount;
        this.description = description;
        this.platformFee = platformFee;
        this.useRealTimeRate = useRealTimeRate;
    }

    // Getters and Setters
    public String getCryptoType() {
        return cryptoType;
    }

    public void setCryptoType(String cryptoType) {
        this.cryptoType = cryptoType;
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

    public BigDecimal getPlatformFee() {
        return platformFee;
    }

    public void setPlatformFee(BigDecimal platformFee) {
        this.platformFee = platformFee != null ? platformFee : BigDecimal.ZERO;
    }

    public boolean isUseRealTimeRate() {
        return useRealTimeRate;
    }

    public void setUseRealTimeRate(boolean useRealTimeRate) {
        this.useRealTimeRate = useRealTimeRate;
    }

    // Utility methods
    public BigDecimal getTotalCost() {
        return amount.add(platformFee);
    }

    @Override
    public String toString() {
        return "CryptoBuyFromMainRequest{" +
                "cryptoType='" + cryptoType + '\'' +
                ", amount=" + amount +
                ", description='" + description + '\'' +
                ", platformFee=" + platformFee +
                ", useRealTimeRate=" + useRealTimeRate +
                '}';
    }
}
