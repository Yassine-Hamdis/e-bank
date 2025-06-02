package com.ebanking.service;

import com.ebanking.dto.request.CryptoBuyFromMainRequest;
import com.ebanking.dto.response.CryptoBuyFromMainResponse;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Test class for CryptoBuyFromMain functionality
 */
@ExtendWith(MockitoExtension.class)
public class CryptoBuyFromMainTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CryptoWalletRepository cryptoWalletRepository;

    @Mock
    private CryptoWalletBalanceRepository cryptoWalletBalanceRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private BinanceService binanceService;

    @InjectMocks
    private ClientServiceImpl clientService;

    private Client testClient;
    private Account testAccount;
    private CryptoWallet testWallet;
    private CryptoBuyFromMainRequest testRequest;

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
        testClient.setIdentificationNumber("ID123456789");
        testClient.setStatus(UserStatus.ACTIVE);

        // Create test account with sufficient balance
        testAccount = new Account();
        testAccount.setId(1L);
        testAccount.setAccountId("ACC001");
        testAccount.setClient(testClient);
        testAccount.setBalance(BigDecimal.valueOf(10000.00)); // 10,000 MAD
        testAccount.setAccountType(AccountType.CHECKING);
        testAccount.setStatus(AccountStatus.ACTIVE);
        testAccount.setCurrency("MAD");

        // Create test crypto wallet
        testWallet = new CryptoWallet();
        testWallet.setId(1L);
        testWallet.setWalletAddress("test-wallet-address");
        testWallet.setClient(testClient);
        testWallet.setStatus(WalletStatus.ACTIVE);

        // Create test request
        testRequest = new CryptoBuyFromMainRequest();
        testRequest.setCryptoType("BTC");
        testRequest.setAmount(BigDecimal.valueOf(1000.00)); // 1,000 MAD
        testRequest.setDescription("Test BTC purchase");
        testRequest.setPlatformFee(BigDecimal.valueOf(10.00)); // 10 MAD fee
        testRequest.setUseRealTimeRate(false); // Use mock rates for testing
    }

    @Test
    void testBuyCryptocurrencyFromMain_Success() {
        // Arrange
        when(clientRepository.findById(1L)).thenReturn(Optional.of(testClient));
        when(accountRepository.findByClientAndAccountType(testClient, AccountType.CHECKING))
                .thenReturn(java.util.Arrays.asList(testAccount));
        when(cryptoWalletRepository.findByClient(testClient)).thenReturn(Optional.of(testWallet));
        when(cryptoWalletBalanceRepository.findByCryptoWalletAndCryptoType(testWallet, "BTC"))
                .thenReturn(Optional.empty());
        when(accountRepository.save(any(Account.class))).thenReturn(testAccount);
        when(cryptoWalletBalanceRepository.save(any(CryptoWalletBalance.class)))
                .thenReturn(new CryptoWalletBalance());
        when(transactionRepository.save(any(CryptoTransaction.class)))
                .thenReturn(new CryptoTransaction());
        when(cryptoWalletBalanceRepository.findByCryptoWallet(testWallet))
                .thenReturn(java.util.Arrays.asList());

        // Act
        CryptoBuyFromMainResponse response = clientService.buyCryptocurrencyFromMain(testRequest, 1L);

        // Assert
        assertNotNull(response);
        assertEquals("BTC", response.getCryptoType());
        assertEquals(BigDecimal.valueOf(1000.00), response.getMadAmount());
        assertEquals(BigDecimal.valueOf(10.00), response.getPlatformFee());
        assertEquals("COMPLETED", response.getStatus());
        assertNotNull(response.getTransactionId());
        assertNotNull(response.getCryptoAmount());
        assertEquals("test-wallet-address", response.getWalletAddress());

        // Verify that account balance was updated
        verify(accountRepository).save(testAccount);
        assertEquals(BigDecimal.valueOf(8990.00), testAccount.getBalance()); // 10000 - 1000 - 10

        // Verify that crypto wallet balance was saved
        verify(cryptoWalletBalanceRepository).save(any(CryptoWalletBalance.class));

        // Verify that transaction was saved
        verify(transactionRepository).save(any(CryptoTransaction.class));

        // Verify that notification was created
        verify(notificationService).createTransactionNotification(
                eq(testClient), anyString(), eq("1000.0"), eq("CRYPTO_BUY_FROM_MAIN"));
    }

    @Test
    void testBuyCryptocurrencyFromMain_InsufficientBalance() {
        // Arrange
        testAccount.setBalance(BigDecimal.valueOf(500.00)); // Insufficient balance
        when(clientRepository.findById(1L)).thenReturn(Optional.of(testClient));
        when(accountRepository.findByClientAndAccountType(testClient, AccountType.CHECKING))
                .thenReturn(java.util.Arrays.asList(testAccount));
        when(cryptoWalletRepository.findByClient(testClient)).thenReturn(Optional.of(testWallet));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            clientService.buyCryptocurrencyFromMain(testRequest, 1L);
        });

        assertTrue(exception.getMessage().contains("Insufficient balance for crypto purchase"));
    }

    @Test
    void testBuyCryptocurrencyFromMain_ClientNotFound() {
        // Arrange
        when(clientRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            clientService.buyCryptocurrencyFromMain(testRequest, 1L);
        });

        assertEquals("Client not found with ID: 1", exception.getMessage());
    }

    @Test
    void testBuyCryptocurrencyFromMain_WalletNotFound() {
        // Arrange
        when(clientRepository.findById(1L)).thenReturn(Optional.of(testClient));
        when(accountRepository.findByClientAndAccountType(testClient, AccountType.CHECKING))
                .thenReturn(java.util.Arrays.asList(testAccount));
        when(cryptoWalletRepository.findByClient(testClient)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            clientService.buyCryptocurrencyFromMain(testRequest, 1L);
        });

        assertEquals("Crypto wallet not found for client", exception.getMessage());
    }
}
