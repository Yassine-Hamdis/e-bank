package com.ebanking.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * DTO for updating crypto wallet address
 */
public class WalletUpdateRequest {

    @NotBlank(message = "New wallet address is required")
    @Size(min = 26, max = 100, message = "Wallet address must be between 26 and 100 characters")
    private String newWalletAddress;

    // Constructors
    public WalletUpdateRequest() {}

    public WalletUpdateRequest(String newWalletAddress) {
        this.newWalletAddress = newWalletAddress;
    }

    // Getters and Setters
    public String getNewWalletAddress() {
        return newWalletAddress;
    }

    public void setNewWalletAddress(String newWalletAddress) {
        this.newWalletAddress = newWalletAddress;
    }

    @Override
    public String toString() {
        return "WalletUpdateRequest{" +
                "newWalletAddress='" + newWalletAddress + '\'' +
                '}';
    }
}
