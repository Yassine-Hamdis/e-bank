package com.ebanking.repository;

import com.ebanking.entity.Account;
import com.ebanking.entity.AccountType;
import com.ebanking.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Account entity
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    /**
     * Find account by account ID
     * @param accountId the account ID
     * @return Optional containing the account if found
     */
    Optional<Account> findByAccountId(String accountId);

    /**
     * Find all accounts for a specific client
     * @param client the client
     * @return list of accounts for the client
     */
    List<Account> findByClient(Client client);

    /**
     * Find accounts by client and account type
     * @param client the client
     * @param accountType the account type
     * @return list of accounts for the client with the specified type
     */
    List<Account> findByClientAndAccountType(Client client, AccountType accountType);

    /**
     * Find all accounts for a specific client ID
     * @param clientId the client ID
     * @return list of accounts for the client
     */
    @Query("SELECT a FROM Account a WHERE a.client.clientId = :clientId")
    List<Account> findByClientId(@Param("clientId") String clientId);

    /**
     * Find accounts by status
     * @param status the account status
     * @return list of accounts with the specified status
     */
    @Query("SELECT a FROM Account a WHERE a.status = :status")
    List<Account> findByStatus(@Param("status") String status);

    /**
     * Find accounts by currency
     * @param currency the currency
     * @return list of accounts with the specified currency
     */
    List<Account> findByCurrency(String currency);

    /**
     * Check if account ID exists
     * @param accountId the account ID
     * @return true if exists, false otherwise
     */
    boolean existsByAccountId(String accountId);

    /**
     * Count accounts for a specific client
     * @param client the client
     * @return number of accounts for the client
     */
    long countByClient(Client client);

    /**
     * Find accounts with balance greater than specified amount
     * @param amount the minimum balance
     * @return list of accounts with balance greater than amount
     */
    @Query("SELECT a FROM Account a WHERE a.balance > :amount")
    List<Account> findAccountsWithBalanceGreaterThan(@Param("amount") java.math.BigDecimal amount);
}
