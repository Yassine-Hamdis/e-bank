package com.ebanking.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Transfer entity extending Transaction
 * Represents transfers between accounts
 */
@Entity
@DiscriminatorValue("TRANSFER")
public class Transfer extends Transaction implements Serializable {

    @Column(name = "source_account_id", length = 20)
    private String sourceAccountId;

    @Column(name = "destination_account_id", length = 20)
    private String destinationAccountId;

    @Column(name = "transfer_fee", precision = 10, scale = 2)
    private BigDecimal transferFee = BigDecimal.ZERO;

    // Constructors
    public Transfer() {
        super();
        setType(TransactionType.TRANSFER);
    }

    public Transfer(String transactionId, BigDecimal amount, String description,
                   String sourceAccountId, String destinationAccountId, BigDecimal transferFee) {
        super(transactionId, amount, TransactionType.TRANSFER, description);
        this.sourceAccountId = sourceAccountId;
        this.destinationAccountId = destinationAccountId;
        this.transferFee = transferFee != null ? transferFee : BigDecimal.ZERO;
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

    @Override
    public String toString() {
        return "Transfer{" +
                "sourceAccountId='" + sourceAccountId + '\'' +
                ", destinationAccountId='" + destinationAccountId + '\'' +
                ", transferFee=" + transferFee +
                ", transactionId='" + getTransactionId() + '\'' +
                ", amount=" + getAmount() +
                ", status=" + getStatus() +
                '}';
    }
}
