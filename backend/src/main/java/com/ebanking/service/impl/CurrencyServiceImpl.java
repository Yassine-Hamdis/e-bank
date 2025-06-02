package com.ebanking.service.impl;

import com.ebanking.dto.request.CurrencyCreateRequest;
import com.ebanking.dto.response.CurrencyDTO;
import com.ebanking.entity.Currency;
import com.ebanking.repository.CurrencyRepository;
import com.ebanking.service.BinanceService;
import com.ebanking.service.CurrencyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of CurrencyService for currency management operations
 */
@Service
@Transactional
public class CurrencyServiceImpl implements CurrencyService {

    private static final Logger logger = LoggerFactory.getLogger(CurrencyServiceImpl.class);

    @Autowired
    private CurrencyRepository currencyRepository;

    @Autowired
    private BinanceService binanceService;

    @Override
    @Transactional(readOnly = true)
    public List<CurrencyDTO> getAllCurrencies() {
        logger.debug("Fetching all currencies");
        List<Currency> currencies = currencyRepository.findAllActiveCurrenciesOrderBySymbol();
        return currencies.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CurrencyDTO> getActiveCurrencies() {
        logger.debug("Fetching active currencies");
        List<Currency> currencies = currencyRepository.findByIsActiveTrue();
        return currencies.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CurrencyDTO getCurrencyBySymbol(String symbol) {
        logger.debug("Fetching currency by symbol: {}", symbol);
        Currency currency = currencyRepository.findBySymbolIgnoreCase(symbol)
                .orElseThrow(() -> new RuntimeException("Currency not found with symbol: " + symbol));
        return convertToDTO(currency);
    }

    @Override
    public CurrencyDTO createOrUpdateCurrency(CurrencyCreateRequest request) {
        logger.info("Creating or updating currency: {}", request.getSymbol());

        Optional<Currency> existingCurrency = currencyRepository.findBySymbolIgnoreCase(request.getSymbol());
        
        Currency currency;
        if (existingCurrency.isPresent()) {
            // Update existing currency
            currency = existingCurrency.get();
            logger.info("Updating existing currency: {}", currency.getSymbol());
        } else {
            // Create new currency
            currency = new Currency();
            currency.setSymbol(request.getSymbol().toUpperCase());
            logger.info("Creating new currency: {}", request.getSymbol());
        }

        // Update currency fields
        currency.setName(request.getName());
        currency.setCurrentPrice(request.getCurrentPrice());
        currency.setPriceChange24h(request.getPriceChange24h());
        currency.setMarketCap(request.getMarketCap());
        currency.setVolume24h(request.getVolume24h());
        currency.setIsActive(request.getIsActive());
        currency.setIsManual(true); // Mark as manually added/updated
        currency.setLastUpdated(LocalDateTime.now());

        Currency savedCurrency = currencyRepository.save(currency);
        logger.info("Currency saved successfully: {}", savedCurrency.getSymbol());
        
        return convertToDTO(savedCurrency);
    }

    @Override
    public CurrencyDTO updateCurrencyStatus(String symbol, Boolean isActive) {
        logger.info("Updating currency status for symbol: {} to {}", symbol, isActive);
        
        Currency currency = currencyRepository.findBySymbolIgnoreCase(symbol)
                .orElseThrow(() -> new RuntimeException("Currency not found with symbol: " + symbol));
        
        currency.setIsActive(isActive);
        currency.setUpdatedAt(LocalDateTime.now());
        
        Currency savedCurrency = currencyRepository.save(currency);
        logger.info("Currency status updated successfully for symbol: {}", symbol);
        
        return convertToDTO(savedCurrency);
    }

    @Override
    public void deleteCurrency(String symbol) {
        logger.info("Deleting currency with symbol: {}", symbol);
        
        Currency currency = currencyRepository.findBySymbolIgnoreCase(symbol)
                .orElseThrow(() -> new RuntimeException("Currency not found with symbol: " + symbol));
        
        if (!currency.getIsManual()) {
            throw new RuntimeException("Cannot delete Binance currency. Only manually added currencies can be deleted.");
        }
        
        currencyRepository.delete(currency);
        logger.info("Currency deleted successfully: {}", symbol);
    }

    @Override
    public List<CurrencyDTO> refreshBinanceCurrencies() {
        logger.info("Refreshing currencies from Binance API");
        
        List<CurrencyDTO> updatedCurrencies = new ArrayList<>();
        
        try {
            // Check if Binance API is available
            if (!binanceService.isApiAvailable()) {
                logger.warn("Binance API is not available, skipping refresh");
                return updatedCurrencies;
            }
            
            // Fetch popular cryptocurrencies from Binance
            List<CurrencyDTO> binanceCurrencies = binanceService.fetchPopularCryptocurrencies();
            
            for (CurrencyDTO binanceCurrency : binanceCurrencies) {
                try {
                    Optional<Currency> existingCurrency = currencyRepository.findBySymbolIgnoreCase(binanceCurrency.getSymbol());
                    
                    Currency currency;
                    if (existingCurrency.isPresent() && !existingCurrency.get().getIsManual()) {
                        // Update existing Binance currency
                        currency = existingCurrency.get();
                    } else if (!existingCurrency.isPresent()) {
                        // Create new Binance currency
                        currency = new Currency();
                        currency.setSymbol(binanceCurrency.getSymbol());
                        currency.setIsManual(false);
                        currency.setIsActive(true);
                    } else {
                        // Skip manually added currencies
                        continue;
                    }
                    
                    // Update with Binance data
                    currency.setName(binanceCurrency.getName());
                    currency.setCurrentPrice(binanceCurrency.getCurrentPrice());
                    currency.setPriceChange24h(binanceCurrency.getPriceChange24h());
                    currency.setVolume24h(binanceCurrency.getVolume24h());
                    currency.setLastUpdated(LocalDateTime.now());
                    
                    Currency savedCurrency = currencyRepository.save(currency);
                    updatedCurrencies.add(convertToDTO(savedCurrency));
                    
                } catch (Exception e) {
                    logger.error("Failed to update currency: {}", binanceCurrency.getSymbol(), e);
                }
            }
            
            logger.info("Successfully refreshed {} currencies from Binance", updatedCurrencies.size());
            
        } catch (Exception e) {
            logger.error("Failed to refresh currencies from Binance", e);
            throw new RuntimeException("Failed to refresh currencies from Binance: " + e.getMessage(), e);
        }
        
        return updatedCurrencies;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CurrencyDTO> getCurrenciesBySource(Boolean isManual) {
        logger.debug("Fetching currencies by source - isManual: {}", isManual);
        List<Currency> currencies = currencyRepository.findActiveByIsManual(isManual);
        return currencies.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CurrencyStatistics getCurrencyStatistics() {
        logger.debug("Fetching currency statistics");
        
        long totalCurrencies = currencyRepository.count();
        long activeCurrencies = currencyRepository.countActiveCurrencies();
        long inactiveCurrencies = totalCurrencies - activeCurrencies;
        long manualCurrencies = currencyRepository.countManualCurrencies();
        long binanceCurrencies = currencyRepository.countBinanceCurrencies();
        
        return new CurrencyStatistics(totalCurrencies, activeCurrencies, inactiveCurrencies, 
                                    manualCurrencies, binanceCurrencies);
    }

    /**
     * Convert Currency entity to CurrencyDTO
     */
    private CurrencyDTO convertToDTO(Currency currency) {
        CurrencyDTO dto = new CurrencyDTO();
        dto.setId(currency.getId());
        dto.setSymbol(currency.getSymbol());
        dto.setName(currency.getName());
        dto.setCurrentPrice(currency.getCurrentPrice());
        dto.setPriceChange24h(currency.getPriceChange24h());
        dto.setMarketCap(currency.getMarketCap());
        dto.setVolume24h(currency.getVolume24h());
        dto.setLastUpdated(currency.getLastUpdated());
        dto.setIsActive(currency.getIsActive());
        dto.setIsManual(currency.getIsManual());
        dto.setCreatedAt(currency.getCreatedAt());
        dto.setUpdatedAt(currency.getUpdatedAt());
        return dto;
    }
}
