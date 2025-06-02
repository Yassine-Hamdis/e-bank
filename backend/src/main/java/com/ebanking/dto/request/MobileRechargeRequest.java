package com.ebanking.dto.request;

import javax.validation.constraints.*;
import java.math.BigDecimal;

/**
 * DTO for mobile recharge requests
 */
public class MobileRechargeRequest {

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^(\\+212|0)[5-7][0-9]{8}$", 
             message = "Phone number must be a valid Moroccan mobile number")
    private String phoneNumber;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "5.00", message = "Minimum recharge amount is 5.00 MAD")
    @DecimalMax(value = "500.00", message = "Maximum recharge amount is 500.00 MAD")
    @Digits(integer = 3, fraction = 2, message = "Amount format is invalid")
    private BigDecimal amount;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    private String type = "MOBILE_RECHARGE";

    @Size(max = 50, message = "Operator must not exceed 50 characters")
    private String operator; // Optional: Orange, Maroc Telecom, Inwi

    @Pattern(regexp = "PREPAID|POSTPAID", message = "Recharge type must be PREPAID or POSTPAID")
    private String rechargeType = "PREPAID";

    // Constructors
    public MobileRechargeRequest() {}

    public MobileRechargeRequest(String phoneNumber, BigDecimal amount, String description) {
        this.phoneNumber = phoneNumber;
        this.amount = amount;
        this.description = description;
    }

    public MobileRechargeRequest(String phoneNumber, BigDecimal amount, String description, 
                                String operator, String rechargeType) {
        this(phoneNumber, amount, description);
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
        return "MobileRechargeRequest{" +
                "phoneNumber='" + phoneNumber + '\'' +
                ", amount=" + amount +
                ", description='" + description + '\'' +
                ", type='" + type + '\'' +
                ", operator='" + operator + '\'' +
                ", rechargeType='" + rechargeType + '\'' +
                '}';
    }
}
