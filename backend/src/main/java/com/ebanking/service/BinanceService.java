package com.ebanking.service;

import com.ebanking.dto.response.CurrencyDTO;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service interface for Binance API integration
 */
public interface BinanceService {

    /**
     * Fetch current prices for popular cryptocurrencies from Binance
     * @return list of currencies with current prices
     */
    List<CurrencyDTO> fetchPopularCryptocurrencies();

    /**
     * Fetch price for a specific cryptocurrency from Binance
     * @param symbol the cryptocurrency symbol (e.g., "BTCUSDT")
     * @return currency with current price
     * @throws RuntimeException if currency not found or API error
     */
    CurrencyDTO fetchCryptocurrencyPrice(String symbol);

    /**
     * Fetch 24h ticker statistics for a specific symbol
     * @param symbol the cryptocurrency symbol (e.g., "BTCUSDT")
     * @return currency with detailed statistics
     * @throws RuntimeException if currency not found or API error
     */
    CurrencyDTO fetchCryptocurrencyTicker(String symbol);

    /**
     * Check if Binance API is available
     * @return true if API is reachable
     */
    boolean isApiAvailable();

    /**
     * Get list of supported cryptocurrency symbols from Binance
     * @return list of supported symbols
     */
    List<String> getSupportedSymbols();

    /**
     * Get MAD to USD exchange rate
     * This method fetches the current MAD/USD exchange rate for currency conversion
     * @return MAD to USD exchange rate (1 MAD = X USD)
     * @throws RuntimeException if exchange rate cannot be fetched
     */
    BigDecimal getMadToUsdRate();
}
