package com.ebanking.controller;

import com.ebanking.dto.request.ClientCreateRequest;
import com.ebanking.dto.request.ClientUpdateRequest;
import com.ebanking.dto.request.DepositRequest;
import com.ebanking.dto.response.*;
import com.ebanking.entity.TransactionStatus;
import com.ebanking.service.BankAgentService;
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
 * REST Controller for Bank Agent operations
 * Handles client management and transaction verification
 */
@RestController
@RequestMapping("/api/agent")
@PreAuthorize("hasRole('AGENT')")
@CrossOrigin(origins = {"http://localhost:45571", "http://localhost:4200"}, maxAge = 3600)

public class BankAgentController {

    private static final Logger logger = LoggerFactory.getLogger(BankAgentController.class);

    @Autowired
    private BankAgentService bankAgentService;

    // ==================== CLIENT MANAGEMENT ENDPOINTS ====================

    /**
     * Create a new client with automatic Account and CryptoWallet creation
     * POST /agent/clients
     *
     * @param request the client creation request
     * @return ResponseEntity containing the complete client enrollment response
     */
    @PostMapping("/clients")
    public ResponseEntity<?> createClient(@Valid @RequestBody ClientCreateRequest request) {
        logger.info("Creating new client with username: {}", request.getUsername());

        try {
            Long agentId = getCurrentAgentId();
            ClientEnrollmentResponse response = bankAgentService.createClient(request, agentId);

            logger.info("Client created successfully with ID: {}", response.getClient().getClientId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            logger.error("Failed to create client: {}", e.getMessage());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Client creation failed");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    /**
     * Get all clients managed by the current bank agent
     * GET /agent/clients
     *
     * @return ResponseEntity containing list of managed clients
     */
    @GetMapping("/clients")
    public ResponseEntity<?> getManagedClients() {
        logger.debug("Fetching managed clients for current agent");

        try {
            Long agentId = getCurrentAgentId();
            List<ClientDTO> clients = bankAgentService.getManagedClients(agentId);

            logger.debug("Retrieved {} managed clients", clients.size());
            return ResponseEntity.ok(clients);

        } catch (Exception e) {
            logger.error("Failed to fetch managed clients: {}", e.getMessage());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to fetch clients");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Get client details by client ID
     * GET /agent/clients/{clientId}
     *
     * @param clientId the client ID
     * @return ResponseEntity containing the client details
     */
    @GetMapping("/clients/{clientId}")
    public ResponseEntity<?> getClientById(@PathVariable String clientId) {
        logger.debug("Fetching client details for ID: {}", clientId);

        try {
            Long agentId = getCurrentAgentId();
            ClientDTO client = bankAgentService.getClientById(clientId, agentId);

            return ResponseEntity.ok(client);

        } catch (Exception e) {
            logger.error("Failed to fetch client {}: {}", clientId, e.getMessage());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Client not found or access denied");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    /**
     * Update client information
     * PUT /agent/clients/{clientId}
     *
     * @param clientId the client ID
     * @param request the client update request
     * @return ResponseEntity containing the updated client information
     */
    @PutMapping("/clients/{clientId}")
    public ResponseEntity<?> updateClient(@PathVariable String clientId,
                                        @Valid @RequestBody ClientUpdateRequest request) {
        logger.info("Updating client: {}", clientId);

        try {
            Long agentId = getCurrentAgentId();
            ClientDTO updatedClient = bankAgentService.updateClient(clientId, request, agentId);

            logger.info("Client updated successfully: {}", clientId);
            return ResponseEntity.ok(updatedClient);

        } catch (Exception e) {
            logger.error("Failed to update client {}: {}", clientId, e.getMessage());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to update client");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    /**
     * Delete client
     * DELETE /agent/clients/{clientId}
     *
     * @param clientId the client ID
     * @return ResponseEntity indicating deletion status
     */
    @DeleteMapping("/clients/{clientId}")
    public ResponseEntity<?> deleteClient(@PathVariable String clientId) {
        logger.info("Deleting client: {}", clientId);

        try {
            Long agentId = getCurrentAgentId();
            bankAgentService.deleteClient(clientId, agentId);

            logger.info("Client deleted successfully: {}", clientId);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Client deleted successfully");
            response.put("clientId", clientId);
            response.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Failed to delete client {}: {}", clientId, e.getMessage());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to delete client");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    // ==================== TRANSACTION VERIFICATION ENDPOINTS ====================

    /**
     * Get transactions that need verification for managed clients
     * GET /agent/transactions/pending
     *
     * @return ResponseEntity containing list of pending transactions for managed clients
     */
    @GetMapping("/transactions/pending")
    public ResponseEntity<?> getTransactionsForVerification() {
        logger.debug("Fetching pending transactions for verification");

        try {
            Long agentId = getCurrentAgentId();
            List<TransactionDTO> transactions = bankAgentService.getTransactionsForVerification(agentId);

            logger.debug("Retrieved {} pending transactions for verification", transactions.size());
            return ResponseEntity.ok(transactions);

        } catch (Exception e) {
            logger.error("Failed to fetch pending transactions for verification: {}", e.getMessage());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to fetch pending transactions");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Get all transactions for managed clients (including transfers, recharges, etc.)
     * GET /agent/transactions
     *
     * @return ResponseEntity containing list of all transactions for managed clients
     */
    @GetMapping("/transactions")
    public ResponseEntity<?> getAllTransactionsForManagedClients() {
        logger.debug("Fetching all transactions for managed clients");

        try {
            Long agentId = getCurrentAgentId();
            List<TransactionDTO> transactions = bankAgentService.getAllTransactionsForManagedClients(agentId);

            logger.debug("Retrieved {} total transactions for managed clients", transactions.size());
            return ResponseEntity.ok(transactions);

        } catch (Exception e) {
            logger.error("Failed to fetch transactions for managed clients: {}", e.getMessage());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to fetch transactions");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Verify a transaction
     * POST /agent/transactions/{id}/verify
     *
     * @param id the transaction ID
     * @param verificationRequest the verification request
     * @return ResponseEntity containing the updated transaction
     */
    @PostMapping("/transactions/{id}/verify")
    public ResponseEntity<?> verifyTransaction(@PathVariable String id,
                                             @RequestBody TransactionVerificationRequest verificationRequest) {
        logger.info("Verifying transaction: {} with status: {}", id, verificationRequest.getStatus());

        try {
            Long agentId = getCurrentAgentId();
            TransactionStatus status = TransactionStatus.valueOf(verificationRequest.getStatus().toUpperCase());

            TransactionDTO updatedTransaction = bankAgentService.verifyTransaction(
                id, status, verificationRequest.getDescription(), agentId);

            logger.info("Transaction verified successfully: {}", id);
            return ResponseEntity.ok(updatedTransaction);

        } catch (Exception e) {
            logger.error("Failed to verify transaction {}: {}", id, e.getMessage());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to verify transaction");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    // ==================== ENROLLMENT HISTORY ENDPOINT ====================

    /**
     * Get enrollment history for clients managed by the agent
     * GET /agent/enrollments
     *
     * @return ResponseEntity containing enrollment history
     */
    @GetMapping("/enrollments")
    public ResponseEntity<?> getEnrollmentHistory() {
        logger.debug("Fetching enrollment history");

        try {
            Long agentId = getCurrentAgentId();
            List<BankAgentService.ClientEnrollmentHistoryDTO> history =
                bankAgentService.getEnrollmentHistory(agentId);

            logger.debug("Retrieved enrollment history with {} entries", history.size());
            return ResponseEntity.ok(history);

        } catch (Exception e) {
            logger.error("Failed to fetch enrollment history: {}", e.getMessage());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to fetch enrollment history");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // ==================== DEPOSIT OPERATIONS ====================

    /**
     * Create a deposit transaction for a client's account
     * POST /agent/deposit
     *
     * @param request the deposit request containing client ID, account ID, amount, and description
     * @return ResponseEntity containing the deposit transaction details
     */
    @PostMapping("/deposit")
    public ResponseEntity<?> createDeposit(@Valid @RequestBody DepositRequest request) {
        logger.info("Creating deposit for client: {} account: {} amount: {}",
                   request.getClientId(), request.getAccountId(), request.getAmount());

        try {
            Long agentId = getCurrentAgentId();
            TransactionDTO depositTransaction = bankAgentService.createDeposit(request, agentId);

            logger.info("Deposit created successfully: {}", depositTransaction.getTransactionId());
            return ResponseEntity.status(HttpStatus.CREATED).body(depositTransaction);

        } catch (Exception e) {
            logger.error("Failed to create deposit: {}", e.getMessage());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to create deposit");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    /**
     * Get deposit statistics for the current agent
     * GET /agent/deposit/statistics
     *
     * @return ResponseEntity containing deposit statistics for the agent
     */
    @GetMapping("/deposit/statistics")
    public ResponseEntity<?> getDepositStatistics() {
        logger.debug("Fetching deposit statistics for current agent");

        try {
            Long agentId = getCurrentAgentId();
            AgentDepositStatisticsDTO statistics = bankAgentService.getDepositStatistics(agentId);

            logger.debug("Deposit statistics retrieved successfully for agent: {}", agentId);
            return ResponseEntity.ok(statistics);

        } catch (Exception e) {
            logger.error("Failed to fetch deposit statistics: {}", e.getMessage());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to fetch deposit statistics");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // ==================== HELPER METHODS ====================

    /**
     * Get the current authenticated bank agent's ID
     * @return the agent ID
     */
    private Long getCurrentAgentId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsServiceImpl.UserPrincipal) {
            UserDetailsServiceImpl.UserPrincipal userPrincipal = (UserDetailsServiceImpl.UserPrincipal) authentication.getPrincipal();
            return userPrincipal.getId();
        }
        throw new RuntimeException("Unable to get current agent ID from authentication context");
    }

    // ==================== INNER CLASSES ====================

    /**
     * DTO for transaction verification request
     */
    public static class TransactionVerificationRequest {
        private String status;
        private String description;

        public TransactionVerificationRequest() {}

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}
