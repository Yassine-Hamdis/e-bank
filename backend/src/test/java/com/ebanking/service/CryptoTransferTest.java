package com.ebanking.service;

import com.ebanking.dto.request.CryptoTransferRequest;
import com.ebanking.dto.response.CryptoTransferResponse;
import com.ebanking.entity.*;
import com.ebanking.repository.*;
import com.ebanking.service.impl.ClientServiceImpl;
import com.ebanking.service.NotificationService;
import com.ebanking.service.CurrencyService;
import com.ebanking.service.BinanceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for crypto transfer functionality
 */
@ExtendWith(MockitoExtension.class)
class CryptoTransferTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private CryptoWalletRepository cryptoWalletRepository;

    @Mock
    private CryptoWalletBalanceRepository cryptoWalletBalanceRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private CurrencyService currencyService;

    @Mock
    private BinanceService binanceService;

    @InjectMocks
    private ClientServiceImpl clientService;

    private Client senderClient;
    private Client recipientClient;
    private CryptoWallet senderWallet;
    private CryptoWallet recipientWallet;
    private CryptoWalletBalance senderBalance;
    private CryptoWalletBalance recipientBalance;
    private CryptoTransferRequest transferRequest;

    @BeforeEach
    void setUp() {
        // Setup sender client
        senderClient = new Client();
        senderClient.setId(1L);
        senderClient.setClientId("CLT001");
        senderClient.setUsername("sender");

        // Setup recipient client
        recipientClient = new Client();
        recipientClient.setId(2L);
        recipientClient.setClientId("CLT002");
        recipientClient.setUsername("recipient");

        // Setup sender wallet
        senderWallet = new CryptoWallet();
        senderWallet.setId(1L);
        senderWallet.setWalletAddress("sender-wallet-address");
        senderWallet.setClient(senderClient);
        senderWallet.setStatus(WalletStatus.ACTIVE);

        // Setup recipient wallet
        recipientWallet = new CryptoWallet();
        recipientWallet.setId(2L);
        recipientWallet.setWalletAddress("recipient-wallet-address");
        recipientWallet.setClient(recipientClient);
        recipientWallet.setStatus(WalletStatus.ACTIVE);

        // Setup sender balance
        senderBalance = new CryptoWalletBalance();
        senderBalance.setId(1L);
        senderBalance.setCryptoWallet(senderWallet);
        senderBalance.setCryptoType("BTC");
        senderBalance.setBalance(BigDecimal.valueOf(1.0)); // 1 BTC

        // Setup recipient balance (initially zero)
        recipientBalance = new CryptoWalletBalance();
        recipientBalance.setId(2L);
        recipientBalance.setCryptoWallet(recipientWallet);
        recipientBalance.setCryptoType("BTC");
        recipientBalance.setBalance(BigDecimal.ZERO);

        // Setup transfer request
        transferRequest = new CryptoTransferRequest();
        transferRequest.setRecipientWalletAddress("recipient-wallet-address");
        transferRequest.setCryptoType("BTC");
        transferRequest.setCryptoAmount(BigDecimal.valueOf(0.5)); // Transfer 0.5 BTC
        transferRequest.setNetworkFee(BigDecimal.valueOf(0.001)); // 0.001 BTC network fee
        transferRequest.setDescription("Test crypto transfer");
    }

    @Test
    void testTransferCryptocurrency_Success() {
        // Arrange
        when(clientRepository.findById(1L)).thenReturn(Optional.of(senderClient));
        when(cryptoWalletRepository.findByClient(senderClient)).thenReturn(Optional.of(senderWallet));
        when(cryptoWalletRepository.findByWalletAddress("recipient-wallet-address"))
                .thenReturn(Optional.of(recipientWallet));
        when(cryptoWalletBalanceRepository.findByCryptoWalletAndCryptoType(senderWallet, "BTC"))
                .thenReturn(Optional.of(senderBalance));
        when(cryptoWalletBalanceRepository.findByCryptoWalletAndCryptoType(recipientWallet, "BTC"))
                .thenReturn(Optional.of(recipientBalance));
        when(cryptoWalletBalanceRepository.save(any(CryptoWalletBalance.class)))
                .thenReturn(new CryptoWalletBalance());
        when(transactionRepository.save(any(CryptoTransaction.class)))
                .thenReturn(createMockTransaction());

        // Act
        CryptoTransferResponse response = clientService.transferCryptocurrency(transferRequest, 1L);

        // Assert
        assertNotNull(response);
        assertEquals("BTC", response.getCryptoType());
        assertEquals(BigDecimal.valueOf(0.5), response.getCryptoAmount());
        assertEquals("sender-wallet-address", response.getSenderWalletAddress());
        assertEquals("recipient-wallet-address", response.getRecipientWalletAddress());
        assertEquals("COMPLETED", response.getStatus());
        assertNotNull(response.getTransactionId());

        // Verify balances were updated
        verify(cryptoWalletBalanceRepository, times(2)).save(any(CryptoWalletBalance.class));
        
        // Verify notifications were sent
        verify(notificationService).createTransactionNotification(eq(senderClient), anyString(), anyString(), eq("CRYPTO_TRANSFER_SENT"));
        verify(notificationService).createTransactionNotification(eq(recipientClient), anyString(), anyString(), eq("CRYPTO_TRANSFER_RECEIVED"));
    }

    @Test
    void testTransferCryptocurrency_InsufficientBalance() {
        // Arrange - sender has insufficient balance
        senderBalance.setBalance(BigDecimal.valueOf(0.1)); // Only 0.1 BTC, but trying to transfer 0.5 + 0.001 fee
        
        when(clientRepository.findById(1L)).thenReturn(Optional.of(senderClient));
        when(cryptoWalletRepository.findByClient(senderClient)).thenReturn(Optional.of(senderWallet));
        when(cryptoWalletRepository.findByWalletAddress("recipient-wallet-address"))
                .thenReturn(Optional.of(recipientWallet));
        when(cryptoWalletBalanceRepository.findByCryptoWalletAndCryptoType(senderWallet, "BTC"))
                .thenReturn(Optional.of(senderBalance));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            clientService.transferCryptocurrency(transferRequest, 1L);
        });

        assertTrue(exception.getMessage().contains("Insufficient balance for crypto transfer"));
    }

    @Test
    void testTransferCryptocurrency_RecipientWalletNotFound() {
        // Arrange
        when(clientRepository.findById(1L)).thenReturn(Optional.of(senderClient));
        when(cryptoWalletRepository.findByClient(senderClient)).thenReturn(Optional.of(senderWallet));
        when(cryptoWalletRepository.findByWalletAddress("recipient-wallet-address"))
                .thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            clientService.transferCryptocurrency(transferRequest, 1L);
        });

        assertEquals("Recipient wallet not found with address: recipient-wallet-address", exception.getMessage());
    }

    @Test
    void testTransferCryptocurrency_SelfTransfer() {
        // Arrange - trying to transfer to own wallet
        transferRequest.setRecipientWalletAddress("sender-wallet-address");
        
        when(clientRepository.findById(1L)).thenReturn(Optional.of(senderClient));
        when(cryptoWalletRepository.findByClient(senderClient)).thenReturn(Optional.of(senderWallet));
        when(cryptoWalletRepository.findByWalletAddress("sender-wallet-address"))
                .thenReturn(Optional.of(senderWallet));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            clientService.transferCryptocurrency(transferRequest, 1L);
        });

        assertEquals("Cannot transfer to your own wallet", exception.getMessage());
    }

    @Test
    void testTransferCryptocurrency_SenderWalletNotFound() {
        // Arrange
        when(clientRepository.findById(1L)).thenReturn(Optional.of(senderClient));
        when(cryptoWalletRepository.findByClient(senderClient)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            clientService.transferCryptocurrency(transferRequest, 1L);
        });

        assertEquals("Sender crypto wallet not found", exception.getMessage());
    }

    @Test
    void testTransferCryptocurrency_SenderClientNotFound() {
        // Arrange
        when(clientRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            clientService.transferCryptocurrency(transferRequest, 1L);
        });

        assertEquals("Sender client not found with ID: 1", exception.getMessage());
    }

    @Test
    void testTransferCryptocurrency_NewRecipientBalance() {
        // Arrange - recipient has no existing balance for this crypto type
        when(clientRepository.findById(1L)).thenReturn(Optional.of(senderClient));
        when(cryptoWalletRepository.findByClient(senderClient)).thenReturn(Optional.of(senderWallet));
        when(cryptoWalletRepository.findByWalletAddress("recipient-wallet-address"))
                .thenReturn(Optional.of(recipientWallet));
        when(cryptoWalletBalanceRepository.findByCryptoWalletAndCryptoType(senderWallet, "BTC"))
                .thenReturn(Optional.of(senderBalance));
        when(cryptoWalletBalanceRepository.findByCryptoWalletAndCryptoType(recipientWallet, "BTC"))
                .thenReturn(Optional.empty()); // No existing balance
        when(cryptoWalletBalanceRepository.save(any(CryptoWalletBalance.class)))
                .thenReturn(new CryptoWalletBalance());
        when(transactionRepository.save(any(CryptoTransaction.class)))
                .thenReturn(createMockTransaction());

        // Act
        CryptoTransferResponse response = clientService.transferCryptocurrency(transferRequest, 1L);

        // Assert
        assertNotNull(response);
        assertEquals("COMPLETED", response.getStatus());
        
        // Verify that a new balance was created and saved
        verify(cryptoWalletBalanceRepository, times(2)).save(any(CryptoWalletBalance.class));
    }

    private CryptoTransaction createMockTransaction() {
        CryptoTransaction transaction = new CryptoTransaction();
        transaction.setId(1L);
        transaction.setTransactionId("TXN123456789");
        transaction.setDate(LocalDateTime.now());
        transaction.setType(TransactionType.CRYPTO_TRANSFER);
        transaction.setStatus(TransactionStatus.COMPLETED);
        return transaction;
    }
}
