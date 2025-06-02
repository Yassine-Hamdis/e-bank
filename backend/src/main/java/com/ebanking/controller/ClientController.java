package com.ebanking.controller;

import com.ebanking.dto.request.*;
import com.ebanking.dto.response.*;
import com.ebanking.service.ClientService;
import com.ebanking.service.impl.UserDetailsServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for client operations
 */
@RestController
@RequestMapping("/api/client")
@CrossOrigin(origins = {"http://localhost:45571", "http://localhost:4200"}, maxAge = 3600)
public class ClientController {

    private static final Logger logger = LoggerFactory.getLogger(ClientController.class);

    @Autowired
    private ClientService clientService;

    /**
     * Get current client's profile
     */
    @GetMapping("/profile")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<?> getProfile() {
        try {
            Long clientId = getCurrentClientId();
            ClientProfileDTO profile = clientService.getClientProfile(clientId);

            logger.info("Profile retrieved successfully for client: {}", clientId);
            return ResponseEntity.ok(profile);

        } catch (Exception e) {
            logger.error("Error retrieving client profile: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to retrieve profile", e.getMessage()));
        }
    }

    /**
     * Get client's main account details
     */
    @GetMapping("/account")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<?> getMainAccount() {
        try {
            Long clientId = getCurrentClientId();
            AccountDTO account = clientService.getMainAccount(clientId);

            logger.info("Main account retrieved successfully for client: {}", clientId);
            return ResponseEntity.ok(account);

        } catch (Exception e) {
            logger.error("Error retrieving main account: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to retrieve account", e.getMessage()));
        }
    }

    /**
     * Get account balance
     */
    @GetMapping("/account/balance")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<?> getAccountBalance() {
        try {
            Long clientId = getCurrentClientId();
            Map<String, Object> balance = clientService.getAccountBalance(clientId);

            logger.info("Account balance retrieved successfully for client: {}", clientId);
            return ResponseEntity.ok(balance);

        } catch (Exception e) {
            logger.error("Error retrieving account balance: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to retrieve balance", e.getMessage()));
        }
    }

    /**
     * Create additional account
     */
    @PostMapping("/accounts")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<?> createAdditionalAccount(@Valid @RequestBody Map<String, Object> request) {
        try {
            Long clientId = getCurrentClientId();
            AccountDTO account = clientService.createAdditionalAccount(request, clientId);

            logger.info("Additional account created successfully for client: {}", clientId);
            return ResponseEntity.status(HttpStatus.CREATED).body(account);

        } catch (Exception e) {
            logger.error("Error creating additional account: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse("Failed to create account", e.getMessage()));
        }
    }

    /**
     * Get all accounts for client
     */
    @GetMapping("/accounts")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<?> getAllAccounts() {
        try {
            Long clientId = getCurrentClientId();
            List<AccountDTO> accounts = clientService.getAllAccounts(clientId);

            logger.info("All accounts retrieved successfully for client: {}", clientId);
            return ResponseEntity.ok(accounts);

        } catch (Exception e) {
            logger.error("Error retrieving accounts: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to retrieve accounts", e.getMessage()));
        }
    }

    /**
     * Get specific account details
     */
    @GetMapping("/accounts/{accountId}")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<?> getAccountById(@PathVariable String accountId) {
        try {
            Long clientId = getCurrentClientId();
            AccountDTO account = clientService.getAccountById(accountId, clientId);

            logger.info("Account {} retrieved successfully for client: {}", accountId, clientId);
            return ResponseEntity.ok(account);

        } catch (Exception e) {
            logger.error("Error retrieving account {}: {}", accountId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Account not found", e.getMessage()));
        }
    }

    /**
     * Create a basic transaction
     */
    @PostMapping("/transactions")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<?> createTransaction(@Valid @RequestBody TransactionCreateRequest request) {
        try {
            Long clientId = getCurrentClientId();
            TransactionDTO transaction = clientService.createTransaction(request, clientId);

            logger.info("Transaction created successfully for client: {}", clientId);
            return ResponseEntity.status(HttpStatus.CREATED).body(transaction);

        } catch (Exception e) {
            logger.error("Error creating transaction: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse("Failed to create transaction", e.getMessage()));
        }
    }

    /**
     * Get all transactions for client
     */
    @GetMapping("/transactions")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<?> getTransactions() {
        try {
            Long clientId = getCurrentClientId();
            List<TransactionDTO> transactions = clientService.getClientTransactions(clientId);

            logger.info("Transactions retrieved successfully for client: {}", clientId);
            return ResponseEntity.ok(transactions);

        } catch (Exception e) {
            logger.error("Error retrieving transactions: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to retrieve transactions", e.getMessage()));
        }
    }

    /**
     * Get specific transaction details
     */
    @GetMapping("/transactions/{transactionId}")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<?> getTransactionById(@PathVariable String transactionId) {
        try {
            Long clientId = getCurrentClientId();
            TransactionDTO transaction = clientService.getTransactionById(transactionId, clientId);

            logger.info("Transaction {} retrieved successfully for client: {}", transactionId, clientId);
            return ResponseEntity.ok(transaction);

        } catch (Exception e) {
            logger.error("Error retrieving transaction {}: {}", transactionId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Transaction not found", e.getMessage()));
        }
    }

    /**
     * Create a transfer between accounts
     */
    @PostMapping("/transfers")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<?> createTransfer(@Valid @RequestBody TransferRequest request) {
        try {
            Long clientId = getCurrentClientId();
            TransactionDTO transaction = clientService.createTransfer(request, clientId);

            logger.info("Transfer created successfully for client: {}", clientId);
            return ResponseEntity.status(HttpStatus.CREATED).body(transaction);

        } catch (Exception e) {
            logger.error("Error creating transfer: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse("Failed to create transfer", e.getMessage()));
        }
    }

    /**
     * Create a mobile recharge transaction
     */
    @PostMapping("/mobile-recharge")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<?> createMobileRecharge(@Valid @RequestBody MobileRechargeRequest request) {
        try {
            Long clientId = getCurrentClientId();
            TransactionDTO transaction = clientService.createMobileRecharge(request, clientId);

            logger.info("Mobile recharge created successfully for client: {}", clientId);
            return ResponseEntity.status(HttpStatus.CREATED).body(transaction);

        } catch (Exception e) {
            logger.error("Error creating mobile recharge: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse("Failed to create mobile recharge", e.getMessage()));
        }
    }

    /**
     * Get crypto wallet details
     */
    @GetMapping("/crypto/wallet")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<?> getCryptoWallet() {
        try {
            Long clientId = getCurrentClientId();
            Map<String, Object> wallet = clientService.getCryptoWallet(clientId);

            logger.info("Crypto wallet retrieved successfully for client: {}", clientId);
            return ResponseEntity.ok(wallet);

        } catch (Exception e) {
            logger.error("Error retrieving crypto wallet: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to retrieve crypto wallet", e.getMessage()));
        }
    }

    /**
     * Update crypto wallet address
     */
    @PutMapping("/crypto/wallet/address")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<?> updateWalletAddress(@Valid @RequestBody WalletUpdateRequest request) {
        try {
            Long clientId = getCurrentClientId();
            Map<String, Object> wallet = clientService.updateWalletAddress(request, clientId);

            logger.info("Crypto wallet address updated successfully for client: {}", clientId);
            return ResponseEntity.ok(wallet);

        } catch (Exception e) {
            logger.error("Error updating wallet address: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse("Failed to update wallet address", e.getMessage()));
        }
    }

    /**
     * Buy cryptocurrency
     */
    @PostMapping("/crypto/buy")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<?> buyCryptocurrency(@Valid @RequestBody CryptoBuyRequest request) {
        try {
            Long clientId = getCurrentClientId();
            TransactionDTO transaction = clientService.buyCryptocurrency(request, clientId);

            logger.info("Crypto buy transaction created successfully for client: {}", clientId);
            return ResponseEntity.status(HttpStatus.CREATED).body(transaction);

        } catch (Exception e) {
            logger.error("Error creating crypto buy transaction: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse("Failed to buy cryptocurrency", e.getMessage()));
        }
    }

    /**
     * Sell cryptocurrency
     */
    @PostMapping("/crypto/sell")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<?> sellCryptocurrency(@Valid @RequestBody CryptoSellRequest request) {
        try {
            Long clientId = getCurrentClientId();
            TransactionDTO transaction = clientService.sellCryptocurrency(request, clientId);

            logger.info("Crypto sell transaction created successfully for client: {}", clientId);
            return ResponseEntity.status(HttpStatus.CREATED).body(transaction);

        } catch (Exception e) {
            logger.error("Error creating crypto sell transaction: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse("Failed to sell cryptocurrency", e.getMessage()));
        }
    }

    /**
     * Buy cryptocurrency from main account with real-time rates
     */
    @PostMapping("/crypto/buy-from-main")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<?> buyCryptocurrencyFromMain(@Valid @RequestBody CryptoBuyFromMainRequest request) {
        try {
            Long clientId = getCurrentClientId();
            CryptoBuyFromMainResponse response = clientService.buyCryptocurrencyFromMain(request, clientId);

            logger.info("Crypto buy from main transaction created successfully for client: {}", clientId);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            logger.error("Error creating crypto buy from main transaction: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse("Failed to buy cryptocurrency from main account", e.getMessage()));
        }
    }

    /**
     * Transfer cryptocurrency to another wallet
     */
    @PostMapping("/crypto/transfer")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<?> transferCryptocurrency(@Valid @RequestBody CryptoTransferRequest request) {
        try {
            Long clientId = getCurrentClientId();
            CryptoTransferResponse response = clientService.transferCryptocurrency(request, clientId);

            logger.info("Crypto transfer transaction created successfully for client: {}", clientId);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            logger.error("Error creating crypto transfer transaction: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse("Failed to transfer cryptocurrency", e.getMessage()));
        }
    }

    /**
     * Get crypto transaction history
     */
    @GetMapping("/crypto/transactions")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<?> getCryptoTransactions() {
        try {
            Long clientId = getCurrentClientId();
            List<TransactionDTO> transactions = clientService.getCryptoTransactionHistory(clientId);

            logger.info("Crypto transaction history retrieved successfully for client: {}", clientId);
            return ResponseEntity.ok(transactions);

        } catch (Exception e) {
            logger.error("Error retrieving crypto history: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to retrieve crypto history", e.getMessage()));
        }
    }

    /**
     * Get current crypto rates
     */
    @GetMapping("/crypto/rates")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<?> getCryptoRates() {
        try {
            Map<String, Object> rates = clientService.getCryptoRates();

            logger.info("Crypto rates retrieved successfully");
            return ResponseEntity.ok(rates);

        } catch (Exception e) {
            logger.error("Error retrieving crypto rates: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to retrieve crypto rates", e.getMessage()));
        }
    }

    /**
     * Get client notifications
     */
    @GetMapping("/notifications")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<?> getNotifications() {
        try {
            Long clientId = getCurrentClientId();
            List<NotificationDTO> notifications = clientService.getClientNotifications(clientId);

            logger.info("Notifications retrieved successfully for client: {}", clientId);
            return ResponseEntity.ok(notifications);

        } catch (Exception e) {
            logger.error("Error retrieving notifications: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to retrieve notifications", e.getMessage()));
        }
    }

    /**
     * Mark notification as read
     */
    @PutMapping("/notifications/{notificationId}/read")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<?> markNotificationAsRead(@PathVariable String notificationId) {
        try {
            Long clientId = getCurrentClientId();
            NotificationDTO notification = clientService.markNotificationAsRead(notificationId, clientId);

            logger.info("Notification {} marked as read for client: {}", notificationId, clientId);
            return ResponseEntity.ok(notification);

        } catch (Exception e) {
            logger.error("Error marking notification as read: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Failed to mark notification as read", e.getMessage()));
        }
    }

    /**
     * Delete notification
     */
    @DeleteMapping("/notifications/{notificationId}")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<?> deleteNotification(@PathVariable String notificationId) {
        try {
            Long clientId = getCurrentClientId();
            boolean deleted = clientService.deleteNotification(notificationId, clientId);

            if (deleted) {
                logger.info("Notification {} deleted successfully for client: {}", notificationId, clientId);
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Notification deleted successfully");
                response.put("notificationId", notificationId);
                response.put("timestamp", System.currentTimeMillis());
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(createErrorResponse("Notification not found", "Could not delete notification"));
            }

        } catch (Exception e) {
            logger.error("Error deleting notification: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Failed to delete notification", e.getMessage()));
        }
    }

    // Utility methods
    private Long getCurrentClientId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsServiceImpl.UserPrincipal userPrincipal =
                (UserDetailsServiceImpl.UserPrincipal) authentication.getPrincipal();
        return userPrincipal.getId();
    }

    private Map<String, Object> createErrorResponse(String error, String message) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", error);
        errorResponse.put("message", message);
        errorResponse.put("timestamp", System.currentTimeMillis());
        return errorResponse;
    }
}
