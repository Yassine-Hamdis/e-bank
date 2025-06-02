package com.ebanking.repository;

import com.ebanking.entity.BankAgent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for BankAgent entity
 * Provides CRUD operations and custom queries for BankAgent management
 */
@Repository
public interface BankAgentRepository extends JpaRepository<BankAgent, Long> {

    /**
     * Find bank agent by employee ID
     * @param employeeId the employee ID to search for
     * @return Optional containing the bank agent if found
     */
    Optional<BankAgent> findByEmployeeId(String employeeId);

    /**
     * Check if employee ID exists
     * @param employeeId the employee ID to check
     * @return true if employee ID exists, false otherwise
     */
    boolean existsByEmployeeId(String employeeId);

    /**
     * Find bank agents by branch
     * @param branch the branch to search for
     * @return list of bank agents in the specified branch
     */
    List<BankAgent> findByBranch(String branch);

    /**
     * Find bank agents by position
     * @param position the position to search for
     * @return list of bank agents with the specified position
     */
    List<BankAgent> findByPosition(String position);

    /**
     * Find bank agents by branch and position
     * @param branch the branch
     * @param position the position
     * @return list of bank agents in the specified branch and position
     */
    List<BankAgent> findByBranchAndPosition(String branch, String position);

    /**
     * Count bank agents by branch
     * @param branch the branch
     * @return count of bank agents in the specified branch
     */
    long countByBranch(String branch);

    /**
     * Count bank agents by position
     * @param position the position
     * @return count of bank agents with the specified position
     */
    long countByPosition(String position);

    /**
     * Find bank agents with managed clients count
     * @return list of bank agents with their managed clients count
     */
    @Query("SELECT ba FROM BankAgent ba LEFT JOIN FETCH ba.managedClients")
    List<BankAgent> findAllWithManagedClients();

    /**
     * Find bank agents by branch with managed clients count
     * @param branch the branch
     * @return list of bank agents in the specified branch with their managed clients
     */
    @Query("SELECT ba FROM BankAgent ba LEFT JOIN FETCH ba.managedClients WHERE ba.branch = :branch")
    List<BankAgent> findByBranchWithManagedClients(@Param("branch") String branch);

    /**
     * Count managed clients by bank agent
     * @param agentId the bank agent ID
     * @return count of clients managed by the specified agent
     */
    @Query("SELECT COUNT(c) FROM Client c WHERE c.managingAgent.id = :agentId")
    long countManagedClientsByAgentId(@Param("agentId") Long agentId);
}
