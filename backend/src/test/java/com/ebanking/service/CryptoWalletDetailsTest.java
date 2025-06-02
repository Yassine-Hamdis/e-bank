package com.ebanking.service;

import com.ebanking.entity.*;
import com.ebanking.repository.*;
import com.ebanking.service.impl.ClientServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test class for enhanced crypto wallet details functionality
 */
@ExtendWith(MockitoExtension.class)
public class CryptoWalletDetailsTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private CryptoWalletRepository cryptoWalletRepository;

    @Mock
    private CryptoWalletBalanceRepository cryptoWalletBalanceRepository;

    @InjectMocks
    private ClientServiceImpl clientService;

    private Client testClient;
    private CryptoWallet testWallet;
    private List<CryptoWalletBalance> testBalances;

    @BeforeEach
    void setUp() {
        // Create test client
        testClient = new Client();
        testClient.setId(1L);
        testClient.setClientId("CLT001");
        testClient.setUsername("testuser");
        testClient.setEmail("test@example.com");
        testClient.setPhoneNumber("+1234567890");
        testClient.setNationalId("ID123456789");
        testClient.setDateOfBirth(LocalDate.of(1990, 1, 1));
        testClient.setAddress("Test Address");
        testClient.setStatus(UserStatus.ACTIVE);

        // Create test crypto wallet
        testWallet = new CryptoWallet();
        testWallet.setId(2L);
        testWallet.setWalletAddress("test-wallet-address-123");
        testWallet.setClient(testClient);
        testWallet.setStatus(WalletStatus.ACTIVE);
        testWallet.setCreatedDate(LocalDateTime.now().minusDays(30));
        testWallet.setUpdatedDate(LocalDateTime.now().minusDays(1));

        // Create test crypto wallet balances (matching the database data you provided)
        CryptoWalletBalance ethBalance = new CryptoWalletBalance();
        ethBalance.setId(1L);
        ethBalance.setCryptoWallet(testWallet);
        ethBalance.setCryptoType("ETH");
        ethBalance.setBalance(new BigDecimal("0.39441042"));
        ethBalance.setCreatedDate(LocalDateTime.of(2025, 5, 31, 16, 0, 20, 334773000));

        CryptoWalletBalance usdtBalance = new CryptoWalletBalance();
        usdtBalance.setId(2L);
        usdtBalance.setCryptoWallet(testWallet);
        usdtBalance.setCryptoType("USDT");
        usdtBalance.setBalance(new BigDecimal("2000.00000000"));
        usdtBalance.setCreatedDate(LocalDateTime.of(2025, 5, 31, 16, 1, 15, 121494000));

        testBalances = Arrays.asList(ethBalance, usdtBalance);
    }

    @Test
    void testGetCryptoWallet_WithActualBalances() {
        // Arrange
        when(clientRepository.findById(1L)).thenReturn(Optional.of(testClient));
        when(cryptoWalletRepository.findByClient(testClient)).thenReturn(Optional.of(testWallet));
        when(cryptoWalletBalanceRepository.findByCryptoWallet(testWallet)).thenReturn(testBalances);

        // Act
        Map<String, Object> result = clientService.getCryptoWallet(1L);

        // Assert
        assertNotNull(result);
        assertEquals(2L, result.get("id"));
        assertEquals("test-wallet-address-123", result.get("walletAddress"));
        assertEquals("CLT001", result.get("clientId"));
        assertEquals("ACTIVE", result.get("status"));

        // Check crypto balances
        @SuppressWarnings("unchecked")
        Map<String, BigDecimal> cryptoBalances = (Map<String, BigDecimal>) result.get("cryptoBalances");
        assertNotNull(cryptoBalances);
        assertEquals(new BigDecimal("0.39441042"), cryptoBalances.get("ETH"));
        assertEquals(new BigDecimal("2000.00000000"), cryptoBalances.get("USDT"));
        assertEquals(BigDecimal.ZERO, cryptoBalances.get("BTC")); // Should be zero (not in database)
        assertEquals(BigDecimal.ZERO, cryptoBalances.get("BNB")); // Should be zero (not in database)

        // Check balance details
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> balanceDetails = (List<Map<String, Object>>) result.get("balanceDetails");
        assertNotNull(balanceDetails);
        assertEquals(2, balanceDetails.size());

        // Check ETH balance detail
        Map<String, Object> ethDetail = balanceDetails.stream()
                .filter(detail -> "ETH".equals(detail.get("cryptoType")))
                .findFirst()
                .orElse(null);
        assertNotNull(ethDetail);
        assertEquals(1L, ethDetail.get("id"));
        assertEquals("ETH", ethDetail.get("cryptoType"));
        assertEquals(new BigDecimal("0.39441042"), ethDetail.get("balance"));
        assertNotNull(ethDetail.get("valueInMAD")); // Should have calculated MAD value

        // Check USDT balance detail
        Map<String, Object> usdtDetail = balanceDetails.stream()
                .filter(detail -> "USDT".equals(detail.get("cryptoType")))
                .findFirst()
                .orElse(null);
        assertNotNull(usdtDetail);
        assertEquals(2L, usdtDetail.get("id"));
        assertEquals("USDT", usdtDetail.get("cryptoType"));
        assertEquals(new BigDecimal("2000.00000000"), usdtDetail.get("balance"));
        assertNotNull(usdtDetail.get("valueInMAD")); // Should have calculated MAD value

        // Check total values
        assertNotNull(result.get("totalValueMAD"));
        assertEquals(2, result.get("totalBalances"));

        // Verify repository calls
        verify(clientRepository).findById(1L);
        verify(cryptoWalletRepository).findByClient(testClient);
        verify(cryptoWalletBalanceRepository).findByCryptoWallet(testWallet);
    }

    @Test
    void testGetCryptoWallet_WithNoBalances() {
        // Arrange
        when(clientRepository.findById(1L)).thenReturn(Optional.of(testClient));
        when(cryptoWalletRepository.findByClient(testClient)).thenReturn(Optional.of(testWallet));
        when(cryptoWalletBalanceRepository.findByCryptoWallet(testWallet)).thenReturn(Arrays.asList());

        // Act
        Map<String, Object> result = clientService.getCryptoWallet(1L);

        // Assert
        assertNotNull(result);
        
        // Check crypto balances - all should be zero
        @SuppressWarnings("unchecked")
        Map<String, BigDecimal> cryptoBalances = (Map<String, BigDecimal>) result.get("cryptoBalances");
        assertNotNull(cryptoBalances);
        assertEquals(BigDecimal.ZERO, cryptoBalances.get("BTC"));
        assertEquals(BigDecimal.ZERO, cryptoBalances.get("ETH"));
        assertEquals(BigDecimal.ZERO, cryptoBalances.get("USDT"));
        assertEquals(BigDecimal.ZERO, cryptoBalances.get("BNB"));

        // Check balance details - should be empty
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> balanceDetails = (List<Map<String, Object>>) result.get("balanceDetails");
        assertNotNull(balanceDetails);
        assertEquals(0, balanceDetails.size());

        // Check totals
        assertEquals(BigDecimal.ZERO, result.get("totalValueMAD"));
        assertEquals(0, result.get("totalBalances"));
    }

    @Test
    void testGetCryptoWallet_ClientNotFound() {
        // Arrange
        when(clientRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            clientService.getCryptoWallet(1L);
        });

        assertEquals("Client not found with ID: 1", exception.getMessage());
    }

    @Test
    void testGetCryptoWallet_WalletNotFound() {
        // Arrange
        when(clientRepository.findById(1L)).thenReturn(Optional.of(testClient));
        when(cryptoWalletRepository.findByClient(testClient)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            clientService.getCryptoWallet(1L);
        });

        assertEquals("Crypto wallet not found for client", exception.getMessage());
    }
}
