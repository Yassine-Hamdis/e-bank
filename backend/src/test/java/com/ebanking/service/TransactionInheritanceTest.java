package com.ebanking.service;

import com.ebanking.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class to verify transaction inheritance and database storage
 */
public class TransactionInheritanceTest {

    private Client testClient;
    private Account testAccount;

    @BeforeEach
    void setUp() {
        // Create test client
        testClient = new Client();
        testClient.setClientId("CLT001");
        testClient.setUsername("testclient");
        testClient.setPassword("password");
        testClient.setEmail("test@example.com");
        testClient.setPhoneNumber("0612345678");
        testClient.setDateOfBirth(LocalDate.now().minusYears(25));
        testClient.setAddress("Test Address");

        // Create test account
        testAccount = new Account();
        testAccount.setAccountId("ACC001");
        testAccount.setAccountType(AccountType.CHECKING);
        testAccount.setBalance(new BigDecimal("10000.00"));
        testAccount.setClient(testClient);
        testAccount.setStatus(AccountStatus.ACTIVE);
    }

    @Test
    void testTransactionInheritanceStructure() {
        // Test that all transaction types inherit from Transaction
        Transaction baseTransaction = createBaseTransaction();
        Transfer transfer = createTransfer();
        MobileRecharge mobileRecharge = createMobileRecharge();
        CryptoTransaction cryptoTransaction = createCryptoTransaction();

        // Verify inheritance
        assertTrue(baseTransaction instanceof Transaction);
        assertTrue(transfer instanceof Transaction);
        assertTrue(mobileRecharge instanceof Transaction);
        assertTrue(cryptoTransaction instanceof Transaction);

        // Verify discriminator values
        assertEquals(TransactionType.DEPOSIT, baseTransaction.getType());
        assertEquals(TransactionType.TRANSFER, transfer.getType());
        assertEquals(TransactionType.MOBILE_RECHARGE, mobileRecharge.getType());
        assertEquals(TransactionType.CRYPTO_BUY, cryptoTransaction.getType());
    }

    @Test
    void testTransactionPolymorphism() {
        // Create different transaction types
        Transfer transfer = createTransfer();
        MobileRecharge mobileRecharge = createMobileRecharge();
        CryptoTransaction cryptoTransaction = createCryptoTransaction();

        // Test polymorphic behavior - all can be treated as Transaction
        Transaction[] transactions = {transfer, mobileRecharge, cryptoTransaction};

        for (Transaction transaction : transactions) {
            // All should have common Transaction properties
            assertNotNull(transaction.getTransactionId());
            assertNotNull(transaction.getAmount());
            assertNotNull(transaction.getType());
            assertNotNull(transaction.getStatus());

            // Test specific type casting
            if (transaction instanceof Transfer) {
                Transfer t = (Transfer) transaction;
                assertNotNull(t.getSourceAccountId(), "Transfer should have source account ID");
                assertNotNull(t.getDestinationAccountId(), "Transfer should have destination account ID");
            } else if (transaction instanceof MobileRecharge) {
                MobileRecharge mr = (MobileRecharge) transaction;
                assertNotNull(mr.getPhoneNumber(), "MobileRecharge should have phone number");
                assertNotNull(mr.getOperator(), "MobileRecharge should have operator");
            } else if (transaction instanceof CryptoTransaction) {
                CryptoTransaction ct = (CryptoTransaction) transaction;
                assertNotNull(ct.getCryptoType(), "CryptoTransaction should have crypto type");
                assertNotNull(ct.getExchangeRate(), "CryptoTransaction should have exchange rate");
            }
        }
    }

    private Transaction createBaseTransaction() {
        Transaction transaction = new Transaction();
        transaction.setTransactionId("TXN001");
        transaction.setAmount(new BigDecimal("100.00"));
        transaction.setType(TransactionType.DEPOSIT);
        transaction.setDescription("Test deposit");
        transaction.setFromAccount(testAccount);
        transaction.setStatus(TransactionStatus.PENDING);
        return transaction;
    }

    private Transfer createTransfer() {
        Transfer transfer = new Transfer();
        transfer.setTransactionId("TXN002");
        transfer.setAmount(new BigDecimal("500.00"));
        transfer.setDescription("Test transfer");
        transfer.setSourceAccountId("ACC001");
        transfer.setDestinationAccountId("ACC002");
        transfer.setTransferFee(new BigDecimal("5.00"));
        transfer.setFromAccount(testAccount);
        transfer.setStatus(TransactionStatus.PENDING);
        return transfer;
    }

    private MobileRecharge createMobileRecharge() {
        MobileRecharge mobileRecharge = new MobileRecharge();
        mobileRecharge.setTransactionId("TXN003");
        mobileRecharge.setAmount(new BigDecimal("50.00"));
        mobileRecharge.setDescription("Test mobile recharge");
        mobileRecharge.setPhoneNumber("0612345678");
        mobileRecharge.setOperator("Orange");
        mobileRecharge.setRechargeType("PREPAID");
        mobileRecharge.setFromAccount(testAccount);
        mobileRecharge.setStatus(TransactionStatus.PENDING);
        return mobileRecharge;
    }

    private CryptoTransaction createCryptoTransaction() {
        CryptoTransaction cryptoTransaction = new CryptoTransaction();
        cryptoTransaction.setTransactionId("TXN004");
        cryptoTransaction.setAmount(new BigDecimal("1000.00"));
        cryptoTransaction.setType(TransactionType.CRYPTO_BUY);
        cryptoTransaction.setDescription("Test crypto buy");
        cryptoTransaction.setCryptoType("BTC");
        cryptoTransaction.setExchangeRate(new BigDecimal("45000.00"));
        cryptoTransaction.setWalletAddress("1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa");
        cryptoTransaction.setCryptoAmount(new BigDecimal("0.02222222"));
        cryptoTransaction.setNetworkFee(new BigDecimal("0.0001"));
        cryptoTransaction.setPlatformFee(new BigDecimal("10.00"));
        cryptoTransaction.setFromAccount(testAccount);
        cryptoTransaction.setStatus(TransactionStatus.PENDING);
        return cryptoTransaction;
    }
}
