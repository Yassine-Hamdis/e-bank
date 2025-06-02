package com.ebanking.dto.request;

import javax.validation.constraints.*;
import java.math.BigDecimal;

/**
 * DTO for transfer requests
 */
public class TransferRequest {

    @NotBlank(message = "Source account ID is required")
    @Size(max = 20, message = "Source account ID must not exceed 20 characters")
    private String sourceAccountId;

    @NotBlank(message = "Destination account ID is required")
    @Size(max = 20, message = "Destination account ID must not exceed 20 characters")
    private String destinationAccountId;

    @DecimalMin(value = "0.00", message = "Transfer fee cannot be negative")
    @Digits(integer = 8, fraction = 2, message = "Transfer fee format is invalid")
    private BigDecimal transferFee = BigDecimal.ZERO;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @Digits(integer = 13, fraction = 2, message = "Amount format is invalid")
    private BigDecimal amount;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    private String type = "TRANSFER";

    // Constructors
    public TransferRequest() {}

    public TransferRequest(String sourceAccountId, String destinationAccountId, 
                          BigDecimal amount, String description) {
        this.sourceAccountId = sourceAccountId;
        this.destinationAccountId = destinationAccountId;
        this.amount = amount;
        this.description = description;
    }

    public TransferRequest(String sourceAccountId, String destinationAccountId, 
                          BigDecimal transferFee, BigDecimal amount, String description) {
        this(sourceAccountId, destinationAccountId, amount, description);
        this.transferFee = transferFee;
    }

    // Getters and Setters
    public String getSourceAccountId() {
        return sourceAccountId;
    }

    public void setSourceAccountId(String sourceAccountId) {
        this.sourceAccountId = sourceAccountId;
    }

    public String getDestinationAccountId() {
        return destinationAccountId;
    }

    public void setDestinationAccountId(String destinationAccountId) {
        this.destinationAccountId = destinationAccountId;
    }

    public BigDecimal getTransferFee() {
        return transferFee;
    }

    public void setTransferFee(BigDecimal transferFee) {
        this.transferFee = transferFee != null ? transferFee : BigDecimal.ZERO;
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

    @Override
    public String toString() {
        return "TransferRequest{" +
                "sourceAccountId='" + sourceAccountId + '\'' +
                ", destinationAccountId='" + destinationAccountId + '\'' +
                ", transferFee=" + transferFee +
                ", amount=" + amount +
                ", description='" + description + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
