package com.ebanking.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for creating basic transactions
 */
public class TransactionCreateRequest {

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @Digits(integer = 13, fraction = 2, message = "Amount format is invalid")
    private BigDecimal amount;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime date;

    @NotBlank(message = "Status is required")
    @Pattern(regexp = "PENDING|VERIFIED|REJECTED|COMPLETED|FAILED|CANCELLED",
             message = "Status must be one of: PENDING, VERIFIED, REJECTED, COMPLETED, FAILED, CANCELLED")
    private String status = "PENDING";

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @NotBlank(message = "Transaction type is required")
    @Pattern(regexp = "TRANSFER|DEPOSIT|WITHDRAWAL|PAYMENT|REFUND|FEE|INTEREST|MOBILE_RECHARGE|CRYPTO_BUY|CRYPTO_SELL|CRYPTO_TRANSFER",
             message = "Invalid transaction type")
    private String type;

    // Constructors
    public TransactionCreateRequest() {}

    public TransactionCreateRequest(BigDecimal amount, String description, String type) {
        this.amount = amount;
        this.description = description;
        this.type = type;
        this.date = LocalDateTime.now();
    }

    // Getters and Setters
    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "TransactionCreateRequest{" +
                "amount=" + amount +
                ", date=" + date +
                ", status='" + status + '\'' +
                ", description='" + description + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
