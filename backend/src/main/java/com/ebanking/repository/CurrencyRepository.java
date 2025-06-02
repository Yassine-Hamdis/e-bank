package com.ebanking.repository;

import com.ebanking.entity.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Currency entity
 */
@Repository
public interface CurrencyRepository extends JpaRepository<Currency, Long> {

    /**
     * Find currency by symbol
     * @param symbol the currency symbol (e.g., "BTC", "ETH")
     * @return Optional containing the currency if found
     */
    Optional<Currency> findBySymbol(String symbol);

    /**
     * Find currency by symbol ignoring case
     * @param symbol the currency symbol
     * @return Optional containing the currency if found
     */
    Optional<Currency> findBySymbolIgnoreCase(String symbol);

    /**
     * Find all active currencies
     * @return list of active currencies
     */
    List<Currency> findByIsActiveTrue();

    /**
     * Find all active currencies ordered by symbol
     * @return list of active currencies ordered by symbol
     */
    @Query("SELECT c FROM Currency c WHERE c.isActive = true ORDER BY c.symbol ASC")
    List<Currency> findAllActiveCurrenciesOrderBySymbol();

    /**
     * Find currencies by manual flag
     * @param isManual true for manually added currencies, false for Binance currencies
     * @return list of currencies
     */
    List<Currency> findByIsManual(Boolean isManual);

    /**
     * Find active currencies by manual flag
     * @param isManual true for manually added currencies, false for Binance currencies
     * @return list of active currencies
     */
    @Query("SELECT c FROM Currency c WHERE c.isActive = true AND c.isManual = :isManual ORDER BY c.symbol ASC")
    List<Currency> findActiveByIsManual(@Param("isManual") Boolean isManual);

    /**
     * Check if currency exists by symbol
     * @param symbol the currency symbol
     * @return true if currency exists
     */
    boolean existsBySymbol(String symbol);

    /**
     * Check if currency exists by symbol ignoring case
     * @param symbol the currency symbol
     * @return true if currency exists
     */
    boolean existsBySymbolIgnoreCase(String symbol);

    /**
     * Count active currencies
     * @return number of active currencies
     */
    @Query("SELECT COUNT(c) FROM Currency c WHERE c.isActive = true")
    long countActiveCurrencies();

    /**
     * Count manual currencies
     * @return number of manually added currencies
     */
    @Query("SELECT COUNT(c) FROM Currency c WHERE c.isManual = true")
    long countManualCurrencies();

    /**
     * Count Binance currencies
     * @return number of Binance currencies
     */
    @Query("SELECT COUNT(c) FROM Currency c WHERE c.isManual = false")
    long countBinanceCurrencies();
}
