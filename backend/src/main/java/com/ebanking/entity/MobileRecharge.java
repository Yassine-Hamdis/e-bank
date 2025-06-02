package com.ebanking.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * MobileRecharge entity extending Transaction
 * Represents mobile phone recharge transactions
 */
@Entity
@DiscriminatorValue("MOBILE_RECHARGE")
public class MobileRecharge extends Transaction implements Serializable {

    @Column(name = "phone_number", nullable = true, length = 20)
    private String phoneNumber;

    @Column(name = "operator", length = 50)
    private String operator; // e.g., "Orange", "Maroc Telecom", "Inwi"

    @Column(name = "recharge_type", length = 20)
    private String rechargeType = "PREPAID"; // PREPAID, POSTPAID

    // Constructors
    public MobileRecharge() {
        super();
        setType(TransactionType.MOBILE_RECHARGE);
    }

    public MobileRecharge(String transactionId, BigDecimal amount, String description, String phoneNumber) {
        super(transactionId, amount, TransactionType.MOBILE_RECHARGE, description);
        this.phoneNumber = phoneNumber;
    }

    public MobileRecharge(String transactionId, BigDecimal amount, String description,
                         String phoneNumber, String operator, String rechargeType) {
        super(transactionId, amount, TransactionType.MOBILE_RECHARGE, description);
        this.phoneNumber = phoneNumber;
        this.operator = operator;
        this.rechargeType = rechargeType;
    }

    // Getters and Setters
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getRechargeType() {
        return rechargeType;
    }

    public void setRechargeType(String rechargeType) {
        this.rechargeType = rechargeType;
    }

    @Override
    public String toString() {
        return "MobileRecharge{" +
                "phoneNumber='" + phoneNumber + '\'' +
                ", operator='" + operator + '\'' +
                ", rechargeType='" + rechargeType + '\'' +
                ", transactionId='" + getTransactionId() + '\'' +
                ", amount=" + getAmount() +
                ", status=" + getStatus() +
                '}';
    }
}
