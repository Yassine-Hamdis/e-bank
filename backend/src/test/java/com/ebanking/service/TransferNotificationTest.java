package com.ebanking.service;

import com.ebanking.dto.request.TransferRequest;
import com.ebanking.dto.response.TransactionDTO;
import com.ebanking.dto.response.NotificationDTO;
import com.ebanking.entity.*;
import com.ebanking.repository.*;
import com.ebanking.service.impl.ClientServiceImpl;
import com.ebanking.service.NotificationService;
import com.ebanking.service.CurrencyService;
import com.ebanking.service.BinanceService;
import com.ebanking.service.GlobalSettingsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Test class for transfer notification functionality
 */
@ExtendWith(MockitoExtension.class)
class TransferNotificationTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private CurrencyService currencyService;

    @Mock
    private BinanceService binanceService;

    @Mock
    private CryptoWalletRepository cryptoWalletRepository;

    @Mock
    private CryptoWalletBalanceRepository cryptoWalletBalanceRepository;

    @Mock
    private GlobalSettingsRepository globalSettingsRepository;

    @Mock
    private GlobalSettingsService globalSettingsService;

    @InjectMocks
    private ClientServiceImpl clientService;

    private Client senderClient;
    private Client recipientClient;
    private Account sourceAccount;
    private Account destinationAccount;
    private TransferRequest transferRequest;

    @BeforeEach
    void setUp() {
        // Create sender client
        senderClient = new Client();
        senderClient.setId(1L);
        senderClient.setClientId("CLT001");
        senderClient.setUsername("sender");
        senderClient.setEmail("sender@test.com");
        senderClient.setPhoneNumber("0612345678");
        senderClient.setDateOfBirth(LocalDate.now().minusYears(30));

        // Create recipient client
        recipientClient = new Client();
        recipientClient.setId(2L);
        recipientClient.setClientId("CLT002");
        recipientClient.setUsername("recipient");
        recipientClient.setEmail("recipient@test.com");
        recipientClient.setPhoneNumber("0687654321");
        recipientClient.setDateOfBirth(LocalDate.now().minusYears(25));

        // Create source account (sender's account)
        sourceAccount = new Account();
        sourceAccount.setId(1L);
        sourceAccount.setAccountId("ACC001");
        sourceAccount.setClient(senderClient);
        sourceAccount.setBalance(new BigDecimal("1000.00"));
        sourceAccount.setAccountType(AccountType.CHECKING);
        sourceAccount.setStatus(AccountStatus.ACTIVE);

        // Create destination account (recipient's account)
        destinationAccount = new Account();
        destinationAccount.setId(2L);
        destinationAccount.setAccountId("ACC002");
        destinationAccount.setClient(recipientClient);
        destinationAccount.setBalance(new BigDecimal("500.00"));
        destinationAccount.setAccountType(AccountType.CHECKING);
        destinationAccount.setStatus(AccountStatus.ACTIVE);

        // Create transfer request
        transferRequest = new TransferRequest();
        transferRequest.setSourceAccountId("ACC001");
        transferRequest.setDestinationAccountId("ACC002");
        transferRequest.setAmount(new BigDecimal("100.00"));
        transferRequest.setDescription("Test transfer");

        // Mock global settings service for fee calculation
        when(globalSettingsService.getSettingValueAsDecimal("feePercentage"))
                .thenReturn(new BigDecimal("1.0")); // 1% fee
    }

    @Test
    void testCreateTransfer_SendsNotificationsToBothSenderAndRecipient() {
        // Arrange
        when(clientRepository.findById(1L)).thenReturn(Optional.of(senderClient));
        when(accountRepository.findByAccountId("ACC001")).thenReturn(Optional.of(sourceAccount));
        when(accountRepository.findByAccountId("ACC002")).thenReturn(Optional.of(destinationAccount));
        when(accountRepository.save(any(Account.class))).thenReturn(sourceAccount);
        when(transactionRepository.save(any(Transfer.class))).thenReturn(createMockTransfer());

        // Act
        TransactionDTO result = clientService.createTransfer(transferRequest, 1L);

        // Assert
        assertNotNull(result);
        assertEquals("TRANSFER", result.getType());
        assertEquals(new BigDecimal("100.00"), result.getAmount());

        // Verify that notifications were sent to both sender and recipient
        verify(notificationService).createTransactionNotification(
                eq(senderClient), 
                anyString(), 
                eq("100.00"), 
                eq("TRANSFER_SENT")
        );
        
        verify(notificationService).createTransactionNotification(
                eq(recipientClient), 
                anyString(), 
                eq("100.00"), 
                eq("TRANSFER_RECEIVED")
        );

        // Verify that both notifications were called exactly once each
        verify(notificationService, times(2)).createTransactionNotification(any(), anyString(), anyString(), anyString());
    }

    @Test
    void testCreateTransfer_VerifyAccountBalancesUpdated() {
        // Arrange
        when(clientRepository.findById(1L)).thenReturn(Optional.of(senderClient));
        when(accountRepository.findByAccountId("ACC001")).thenReturn(Optional.of(sourceAccount));
        when(accountRepository.findByAccountId("ACC002")).thenReturn(Optional.of(destinationAccount));
        when(accountRepository.save(any(Account.class))).thenReturn(sourceAccount);
        when(transactionRepository.save(any(Transfer.class))).thenReturn(createMockTransfer());

        // Act
        clientService.createTransfer(transferRequest, 1L);

        // Assert - verify account balances were updated correctly
        // Source account should be debited: 1000 - (100 + 1% fee) = 1000 - 101 = 899
        assertEquals(new BigDecimal("899.00"), sourceAccount.getBalance());
        
        // Destination account should be credited: 500 + 100 = 600
        assertEquals(new BigDecimal("600.00"), destinationAccount.getBalance());

        // Verify accounts were saved
        verify(accountRepository, times(2)).save(any(Account.class));
    }

    @Test
    void testCreateTransfer_InsufficientBalance_NoNotificationsSent() {
        // Arrange - set source account balance too low
        sourceAccount.setBalance(new BigDecimal("50.00")); // Not enough for 100 + 1% fee
        
        when(clientRepository.findById(1L)).thenReturn(Optional.of(senderClient));
        when(accountRepository.findByAccountId("ACC001")).thenReturn(Optional.of(sourceAccount));
        when(accountRepository.findByAccountId("ACC002")).thenReturn(Optional.of(destinationAccount));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            clientService.createTransfer(transferRequest, 1L);
        });

        assertTrue(exception.getMessage().contains("Insufficient balance"));
        
        // Verify no notifications were sent when transfer fails
        verify(notificationService, never()).createTransactionNotification(any(), anyString(), anyString(), anyString());
    }

    @Test
    void testTransferNotificationIntegration_VerifyBothClientsReceiveCorrectNotifications() {
        // Arrange - Setup complete transfer scenario
        when(clientRepository.findById(1L)).thenReturn(Optional.of(senderClient));
        when(accountRepository.findByAccountId("ACC001")).thenReturn(Optional.of(sourceAccount));
        when(accountRepository.findByAccountId("ACC002")).thenReturn(Optional.of(destinationAccount));
        when(accountRepository.save(any(Account.class))).thenReturn(sourceAccount);
        when(transactionRepository.save(any(Transfer.class))).thenReturn(createMockTransfer());

        // Mock notification creation to verify correct parameters
        NotificationDTO senderNotification = new NotificationDTO();
        senderNotification.setNotificationId("NOT001");
        senderNotification.setTitle("Transfer Sent");
        senderNotification.setMessage("You have successfully sent 100.00 MAD to another account. Transaction ID: TXN123");
        senderNotification.setType("TRANSACTION");
        senderNotification.setIsRead(false);

        NotificationDTO recipientNotification = new NotificationDTO();
        recipientNotification.setNotificationId("NOT002");
        recipientNotification.setTitle("Transfer Received");
        recipientNotification.setMessage("You have received 100.00 MAD from another account. Transaction ID: TXN123");
        recipientNotification.setType("TRANSACTION");
        recipientNotification.setIsRead(false);

        when(notificationService.createTransactionNotification(eq(senderClient), anyString(), eq("100.00"), eq("TRANSFER_SENT")))
                .thenReturn(senderNotification);
        when(notificationService.createTransactionNotification(eq(recipientClient), anyString(), eq("100.00"), eq("TRANSFER_RECEIVED")))
                .thenReturn(recipientNotification);

        // Act
        TransactionDTO result = clientService.createTransfer(transferRequest, 1L);

        // Assert
        assertNotNull(result);
        assertEquals("TRANSFER", result.getType());
        assertEquals(new BigDecimal("100.00"), result.getAmount());

        // Verify sender notification with correct parameters
        verify(notificationService).createTransactionNotification(
                eq(senderClient),
                anyString(),
                eq("100.00"),
                eq("TRANSFER_SENT")
        );

        // Verify recipient notification with correct parameters
        verify(notificationService).createTransactionNotification(
                eq(recipientClient),
                anyString(),
                eq("100.00"),
                eq("TRANSFER_RECEIVED")
        );

        // Verify exactly 2 notifications were created (one for sender, one for recipient)
        verify(notificationService, times(2)).createTransactionNotification(any(), anyString(), anyString(), anyString());

        // Verify account balances were updated correctly
        verify(accountRepository, times(2)).save(any(Account.class));

        // Verify transaction was saved
        verify(transactionRepository).save(any(Transfer.class));

        // Verify the recipient client was correctly identified from destination account
        assertEquals(recipientClient, destinationAccount.getClient());
    }

    private Transfer createMockTransfer() {
        Transfer transfer = new Transfer();
        transfer.setId(1L);
        transfer.setTransactionId("TXN" + System.currentTimeMillis());
        transfer.setAmount(new BigDecimal("100.00"));
        transfer.setDescription("Test transfer");
        transfer.setSourceAccountId("ACC001");
        transfer.setDestinationAccountId("ACC002");
        transfer.setTransferFee(new BigDecimal("1.00"));
        transfer.setFromAccount(sourceAccount);
        transfer.setToAccount(destinationAccount);
        transfer.setStatus(TransactionStatus.PENDING);
        transfer.setDate(LocalDateTime.now());
        transfer.setCreatedDate(LocalDateTime.now());
        return transfer;
    }
}
