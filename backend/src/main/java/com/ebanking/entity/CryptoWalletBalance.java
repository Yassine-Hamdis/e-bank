package com.ebanking.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * CryptoWalletBalance entity representing individual cryptocurrency balances
 * Each crypto wallet can have multiple crypto balances (BTC, ETH, USDT, etc.)
 */
@Entity
@Table(name = "crypto_wallet_balances", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"crypto_wallet_id", "crypto_type"}))
public class CryptoWalletBalance implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crypto_wallet_id", nullable = false)
    private CryptoWallet cryptoWallet;

    @Column(name = "crypto_type", nullable = false, length = 10)
    private String cryptoType; // BTC, ETH, USDT, BNB

    @Column(name = "balance", precision = 20, scale = 8, nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    // Constructors
    public CryptoWalletBalance() {
        this.createdDate = LocalDateTime.now();
    }

    public CryptoWalletBalance(CryptoWallet cryptoWallet, String cryptoType) {
        this();
        this.cryptoWallet = cryptoWallet;
        this.cryptoType = cryptoType;
    }

    public CryptoWalletBalance(CryptoWallet cryptoWallet, String cryptoType, BigDecimal balance) {
        this(cryptoWallet, cryptoType);
        this.balance = balance;
    }

    // JPA lifecycle callbacks
    @PreUpdate
    public void preUpdate() {
        this.updatedDate = LocalDateTime.now();
    }

    // Business methods
    public void addBalance(BigDecimal amount) {
        if (amount != null && amount.compareTo(BigDecimal.ZERO) > 0) {
            this.balance = this.balance.add(amount);
        }
    }

    public void subtractBalance(BigDecimal amount) {
        if (amount != null && amount.compareTo(BigDecimal.ZERO) > 0) {
            if (this.balance.compareTo(amount) >= 0) {
                this.balance = this.balance.subtract(amount);
            } else {
                throw new RuntimeException("Insufficient crypto balance for " + cryptoType);
            }
        }
    }

    public boolean hasSufficientBalance(BigDecimal amount) {
        return amount != null && this.balance.compareTo(amount) >= 0;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CryptoWallet getCryptoWallet() {
        return cryptoWallet;
    }

    public void setCryptoWallet(CryptoWallet cryptoWallet) {
        this.cryptoWallet = cryptoWallet;
    }

    public String getCryptoType() {
        return cryptoType;
    }

    public void setCryptoType(String cryptoType) {
        this.cryptoType = cryptoType;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance != null ? balance : BigDecimal.ZERO;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(LocalDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }

    @Override
    public String toString() {
        return "CryptoWalletBalance{" +
                "id=" + id +
                ", cryptoType='" + cryptoType + '\'' +
                ", balance=" + balance +
                ", createdDate=" + createdDate +
                ", updatedDate=" + updatedDate +
                '}';
    }
}
