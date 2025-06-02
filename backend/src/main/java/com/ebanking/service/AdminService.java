package com.ebanking.service;

import com.ebanking.dto.request.BankAgentCreateRequest;
import com.ebanking.dto.response.BankAgentDTO;
import com.ebanking.entity.BankAgent;
import com.ebanking.entity.UserStatus;

import java.util.List;

/**
 * Service interface for admin operations
 */
public interface AdminService {

    /**
     * Create a new bank agent
     * @param request the bank agent creation request
     * @return BankAgentDTO containing the created agent information
     * @throws RuntimeException if creation fails
     */
    BankAgentDTO createBankAgent(BankAgentCreateRequest request);

    /**
     * Get all bank agents
     * @return list of all bank agents
     */
    List<BankAgentDTO> getAllBankAgents();

    /**
     * Get bank agent by ID
     * @param id the bank agent ID
     * @return BankAgentDTO if found
     * @throws RuntimeException if agent not found
     */
    BankAgentDTO getBankAgentById(Long id);

    /**
     * Get bank agent by employee ID
     * @param employeeId the employee ID
     * @return BankAgentDTO if found
     * @throws RuntimeException if agent not found
     */
    BankAgentDTO getBankAgentByEmployeeId(String employeeId);

    /**
     * Get bank agents by branch
     * @param branch the branch name
     * @return list of bank agents in the specified branch
     */
    List<BankAgentDTO> getBankAgentsByBranch(String branch);

    /**
     * Update bank agent status
     * @param id the bank agent ID
     * @param status the new status
     * @return updated BankAgentDTO
     * @throws RuntimeException if agent not found
     */
    BankAgentDTO updateBankAgentStatus(Long id, UserStatus status);

    /**
     * Delete bank agent
     * @param id the bank agent ID
     * @throws RuntimeException if agent not found or has managed clients
     */
    void deleteBankAgent(Long id);

    /**
     * Get bank agents statistics
     * @return statistics about bank agents
     */
    BankAgentStatistics getBankAgentStatistics();

    /**
     * Inner class for bank agent statistics
     */
    class BankAgentStatistics {
        private long totalAgents;
        private long activeAgents;
        private long inactiveAgents;
        private long totalManagedClients;

        public BankAgentStatistics(long totalAgents, long activeAgents, long inactiveAgents, long totalManagedClients) {
            this.totalAgents = totalAgents;
            this.activeAgents = activeAgents;
            this.inactiveAgents = inactiveAgents;
            this.totalManagedClients = totalManagedClients;
        }

        // Getters and setters
        public long getTotalAgents() { return totalAgents; }
        public void setTotalAgents(long totalAgents) { this.totalAgents = totalAgents; }
        
        public long getActiveAgents() { return activeAgents; }
        public void setActiveAgents(long activeAgents) { this.activeAgents = activeAgents; }
        
        public long getInactiveAgents() { return inactiveAgents; }
        public void setInactiveAgents(long inactiveAgents) { this.inactiveAgents = inactiveAgents; }
        
        public long getTotalManagedClients() { return totalManagedClients; }
        public void setTotalManagedClients(long totalManagedClients) { this.totalManagedClients = totalManagedClients; }
    }
}
