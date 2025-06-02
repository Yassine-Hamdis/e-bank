package com.ebanking.service;

import com.ebanking.dto.request.ClientCreateRequest;
import com.ebanking.dto.request.ClientUpdateRequest;
import com.ebanking.dto.request.DepositRequest;
import com.ebanking.dto.response.*;
import com.ebanking.entity.TransactionStatus;

import java.util.List;

/**
 * Service interface for bank agent operations
 */
public interface BankAgentService {

    /**
     * Create a new client with automatic Account and CryptoWallet creation
     * @param request the client creation request
     * @param agentId the ID of the bank agent creating the client
     * @return ClientEnrollmentResponse containing client, account, and crypto wallet information
     * @throws RuntimeException if creation fails
     */
    ClientEnrollmentResponse createClient(ClientCreateRequest request, Long agentId);

    /**
     * Get all clients managed by a specific bank agent
     * @param agentId the bank agent ID
     * @return list of clients managed by the agent
     */
    List<ClientDTO> getManagedClients(Long agentId);

    /**
     * Get client details by client ID
     * @param clientId the client ID
     * @param agentId the bank agent ID (for authorization)
     * @return ClientDTO if found and agent has access
     * @throws RuntimeException if client not found or access denied
     */
    ClientDTO getClientById(String clientId, Long agentId);

    /**
     * Update client information
     * @param clientId the client ID
     * @param request the client update request
     * @param agentId the bank agent ID (for authorization)
     * @return updated ClientDTO
     * @throws RuntimeException if client not found or access denied
     */
    ClientDTO updateClient(String clientId, ClientUpdateRequest request, Long agentId);

    /**
     * Delete client
     * @param clientId the client ID
     * @param agentId the bank agent ID (for authorization)
     * @throws RuntimeException if client not found or access denied
     */
    void deleteClient(String clientId, Long agentId);

    /**
     * Get transactions that need verification for clients managed by the agent
     * @param agentId the bank agent ID
     * @return list of pending transactions for managed clients
     */
    List<TransactionDTO> getTransactionsForVerification(Long agentId);

    /**
     * Get all transactions for clients managed by the agent
     * @param agentId the bank agent ID
     * @return list of all transactions for managed clients
     */
    List<TransactionDTO> getAllTransactionsForManagedClients(Long agentId);

    /**
     * Verify a transaction
     * @param transactionId the transaction ID
     * @param status the verification status (VERIFIED or REJECTED)
     * @param description the verification description
     * @param agentId the bank agent ID
     * @return updated TransactionDTO
     * @throws RuntimeException if transaction not found
     */
    TransactionDTO verifyTransaction(String transactionId, TransactionStatus status, String description, Long agentId);

    /**
     * Get enrollment history for clients managed by the agent
     * @param agentId the bank agent ID
     * @return list of client enrollment information
     */
    List<ClientEnrollmentHistoryDTO> getEnrollmentHistory(Long agentId);

    /**
     * Create a deposit transaction for a client's account
     * @param request the deposit request containing client ID, account ID, amount, and description
     * @param agentId the bank agent ID performing the deposit
     * @return TransactionDTO containing the deposit transaction details
     * @throws RuntimeException if client not found, account not found, or agent doesn't manage the client
     */
    TransactionDTO createDeposit(DepositRequest request, Long agentId);

    /**
     * Get deposit statistics for a specific agent
     * @param agentId the bank agent ID
     * @return AgentDepositStatisticsDTO containing deposit statistics for the agent
     * @throws RuntimeException if agent not found
     */
    AgentDepositStatisticsDTO getDepositStatistics(Long agentId);

    /**
     * Inner class for client enrollment history
     */
    class ClientEnrollmentHistoryDTO {
        private String clientId;
        private String enrollmentDate;
        private String agentId;
        private String status;

        public ClientEnrollmentHistoryDTO() {}

        public ClientEnrollmentHistoryDTO(String clientId, String enrollmentDate, String agentId, String status) {
            this.clientId = clientId;
            this.enrollmentDate = enrollmentDate;
            this.agentId = agentId;
            this.status = status;
        }

        // Getters and Setters
        public String getClientId() {
            return clientId;
        }

        public void setClientId(String clientId) {
            this.clientId = clientId;
        }

        public String getEnrollmentDate() {
            return enrollmentDate;
        }

        public void setEnrollmentDate(String enrollmentDate) {
            this.enrollmentDate = enrollmentDate;
        }

        public String getAgentId() {
            return agentId;
        }

        public void setAgentId(String agentId) {
            this.agentId = agentId;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        @Override
        public String toString() {
            return "ClientEnrollmentHistoryDTO{" +
                    "clientId='" + clientId + '\'' +
                    ", enrollmentDate='" + enrollmentDate + '\'' +
                    ", agentId='" + agentId + '\'' +
                    ", status='" + status + '\'' +
                    '}';
        }
    }
}
