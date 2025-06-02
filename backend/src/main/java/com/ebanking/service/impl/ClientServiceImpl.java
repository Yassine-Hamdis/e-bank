package com.ebanking.service.impl;

import com.ebanking.dto.request.*;
import com.ebanking.dto.response.*;
import com.ebanking.entity.*;
import com.ebanking.repository.*;
import com.ebanking.service.ClientService;
import com.ebanking.service.NotificationService;
import com.ebanking.service.CurrencyService;
import com.ebanking.service.BinanceService;
import com.ebanking.service.GlobalSettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of ClientService
 */
@Service
@Transactional
public class ClientServiceImpl implements ClientService {

    private static final Logger logger = LoggerFactory.getLogger(ClientServiceImpl.class);

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CryptoWalletRepository cryptoWalletRepository;

    @Autowired
    private CryptoWalletBalanceRepository cryptoWalletBalanceRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private CurrencyService currencyService;

    @Autowired
    private BinanceService binanceService;

    @Autowired
    private GlobalSettingsService globalSettingsService;

    @Override
    @Transactional(readOnly = true)
    public ClientProfileDTO getClientProfile(Long clientId) {
        logger.debug("Fetching profile for client ID: {}", clientId);

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client not found with ID: " + clientId));

        return convertToClientProfileDTO(client);
    }

    @Override
    @Transactional(readOnly = true)
    public AccountDTO getMainAccount(Long clientId) {
        logger.debug("Fetching main account for client ID: {}", clientId);

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client not found with ID: " + clientId));

        // Get the first account (main account)
        Account mainAccount = accountRepository.findByClientAndAccountType(client, AccountType.CHECKING)
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Main account not found for client"));

        return convertToAccountDTO(mainAccount);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getAccountBalance(Long clientId) {
        logger.debug("Fetching account balance for client ID: {}", clientId);

        AccountDTO mainAccount = getMainAccount(clientId);

        Map<String, Object> response = new HashMap<>();
        response.put("accountId", mainAccount.getAccountId());
        response.put("balance", mainAccount.getBalance());
        response.put("currency", mainAccount.getCurrency());
        response.put("lastUpdated", LocalDateTime.now());

        return response;
    }

    @Override
    public AccountDTO createAdditionalAccount(Map<String, Object> request, Long clientId) {
        logger.info("Creating additional account for client ID: {}", clientId);

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client not found with ID: " + clientId));

        String accountTypeStr = (String) request.get("accountType");
        AccountType accountType = AccountType.valueOf(accountTypeStr.toUpperCase());

        BigDecimal initialDeposit = new BigDecimal(request.get("initialDeposit").toString());
        String currency = (String) request.getOrDefault("currency", "MAD");

        // Generate unique account ID
        String accountId = generateAccountId();

        Account newAccount = new Account(accountId, client, accountType, currency);
        newAccount.setBalance(initialDeposit);

        Account savedAccount = accountRepository.save(newAccount);
        logger.info("Additional account created successfully: {}", accountId);

        // Create notification
        notificationService.createNotification(client,
                "New Account Created",
                String.format("Your new %s account has been created successfully. Account ID: %s",
                            accountType.name(), accountId),
                Notification.NotificationType.INFO,
                Notification.NotificationPriority.NORMAL);

        return convertToAccountDTO(savedAccount);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountDTO> getAllAccounts(Long clientId) {
        logger.debug("Fetching all accounts for client ID: {}", clientId);

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client not found with ID: " + clientId));

        List<Account> accounts = accountRepository.findByClient(client);

        return accounts.stream()
                .map(this::convertToAccountDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AccountDTO getAccountById(String accountId, Long clientId) {
        logger.debug("Fetching account: {} for client ID: {}", accountId, clientId);

        Account account = accountRepository.findByAccountId(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found with ID: " + accountId));

        // Security check: ensure account belongs to the client
        if (!account.getClient().getId().equals(clientId)) {
            throw new RuntimeException("Access denied: Account does not belong to client");
        }

        return convertToAccountDTO(account);
    }

    @Override
    public TransactionDTO createTransaction(TransactionCreateRequest request, Long clientId) {
        logger.info("Creating transaction for client ID: {}", clientId);

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client not found with ID: " + clientId));

        // Get client's main account
        Account mainAccount = accountRepository.findByClientAndAccountType(client, AccountType.CHECKING)
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Main account not found for client"));

        // Generate unique transaction ID
        String transactionId = generateTransactionId();

        Transaction transaction = new Transaction();
        transaction.setTransactionId(transactionId);
        transaction.setAmount(request.getAmount());
        transaction.setDescription(request.getDescription());
        transaction.setType(TransactionType.valueOf(request.getType()));
        transaction.setStatus(TransactionStatus.valueOf(request.getStatus()));
        transaction.setFromAccount(mainAccount);
        transaction.setDate(request.getDate() != null ? request.getDate() : LocalDateTime.now());

        Transaction savedTransaction = transactionRepository.save(transaction);
        logger.info("Transaction created successfully: {}", transactionId);

        // Create notification
        notificationService.createTransactionNotification(client, transactionId,
                request.getAmount().toString(), request.getType());

        return convertToTransactionDTO(savedTransaction);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionDTO> getClientTransactions(Long clientId) {
        logger.debug("Fetching transactions for client ID: {}", clientId);

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client not found with ID: " + clientId));

        List<Transaction> transactions = transactionRepository.findByClientId(client.getClientId());

        return transactions.stream()
                .map(this::convertToTransactionDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TransactionDTO getTransactionById(String transactionId, Long clientId) {
        logger.debug("Fetching transaction: {} for client ID: {}", transactionId, clientId);

        Transaction transaction = transactionRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found with ID: " + transactionId));

        // Security check: ensure transaction belongs to the client
        boolean belongsToClient = false;
        if (transaction.getFromAccount() != null &&
            transaction.getFromAccount().getClient().getId().equals(clientId)) {
            belongsToClient = true;
        }
        if (transaction.getToAccount() != null &&
            transaction.getToAccount().getClient().getId().equals(clientId)) {
            belongsToClient = true;
        }

        if (!belongsToClient) {
            throw new RuntimeException("Access denied: Transaction does not belong to client");
        }

        return convertToTransactionDTO(transaction);
    }

    // Utility methods for ID generation
    private String generateAccountId() {
        return "ACC" + System.currentTimeMillis();
    }

    private String generateTransactionId() {
        return "TXN" + System.currentTimeMillis();
    }

    /**
     * Calculate fee based on global fee percentage setting
     * @param amount the base amount to calculate fee on
     * @return calculated fee amount
     */
    private BigDecimal calculateGlobalFee(BigDecimal amount) {
        try {
            BigDecimal feePercentage = globalSettingsService.getSettingValueAsDecimal("feePercentage");
            if (feePercentage == null) {
                logger.warn("Fee percentage setting not found, using default 1.5%");
                feePercentage = new BigDecimal("1.5");
            }

            // Calculate fee: amount * (percentage / 100)
            BigDecimal fee = amount.multiply(feePercentage).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP);
            logger.debug("Calculated fee: {} MAD ({}% of {} MAD)", fee, feePercentage, amount);
            return fee;
        } catch (Exception e) {
            logger.error("Error calculating global fee, using default 1.5%: {}", e.getMessage());
            return amount.multiply(new BigDecimal("0.015")).setScale(2, BigDecimal.ROUND_HALF_UP);
        }
    }

    // Conversion methods
    private ClientProfileDTO convertToClientProfileDTO(Client client) {
        ClientProfileDTO dto = new ClientProfileDTO();
        dto.setClientId(client.getClientId());
        dto.setAddress(client.getAddress());
        dto.setIdentificationNumber(client.getIdentificationNumber());
        dto.setEnrollmentDate(client.getEnrollmentDate().atStartOfDay());
        dto.setUsername(client.getUsername());
        dto.setEmail(client.getEmail());
        dto.setPhoneNumber(client.getPhoneNumber());
        dto.setStatus(client.getStatus().name());
        dto.setLastLogin(client.getLastLogin());
        dto.setNationalId(client.getNationalId());
        dto.setDateOfBirth(client.getDateOfBirth());
        return dto;
    }

    private AccountDTO convertToAccountDTO(Account account) {
        AccountDTO dto = new AccountDTO();
        dto.setId(account.getId());
        dto.setAccountId(account.getAccountId());
        dto.setClientId(account.getClient().getClientId());
        dto.setBalance(account.getBalance());
        dto.setAccountType(account.getAccountType().name());
        dto.setStatus(account.getStatus().name());
        dto.setCreatedDate(account.getCreatedDate());
        dto.setCurrency(account.getCurrency());
        dto.setLastTransactionDate(account.getUpdatedDate());
        return dto;
    }

    private TransactionDTO convertToTransactionDTO(Transaction transaction) {
        TransactionDTO dto = new TransactionDTO();
        dto.setId(transaction.getId());
        dto.setTransactionId(transaction.getTransactionId());
        dto.setAmount(transaction.getAmount());
        dto.setDate(transaction.getDate());
        dto.setStatus(transaction.getStatus().name());
        dto.setDescription(transaction.getDescription());
        dto.setType(transaction.getType().name());

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

    @Override
    public TransactionDTO createTransfer(TransferRequest request, Long clientId) {
        logger.info("Creating transfer for client ID: {}", clientId);

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client not found with ID: " + clientId));

        // Validate source and destination accounts
        Account sourceAccount = accountRepository.findByAccountId(request.getSourceAccountId())
                .orElseThrow(() -> new RuntimeException("Source account not found"));

        Account destinationAccount = accountRepository.findByAccountId(request.getDestinationAccountId())
                .orElseThrow(() -> new RuntimeException("Destination account not found"));

        // Security check: ensure source account belongs to the client
        if (!sourceAccount.getClient().getId().equals(clientId)) {
            throw new RuntimeException("Access denied: Source account does not belong to client");
        }

        // Calculate transfer fee based on global fee percentage
        BigDecimal transferFee = calculateGlobalFee(request.getAmount());
        BigDecimal totalAmount = request.getAmount().add(transferFee);

        // Check sufficient balance
        if (sourceAccount.getBalance().compareTo(totalAmount) < 0) {
            throw new RuntimeException("Insufficient balance for transfer. Required: " +
                                     totalAmount + " MAD (Amount: " + request.getAmount() +
                                     " + Fee: " + transferFee + "), Available: " + sourceAccount.getBalance() + " MAD");
        }

        // Generate unique transaction ID
        String transactionId = generateTransactionId();

        // Create Transfer entity
        Transfer transfer = new Transfer();
        transfer.setTransactionId(transactionId);
        transfer.setAmount(request.getAmount());
        transfer.setDescription(request.getDescription());
        transfer.setSourceAccountId(request.getSourceAccountId());
        transfer.setDestinationAccountId(request.getDestinationAccountId());
        transfer.setTransferFee(transferFee); // Use calculated fee instead of request fee
        transfer.setFromAccount(sourceAccount);
        transfer.setToAccount(destinationAccount);
        transfer.setStatus(TransactionStatus.PENDING);

        // Update account balances
        sourceAccount.setBalance(sourceAccount.getBalance().subtract(totalAmount));
        destinationAccount.setBalance(destinationAccount.getBalance().add(request.getAmount()));

        accountRepository.save(sourceAccount);
        accountRepository.save(destinationAccount);

        Transaction savedTransaction = transactionRepository.save(transfer);
        logger.info("Transfer created successfully: {} with fee: {} MAD", transactionId, transferFee);

        // Create notification for sender
        notificationService.createTransactionNotification(client, transactionId,
                request.getAmount().toString(), "TRANSFER_SENT");

        // Create notification for recipient
        Client recipientClient = destinationAccount.getClient();
        notificationService.createTransactionNotification(recipientClient, transactionId,
                request.getAmount().toString(), "TRANSFER_RECEIVED");

        return convertToTransactionDTO(savedTransaction);
    }

    @Override
    public TransactionDTO createMobileRecharge(MobileRechargeRequest request, Long clientId) {
        logger.info("Creating mobile recharge for client ID: {}", clientId);

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client not found with ID: " + clientId));

        // Get client's main account
        Account mainAccount = accountRepository.findByClientAndAccountType(client, AccountType.CHECKING)
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Main account not found for client"));

        // Check sufficient balance
        if (mainAccount.getBalance().compareTo(request.getAmount()) < 0) {
            throw new RuntimeException("Insufficient balance for mobile recharge");
        }

        // Generate unique transaction ID
        String transactionId = generateTransactionId();

        // Create MobileRecharge entity
        MobileRecharge mobileRecharge = new MobileRecharge();
        mobileRecharge.setTransactionId(transactionId);
        mobileRecharge.setAmount(request.getAmount());
        mobileRecharge.setDescription(request.getDescription());
        mobileRecharge.setPhoneNumber(request.getPhoneNumber());
        mobileRecharge.setOperator(request.getOperator());
        mobileRecharge.setRechargeType(request.getRechargeType());
        mobileRecharge.setFromAccount(mainAccount);
        mobileRecharge.setStatus(TransactionStatus.PENDING);

        // Update account balance
        mainAccount.setBalance(mainAccount.getBalance().subtract(request.getAmount()));
        accountRepository.save(mainAccount);

        Transaction savedTransaction = transactionRepository.save(mobileRecharge);
        logger.info("Mobile recharge created successfully: {}", transactionId);

        // Create notification
        notificationService.createTransactionNotification(client, transactionId,
                request.getAmount().toString(), "MOBILE_RECHARGE");

        return convertToTransactionDTO(savedTransaction);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getCryptoWallet(Long clientId) {
        logger.debug("Fetching crypto wallet for client ID: {}", clientId);

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client not found with ID: " + clientId));

        CryptoWallet wallet = cryptoWalletRepository.findByClient(client)
                .orElseThrow(() -> new RuntimeException("Crypto wallet not found for client"));

        // Get actual crypto balances from database
        List<CryptoWalletBalance> walletBalances = cryptoWalletBalanceRepository.findByCryptoWallet(wallet);

        Map<String, Object> response = new HashMap<>();
        response.put("id", wallet.getId());
        response.put("walletAddress", wallet.getWalletAddress());
        response.put("clientId", client.getClientId());
        response.put("createdDate", wallet.getCreatedDate());
        response.put("updatedDate", wallet.getUpdatedDate());
        response.put("status", wallet.getStatus().name());
        response.put("supportedCryptos", Arrays.asList("BTC", "ETH", "USDT", "BNB"));
        response.put("lastAccessDate", LocalDateTime.now());

        // Build crypto balances map from actual database data
        Map<String, BigDecimal> cryptoBalances = new HashMap<>();
        List<Map<String, Object>> balanceDetails = new ArrayList<>();

        // Initialize all supported cryptos with zero balance
        for (String crypto : Arrays.asList("BTC", "ETH", "USDT", "BNB")) {
            cryptoBalances.put(crypto, BigDecimal.ZERO);
        }

        // Update with actual balances and create detailed balance info
        BigDecimal totalValueMAD = BigDecimal.ZERO;
        for (CryptoWalletBalance balance : walletBalances) {
            cryptoBalances.put(balance.getCryptoType(), balance.getBalance());

            // Create detailed balance entry
            Map<String, Object> balanceDetail = new HashMap<>();
            balanceDetail.put("id", balance.getId());
            balanceDetail.put("cryptoType", balance.getCryptoType());
            balanceDetail.put("balance", balance.getBalance());
            balanceDetail.put("createdDate", balance.getCreatedDate());
            balanceDetail.put("updatedDate", balance.getUpdatedDate());

            // Calculate approximate MAD value (using mock rates for now)
            BigDecimal madValue = calculateCryptoValueInMAD(balance.getCryptoType(), balance.getBalance());
            balanceDetail.put("valueInMAD", madValue);
            totalValueMAD = totalValueMAD.add(madValue);

            balanceDetails.add(balanceDetail);
        }

        response.put("cryptoBalances", cryptoBalances);
        response.put("balanceDetails", balanceDetails);
        response.put("totalValueMAD", totalValueMAD);
        response.put("totalBalances", walletBalances.size());

        return response;
    }

    /**
     * Calculate the approximate value of crypto amount in MAD
     * This uses mock exchange rates - in production, this would use real-time rates
     */
    private BigDecimal calculateCryptoValueInMAD(String cryptoType, BigDecimal cryptoAmount) {
        BigDecimal exchangeRate = getMockExchangeRate(cryptoType);
        return cryptoAmount.multiply(exchangeRate);
    }

    @Override
    public Map<String, Object> updateWalletAddress(WalletUpdateRequest request, Long clientId) {
        logger.info("Updating wallet address for client ID: {}", clientId);

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client not found with ID: " + clientId));

        CryptoWallet wallet = cryptoWalletRepository.findByClient(client)
                .orElseThrow(() -> new RuntimeException("Crypto wallet not found for client"));

        // Check if new address is already in use
        if (cryptoWalletRepository.existsByWalletAddress(request.getNewWalletAddress())) {
            throw new RuntimeException("Wallet address already in use");
        }

        wallet.setWalletAddress(request.getNewWalletAddress());
        CryptoWallet updatedWallet = cryptoWalletRepository.save(wallet);

        logger.info("Wallet address updated successfully for client: {}", clientId);

        // Create security notification
        notificationService.createSecurityNotification(client,
                "Wallet Address Updated",
                "Your crypto wallet address has been updated successfully");

        Map<String, Object> response = new HashMap<>();
        response.put("walletAddress", updatedWallet.getWalletAddress());
        response.put("clientId", client.getClientId());
        response.put("updatedDate", LocalDateTime.now());
        response.put("status", updatedWallet.getStatus().name());

        return response;
    }

    @Override
    public TransactionDTO buyCryptocurrency(CryptoBuyRequest request, Long clientId) {
        logger.info("Creating crypto buy transaction for client ID: {}", clientId);

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client not found with ID: " + clientId));

        // Get client's main account
        Account mainAccount = accountRepository.findByClientAndAccountType(client, AccountType.CHECKING)
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Main account not found for client"));

        // Check sufficient balance
        BigDecimal totalCost = request.getAmount().add(request.getPlatformFee());
        if (mainAccount.getBalance().compareTo(totalCost) < 0) {
            throw new RuntimeException("Insufficient balance for crypto purchase");
        }

        // Generate unique transaction ID
        String transactionId = generateTransactionId();

        // Create CryptoTransaction entity
        CryptoTransaction cryptoTransaction = new CryptoTransaction();
        cryptoTransaction.setTransactionId(transactionId);
        cryptoTransaction.setAmount(request.getAmount());
        cryptoTransaction.setDescription(request.getDescription());
        cryptoTransaction.setCryptoType(request.getCryptoType());
        cryptoTransaction.setExchangeRate(request.getExchangeRate());
        cryptoTransaction.setWalletAddress(request.getWalletAddress());
        cryptoTransaction.setNetworkFee(request.getNetworkFee());
        cryptoTransaction.setPlatformFee(request.getPlatformFee());
        cryptoTransaction.setFromAccount(mainAccount);
        cryptoTransaction.setType(TransactionType.CRYPTO_BUY);
        cryptoTransaction.setStatus(TransactionStatus.PENDING);

        // Update account balance
        mainAccount.setBalance(mainAccount.getBalance().subtract(totalCost));
        accountRepository.save(mainAccount);

        Transaction savedTransaction = transactionRepository.save(cryptoTransaction);
        logger.info("Crypto buy transaction created successfully: {}", transactionId);

        // Create notification
        notificationService.createTransactionNotification(client, transactionId,
                request.getAmount().toString(), "CRYPTO_BUY");

        return convertToTransactionDTO(savedTransaction);
    }

    @Override
    public TransactionDTO sellCryptocurrency(CryptoSellRequest request, Long clientId) {
        logger.info("Creating crypto sell transaction for client ID: {}", clientId);

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client not found with ID: " + clientId));

        // Get client's main account
        Account mainAccount = accountRepository.findByClientAndAccountType(client, AccountType.CHECKING)
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Main account not found for client"));

        // Calculate fiat amount from crypto amount
        BigDecimal fiatAmount = request.calculateFiatAmount();
        BigDecimal netAmount = fiatAmount.subtract(request.getPlatformFee());

        // Generate unique transaction ID
        String transactionId = generateTransactionId();

        // Create CryptoTransaction entity
        CryptoTransaction cryptoTransaction = new CryptoTransaction();
        cryptoTransaction.setTransactionId(transactionId);
        cryptoTransaction.setAmount(fiatAmount);
        cryptoTransaction.setDescription(request.getDescription());
        cryptoTransaction.setCryptoType(request.getCryptoType());
        cryptoTransaction.setExchangeRate(request.getExchangeRate());
        cryptoTransaction.setWalletAddress(request.getWalletAddress());
        cryptoTransaction.setCryptoAmount(request.getCryptoAmount());
        cryptoTransaction.setNetworkFee(request.getNetworkFee());
        cryptoTransaction.setPlatformFee(request.getPlatformFee());
        cryptoTransaction.setToAccount(mainAccount);
        cryptoTransaction.setType(TransactionType.CRYPTO_SELL);
        cryptoTransaction.setStatus(TransactionStatus.PENDING);

        // Update account balance (add the net amount from sale)
        mainAccount.setBalance(mainAccount.getBalance().add(netAmount));
        accountRepository.save(mainAccount);

        Transaction savedTransaction = transactionRepository.save(cryptoTransaction);
        logger.info("Crypto sell transaction created successfully: {}", transactionId);

        // Create notification
        notificationService.createTransactionNotification(client, transactionId,
                fiatAmount.toString(), "CRYPTO_SELL");

        return convertToTransactionDTO(savedTransaction);
    }

    @Override
    public CryptoBuyFromMainResponse buyCryptocurrencyFromMain(CryptoBuyFromMainRequest request, Long clientId) {
        logger.info("Creating crypto buy from main transaction for client ID: {}", clientId);

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client not found with ID: " + clientId));

        // Get client's main account
        Account mainAccount = accountRepository.findByClientAndAccountType(client, AccountType.CHECKING)
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Main account not found for client"));

        // Get client's crypto wallet
        CryptoWallet cryptoWallet = cryptoWalletRepository.findByClient(client)
                .orElseThrow(() -> new RuntimeException("Crypto wallet not found for client"));

        // Step 1: Get MAD to USD exchange rate
        BigDecimal madToUsdRate;
        String rateSource = "BINANCE";
        LocalDateTime rateTimestamp = LocalDateTime.now();

        try {
            if (request.isUseRealTimeRate()) {
                madToUsdRate = binanceService.getMadToUsdRate();
                logger.info("Fetched real-time MAD to USD rate: {}", madToUsdRate);
            } else {
                // Fallback to approximate rate
                madToUsdRate = new BigDecimal("0.10"); // 1 MAD ≈ 0.10 USD
                rateSource = "MOCK";
                logger.warn("Using mock MAD to USD rate: {}", madToUsdRate);
            }
        } catch (Exception e) {
            logger.warn("Failed to fetch real-time MAD/USD rate, using fallback rate: {}", e.getMessage());
            madToUsdRate = new BigDecimal("0.10"); // Fallback rate
            rateSource = "MOCK_FALLBACK";
        }

        // Step 2: Convert MAD amount to USD
        BigDecimal usdAmount = request.getAmount().multiply(madToUsdRate);
        logger.info("Converted {} MAD to {} USD using rate {}", request.getAmount(), usdAmount, madToUsdRate);

        // Step 3: Fetch cryptocurrency price in USD from Binance
        BigDecimal cryptoUsdPrice;
        try {
            if (request.isUseRealTimeRate()) {
                String symbol = request.getCryptoType() + "USDT";
                CurrencyDTO currency = binanceService.fetchCryptocurrencyPrice(symbol);
                cryptoUsdPrice = currency.getCurrentPrice();
                logger.info("Fetched real-time {} price in USD: {}", request.getCryptoType(), cryptoUsdPrice);
            } else {
                // Fallback to mock rates in USD
                cryptoUsdPrice = getMockExchangeRateInUsd(request.getCryptoType());
                logger.warn("Using mock {} price in USD: {}", request.getCryptoType(), cryptoUsdPrice);
            }
        } catch (Exception e) {
            logger.warn("Failed to fetch real-time crypto price for {}, using mock rate: {}",
                       request.getCryptoType(), e.getMessage());
            cryptoUsdPrice = getMockExchangeRateInUsd(request.getCryptoType());
        }

        // Step 4: Calculate crypto amount using USD
        BigDecimal cryptoAmount = usdAmount.divide(cryptoUsdPrice, 8, BigDecimal.ROUND_HALF_UP);
        logger.info("Calculated crypto amount: {} {} for {} USD", cryptoAmount, request.getCryptoType(), usdAmount);

        // Calculate platform fee based on global fee percentage
        BigDecimal platformFee = calculateGlobalFee(request.getAmount());
        BigDecimal totalCost = request.getAmount().add(platformFee);

        // Check sufficient balance
        if (mainAccount.getBalance().compareTo(totalCost) < 0) {
            throw new RuntimeException("Insufficient balance for crypto purchase. Required: " +
                                     totalCost + " MAD (Amount: " + request.getAmount() +
                                     " + Fee: " + platformFee + "), Available: " + mainAccount.getBalance() + " MAD");
        }

        // Generate unique transaction ID
        String transactionId = generateTransactionId();

        // Create CryptoTransaction entity
        CryptoTransaction cryptoTransaction = new CryptoTransaction();
        cryptoTransaction.setTransactionId(transactionId);
        cryptoTransaction.setAmount(request.getAmount());
        cryptoTransaction.setDescription(request.getDescription() != null ?
                                       request.getDescription() :
                                       "Buy " + request.getCryptoType() + " from main account");
        cryptoTransaction.setCryptoType(request.getCryptoType());
        cryptoTransaction.setExchangeRate(cryptoUsdPrice);
        cryptoTransaction.setWalletAddress(cryptoWallet.getWalletAddress());
        cryptoTransaction.setCryptoAmount(cryptoAmount);
        cryptoTransaction.setNetworkFee(BigDecimal.ZERO);
        cryptoTransaction.setPlatformFee(platformFee); // Use calculated fee instead of request fee
        cryptoTransaction.setFromAccount(mainAccount);
        cryptoTransaction.setType(TransactionType.CRYPTO_BUY);
        cryptoTransaction.setStatus(TransactionStatus.COMPLETED); // Auto-complete for main account purchases

        // Update main account balance
        mainAccount.setBalance(mainAccount.getBalance().subtract(totalCost));
        accountRepository.save(mainAccount);

        // Update crypto wallet balance
        CryptoWalletBalance walletBalance = cryptoWalletBalanceRepository
                .findByCryptoWalletAndCryptoType(cryptoWallet, request.getCryptoType())
                .orElse(new CryptoWalletBalance(cryptoWallet, request.getCryptoType()));

        walletBalance.addBalance(cryptoAmount);
        cryptoWalletBalanceRepository.save(walletBalance);

        // Save transaction
        Transaction savedTransaction = transactionRepository.save(cryptoTransaction);
        logger.info("Crypto buy from main transaction created successfully: {} with fee: {} MAD", transactionId, platformFee);

        // Create notification
        notificationService.createTransactionNotification(client, transactionId,
                request.getAmount().toString(), "CRYPTO_BUY_FROM_MAIN");

        // Build response with updated balances
        CryptoBuyFromMainResponse response = new CryptoBuyFromMainResponse();
        response.setTransactionId(transactionId);
        response.setCryptoType(request.getCryptoType());
        response.setMadAmount(request.getAmount());
        response.setUsdAmount(usdAmount);
        response.setCryptoAmount(cryptoAmount);
        response.setExchangeRate(cryptoUsdPrice); // Crypto price in USD
        response.setMadToUsdRate(madToUsdRate); // MAD to USD conversion rate
        response.setPlatformFee(platformFee); // Use calculated fee instead of request fee
        response.setStatus(TransactionStatus.COMPLETED.name());
        response.setDescription(cryptoTransaction.getDescription());
        response.setTransactionDate(savedTransaction.getDate());
        response.setNewMainAccountBalance(mainAccount.getBalance());
        response.setWalletAddress(cryptoWallet.getWalletAddress());
        response.setCurrentRate(cryptoUsdPrice); // Crypto price in USD
        response.setRateSource(rateSource);
        response.setRateTimestamp(rateTimestamp);

        // Get all crypto balances for response
        Map<String, BigDecimal> cryptoBalances = new HashMap<>();
        List<CryptoWalletBalance> allBalances = cryptoWalletBalanceRepository.findByCryptoWallet(cryptoWallet);
        for (CryptoWalletBalance balance : allBalances) {
            cryptoBalances.put(balance.getCryptoType(), balance.getBalance());
        }
        response.setUpdatedCryptoBalances(cryptoBalances);

        return response;
    }

    @Override
    public CryptoTransferResponse transferCryptocurrency(CryptoTransferRequest request, Long clientId) {
        logger.info("Creating crypto transfer for client ID: {}", clientId);

        // Validate sender
        Client senderClient = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Sender client not found with ID: " + clientId));

        // Get sender's crypto wallet
        CryptoWallet senderWallet = cryptoWalletRepository.findByClient(senderClient)
                .orElseThrow(() -> new RuntimeException("Sender crypto wallet not found"));

        // Find recipient wallet by address
        CryptoWallet recipientWallet = cryptoWalletRepository.findByWalletAddress(request.getRecipientWalletAddress())
                .orElseThrow(() -> new RuntimeException("Recipient wallet not found with address: " + request.getRecipientWalletAddress()));

        // Validate sender is not transferring to themselves
        if (senderWallet.getWalletAddress().equals(request.getRecipientWalletAddress())) {
            throw new RuntimeException("Cannot transfer to your own wallet");
        }

        // Get sender's balance for the crypto type
        CryptoWalletBalance senderBalance = cryptoWalletBalanceRepository
                .findByCryptoWalletAndCryptoType(senderWallet, request.getCryptoType())
                .orElseThrow(() -> new RuntimeException("Sender has no balance for crypto type: " + request.getCryptoType()));

        // Calculate total amount needed (crypto amount + network fee)
        BigDecimal totalAmountNeeded = request.getCryptoAmount().add(request.getNetworkFee());

        // Check if sender has sufficient balance
        if (senderBalance.getBalance().compareTo(totalAmountNeeded) < 0) {
            throw new RuntimeException("Insufficient balance for crypto transfer. Required: " +
                                     totalAmountNeeded + ", Available: " + senderBalance.getBalance());
        }

        // Get or create recipient's balance for the crypto type
        CryptoWalletBalance recipientBalance = cryptoWalletBalanceRepository
                .findByCryptoWalletAndCryptoType(recipientWallet, request.getCryptoType())
                .orElse(new CryptoWalletBalance(recipientWallet, request.getCryptoType()));

        // Generate transaction ID
        String transactionId = "TXN" + System.currentTimeMillis();

        // Update balances
        senderBalance.setBalance(senderBalance.getBalance().subtract(totalAmountNeeded));
        recipientBalance.setBalance(recipientBalance.getBalance().add(request.getCryptoAmount()));

        // Save updated balances
        cryptoWalletBalanceRepository.save(senderBalance);
        cryptoWalletBalanceRepository.save(recipientBalance);

        // Create CryptoTransaction entity
        CryptoTransaction cryptoTransaction = new CryptoTransaction();
        cryptoTransaction.setTransactionId(transactionId);
        cryptoTransaction.setAmount(BigDecimal.ZERO); // No fiat amount for crypto-to-crypto transfer
        cryptoTransaction.setDescription(request.getDescription() != null ?
                                       request.getDescription() :
                                       "Transfer " + request.getCryptoAmount() + " " + request.getCryptoType());
        cryptoTransaction.setCryptoType(request.getCryptoType());
        cryptoTransaction.setWalletAddress(request.getRecipientWalletAddress());
        cryptoTransaction.setCryptoAmount(request.getCryptoAmount());
        cryptoTransaction.setNetworkFee(request.getNetworkFee());
        cryptoTransaction.setPlatformFee(BigDecimal.ZERO);
        cryptoTransaction.setType(TransactionType.CRYPTO_TRANSFER);
        cryptoTransaction.setStatus(TransactionStatus.COMPLETED); // Auto-complete crypto transfers

        Transaction savedTransaction = transactionRepository.save(cryptoTransaction);
        logger.info("Crypto transfer transaction created successfully: {}", transactionId);

        // Create notifications for both sender and recipient
        notificationService.createTransactionNotification(senderClient, transactionId,
                request.getCryptoAmount().toString() + " " + request.getCryptoType(), "CRYPTO_TRANSFER_SENT");

        notificationService.createTransactionNotification(recipientWallet.getClient(), transactionId,
                request.getCryptoAmount().toString() + " " + request.getCryptoType(), "CRYPTO_TRANSFER_RECEIVED");

        // Build response
        CryptoTransferResponse response = new CryptoTransferResponse();
        response.setTransactionId(transactionId);
        response.setCryptoType(request.getCryptoType());
        response.setCryptoAmount(request.getCryptoAmount());
        response.setSenderWalletAddress(senderWallet.getWalletAddress());
        response.setRecipientWalletAddress(request.getRecipientWalletAddress());
        response.setStatus(TransactionStatus.COMPLETED.name());
        response.setDescription(cryptoTransaction.getDescription());
        response.setNetworkFee(request.getNetworkFee());
        response.setTransactionDate(savedTransaction.getDate());
        response.setUpdatedSenderBalance(senderBalance.getBalance());
        response.setUpdatedRecipientBalance(recipientBalance.getBalance());

        return response;
    }

    /**
     * Get mock exchange rates in USD (for fallback when Binance API is unavailable)
     * These are approximate USD prices for cryptocurrencies
     */
    private BigDecimal getMockExchangeRateInUsd(String cryptoType) {
        switch (cryptoType) {
            case "BTC": return BigDecimal.valueOf(45000.00); // Bitcoin price in USD
            case "ETH": return BigDecimal.valueOf(3000.00);  // Ethereum price in USD
            case "USDT": return BigDecimal.valueOf(1.00);    // USDT is pegged to USD
            case "BNB": return BigDecimal.valueOf(300.00);   // Binance Coin price in USD
            default: throw new RuntimeException("Unsupported cryptocurrency: " + cryptoType);
        }
    }

    /**
     * Get mock exchange rates in MAD (legacy method for backward compatibility)
     * Note: This method is deprecated and should use proper MAD->USD->Crypto conversion
     */
    @Deprecated
    private BigDecimal getMockExchangeRate(String cryptoType) {
        // Convert USD prices to MAD using approximate rate (1 USD = 10 MAD)
        BigDecimal usdPrice = getMockExchangeRateInUsd(cryptoType);
        return usdPrice.multiply(BigDecimal.valueOf(10.0)); // Approximate MAD price
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionDTO> getCryptoTransactionHistory(Long clientId) {
        logger.debug("Fetching crypto transaction history for client ID: {}", clientId);

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client not found with ID: " + clientId));

        List<Transaction> cryptoTransactions = transactionRepository.findByClientIdAndType(
                client.getClientId(), TransactionType.CRYPTO_BUY);

        List<Transaction> cryptoSellTransactions = transactionRepository.findByClientIdAndType(
                client.getClientId(), TransactionType.CRYPTO_SELL);

        List<Transaction> cryptoTransferTransactions = transactionRepository.findByClientIdAndType(
                client.getClientId(), TransactionType.CRYPTO_TRANSFER);

        // Combine all lists
        List<Transaction> allCryptoTransactions = new ArrayList<>();
        allCryptoTransactions.addAll(cryptoTransactions);
        allCryptoTransactions.addAll(cryptoSellTransactions);
        allCryptoTransactions.addAll(cryptoTransferTransactions);

        // Sort by date descending
        allCryptoTransactions.sort((t1, t2) -> t2.getDate().compareTo(t1.getDate()));

        return allCryptoTransactions.stream()
                .map(this::convertToTransactionDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getCryptoRates() {
        logger.debug("Fetching current crypto rates");

        try {
            // Use the existing currency service to get rates
            List<CurrencyDTO> currencies = currencyService.getAllCurrencies();

            Map<String, Object> rates = new HashMap<>();
            for (CurrencyDTO currency : currencies) {
                if (Arrays.asList("BTC", "ETH", "USDT", "BNB").contains(currency.getSymbol())) {
                    Map<String, Object> cryptoInfo = new HashMap<>();
                    cryptoInfo.put("price", currency.getCurrentPrice());
                    cryptoInfo.put("change24h", currency.getPriceChange24h());
                    cryptoInfo.put("lastUpdated", currency.getLastUpdated());
                    rates.put(currency.getSymbol(), cryptoInfo);
                }
            }

            return rates;
        } catch (Exception e) {
            logger.warn("Failed to fetch live crypto rates, using mock data: {}", e.getMessage());

            // Fallback to mock data
            Map<String, Object> mockRates = new HashMap<>();

            Map<String, Object> btc = new HashMap<>();
            btc.put("price", BigDecimal.valueOf(45000.00));
            btc.put("change24h", BigDecimal.valueOf(2.5));
            btc.put("lastUpdated", LocalDateTime.now());
            mockRates.put("BTC", btc);

            Map<String, Object> eth = new HashMap<>();
            eth.put("price", BigDecimal.valueOf(3000.00));
            eth.put("change24h", BigDecimal.valueOf(-1.2));
            eth.put("lastUpdated", LocalDateTime.now());
            mockRates.put("ETH", eth);

            Map<String, Object> usdt = new HashMap<>();
            usdt.put("price", BigDecimal.valueOf(1.00));
            usdt.put("change24h", BigDecimal.valueOf(0.1));
            usdt.put("lastUpdated", LocalDateTime.now());
            mockRates.put("USDT", usdt);

            return mockRates;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> getClientNotifications(Long clientId) {
        logger.debug("Fetching notifications for client ID: {}", clientId);
        return notificationService.getUserNotifications(clientId);
    }

    @Override
    public NotificationDTO markNotificationAsRead(String notificationId, Long clientId) {
        logger.info("Marking notification as read: {} for client: {}", notificationId, clientId);
        return notificationService.markAsRead(notificationId, clientId);
    }

    @Override
    public boolean deleteNotification(String notificationId, Long clientId) {
        logger.info("Deleting notification: {} for client: {}", notificationId, clientId);
        return notificationService.deleteNotification(notificationId, clientId);
    }
}
