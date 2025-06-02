package com.ebanking.repository;

import com.ebanking.entity.Client;
import com.ebanking.entity.CryptoWallet;
import com.ebanking.entity.WalletStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for CryptoWallet entity
 */
@Repository
public interface CryptoWalletRepository extends JpaRepository<CryptoWallet, Long> {

    /**
     * Find crypto wallet by wallet address
     * @param walletAddress the wallet address
     * @return Optional containing the crypto wallet if found
     */
    Optional<CryptoWallet> findByWalletAddress(String walletAddress);

    /**
     * Find crypto wallet by client
     * @param client the client
     * @return Optional containing the crypto wallet if found
     */
    Optional<CryptoWallet> findByClient(Client client);

    /**
     * Find crypto wallet by client ID
     * @param clientId the client ID
     * @return Optional containing the crypto wallet if found
     */
    @Query("SELECT cw FROM CryptoWallet cw WHERE cw.client.clientId = :clientId")
    Optional<CryptoWallet> findByClientId(@Param("clientId") String clientId);

    /**
     * Find crypto wallets by status
     * @param status the wallet status
     * @return list of crypto wallets with the specified status
     */
    List<CryptoWallet> findByStatus(WalletStatus status);

    /**
     * Check if wallet address exists
     * @param walletAddress the wallet address
     * @return true if exists, false otherwise
     */
    boolean existsByWalletAddress(String walletAddress);

    /**
     * Check if client already has a crypto wallet
     * @param client the client
     * @return true if client has a wallet, false otherwise
     */
    boolean existsByClient(Client client);

    /**
     * Count crypto wallets by status
     * @param status the wallet status
     * @return number of wallets with the specified status
     */
    long countByStatus(WalletStatus status);

    /**
     * Find all active crypto wallets
     * @return list of active crypto wallets
     */
    @Query("SELECT cw FROM CryptoWallet cw WHERE cw.status = 'ACTIVE'")
    List<CryptoWallet> findAllActiveWallets();
}
