package com.ebanking.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * CryptoWallet entity representing cryptocurrency wallets
 * Each client has one crypto wallet
 */
@Entity
@Table(name = "crypto_wallets")
public class CryptoWallet implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "wallet_address", unique = true, nullable = false, length = 100)
    private String walletAddress;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private WalletStatus status = WalletStatus.ACTIVE;

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    // Supported cryptocurrencies (as a constant list)
    private static final List<String> SUPPORTED_CRYPTOS = Arrays.asList("BTC", "ETH", "USDT");

    // Constructors
    public CryptoWallet() {
        this.createdDate = LocalDateTime.now();
    }

    public CryptoWallet(String walletAddress, Client client) {
        this();
        this.walletAddress = walletAddress;
        this.client = client;
    }

    // JPA lifecycle callbacks
    @PreUpdate
    public void preUpdate() {
        this.updatedDate = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWalletAddress() {
        return walletAddress;
    }

    public void setWalletAddress(String walletAddress) {
        this.walletAddress = walletAddress;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public WalletStatus getStatus() {
        return status;
    }

    public void setStatus(WalletStatus status) {
        this.status = status;
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

    public List<String> getSupportedCryptos() {
        return SUPPORTED_CRYPTOS;
    }

    @Override
    public String toString() {
        return "CryptoWallet{" +
                "id=" + id +
                ", walletAddress='" + walletAddress + '\'' +
                ", status=" + status +
                ", createdDate=" + createdDate +
                ", supportedCryptos=" + SUPPORTED_CRYPTOS +
                '}';
    }
}
