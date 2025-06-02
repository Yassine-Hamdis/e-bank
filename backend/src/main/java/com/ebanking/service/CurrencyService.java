package com.ebanking.service;

import com.ebanking.dto.request.CurrencyCreateRequest;
import com.ebanking.dto.response.CurrencyDTO;

import java.util.List;

/**
 * Service interface for currency management operations
 */
public interface CurrencyService {

    /**
     * Get all supported currencies with current rates
     * Combines both Binance data and manually added currencies
     * @return list of all currencies
     */
    List<CurrencyDTO> getAllCurrencies();

    /**
     * Get all active currencies
     * @return list of active currencies
     */
    List<CurrencyDTO> getActiveCurrencies();

    /**
     * Get currency by symbol
     * @param symbol the currency symbol
     * @return currency DTO if found
     * @throws RuntimeException if currency not found
     */
    CurrencyDTO getCurrencyBySymbol(String symbol);

    /**
     * Create or update a currency manually
     * @param request the currency creation request
     * @return created/updated currency DTO
     */
    CurrencyDTO createOrUpdateCurrency(CurrencyCreateRequest request);

    /**
     * Update currency status (active/inactive)
     * @param symbol the currency symbol
     * @param isActive the new status
     * @return updated currency DTO
     * @throws RuntimeException if currency not found
     */
    CurrencyDTO updateCurrencyStatus(String symbol, Boolean isActive);

    /**
     * Delete a currency (only manually added currencies can be deleted)
     * @param symbol the currency symbol
     * @throws RuntimeException if currency not found or is from Binance
     */
    void deleteCurrency(String symbol);

    /**
     * Refresh currencies from Binance API
     * Updates existing Binance currencies and adds new popular ones
     * @return list of updated currencies from Binance
     */
    List<CurrencyDTO> refreshBinanceCurrencies();

    /**
     * Get currencies by source (manual or Binance)
     * @param isManual true for manually added, false for Binance
     * @return list of currencies
     */
    List<CurrencyDTO> getCurrenciesBySource(Boolean isManual);

    /**
     * Get currency statistics
     * @return statistics about currencies
     */
    CurrencyStatistics getCurrencyStatistics();

    /**
     * Inner class for currency statistics
     */
    class CurrencyStatistics {
        private long totalCurrencies;
        private long activeCurrencies;
        private long inactiveCurrencies;
        private long manualCurrencies;
        private long binanceCurrencies;

        public CurrencyStatistics(long totalCurrencies, long activeCurrencies, long inactiveCurrencies, 
                                long manualCurrencies, long binanceCurrencies) {
            this.totalCurrencies = totalCurrencies;
            this.activeCurrencies = activeCurrencies;
            this.inactiveCurrencies = inactiveCurrencies;
            this.manualCurrencies = manualCurrencies;
            this.binanceCurrencies = binanceCurrencies;
        }

        // Getters and setters
        public long getTotalCurrencies() { return totalCurrencies; }
        public void setTotalCurrencies(long totalCurrencies) { this.totalCurrencies = totalCurrencies; }
        
        public long getActiveCurrencies() { return activeCurrencies; }
        public void setActiveCurrencies(long activeCurrencies) { this.activeCurrencies = activeCurrencies; }
        
        public long getInactiveCurrencies() { return inactiveCurrencies; }
        public void setInactiveCurrencies(long inactiveCurrencies) { this.inactiveCurrencies = inactiveCurrencies; }
        
        public long getManualCurrencies() { return manualCurrencies; }
        public void setManualCurrencies(long manualCurrencies) { this.manualCurrencies = manualCurrencies; }
        
        public long getBinanceCurrencies() { return binanceCurrencies; }
        public void setBinanceCurrencies(long binanceCurrencies) { this.binanceCurrencies = binanceCurrencies; }
    }
}
