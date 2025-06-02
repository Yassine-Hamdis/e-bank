package com.ebanking.service;

import com.ebanking.dto.request.*;
import com.ebanking.dto.response.*;
import com.ebanking.entity.Account;

import java.util.List;
import java.util.Map;

/**
 * Service interface for client operations
 */
public interface ClientService {

    // Profile Management
    /**
     * Get client profile information
     * @param clientId the client ID
     * @return client profile DTO
     */
    ClientProfileDTO getClientProfile(Long clientId);

    // Account Management
    /**
     * Get client's main account details
     * @param clientId the client ID
     * @return account DTO
     */
    AccountDTO getMainAccount(Long clientId);

    /**
     * Get account balance
     * @param clientId the client ID
     * @return balance information
     */
    Map<String, Object> getAccountBalance(Long clientId);

    /**
     * Create additional account for client
     * @param request the account creation request
     * @param clientId the client ID
     * @return created account DTO
     */
    AccountDTO createAdditionalAccount(Map<String, Object> request, Long clientId);

    /**
     * Get all accounts for a client
     * @param clientId the client ID
     * @return list of account DTOs
     */
    List<AccountDTO> getAllAccounts(Long clientId);

    /**
     * Get specific account details
     * @param accountId the account ID
     * @param clientId the client ID (for security check)
     * @return account DTO
     */
    AccountDTO getAccountById(String accountId, Long clientId);

    // Transaction Management
    /**
     * Create a basic transaction
     * @param request the transaction request
     * @param clientId the client ID
     * @return transaction DTO
     */
    TransactionDTO createTransaction(TransactionCreateRequest request, Long clientId);

    /**
     * Get all transactions for a client
     * @param clientId the client ID
     * @return list of transaction DTOs
     */
    List<TransactionDTO> getClientTransactions(Long clientId);

    /**
     * Get specific transaction details
     * @param transactionId the transaction ID
     * @param clientId the client ID (for security check)
     * @return transaction DTO
     */
    TransactionDTO getTransactionById(String transactionId, Long clientId);

    // Transfer Operations
    /**
     * Create a transfer between accounts
     * @param request the transfer request
     * @param clientId the client ID
     * @return transaction DTO
     */
    TransactionDTO createTransfer(TransferRequest request, Long clientId);

    // Mobile Recharge Operations
    /**
     * Create a mobile recharge transaction
     * @param request the mobile recharge request
     * @param clientId the client ID
     * @return transaction DTO
     */
    TransactionDTO createMobileRecharge(MobileRechargeRequest request, Long clientId);

    // Crypto Operations
    /**
     * Get client's crypto wallet details
     * @param clientId the client ID
     * @return crypto wallet DTO with portfolio information
     */
    Map<String, Object> getCryptoWallet(Long clientId);

    /**
     * Update crypto wallet address
     * @param request the wallet update request
     * @param clientId the client ID
     * @return updated wallet information
     */
    Map<String, Object> updateWalletAddress(WalletUpdateRequest request, Long clientId);

    /**
     * Buy cryptocurrency
     * @param request the crypto buy request
     * @param clientId the client ID
     * @return transaction DTO
     */
    TransactionDTO buyCryptocurrency(CryptoBuyRequest request, Long clientId);

    /**
     * Sell cryptocurrency
     * @param request the crypto sell request
     * @param clientId the client ID
     * @return transaction DTO
     */
    TransactionDTO sellCryptocurrency(CryptoSellRequest request, Long clientId);

    /**
     * Buy cryptocurrency from main account with real-time rates
     * @param request the crypto buy from main request
     * @param clientId the client ID
     * @return crypto buy response with updated balances
     */
    CryptoBuyFromMainResponse buyCryptocurrencyFromMain(CryptoBuyFromMainRequest request, Long clientId);

    /**
     * Transfer cryptocurrency to another wallet
     * @param request the crypto transfer request
     * @param clientId the client ID
     * @return crypto transfer response with updated balances
     */
    CryptoTransferResponse transferCryptocurrency(CryptoTransferRequest request, Long clientId);

    /**
     * Get crypto transaction history
     * @param clientId the client ID
     * @return list of crypto transaction DTOs
     */
    List<TransactionDTO> getCryptoTransactionHistory(Long clientId);

    /**
     * Get current crypto rates
     * @return map of crypto rates
     */
    Map<String, Object> getCryptoRates();

    // Notification Management
    /**
     * Get client notifications
     * @param clientId the client ID
     * @return list of notification DTOs
     */
    List<NotificationDTO> getClientNotifications(Long clientId);

    /**
     * Mark notification as read
     * @param notificationId the notification ID
     * @param clientId the client ID
     * @return updated notification DTO
     */
    NotificationDTO markNotificationAsRead(String notificationId, Long clientId);

    /**
     * Delete notification
     * @param notificationId the notification ID
     * @param clientId the client ID
     * @return success status
     */
    boolean deleteNotification(String notificationId, Long clientId);
}
