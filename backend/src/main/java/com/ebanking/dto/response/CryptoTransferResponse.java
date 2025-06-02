package com.ebanking.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for cryptocurrency transfer response
 */
public class CryptoTransferResponse {

    private String transactionId;
    private String cryptoType;
    private BigDecimal cryptoAmount;
    private String senderWalletAddress;
    private String recipientWalletAddress;
    private String status;
    private String description;
    private BigDecimal networkFee;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime transactionDate;

    // Updated balances
    private BigDecimal updatedSenderBalance;
    private BigDecimal updatedRecipientBalance;

    // Constructors
    public CryptoTransferResponse() {}

    public CryptoTransferResponse(String transactionId, String cryptoType, BigDecimal cryptoAmount,
                                 String senderWalletAddress, String recipientWalletAddress, String status) {
        this.transactionId = transactionId;
        this.cryptoType = cryptoType;
        this.cryptoAmount = cryptoAmount;
        this.senderWalletAddress = senderWalletAddress;
        this.recipientWalletAddress = recipientWalletAddress;
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

    public BigDecimal getCryptoAmount() {
        return cryptoAmount;
    }

    public void setCryptoAmount(BigDecimal cryptoAmount) {
        this.cryptoAmount = cryptoAmount;
    }

    public String getSenderWalletAddress() {
        return senderWalletAddress;
    }

    public void setSenderWalletAddress(String senderWalletAddress) {
        this.senderWalletAddress = senderWalletAddress;
    }

    public String getRecipientWalletAddress() {
        return recipientWalletAddress;
    }

    public void setRecipientWalletAddress(String recipientWalletAddress) {
        this.recipientWalletAddress = recipientWalletAddress;
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

    public BigDecimal getNetworkFee() {
        return networkFee;
    }

    public void setNetworkFee(BigDecimal networkFee) {
        this.networkFee = networkFee;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    public BigDecimal getUpdatedSenderBalance() {
        return updatedSenderBalance;
    }

    public void setUpdatedSenderBalance(BigDecimal updatedSenderBalance) {
        this.updatedSenderBalance = updatedSenderBalance;
    }

    public BigDecimal getUpdatedRecipientBalance() {
        return updatedRecipientBalance;
    }

    public void setUpdatedRecipientBalance(BigDecimal updatedRecipientBalance) {
        this.updatedRecipientBalance = updatedRecipientBalance;
    }

    @Override
    public String toString() {
        return "CryptoTransferResponse{" +
                "transactionId='" + transactionId + '\'' +
                ", cryptoType='" + cryptoType + '\'' +
                ", cryptoAmount=" + cryptoAmount +
                ", senderWalletAddress='" + senderWalletAddress + '\'' +
                ", recipientWalletAddress='" + recipientWalletAddress + '\'' +
                ", status='" + status + '\'' +
                ", networkFee=" + networkFee +
                ", updatedSenderBalance=" + updatedSenderBalance +
                ", updatedRecipientBalance=" + updatedRecipientBalance +
                '}';
    }
}
