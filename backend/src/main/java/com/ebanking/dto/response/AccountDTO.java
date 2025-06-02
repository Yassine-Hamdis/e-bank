package com.ebanking.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for Account response
 */
public class AccountDTO {

    private Long id;
    private String accountId;
    private String clientId;
    private BigDecimal balance;
    private String accountType;
    private String status;
    private String currency;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime createdDate;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime lastTransactionDate;

    // Constructors
    public AccountDTO() {}

    public AccountDTO(Long id, String accountId, String clientId, BigDecimal balance,
                     String accountType, String status, String currency, LocalDateTime createdDate) {
        this.id = id;
        this.accountId = accountId;
        this.clientId = clientId;
        this.balance = balance;
        this.accountType = accountType;
        this.status = status;
        this.currency = currency;
        this.createdDate = createdDate;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getLastTransactionDate() {
        return lastTransactionDate;
    }

    public void setLastTransactionDate(LocalDateTime lastTransactionDate) {
        this.lastTransactionDate = lastTransactionDate;
    }

    @Override
    public String toString() {
        return "AccountDTO{" +
                "id=" + id +
                ", accountId='" + accountId + '\'' +
                ", clientId='" + clientId + '\'' +
                ", balance=" + balance +
                ", accountType='" + accountType + '\'' +
                ", status='" + status + '\'' +
                ", currency='" + currency + '\'' +
                ", createdDate=" + createdDate +
                '}';
    }
}
