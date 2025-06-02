package com.ebanking.repository;

import com.ebanking.entity.BankAgent;
import com.ebanking.entity.Client;
import com.ebanking.entity.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Client entity
 */
@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    /**
     * Find client by client ID
     * @param clientId the client ID
     * @return Optional containing the client if found
     */
    Optional<Client> findByClientId(String clientId);

    /**
     * Find clients by managing agent
     * @param managingAgent the bank agent
     * @return list of clients managed by the agent
     */
    List<Client> findByManagingAgent(BankAgent managingAgent);

    /**
     * Find clients by managing agent ID
     * @param agentId the bank agent ID
     * @return list of clients managed by the agent
     */
    @Query("SELECT c FROM Client c WHERE c.managingAgent.id = :agentId")
    List<Client> findByManagingAgentId(@Param("agentId") Long agentId);

    /**
     * Find clients by identification number
     * @param identificationNumber the identification number
     * @return Optional containing the client if found
     */
    Optional<Client> findByIdentificationNumber(String identificationNumber);

    /**
     * Find clients by national ID
     * @param nationalId the national ID
     * @return Optional containing the client if found
     */
    Optional<Client> findByNationalId(String nationalId);

    /**
     * Find clients by status
     * @param status the client status
     * @return list of clients with the specified status
     */
    List<Client> findByStatus(UserStatus status);

    /**
     * Find clients enrolled on a specific date
     * @param enrollmentDate the enrollment date
     * @return list of clients enrolled on the specified date
     */
    List<Client> findByEnrollmentDate(LocalDate enrollmentDate);

    /**
     * Find clients enrolled between dates
     * @param startDate the start date
     * @param endDate the end date
     * @return list of clients enrolled between the dates
     */
    @Query("SELECT c FROM Client c WHERE c.enrollmentDate BETWEEN :startDate AND :endDate ORDER BY c.enrollmentDate DESC")
    List<Client> findByEnrollmentDateBetween(@Param("startDate") LocalDate startDate, 
                                           @Param("endDate") LocalDate endDate);

    /**
     * Check if client ID exists
     * @param clientId the client ID
     * @return true if exists, false otherwise
     */
    boolean existsByClientId(String clientId);

    /**
     * Check if identification number exists
     * @param identificationNumber the identification number
     * @return true if exists, false otherwise
     */
    boolean existsByIdentificationNumber(String identificationNumber);

    /**
     * Check if national ID exists
     * @param nationalId the national ID
     * @return true if exists, false otherwise
     */
    boolean existsByNationalId(String nationalId);

    /**
     * Count clients by managing agent
     * @param managingAgent the bank agent
     * @return number of clients managed by the agent
     */
    long countByManagingAgent(BankAgent managingAgent);

    /**
     * Count clients by status
     * @param status the client status
     * @return number of clients with the specified status
     */
    long countByStatus(UserStatus status);

    /**
     * Find clients with accounts
     * @return list of clients who have accounts
     */
    @Query("SELECT DISTINCT c FROM Client c JOIN c.accounts")
    List<Client> findClientsWithAccounts();

    /**
     * Find clients with crypto wallets
     * @return list of clients who have crypto wallets
     */
    @Query("SELECT c FROM Client c WHERE c.cryptoWallet IS NOT NULL")
    List<Client> findClientsWithCryptoWallets();

    /**
     * Find recent clients (enrolled in last 30 days)
     * @param thirtyDaysAgo the date 30 days ago
     * @return list of recently enrolled clients
     */
    @Query("SELECT c FROM Client c WHERE c.enrollmentDate >= :thirtyDaysAgo ORDER BY c.enrollmentDate DESC")
    List<Client> findRecentClients(@Param("thirtyDaysAgo") LocalDate thirtyDaysAgo);
}
