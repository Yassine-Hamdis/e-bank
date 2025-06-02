package com.ebanking.service.impl;

import com.ebanking.dto.request.ClientCreateRequest;
import com.ebanking.dto.request.ClientUpdateRequest;
import com.ebanking.dto.request.DepositRequest;
import com.ebanking.dto.response.*;
import com.ebanking.entity.*;
import com.ebanking.repository.*;
import com.ebanking.service.BankAgentService;
import com.ebanking.service.EmailService;
import com.ebanking.service.NotificationService;
import com.ebanking.util.PasswordGenerator;
import com.ebanking.util.IdGeneratorUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of BankAgentService
 */
@Service
@Transactional
public class BankAgentServiceImpl implements BankAgentService {

    private static final Logger logger = LoggerFactory.getLogger(BankAgentServiceImpl.class);

    @Autowired
    private BankAgentRepository bankAgentRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CryptoWalletRepository cryptoWalletRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PasswordGenerator passwordGenerator;

    @Autowired
    private IdGeneratorUtil idGeneratorUtil;

    @Autowired
    private EmailService emailService;

    @Autowired
    private NotificationService notificationService;

    @Override
    public ClientEnrollmentResponse createClient(ClientCreateRequest request, Long agentId) {
        logger.info("Creating client by agent: {}", agentId);

        // Find the bank agent
        BankAgent agent = bankAgentRepository.findById(agentId)
                .orElseThrow(() -> new RuntimeException("Bank agent not found with ID: " + agentId));

        // Auto-generate unique client ID
        String clientId = generateUniqueClientId();

        // Auto-generate unique identification number
        String identificationNumber = generateUniqueIdentificationNumber();

        // Auto-set enrollment date to current date
        LocalDateTime enrollmentDate = LocalDateTime.now();

        logger.info("Generated client ID: {} and identification number: {}", clientId, identificationNumber);

        // Validate required fields
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            throw new RuntimeException("Username is required");
        }
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new RuntimeException("Email is required");
        }
        if (request.getPhoneNumber() == null || request.getPhoneNumber().trim().isEmpty()) {
            throw new RuntimeException("Phone number is required");
        }

        // Check for duplicate username and email
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists: " + request.getUsername());
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists: " + request.getEmail());
        }

        // Create client
        Client client = new Client();
        client.setClientId(clientId);
        client.setIdentificationNumber(identificationNumber);
        client.setEnrollmentDate(enrollmentDate.toLocalDate());
        client.setAddress(request.getAddress());
        client.setUsername(request.getUsername());
        client.setEmail(request.getEmail());
        client.setPhoneNumber(request.getPhoneNumber());
        client.setNationalId(request.getNationalId());
        client.setManagingAgent(agent);

        // Generate secure password
        String generatedPassword = passwordGenerator.generatePassword();
        client.setPassword(passwordEncoder.encode(generatedPassword));
        logger.debug("Generated secure password for client: {}", client.getUsername());

        // Save client
        Client savedClient = clientRepository.save(client);
        logger.info("Client created successfully with ID: {}", savedClient.getClientId());

        // Create Account automatically
        Account account = createAccountForClient(savedClient);
        logger.info("Account created automatically for client: {}", account.getAccountId());

        // Create CryptoWallet automatically
        CryptoWallet cryptoWallet = createCryptoWalletForClient(savedClient);
        logger.info("CryptoWallet created automatically for client: {}", cryptoWallet.getWalletAddress());

        // Send credentials via email
        try {
            emailService.sendClientCredentials(
                savedClient.getEmail(),
                savedClient.getUsername(),
                savedClient.getUsername(),
                generatedPassword,
                savedClient.getClientId(),
                account.getAccountId()
            );
            logger.info("Credentials email sent successfully to client: {}", savedClient.getEmail());
        } catch (Exception e) {
            logger.error("Failed to send credentials email to client: {}", savedClient.getEmail(), e);
            // Note: We don't throw exception here to avoid rolling back the client creation
            // The client is created successfully, but email sending failed
        }

        // Convert to DTOs and return response
        ClientDTO clientDTO = convertToClientDTO(savedClient);
        AccountDTO accountDTO = convertToAccountDTO(account);
        CryptoWalletDTO cryptoWalletDTO = convertToCryptoWalletDTO(cryptoWallet);

        return new ClientEnrollmentResponse(clientDTO, accountDTO, cryptoWalletDTO);
    }

    private Account createAccountForClient(Client client) {
        String accountId = generateAccountId();

        Account account = new Account();
        account.setAccountId(accountId);
        account.setClient(client);
        account.setAccountType(AccountType.CHECKING);
        account.setBalance(BigDecimal.ZERO);
        account.setStatus(AccountStatus.ACTIVE);
        account.setCurrency("MAD");

        return accountRepository.save(account);
    }

    private CryptoWallet createCryptoWalletForClient(Client client) {
        String walletAddress = generateWalletAddress();

        CryptoWallet cryptoWallet = new CryptoWallet();
        cryptoWallet.setWalletAddress(walletAddress);
        cryptoWallet.setClient(client);
        cryptoWallet.setStatus(WalletStatus.ACTIVE);

        return cryptoWalletRepository.save(cryptoWallet);
    }

    private String generateAccountId() {
        String prefix = "ACC";
        String suffix = String.format("%06d", (int) (Math.random() * 1000000));
        return prefix + suffix;
    }

    private String generateWalletAddress() {
        // Generate a Bitcoin-like address for demo purposes
        return "1" + UUID.randomUUID().toString().replace("-", "").substring(0, 32);
    }

    private String generateTransactionId() {
        return "TXN" + System.currentTimeMillis();
    }

    /**
     * Generate a unique client ID ensuring no duplicates exist
     */
    private String generateUniqueClientId() {
        String clientId;
        int attempts = 0;
        do {
            clientId = idGeneratorUtil.generateClientId();
            attempts++;
            if (attempts > 100) {
                throw new RuntimeException("Unable to generate unique client ID after 100 attempts");
            }
        } while (clientRepository.existsByClientId(clientId));

        return clientId;
    }

    /**
     * Generate a unique identification number ensuring no duplicates exist
     */
    private String generateUniqueIdentificationNumber() {
        String identificationNumber;
        int attempts = 0;
        do {
            identificationNumber = idGeneratorUtil.generateIdentificationNumber();
            attempts++;
            if (attempts > 100) {
                throw new RuntimeException("Unable to generate unique identification number after 100 attempts");
            }
        } while (clientRepository.existsByIdentificationNumber(identificationNumber));

        return identificationNumber;
    }

    @Override
    public List<ClientDTO> getManagedClients(Long agentId) {
        logger.debug("Fetching managed clients for agent: {}", agentId);

        BankAgent agent = bankAgentRepository.findById(agentId)
                .orElseThrow(() -> new RuntimeException("Bank agent not found with ID: " + agentId));

        List<Client> clients = clientRepository.findByManagingAgent(agent);

        return clients.stream()
                .map(this::convertToClientDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ClientDTO getClientById(String clientId, Long agentId) {
        logger.debug("Fetching client: {} for agent: {}", clientId, agentId);

        Client client = clientRepository.findByClientId(clientId)
                .orElseThrow(() -> new RuntimeException("Client not found with ID: " + clientId));

        // Check if agent has access to this client
        if (!client.getManagingAgent().getId().equals(agentId)) {
            throw new RuntimeException("Access denied: Agent does not manage this client");
        }

        return convertToClientDTO(client);
    }

    @Override
    public ClientDTO updateClient(String clientId, ClientUpdateRequest request, Long agentId) {
        logger.info("Updating client: {} by agent: {}", clientId, agentId);

        Client client = clientRepository.findByClientId(clientId)
                .orElseThrow(() -> new RuntimeException("Client not found with ID: " + clientId));

        // Check if agent has access to this client
        if (!client.getManagingAgent().getId().equals(agentId)) {
            throw new RuntimeException("Access denied: Agent does not manage this client");
        }

        // Update client fields (only if provided)
        if (request.getAddress() != null && !request.getAddress().trim().isEmpty()) {
            client.setAddress(request.getAddress());
        }
        if (request.getIdentificationNumber() != null && !request.getIdentificationNumber().trim().isEmpty()) {
            client.setIdentificationNumber(request.getIdentificationNumber());
        }
        if (request.getUsername() != null && !request.getUsername().trim().isEmpty()) {
            client.setUsername(request.getUsername());
        }
        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            client.setEmail(request.getEmail());
        }
        if (request.getPhoneNumber() != null && !request.getPhoneNumber().trim().isEmpty()) {
            client.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getNationalId() != null && !request.getNationalId().trim().isEmpty()) {
            client.setNationalId(request.getNationalId());
        }

        Client updatedClient = clientRepository.save(client);
        logger.info("Client updated successfully: {}", clientId);

        return convertToClientDTO(updatedClient);
    }

    @Override
    public void deleteClient(String clientId, Long agentId) {
        logger.info("Deleting client: {} by agent: {}", clientId, agentId);

        Client client = clientRepository.findByClientId(clientId)
                .orElseThrow(() -> new RuntimeException("Client not found with ID: " + clientId));

        // Check if agent has access to this client
        if (!client.getManagingAgent().getId().equals(agentId)) {
            throw new RuntimeException("Access denied: Agent does not manage this client");
        }

        clientRepository.delete(client);
        logger.info("Client deleted successfully: {}", clientId);
    }

    @Override
    public List<TransactionDTO> getTransactionsForVerification(Long agentId) {
        logger.debug("Fetching transactions for verification by agent: {}", agentId);

        // Validate bank agent exists
        BankAgent agent = bankAgentRepository.findById(agentId)
                .orElseThrow(() -> new RuntimeException("Bank agent not found with ID: " + agentId));

        // Get pending transactions for clients managed by this agent
        List<Transaction> pendingTransactions = transactionRepository.findPendingTransactionsByManagedClients(agentId);

        logger.debug("Found {} pending transactions for agent {}'s managed clients", pendingTransactions.size(), agentId);

        return pendingTransactions.stream()
                .map(this::convertToTransactionDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TransactionDTO> getAllTransactionsForManagedClients(Long agentId) {
        logger.debug("Fetching all transactions for clients managed by agent: {}", agentId);

        // Validate bank agent exists
        BankAgent agent = bankAgentRepository.findById(agentId)
                .orElseThrow(() -> new RuntimeException("Bank agent not found with ID: " + agentId));

        // Get all transactions for clients managed by this agent
        List<Transaction> transactions = transactionRepository.findTransactionsByManagedClients(agentId);

        logger.debug("Found {} total transactions for agent {}'s managed clients", transactions.size(), agentId);

        return transactions.stream()
                .map(this::convertToTransactionDTO)
                .collect(Collectors.toList());
    }

    @Override
    public TransactionDTO verifyTransaction(String transactionId, TransactionStatus status, String description, Long agentId) {
        logger.info("Verifying transaction: {} with status: {} by agent: {}", transactionId, status, agentId);

        Transaction transaction = transactionRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found with ID: " + transactionId));

        BankAgent agent = bankAgentRepository.findById(agentId)
                .orElseThrow(() -> new RuntimeException("Bank agent not found with ID: " + agentId));

        // Check if agent has permission to verify this transaction
        boolean hasPermission = false;
        if (transaction.getFromAccount() != null &&
            transaction.getFromAccount().getClient().getManagingAgent().getId().equals(agentId)) {
            hasPermission = true;
        } else if (transaction.getToAccount() != null &&
                   transaction.getToAccount().getClient().getManagingAgent().getId().equals(agentId)) {
            hasPermission = true;
        }

        if (!hasPermission) {
            throw new RuntimeException("Access denied: Agent does not manage the client involved in this transaction");
        }

        // Update transaction
        transaction.setStatus(status);
        transaction.setVerificationNotes(description);
        transaction.setVerifiedByAgent(agent);
        transaction.setVerificationDate(LocalDateTime.now());

        Transaction updatedTransaction = transactionRepository.save(transaction);
        logger.info("Transaction verified successfully: {}", transactionId);

        return convertToTransactionDTO(updatedTransaction);
    }

    @Override
    public List<ClientEnrollmentHistoryDTO> getEnrollmentHistory(Long agentId) {
        logger.debug("Fetching enrollment history for agent: {}", agentId);

        BankAgent agent = bankAgentRepository.findById(agentId)
                .orElseThrow(() -> new RuntimeException("Bank agent not found with ID: " + agentId));

        List<Client> clients = clientRepository.findByManagingAgent(agent);

        return clients.stream()
                .map(client -> new ClientEnrollmentHistoryDTO(
                    client.getClientId(),
                    client.getEnrollmentDate().format(DateTimeFormatter.ISO_LOCAL_DATE) + "T10:30:00Z",
                    agent.getEmployeeId(),
                    client.getStatus().toString()
                ))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TransactionDTO createDeposit(DepositRequest request, Long agentId) {
        logger.info("Creating deposit for client: {} account: {} amount: {} by agent: {}",
                   request.getClientId(), request.getAccountId(), request.getAmount(), agentId);

        // Validate bank agent
        BankAgent agent = bankAgentRepository.findById(agentId)
                .orElseThrow(() -> new RuntimeException("Bank agent not found with ID: " + agentId));

        // Validate client
        Client client = clientRepository.findByClientId(request.getClientId())
                .orElseThrow(() -> new RuntimeException("Client not found with ID: " + request.getClientId()));

        // Check if agent manages this client
        if (!client.getManagingAgent().getId().equals(agentId)) {
            throw new RuntimeException("Access denied: Agent does not manage this client");
        }

        // Validate account
        Account account = accountRepository.findByAccountId(request.getAccountId())
                .orElseThrow(() -> new RuntimeException("Account not found with ID: " + request.getAccountId()));

        // Check if account belongs to the client
        if (!account.getClient().getClientId().equals(request.getClientId())) {
            throw new RuntimeException("Account does not belong to the specified client");
        }

        // Check account status
        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new RuntimeException("Cannot deposit to inactive account");
        }

        // Generate unique transaction ID
        String transactionId = generateTransactionId();

        // Create deposit transaction
        Transaction depositTransaction = new Transaction();
        depositTransaction.setTransactionId(transactionId);
        depositTransaction.setAmount(request.getAmount());
        depositTransaction.setType(TransactionType.DEPOSIT);
        depositTransaction.setStatus(TransactionStatus.VERIFIED); // Deposits by agents are auto-verified
        depositTransaction.setDescription(request.getDescription() != null ?
                                        request.getDescription() :
                                        "Deposit by agent " + agent.getEmployeeId());
        depositTransaction.setToAccount(account); // Deposit goes TO the account
        depositTransaction.setDate(request.getDate() != null ? request.getDate() : LocalDateTime.now());
        depositTransaction.setVerifiedByAgent(agent);
        depositTransaction.setVerificationDate(LocalDateTime.now());
        depositTransaction.setVerificationNotes("Auto-verified deposit by bank agent");

        // Update account balance
        BigDecimal newBalance = account.getBalance().add(request.getAmount());
        account.setBalance(newBalance);
        account.setUpdatedDate(LocalDateTime.now());

        // Save entities
        accountRepository.save(account);
        Transaction savedTransaction = transactionRepository.save(depositTransaction);

        // Send notification to client about the deposit
        try {
            notificationService.createTransactionNotification(client, transactionId,
                    request.getAmount().toString(), "DEPOSIT");
            logger.info("Deposit notification sent to client: {}", client.getClientId());
        } catch (Exception e) {
            logger.warn("Failed to send deposit notification to client {}: {}", client.getClientId(), e.getMessage());
        }

        logger.info("Deposit transaction created successfully: {} for amount: {} new balance: {}",
                   transactionId, request.getAmount(), newBalance);

        return convertToTransactionDTO(savedTransaction);
    }

    @Override
    @Transactional(readOnly = true)
    public AgentDepositStatisticsDTO getDepositStatistics(Long agentId) {
        logger.info("Fetching deposit statistics for agent: {}", agentId);

        // Validate bank agent
        BankAgent agent = bankAgentRepository.findById(agentId)
                .orElseThrow(() -> new RuntimeException("Bank agent not found with ID: " + agentId));

        // Calculate date ranges
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfToday = now.toLocalDate().atStartOfDay();
        LocalDateTime endOfToday = startOfToday.plusDays(1).minusSeconds(1);

        LocalDateTime startOfWeek = now.toLocalDate().atStartOfDay().minusDays(now.getDayOfWeek().getValue() - 1);
        LocalDateTime endOfWeek = startOfWeek.plusDays(7).minusSeconds(1);

        LocalDateTime startOfMonth = now.toLocalDate().withDayOfMonth(1).atStartOfDay();
        LocalDateTime endOfMonth = startOfMonth.plusMonths(1).minusSeconds(1);

        // Get statistics from repository
        long totalDeposits = transactionRepository.countDepositsByAgent(agentId);
        BigDecimal totalDepositAmount = transactionRepository.sumDepositAmountByAgent(agentId);

        long depositsToday = transactionRepository.countDepositsByAgentAndDateRange(agentId, startOfToday, endOfToday);
        BigDecimal depositAmountToday = transactionRepository.sumDepositAmountByAgentAndDateRange(agentId, startOfToday, endOfToday);

        long depositsThisWeek = transactionRepository.countDepositsByAgentAndDateRange(agentId, startOfWeek, endOfWeek);
        BigDecimal depositAmountThisWeek = transactionRepository.sumDepositAmountByAgentAndDateRange(agentId, startOfWeek, endOfWeek);

        long depositsThisMonth = transactionRepository.countDepositsByAgentAndDateRange(agentId, startOfMonth, endOfMonth);
        BigDecimal depositAmountThisMonth = transactionRepository.sumDepositAmountByAgentAndDateRange(agentId, startOfMonth, endOfMonth);

        BigDecimal largestDeposit = transactionRepository.findLargestDepositByAgent(agentId);
        BigDecimal smallestDeposit = transactionRepository.findSmallestDepositByAgent(agentId);
        LocalDateTime lastDepositDate = transactionRepository.findLastDepositDateByAgent(agentId);

        // Calculate average deposit amount
        BigDecimal averageDepositAmount = BigDecimal.ZERO;
        if (totalDeposits > 0 && totalDepositAmount.compareTo(BigDecimal.ZERO) > 0) {
            averageDepositAmount = totalDepositAmount.divide(BigDecimal.valueOf(totalDeposits), 2, RoundingMode.HALF_UP);
        }

        // Get managed clients count
        long managedClientsCount = agent.getManagedClients().size();

        // Create and return statistics DTO
        AgentDepositStatisticsDTO statistics = new AgentDepositStatisticsDTO(
            agent.getId().toString(),
            agent.getEmployeeId(),
            agent.getUsername(),
            agent.getBranch(),
            totalDeposits,
            totalDepositAmount,
            depositsToday,
            depositAmountToday,
            depositsThisWeek,
            depositAmountThisWeek,
            depositsThisMonth,
            depositAmountThisMonth,
            averageDepositAmount,
            largestDeposit,
            smallestDeposit,
            lastDepositDate,
            managedClientsCount
        );

        logger.info("Deposit statistics retrieved successfully for agent: {} - Total deposits: {}, Total amount: {}",
                   agentId, totalDeposits, totalDepositAmount);

        return statistics;
    }

    // Conversion methods
    private ClientDTO convertToClientDTO(Client client) {
        ClientDTO dto = new ClientDTO();
        dto.setId(client.getId());
        dto.setClientId(client.getClientId());
        dto.setAddress(client.getAddress());
        dto.setIdentificationNumber(client.getIdentificationNumber());
        dto.setEnrollmentDate(client.getEnrollmentDate().atStartOfDay());
        dto.setUsername(client.getUsername());
        dto.setEmail(client.getEmail());
        dto.setPhoneNumber(client.getPhoneNumber());
        dto.setNationalId(client.getNationalId());
        dto.setDateOfBirth(client.getDateOfBirth());
        dto.setStatus(client.getStatus().toString());

        // Set client attributes from diagram
        List<String> accountIds = client.getAccounts().stream()
                .map(Account::getAccountId)
                .collect(Collectors.toList());
        dto.setViewAccount(accountIds);
        dto.setMakeTransfer(true);
        dto.setViewHistory(Arrays.asList()); // Empty for now

        return dto;
    }

    private AccountDTO convertToAccountDTO(Account account) {
        AccountDTO dto = new AccountDTO();
        dto.setId(account.getId());
        dto.setAccountId(account.getAccountId());
        dto.setClientId(account.getClient().getClientId());
        dto.setBalance(account.getBalance());
        dto.setAccountType(account.getAccountType().toString());
        dto.setStatus(account.getStatus().toString());
        dto.setCurrency(account.getCurrency());
        dto.setCreatedDate(account.getCreatedDate());
        return dto;
    }

    private CryptoWalletDTO convertToCryptoWalletDTO(CryptoWallet cryptoWallet) {
        CryptoWalletDTO dto = new CryptoWalletDTO();
        dto.setId(cryptoWallet.getId());
        dto.setWalletAddress(cryptoWallet.getWalletAddress());
        dto.setClientId(cryptoWallet.getClient().getClientId());
        dto.setStatus(cryptoWallet.getStatus().toString());
        dto.setSupportedCryptos(cryptoWallet.getSupportedCryptos());
        dto.setCreatedDate(cryptoWallet.getCreatedDate());
        return dto;
    }

    private TransactionDTO convertToTransactionDTO(Transaction transaction) {
        TransactionDTO dto = new TransactionDTO();
        dto.setId(transaction.getId());
        dto.setTransactionId(transaction.getTransactionId());
        dto.setAmount(transaction.getAmount());
        dto.setDate(transaction.getDate());
        dto.setStatus(transaction.getStatus().toString());
        dto.setDescription(transaction.getDescription());
        dto.setType(transaction.getType().toString());

        if (transaction.getFromAccount() != null) {
            dto.setFromAccountId(transaction.getFromAccount().getAccountId());
        }
        if (transaction.getToAccount() != null) {
            dto.setToAccountId(transaction.getToAccount().getAccountId());
        }
        if (transaction.getVerifiedByAgent() != null) {
            dto.setVerifiedByAgentId(transaction.getVerifiedByAgent().getEmployeeId());
        }

        dto.setVerificationDate(transaction.getVerificationDate());
        dto.setVerificationNotes(transaction.getVerificationNotes());

        return dto;
    }
}
