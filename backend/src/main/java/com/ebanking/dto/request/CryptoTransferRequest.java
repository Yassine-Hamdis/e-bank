package com.ebanking.dto.request;

import javax.validation.constraints.*;
import java.math.BigDecimal;

/**
 * DTO for cryptocurrency transfer requests
 */
public class CryptoTransferRequest {

    @NotBlank(message = "Recipient wallet address is required")
    @Size(max = 100, message = "Recipient wallet address must not exceed 100 characters")
    private String recipientWalletAddress;

    @NotBlank(message = "Crypto type is required")
    @Pattern(regexp = "BTC|ETH|USDT|BNB", message = "Crypto type must be one of: BTC, ETH, USDT, BNB")
    private String cryptoType;

    @NotNull(message = "Crypto amount is required")
    @DecimalMin(value = "0.00000001", message = "Crypto amount must be greater than 0")
    @Digits(integer = 12, fraction = 8, message = "Crypto amount format is invalid")
    private BigDecimal cryptoAmount;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @DecimalMin(value = "0.00", message = "Network fee cannot be negative")
    @Digits(integer = 10, fraction = 8, message = "Network fee format is invalid")
    private BigDecimal networkFee = BigDecimal.ZERO;

    private String type = "CRYPTO_TRANSFER";

    // Constructors
    public CryptoTransferRequest() {}

    public CryptoTransferRequest(String recipientWalletAddress, String cryptoType, 
                                BigDecimal cryptoAmount, String description) {
        this.recipientWalletAddress = recipientWalletAddress;
        this.cryptoType = cryptoType;
        this.cryptoAmount = cryptoAmount;
        this.description = description;
    }

    public CryptoTransferRequest(String recipientWalletAddress, String cryptoType, 
                                BigDecimal cryptoAmount, String description, BigDecimal networkFee) {
        this(recipientWalletAddress, cryptoType, cryptoAmount, description);
        this.networkFee = networkFee;
    }

    // Getters and Setters
    public String getRecipientWalletAddress() {
        return recipientWalletAddress;
    }

    public void setRecipientWalletAddress(String recipientWalletAddress) {
        this.recipientWalletAddress = recipientWalletAddress;
    }

    public String getCryptoType() {
        return cryptoType;
    }

    public void setCryptoType(String cryptoType) {
        this.cryptoType = cryptoType;
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

    public BigDecimal getNetworkFee() {
        return networkFee;
    }

    public void setNetworkFee(BigDecimal networkFee) {
        this.networkFee = networkFee != null ? networkFee : BigDecimal.ZERO;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    // Utility methods
    public BigDecimal getTotalCryptoAmount() {
        return cryptoAmount.add(networkFee);
    }

    @Override
    public String toString() {
        return "CryptoTransferRequest{" +
                "recipientWalletAddress='" + recipientWalletAddress + '\'' +
                ", cryptoType='" + cryptoType + '\'' +
                ", cryptoAmount=" + cryptoAmount +
                ", description='" + description + '\'' +
                ", networkFee=" + networkFee +
                ", type='" + type + '\'' +
                '}';
    }
}
