package com.ebanking.service;

import com.ebanking.dto.request.ClientCreateRequest;
import com.ebanking.dto.response.ClientEnrollmentResponse;
import com.ebanking.entity.Account;
import com.ebanking.entity.BankAgent;
import com.ebanking.entity.Client;
import com.ebanking.entity.CryptoWallet;
import com.ebanking.repository.*;
import com.ebanking.service.impl.BankAgentServiceImpl;
import com.ebanking.util.IdGeneratorUtil;
import com.ebanking.util.PasswordGenerator;
import com.ebanking.service.EmailService;
import com.ebanking.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Test class for BankAgentService client creation with auto-generated fields
 */
@ExtendWith(MockitoExtension.class)
public class BankAgentServiceClientCreationTest {

    @Mock
    private BankAgentRepository bankAgentRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CryptoWalletRepository cryptoWalletRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private PasswordGenerator passwordGenerator;

    @Mock
    private IdGeneratorUtil idGeneratorUtil;

    @Mock
    private EmailService emailService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private BankAgentServiceImpl bankAgentService;

    private BankAgent testAgent;
    private ClientCreateRequest testRequest;

    @BeforeEach
    void setUp() {
        testAgent = new BankAgent();
        testAgent.setId(1L);
        testAgent.setEmployeeId("EMP001");
        testAgent.setUsername("agent1");
        testAgent.setEmail("agent1@bank.com");

        testRequest = new ClientCreateRequest();
        testRequest.setAddress("123 Test Street, Test City");
        testRequest.setUsername("testuser");
        testRequest.setEmail("testuser@example.com");
        testRequest.setPhoneNumber("+1234567890");
        testRequest.setNationalId("NAT123456");
    }

    @Test
    void testCreateClient_AutoGeneratesClientId() {
        // Arrange
        Long agentId = 1L;
        String generatedClientId = "CLT001001";
        String generatedIdentificationNumber = "ID2024100001";
        String generatedPassword = "SecurePass123!";

        when(bankAgentRepository.findById(agentId)).thenReturn(Optional.of(testAgent));
        when(idGeneratorUtil.generateClientId()).thenReturn(generatedClientId);
        when(idGeneratorUtil.generateIdentificationNumber()).thenReturn(generatedIdentificationNumber);
        when(clientRepository.existsByClientId(generatedClientId)).thenReturn(false);
        when(clientRepository.existsByIdentificationNumber(generatedIdentificationNumber)).thenReturn(false);
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("testuser@example.com")).thenReturn(false);
        when(passwordGenerator.generatePassword()).thenReturn(generatedPassword);
        when(passwordEncoder.encode(generatedPassword)).thenReturn("encodedPassword");
        when(clientRepository.save(any(Client.class))).thenAnswer(invocation -> {
            Client client = invocation.getArgument(0);
            client.setId(1L);
            return client;
        });

        // Mock account creation
        when(accountRepository.save(any())).thenAnswer(invocation -> {
            Account account = invocation.getArgument(0);
            account.setId(1L);
            return account;
        });

        // Mock crypto wallet creation
        when(cryptoWalletRepository.save(any())).thenAnswer(invocation -> {
            CryptoWallet wallet = invocation.getArgument(0);
            wallet.setId(1L);
            return wallet;
        });

        // Act
        ClientEnrollmentResponse response = bankAgentService.createClient(testRequest, agentId);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getClient());
        assertEquals(generatedClientId, response.getClient().getClientId());
        assertEquals(generatedIdentificationNumber, response.getClient().getIdentificationNumber());
        assertEquals("testuser", response.getClient().getUsername());
        assertEquals("testuser@example.com", response.getClient().getEmail());
        assertEquals("+1234567890", response.getClient().getPhoneNumber());
        assertEquals("123 Test Street, Test City", response.getClient().getAddress());
        assertNotNull(response.getClient().getEnrollmentDate());

        // Verify that ID generation methods were called
        verify(idGeneratorUtil).generateClientId();
        verify(idGeneratorUtil).generateIdentificationNumber();
        verify(passwordGenerator).generatePassword();
        verify(clientRepository).save(any(Client.class));
    }

    @Test
    void testCreateClient_RequiredFieldsValidation() {
        // Arrange
        Long agentId = 1L;
        ClientCreateRequest invalidRequest = new ClientCreateRequest();
        invalidRequest.setAddress("123 Test Street");
        // Missing username, email, and phone

        when(bankAgentRepository.findById(agentId)).thenReturn(Optional.of(testAgent));
        when(idGeneratorUtil.generateClientId()).thenReturn("CLT001001");
        when(idGeneratorUtil.generateIdentificationNumber()).thenReturn("ID2024100001");
        when(clientRepository.existsByClientId(anyString())).thenReturn(false);
        when(clientRepository.existsByIdentificationNumber(anyString())).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bankAgentService.createClient(invalidRequest, agentId);
        });

        assertTrue(exception.getMessage().contains("Username is required"));
    }

    @Test
    void testCreateClient_DuplicateUsernameValidation() {
        // Arrange
        Long agentId = 1L;

        when(bankAgentRepository.findById(agentId)).thenReturn(Optional.of(testAgent));
        when(idGeneratorUtil.generateClientId()).thenReturn("CLT001001");
        when(idGeneratorUtil.generateIdentificationNumber()).thenReturn("ID2024100001");
        when(clientRepository.existsByClientId(anyString())).thenReturn(false);
        when(clientRepository.existsByIdentificationNumber(anyString())).thenReturn(false);
        when(userRepository.existsByUsername("testuser")).thenReturn(true); // Username already exists

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bankAgentService.createClient(testRequest, agentId);
        });

        assertTrue(exception.getMessage().contains("Username already exists"));
    }

    @Test
    void testCreateClient_DuplicateEmailValidation() {
        // Arrange
        Long agentId = 1L;

        when(bankAgentRepository.findById(agentId)).thenReturn(Optional.of(testAgent));
        when(idGeneratorUtil.generateClientId()).thenReturn("CLT001001");
        when(idGeneratorUtil.generateIdentificationNumber()).thenReturn("ID2024100001");
        when(clientRepository.existsByClientId(anyString())).thenReturn(false);
        when(clientRepository.existsByIdentificationNumber(anyString())).thenReturn(false);
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("testuser@example.com")).thenReturn(true); // Email already exists

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bankAgentService.createClient(testRequest, agentId);
        });

        assertTrue(exception.getMessage().contains("Email already exists"));
    }

    @Test
    void testCreateClient_UniqueIdGeneration() {
        // Arrange
        Long agentId = 1L;
        String firstClientId = "CLT001001";
        String secondClientId = "CLT001002";
        String identificationNumber = "ID2024100001";

        when(bankAgentRepository.findById(agentId)).thenReturn(Optional.of(testAgent));
        when(idGeneratorUtil.generateClientId())
                .thenReturn(firstClientId)
                .thenReturn(secondClientId);
        when(idGeneratorUtil.generateIdentificationNumber()).thenReturn(identificationNumber);
        when(clientRepository.existsByClientId(firstClientId)).thenReturn(true); // First ID exists
        when(clientRepository.existsByClientId(secondClientId)).thenReturn(false); // Second ID is unique
        when(clientRepository.existsByIdentificationNumber(identificationNumber)).thenReturn(false);
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("testuser@example.com")).thenReturn(false);
        when(passwordGenerator.generatePassword()).thenReturn("SecurePass123!");
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(clientRepository.save(any(Client.class))).thenAnswer(invocation -> {
            Client client = invocation.getArgument(0);
            client.setId(1L);
            return client;
        });

        // Mock account creation
        when(accountRepository.save(any())).thenAnswer(invocation -> {
            Account account = invocation.getArgument(0);
            account.setId(1L);
            return account;
        });

        // Mock crypto wallet creation
        when(cryptoWalletRepository.save(any())).thenAnswer(invocation -> {
            CryptoWallet wallet = invocation.getArgument(0);
            wallet.setId(1L);
            return wallet;
        });

        // Act
        ClientEnrollmentResponse response = bankAgentService.createClient(testRequest, agentId);

        // Assert
        assertNotNull(response);
        assertEquals(secondClientId, response.getClient().getClientId());

        // Verify that ID generation was called twice due to collision
        verify(idGeneratorUtil, times(2)).generateClientId();
        verify(clientRepository, times(2)).existsByClientId(anyString());
    }
}
