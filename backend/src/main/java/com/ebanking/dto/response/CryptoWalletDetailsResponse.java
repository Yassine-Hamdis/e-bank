package com.ebanking.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Enhanced DTO for CryptoWallet response with balance details
 */
public class CryptoWalletDetailsResponse {

    private Long id;
    private String walletAddress;
    private String clientId;
    private String status;
    private List<String> supportedCryptos;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime createdDate;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime updatedDate;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime lastAccessDate;

    // Balance information
    private Map<String, BigDecimal> cryptoBalances;
    private List<CryptoBalanceDetail> balanceDetails;
    private BigDecimal totalValueMAD;
    private Integer totalBalances;

    // Constructors
    public CryptoWalletDetailsResponse() {}

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

    public LocalDateTime getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(LocalDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }

    public LocalDateTime getLastAccessDate() {
        return lastAccessDate;
    }

    public void setLastAccessDate(LocalDateTime lastAccessDate) {
        this.lastAccessDate = lastAccessDate;
    }

    public Map<String, BigDecimal> getCryptoBalances() {
        return cryptoBalances;
    }

    public void setCryptoBalances(Map<String, BigDecimal> cryptoBalances) {
        this.cryptoBalances = cryptoBalances;
    }

    public List<CryptoBalanceDetail> getBalanceDetails() {
        return balanceDetails;
    }

    public void setBalanceDetails(List<CryptoBalanceDetail> balanceDetails) {
        this.balanceDetails = balanceDetails;
    }

    public BigDecimal getTotalValueMAD() {
        return totalValueMAD;
    }

    public void setTotalValueMAD(BigDecimal totalValueMAD) {
        this.totalValueMAD = totalValueMAD;
    }

    public Integer getTotalBalances() {
        return totalBalances;
    }

    public void setTotalBalances(Integer totalBalances) {
        this.totalBalances = totalBalances;
    }

    /**
     * Inner class for detailed balance information
     */
    public static class CryptoBalanceDetail {
        private Long id;
        private String cryptoType;
        private BigDecimal balance;
        private BigDecimal valueInMAD;
        
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
        private LocalDateTime createdDate;
        
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
        private LocalDateTime updatedDate;

        // Constructors
        public CryptoBalanceDetail() {}

        public CryptoBalanceDetail(Long id, String cryptoType, BigDecimal balance, 
                                 BigDecimal valueInMAD, LocalDateTime createdDate, LocalDateTime updatedDate) {
            this.id = id;
            this.cryptoType = cryptoType;
            this.balance = balance;
            this.valueInMAD = valueInMAD;
            this.createdDate = createdDate;
            this.updatedDate = updatedDate;
        }

        // Getters and Setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
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
            this.balance = balance;
        }

        public BigDecimal getValueInMAD() {
            return valueInMAD;
        }

        public void setValueInMAD(BigDecimal valueInMAD) {
            this.valueInMAD = valueInMAD;
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
            return "CryptoBalanceDetail{" +
                    "id=" + id +
                    ", cryptoType='" + cryptoType + '\'' +
                    ", balance=" + balance +
                    ", valueInMAD=" + valueInMAD +
                    ", createdDate=" + createdDate +
                    ", updatedDate=" + updatedDate +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "CryptoWalletDetailsResponse{" +
                "id=" + id +
                ", walletAddress='" + walletAddress + '\'' +
                ", clientId='" + clientId + '\'' +
                ", status='" + status + '\'' +
                ", totalValueMAD=" + totalValueMAD +
                ", totalBalances=" + totalBalances +
                '}';
    }
}
