package com.ebanking.repository;

import com.ebanking.entity.Account;
import com.ebanking.entity.BankAgent;
import com.ebanking.entity.Transaction;
import com.ebanking.entity.TransactionStatus;
import com.ebanking.entity.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Transaction entity
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    /**
     * Find transaction by transaction ID
     * @param transactionId the transaction ID
     * @return Optional containing the transaction if found
     */
    Optional<Transaction> findByTransactionId(String transactionId);

    /**
     * Find transactions by status
     * @param status the transaction status
     * @return list of transactions with the specified status
     */
    List<Transaction> findByStatus(TransactionStatus status);

    /**
     * Find transactions by type
     * @param type the transaction type
     * @return list of transactions with the specified type
     */
    List<Transaction> findByType(TransactionType type);

    /**
     * Find transactions by from account
     * @param fromAccount the from account
     * @return list of transactions from the specified account
     */
    List<Transaction> findByFromAccount(Account fromAccount);

    /**
     * Find transactions by to account
     * @param toAccount the to account
     * @return list of transactions to the specified account
     */
    List<Transaction> findByToAccount(Account toAccount);

    /**
     * Find transactions verified by a specific agent
     * @param agent the bank agent
     * @return list of transactions verified by the agent
     */
    List<Transaction> findByVerifiedByAgent(BankAgent agent);

    /**
     * Find pending transactions that need verification
     * @return list of pending transactions
     */
    @Query("SELECT t FROM Transaction t WHERE t.status = 'PENDING' ORDER BY t.date ASC")
    List<Transaction> findPendingTransactions();

    /**
     * Find transactions by date range
     * @param startDate the start date
     * @param endDate the end date
     * @return list of transactions within the date range
     */
    @Query("SELECT t FROM Transaction t WHERE t.date BETWEEN :startDate AND :endDate ORDER BY t.date DESC")
    List<Transaction> findByDateRange(@Param("startDate") LocalDateTime startDate,
                                     @Param("endDate") LocalDateTime endDate);

    /**
     * Find transactions for a specific client's accounts
     * @param clientId the client ID
     * @return list of transactions for the client's accounts
     */
    @Query("SELECT t FROM Transaction t WHERE t.fromAccount.client.clientId = :clientId OR t.toAccount.client.clientId = :clientId ORDER BY t.date DESC")
    List<Transaction> findByClientId(@Param("clientId") String clientId);

    /**
     * Find transactions for a specific client's accounts by type
     * @param clientId the client ID
     * @param type the transaction type
     * @return list of transactions for the client's accounts of the specified type
     */
    @Query("SELECT t FROM Transaction t WHERE (t.fromAccount.client.clientId = :clientId OR t.toAccount.client.clientId = :clientId) AND t.type = :type ORDER BY t.date DESC")
    List<Transaction> findByClientIdAndType(@Param("clientId") String clientId, @Param("type") TransactionType type);

    /**
     * Check if transaction ID exists
     * @param transactionId the transaction ID
     * @return true if exists, false otherwise
     */
    boolean existsByTransactionId(String transactionId);

    /**
     * Count transactions by status
     * @param status the transaction status
     * @return number of transactions with the specified status
     */
    long countByStatus(TransactionStatus status);

    /**
     * Find recent transactions (last 30 days)
     * @return list of recent transactions
     */
    @Query("SELECT t FROM Transaction t WHERE t.date >= :thirtyDaysAgo ORDER BY t.date DESC")
    List<Transaction> findRecentTransactions(@Param("thirtyDaysAgo") LocalDateTime thirtyDaysAgo);

    /**
     * Find pending transactions for clients managed by a specific agent
     * @param agentId the bank agent ID
     * @return list of pending transactions for the agent's managed clients
     */
    @Query("SELECT t FROM Transaction t WHERE t.status = 'PENDING' AND " +
           "((t.fromAccount IS NOT NULL AND t.fromAccount.client.managingAgent.id = :agentId) OR " +
           "(t.toAccount IS NOT NULL AND t.toAccount.client.managingAgent.id = :agentId)) " +
           "ORDER BY t.date ASC")
    List<Transaction> findPendingTransactionsByManagedClients(@Param("agentId") Long agentId);

    /**
     * Find all transactions for clients managed by a specific agent
     * @param agentId the bank agent ID
     * @return list of all transactions for the agent's managed clients
     */
    @Query("SELECT t FROM Transaction t WHERE " +
           "((t.fromAccount IS NOT NULL AND t.fromAccount.client.managingAgent.id = :agentId) OR " +
           "(t.toAccount IS NOT NULL AND t.toAccount.client.managingAgent.id = :agentId)) " +
           "ORDER BY t.date DESC")
    List<Transaction> findTransactionsByManagedClients(@Param("agentId") Long agentId);

    /**
     * Find transactions by status for clients managed by a specific agent
     * @param agentId the bank agent ID
     * @param status the transaction status
     * @return list of transactions with the specified status for the agent's managed clients
     */
    @Query("SELECT t FROM Transaction t WHERE t.status = :status AND " +
           "((t.fromAccount IS NOT NULL AND t.fromAccount.client.managingAgent.id = :agentId) OR " +
           "(t.toAccount IS NOT NULL AND t.toAccount.client.managingAgent.id = :agentId)) " +
           "ORDER BY t.date DESC")
    List<Transaction> findTransactionsByManagedClientsAndStatus(@Param("agentId") Long agentId, @Param("status") TransactionStatus status);

    // ==================== DEPOSIT STATISTICS METHODS ====================

    /**
     * Count total deposits made by a specific agent
     * @param agentId the bank agent ID
     * @return number of deposits made by the agent
     */
    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.type = 'DEPOSIT' AND t.verifiedByAgent.id = :agentId")
    long countDepositsByAgent(@Param("agentId") Long agentId);

    /**
     * Sum total deposit amount made by a specific agent
     * @param agentId the bank agent ID
     * @return total amount of deposits made by the agent
     */
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.type = 'DEPOSIT' AND t.verifiedByAgent.id = :agentId")
    BigDecimal sumDepositAmountByAgent(@Param("agentId") Long agentId);

    /**
     * Count deposits made by a specific agent within a date range
     * @param agentId the bank agent ID
     * @param startDate the start date
     * @param endDate the end date
     * @return number of deposits made by the agent within the date range
     */
    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.type = 'DEPOSIT' AND t.verifiedByAgent.id = :agentId " +
           "AND t.date >= :startDate AND t.date <= :endDate")
    long countDepositsByAgentAndDateRange(@Param("agentId") Long agentId,
                                         @Param("startDate") LocalDateTime startDate,
                                         @Param("endDate") LocalDateTime endDate);

    /**
     * Sum deposit amount made by a specific agent within a date range
     * @param agentId the bank agent ID
     * @param startDate the start date
     * @param endDate the end date
     * @return total amount of deposits made by the agent within the date range
     */
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.type = 'DEPOSIT' AND t.verifiedByAgent.id = :agentId " +
           "AND t.date >= :startDate AND t.date <= :endDate")
    BigDecimal sumDepositAmountByAgentAndDateRange(@Param("agentId") Long agentId,
                                                  @Param("startDate") LocalDateTime startDate,
                                                  @Param("endDate") LocalDateTime endDate);

    /**
     * Find largest deposit amount made by a specific agent
     * @param agentId the bank agent ID
     * @return largest deposit amount made by the agent
     */
    @Query("SELECT COALESCE(MAX(t.amount), 0) FROM Transaction t WHERE t.type = 'DEPOSIT' AND t.verifiedByAgent.id = :agentId")
    BigDecimal findLargestDepositByAgent(@Param("agentId") Long agentId);

    /**
     * Find smallest deposit amount made by a specific agent
     * @param agentId the bank agent ID
     * @return smallest deposit amount made by the agent
     */
    @Query("SELECT COALESCE(MIN(t.amount), 0) FROM Transaction t WHERE t.type = 'DEPOSIT' AND t.verifiedByAgent.id = :agentId")
    BigDecimal findSmallestDepositByAgent(@Param("agentId") Long agentId);

    /**
     * Find last deposit date made by a specific agent
     * @param agentId the bank agent ID
     * @return last deposit date made by the agent
     */
    @Query("SELECT MAX(t.date) FROM Transaction t WHERE t.type = 'DEPOSIT' AND t.verifiedByAgent.id = :agentId")
    LocalDateTime findLastDepositDateByAgent(@Param("agentId") Long agentId);

    /**
     * Find all deposits made by a specific agent
     * @param agentId the bank agent ID
     * @return list of deposits made by the agent
     */
    @Query("SELECT t FROM Transaction t WHERE t.type = 'DEPOSIT' AND t.verifiedByAgent.id = :agentId ORDER BY t.date DESC")
    List<Transaction> findDepositsByAgent(@Param("agentId") Long agentId);
}
