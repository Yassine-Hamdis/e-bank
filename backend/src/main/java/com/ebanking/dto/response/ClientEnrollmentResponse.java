package com.ebanking.dto.response;

/**
 * DTO for complete client enrollment response
 * Contains client, account, and crypto wallet information
 */
public class ClientEnrollmentResponse {

    private ClientDTO client;
    private AccountDTO account;
    private CryptoWalletDTO cryptoWallet;

    // Constructors
    public ClientEnrollmentResponse() {}

    public ClientEnrollmentResponse(ClientDTO client, AccountDTO account, CryptoWalletDTO cryptoWallet) {
        this.client = client;
        this.account = account;
        this.cryptoWallet = cryptoWallet;
    }

    // Getters and Setters
    public ClientDTO getClient() {
        return client;
    }

    public void setClient(ClientDTO client) {
        this.client = client;
    }

    public AccountDTO getAccount() {
        return account;
    }

    public void setAccount(AccountDTO account) {
        this.account = account;
    }

    public CryptoWalletDTO getCryptoWallet() {
        return cryptoWallet;
    }

    public void setCryptoWallet(CryptoWalletDTO cryptoWallet) {
        this.cryptoWallet = cryptoWallet;
    }

    @Override
    public String toString() {
        return "ClientEnrollmentResponse{" +
                "client=" + client +
                ", account=" + account +
                ", cryptoWallet=" + cryptoWallet +
                '}';
    }
}
