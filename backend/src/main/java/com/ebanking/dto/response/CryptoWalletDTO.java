package com.ebanking.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for CryptoWallet response
 */
public class CryptoWalletDTO {

    private Long id;
    private String walletAddress;
    private String clientId;
    private String status;
    private List<String> supportedCryptos;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime createdDate;

    // Constructors
    public CryptoWalletDTO() {}

    public CryptoWalletDTO(Long id, String walletAddress, String clientId, String status, 
                          List<String> supportedCryptos, LocalDateTime createdDate) {
        this.id = id;
        this.walletAddress = walletAddress;
        this.clientId = clientId;
        this.status = status;
        this.supportedCryptos = supportedCryptos;
        this.createdDate = createdDate;
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

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getSupportedCryptos() {
        return supportedCryptos;
    }

    public void setSupportedCryptos(List<String> supportedCryptos) {
        this.supportedCryptos = supportedCryptos;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public String toString() {
        return "CryptoWalletDTO{" +
                "id=" + id +
                ", walletAddress='" + walletAddress + '\'' +
                ", clientId='" + clientId + '\'' +
                ", status='" + status + '\'' +
                ", supportedCryptos=" + supportedCryptos +
                ", createdDate=" + createdDate +
                '}';
    }
}
