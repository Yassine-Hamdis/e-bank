package com.ebanking.verification;

import com.ebanking.entity.*;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test to verify MobileRecharge inheritance from Transaction
 */
public class MobileRechargeInheritanceTest {

    @Test
    void testMobileRechargeInheritsFromTransaction() {
        System.out.println("🔍 Testing MobileRecharge inheritance from Transaction");

        // Create a MobileRecharge instance
        MobileRecharge mobileRecharge = new MobileRecharge();

        // Verify it's an instance of Transaction
        assertTrue(mobileRecharge instanceof Transaction,
                  "MobileRecharge should be an instance of Transaction");

        System.out.println("✅ MobileRecharge is an instance of Transaction");
    }

    @Test
    void testMobileRechargeCanAccessTransactionProperties() {
        System.out.println("🔍 Testing MobileRecharge access to Transaction properties");

        // Create a MobileRecharge instance
        MobileRecharge mobileRecharge = new MobileRecharge();

        // Test setting Transaction properties through MobileRecharge
        mobileRecharge.setTransactionId("MR001");
        mobileRecharge.setAmount(new BigDecimal("50.00"));
        mobileRecharge.setDescription("Mobile recharge test");
        mobileRecharge.setStatus(TransactionStatus.COMPLETED);
        mobileRecharge.setDate(LocalDateTime.now());

        // Test setting MobileRecharge specific properties
        mobileRecharge.setPhoneNumber("0612345678");
        mobileRecharge.setOperator("Orange");
        mobileRecharge.setRechargeType("PREPAID");

        // Verify all properties are accessible
        assertEquals("MR001", mobileRecharge.getTransactionId());
        assertEquals(new BigDecimal("50.00"), mobileRecharge.getAmount());
        assertEquals("Mobile recharge test", mobileRecharge.getDescription());
        assertEquals(TransactionStatus.COMPLETED, mobileRecharge.getStatus());
        assertEquals(TransactionType.MOBILE_RECHARGE, mobileRecharge.getType());
        assertNotNull(mobileRecharge.getDate());

        // Verify MobileRecharge specific properties
        assertEquals("0612345678", mobileRecharge.getPhoneNumber());
        assertEquals("Orange", mobileRecharge.getOperator());
        assertEquals("PREPAID", mobileRecharge.getRechargeType());

        System.out.println("✅ MobileRecharge can access all Transaction properties");
        System.out.println("✅ MobileRecharge has its own specific properties");
    }

    @Test
    void testMobileRechargePolymorphism() {
        System.out.println("🔍 Testing MobileRecharge polymorphism");

        // Create MobileRecharge instance
        MobileRecharge mobileRecharge = new MobileRecharge();
        mobileRecharge.setTransactionId("MR002");
        mobileRecharge.setAmount(new BigDecimal("100.00"));
        mobileRecharge.setDescription("Data recharge");
        mobileRecharge.setPhoneNumber("0612345678");
        mobileRecharge.setOperator("Maroc Telecom");
        mobileRecharge.setRechargeType("PREPAID");

        // Treat it as Transaction (polymorphism)
        Transaction transaction = mobileRecharge;

        // Verify we can access Transaction properties
        assertEquals("MR002", transaction.getTransactionId());
        assertEquals(new BigDecimal("100.00"), transaction.getAmount());
        assertEquals("Data recharge", transaction.getDescription());
        assertEquals(TransactionType.MOBILE_RECHARGE, transaction.getType());

        // Verify we can cast back to MobileRecharge
        assertTrue(transaction instanceof MobileRecharge);
        MobileRecharge castedBack = (MobileRecharge) transaction;
        assertEquals("0612345678", castedBack.getPhoneNumber());
        assertEquals("Maroc Telecom", castedBack.getOperator());

        System.out.println("✅ MobileRecharge polymorphism works correctly");
    }

    @Test
    void testMobileRechargeConstructors() {
        System.out.println("🔍 Testing MobileRecharge constructors");

        // Test default constructor
        MobileRecharge mr1 = new MobileRecharge();
        assertEquals(TransactionType.MOBILE_RECHARGE, mr1.getType());
        assertNotNull(mr1.getCreatedDate());

        // Test constructor with basic parameters
        MobileRecharge mr2 = new MobileRecharge("MR003", new BigDecimal("25.00"),
                                               "Quick recharge", "0612345678");
        assertEquals("MR003", mr2.getTransactionId());
        assertEquals(new BigDecimal("25.00"), mr2.getAmount());
        assertEquals("Quick recharge", mr2.getDescription());
        assertEquals("0612345678", mr2.getPhoneNumber());
        assertEquals(TransactionType.MOBILE_RECHARGE, mr2.getType());

        // Test constructor with all parameters
        MobileRecharge mr3 = new MobileRecharge("MR004", new BigDecimal("75.00"),
                                               "Full recharge", "0612345678", "Inwi", "POSTPAID");
        assertEquals("MR004", mr3.getTransactionId());
        assertEquals(new BigDecimal("75.00"), mr3.getAmount());
        assertEquals("Full recharge", mr3.getDescription());
        assertEquals("0612345678", mr3.getPhoneNumber());
        assertEquals("Inwi", mr3.getOperator());
        assertEquals("POSTPAID", mr3.getRechargeType());
        assertEquals(TransactionType.MOBILE_RECHARGE, mr3.getType());

        System.out.println("✅ All MobileRecharge constructors work correctly");
    }

    @Test
    void testMobileRechargeToString() {
        System.out.println("🔍 Testing MobileRecharge toString method");

        MobileRecharge mobileRecharge = new MobileRecharge("MR005", new BigDecimal("30.00"),
                                                          "Test recharge", "0612345678", "Orange", "PREPAID");
        mobileRecharge.setStatus(TransactionStatus.PENDING);

        String toStringResult = mobileRecharge.toString();

        // Verify toString includes both MobileRecharge and inherited Transaction properties
        assertTrue(toStringResult.contains("phoneNumber='0612345678'"));
        assertTrue(toStringResult.contains("operator='Orange'"));
        assertTrue(toStringResult.contains("rechargeType='PREPAID'"));
        assertTrue(toStringResult.contains("transactionId='MR005'"));
        assertTrue(toStringResult.contains("amount=30.00"));
        assertTrue(toStringResult.contains("status=PENDING"));

        System.out.println("✅ MobileRecharge toString includes inherited properties");
        System.out.println("ToString result: " + toStringResult);
    }

    @Test
    void testMobileRechargeInheritedMethods() {
        System.out.println("🔍 Testing MobileRecharge inherited methods");

        MobileRecharge mobileRecharge = new MobileRecharge();

        // Test inherited setters and getters
        LocalDateTime testDate = LocalDateTime.now();
        mobileRecharge.setDate(testDate);
        assertEquals(testDate, mobileRecharge.getDate());

        mobileRecharge.setCreatedDate(testDate);
        assertEquals(testDate, mobileRecharge.getCreatedDate());

        mobileRecharge.setUpdatedDate(testDate);
        assertEquals(testDate, mobileRecharge.getUpdatedDate());

        mobileRecharge.setVerificationNotes("Test notes");
        assertEquals("Test notes", mobileRecharge.getVerificationNotes());

        mobileRecharge.setVerificationDate(testDate);
        assertEquals(testDate, mobileRecharge.getVerificationDate());

        System.out.println("✅ All inherited methods work correctly");
    }

    @Test
    void testMobileRechargeDataRechargeScenario() {
        System.out.println("📱 Testing Mobile Data Recharge scenario");

        // Create a mobile data recharge
        MobileRecharge dataRecharge = new MobileRecharge();
        dataRecharge.setTransactionId("DATA001");
        dataRecharge.setAmount(new BigDecimal("100.00"));
        dataRecharge.setDescription("Mobile data recharge 10GB");
        dataRecharge.setPhoneNumber("0612345678");
        dataRecharge.setOperator("Maroc Telecom");
        dataRecharge.setRechargeType("PREPAID");
        dataRecharge.setStatus(TransactionStatus.COMPLETED);

        // Verify it's a valid Transaction
        assertTrue(dataRecharge instanceof Transaction);
        assertEquals(TransactionType.MOBILE_RECHARGE, dataRecharge.getType());

        // Verify it has all Transaction properties
        assertEquals("DATA001", dataRecharge.getTransactionId());
        assertEquals(new BigDecimal("100.00"), dataRecharge.getAmount());
        assertEquals("Mobile data recharge 10GB", dataRecharge.getDescription());
        assertEquals(TransactionStatus.COMPLETED, dataRecharge.getStatus());

        // Verify it has MobileRecharge specific properties
        assertEquals("0612345678", dataRecharge.getPhoneNumber());
        assertEquals("Maroc Telecom", dataRecharge.getOperator());
        assertEquals("PREPAID", dataRecharge.getRechargeType());

        System.out.println("✅ Mobile data recharge works perfectly with inheritance");
        System.out.println("✅ Can access both Transaction and MobileRecharge properties");
    }

    @Test
    void testInheritanceHierarchy() {
        System.out.println("🏗️ Testing inheritance hierarchy");

        MobileRecharge mobileRecharge = new MobileRecharge();

        // Test class hierarchy
        assertEquals("MobileRecharge", mobileRecharge.getClass().getSimpleName());
        assertEquals("Transaction", mobileRecharge.getClass().getSuperclass().getSimpleName());
        assertEquals("Object", mobileRecharge.getClass().getSuperclass().getSuperclass().getSimpleName());

        // Test interface implementation
        assertTrue(mobileRecharge instanceof Serializable);

        System.out.println("✅ Inheritance hierarchy: Object -> Transaction -> MobileRecharge");
        System.out.println("✅ Implements Serializable interface");
    }
}
