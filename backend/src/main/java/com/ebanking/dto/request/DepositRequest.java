package com.ebanking.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for deposit requests by bank agents
 */
public class DepositRequest {

    @NotBlank(message = "Client ID is required")
    @Size(max = 20, message = "Client ID must not exceed 20 characters")
    private String clientId;

    @NotBlank(message = "Account ID is required")
    @Size(max = 20, message = "Account ID must not exceed 20 characters")
    private String accountId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @DecimalMax(value = "1000000.00", message = "Maximum deposit amount is 1,000,000.00 MAD")
    @Digits(integer = 13, fraction = 2, message = "Amount format is invalid")
    private BigDecimal amount;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime date;

    private String type = "DEPOSIT";

    // Constructors
    public DepositRequest() {}

    public DepositRequest(String clientId, String accountId, BigDecimal amount, String description) {
        this.clientId = clientId;
        this.accountId = accountId;
        this.amount = amount;
        this.description = description;
        this.date = LocalDateTime.now();
    }

    // Getters and Setters
    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
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

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "DepositRequest{" +
                "clientId='" + clientId + '\'' +
                ", accountId='" + accountId + '\'' +
                ", amount=" + amount +
                ", description='" + description + '\'' +
                ", date=" + date +
                ", type='" + type + '\'' +
                '}';
    }
}
