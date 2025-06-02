package com.ebanking.service;

import com.ebanking.dto.request.CurrencyCreateRequest;
import com.ebanking.dto.response.CurrencyDTO;
import com.ebanking.entity.Currency;
import com.ebanking.repository.CurrencyRepository;
import com.ebanking.service.impl.CurrencyServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CurrencyService
 */
@ExtendWith(MockitoExtension.class)
class CurrencyServiceTest {

    @Mock
    private CurrencyRepository currencyRepository;

    @Mock
    private BinanceService binanceService;

    @InjectMocks
    private CurrencyServiceImpl currencyService;

    private Currency testCurrency;
    private CurrencyCreateRequest testRequest;

    @BeforeEach
    void setUp() {
        testCurrency = new Currency();
        testCurrency.setId(1L);
        testCurrency.setSymbol("BTC");
        testCurrency.setName("Bitcoin");
        testCurrency.setCurrentPrice(new BigDecimal("45000.00"));
        testCurrency.setPriceChange24h(new BigDecimal("2.5"));
        testCurrency.setIsActive(true);
        testCurrency.setIsManual(true);
        testCurrency.setLastUpdated(LocalDateTime.now());

        testRequest = new CurrencyCreateRequest();
        testRequest.setSymbol("BTC");
        testRequest.setName("Bitcoin");
        testRequest.setCurrentPrice(new BigDecimal("45000.00"));
        testRequest.setPriceChange24h(new BigDecimal("2.5"));
        testRequest.setIsActive(true);
    }

    @Test
    void testGetAllCurrencies() {
        // Given
        List<Currency> currencies = Arrays.asList(testCurrency);
        when(currencyRepository.findAllActiveCurrenciesOrderBySymbol()).thenReturn(currencies);

        // When
        List<CurrencyDTO> result = currencyService.getAllCurrencies();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("BTC", result.get(0).getSymbol());
        assertEquals("Bitcoin", result.get(0).getName());
        verify(currencyRepository, times(1)).findAllActiveCurrenciesOrderBySymbol();
    }

    @Test
    void testGetCurrencyBySymbol() {
        // Given
        when(currencyRepository.findBySymbolIgnoreCase("BTC")).thenReturn(Optional.of(testCurrency));

        // When
        CurrencyDTO result = currencyService.getCurrencyBySymbol("BTC");

        // Then
        assertNotNull(result);
        assertEquals("BTC", result.getSymbol());
        assertEquals("Bitcoin", result.getName());
        verify(currencyRepository, times(1)).findBySymbolIgnoreCase("BTC");
    }

    @Test
    void testGetCurrencyBySymbol_NotFound() {
        // Given
        when(currencyRepository.findBySymbolIgnoreCase("UNKNOWN")).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> currencyService.getCurrencyBySymbol("UNKNOWN"));
        
        assertTrue(exception.getMessage().contains("Currency not found"));
        verify(currencyRepository, times(1)).findBySymbolIgnoreCase("UNKNOWN");
    }

    @Test
    void testCreateOrUpdateCurrency_NewCurrency() {
        // Given
        when(currencyRepository.findBySymbolIgnoreCase("BTC")).thenReturn(Optional.empty());
        when(currencyRepository.save(any(Currency.class))).thenReturn(testCurrency);

        // When
        CurrencyDTO result = currencyService.createOrUpdateCurrency(testRequest);

        // Then
        assertNotNull(result);
        assertEquals("BTC", result.getSymbol());
        assertEquals("Bitcoin", result.getName());
        assertTrue(result.getIsManual());
        verify(currencyRepository, times(1)).findBySymbolIgnoreCase("BTC");
        verify(currencyRepository, times(1)).save(any(Currency.class));
    }

    @Test
    void testCreateOrUpdateCurrency_UpdateExisting() {
        // Given
        when(currencyRepository.findBySymbolIgnoreCase("BTC")).thenReturn(Optional.of(testCurrency));
        when(currencyRepository.save(any(Currency.class))).thenReturn(testCurrency);

        // When
        CurrencyDTO result = currencyService.createOrUpdateCurrency(testRequest);

        // Then
        assertNotNull(result);
        assertEquals("BTC", result.getSymbol());
        verify(currencyRepository, times(1)).findBySymbolIgnoreCase("BTC");
        verify(currencyRepository, times(1)).save(any(Currency.class));
    }

    @Test
    void testUpdateCurrencyStatus() {
        // Given
        when(currencyRepository.findBySymbolIgnoreCase("BTC")).thenReturn(Optional.of(testCurrency));
        when(currencyRepository.save(any(Currency.class))).thenReturn(testCurrency);

        // When
        CurrencyDTO result = currencyService.updateCurrencyStatus("BTC", false);

        // Then
        assertNotNull(result);
        verify(currencyRepository, times(1)).findBySymbolIgnoreCase("BTC");
        verify(currencyRepository, times(1)).save(any(Currency.class));
    }

    @Test
    void testDeleteCurrency_ManualCurrency() {
        // Given
        testCurrency.setIsManual(true);
        when(currencyRepository.findBySymbolIgnoreCase("BTC")).thenReturn(Optional.of(testCurrency));

        // When
        currencyService.deleteCurrency("BTC");

        // Then
        verify(currencyRepository, times(1)).findBySymbolIgnoreCase("BTC");
        verify(currencyRepository, times(1)).delete(testCurrency);
    }

    @Test
    void testDeleteCurrency_BinanceCurrency() {
        // Given
        testCurrency.setIsManual(false);
        when(currencyRepository.findBySymbolIgnoreCase("BTC")).thenReturn(Optional.of(testCurrency));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> currencyService.deleteCurrency("BTC"));
        
        assertTrue(exception.getMessage().contains("Cannot delete Binance currency"));
        verify(currencyRepository, times(1)).findBySymbolIgnoreCase("BTC");
        verify(currencyRepository, never()).delete(any(Currency.class));
    }

    @Test
    void testGetCurrencyStatistics() {
        // Given
        when(currencyRepository.count()).thenReturn(10L);
        when(currencyRepository.countActiveCurrencies()).thenReturn(8L);
        when(currencyRepository.countManualCurrencies()).thenReturn(3L);
        when(currencyRepository.countBinanceCurrencies()).thenReturn(7L);

        // When
        CurrencyService.CurrencyStatistics result = currencyService.getCurrencyStatistics();

        // Then
        assertNotNull(result);
        assertEquals(10L, result.getTotalCurrencies());
        assertEquals(8L, result.getActiveCurrencies());
        assertEquals(2L, result.getInactiveCurrencies());
        assertEquals(3L, result.getManualCurrencies());
        assertEquals(7L, result.getBinanceCurrencies());
    }
}
