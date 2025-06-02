package com.ebanking.repository;

import com.ebanking.entity.CryptoWallet;
import com.ebanking.entity.CryptoWalletBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for CryptoWalletBalance entity
 */
@Repository
public interface CryptoWalletBalanceRepository extends JpaRepository<CryptoWalletBalance, Long> {

    /**
     * Find crypto wallet balance by wallet and crypto type
     * @param cryptoWallet the crypto wallet
     * @param cryptoType the cryptocurrency type
     * @return Optional containing the balance if found
     */
    Optional<CryptoWalletBalance> findByCryptoWalletAndCryptoType(CryptoWallet cryptoWallet, String cryptoType);

    /**
     * Find all balances for a specific crypto wallet
     * @param cryptoWallet the crypto wallet
     * @return list of crypto wallet balances
     */
    List<CryptoWalletBalance> findByCryptoWallet(CryptoWallet cryptoWallet);

    /**
     * Find all balances for a specific crypto wallet with non-zero balance
     * @param cryptoWallet the crypto wallet
     * @return list of crypto wallet balances with balance > 0
     */
    @Query("SELECT cwb FROM CryptoWalletBalance cwb WHERE cwb.cryptoWallet = :cryptoWallet AND cwb.balance > 0")
    List<CryptoWalletBalance> findByCryptoWalletWithNonZeroBalance(@Param("cryptoWallet") CryptoWallet cryptoWallet);

    /**
     * Find balance by wallet ID and crypto type
     * @param walletId the crypto wallet ID
     * @param cryptoType the cryptocurrency type
     * @return Optional containing the balance if found
     */
    @Query("SELECT cwb FROM CryptoWalletBalance cwb WHERE cwb.cryptoWallet.id = :walletId AND cwb.cryptoType = :cryptoType")
    Optional<CryptoWalletBalance> findByWalletIdAndCryptoType(@Param("walletId") Long walletId, @Param("cryptoType") String cryptoType);

    /**
     * Find all balances for a specific crypto type across all wallets
     * @param cryptoType the cryptocurrency type
     * @return list of crypto wallet balances for the specified type
     */
    List<CryptoWalletBalance> findByCryptoType(String cryptoType);

    /**
     * Check if a balance exists for wallet and crypto type
     * @param cryptoWallet the crypto wallet
     * @param cryptoType the cryptocurrency type
     * @return true if balance exists, false otherwise
     */
    boolean existsByCryptoWalletAndCryptoType(CryptoWallet cryptoWallet, String cryptoType);

    /**
     * Get total balance for a specific crypto type across all wallets
     * @param cryptoType the cryptocurrency type
     * @return total balance for the crypto type
     */
    @Query("SELECT COALESCE(SUM(cwb.balance), 0) FROM CryptoWalletBalance cwb WHERE cwb.cryptoType = :cryptoType")
    BigDecimal getTotalBalanceByCryptoType(@Param("cryptoType") String cryptoType);

    /**
     * Count wallets holding a specific cryptocurrency
     * @param cryptoType the cryptocurrency type
     * @return number of wallets holding the crypto
     */
    @Query("SELECT COUNT(DISTINCT cwb.cryptoWallet) FROM CryptoWalletBalance cwb WHERE cwb.cryptoType = :cryptoType AND cwb.balance > 0")
    long countWalletsHoldingCrypto(@Param("cryptoType") String cryptoType);

    /**
     * Find balances by client ID
     * @param clientId the client ID
     * @return list of crypto wallet balances for the client
     */
    @Query("SELECT cwb FROM CryptoWalletBalance cwb WHERE cwb.cryptoWallet.client.id = :clientId")
    List<CryptoWalletBalance> findByClientId(@Param("clientId") Long clientId);

    /**
     * Delete all balances for a specific crypto wallet
     * @param cryptoWallet the crypto wallet
     */
    void deleteByCryptoWallet(CryptoWallet cryptoWallet);
}
