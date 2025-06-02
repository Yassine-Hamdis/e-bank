package com.ebanking.verification;

import com.ebanking.entity.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Simple demonstration of MobileRecharge inheritance from Transaction
 */
public class SimpleInheritanceDemo {

    @Test
    void demonstrateMobileRechargeInheritance() {
        System.out.println("🔍 DEMONSTRATING MOBILECHARGE INHERITANCE FROM TRANSACTION");
        System.out.println("=========================================================");
        
        // Create a MobileRecharge instance
        MobileRecharge mobileRecharge = new MobileRecharge();
        
        System.out.println("✅ 1. MobileRecharge IS-A Transaction:");
        System.out.println("   instanceof Transaction: " + (mobileRecharge instanceof Transaction));
        
        System.out.println("\n✅ 2. MobileRecharge can access ALL Transaction properties:");
        
        // Set Transaction properties through MobileRecharge
        mobileRecharge.setTransactionId("MR001");
        mobileRecharge.setAmount(new BigDecimal("50.00"));
        mobileRecharge.setDescription("Mobile data recharge 5GB");
        mobileRecharge.setStatus(TransactionStatus.COMPLETED);
        mobileRecharge.setDate(LocalDateTime.now());
        
        // Set MobileRecharge specific properties
        mobileRecharge.setPhoneNumber("0612345678");
        mobileRecharge.setOperator("Orange");
        mobileRecharge.setRechargeType("PREPAID");
        
        System.out.println("   Transaction ID: " + mobileRecharge.getTransactionId());
        System.out.println("   Amount: " + mobileRecharge.getAmount());
        System.out.println("   Description: " + mobileRecharge.getDescription());
        System.out.println("   Status: " + mobileRecharge.getStatus());
        System.out.println("   Type: " + mobileRecharge.getType());
        System.out.println("   Date: " + mobileRecharge.getDate());
        
        System.out.println("\n✅ 3. MobileRecharge has its own specific properties:");
        System.out.println("   Phone Number: " + mobileRecharge.getPhoneNumber());
        System.out.println("   Operator: " + mobileRecharge.getOperator());
        System.out.println("   Recharge Type: " + mobileRecharge.getRechargeType());
        
        System.out.println("\n✅ 4. Polymorphism works - can treat MobileRecharge as Transaction:");
        Transaction transaction = mobileRecharge; // Polymorphism
        System.out.println("   As Transaction - ID: " + transaction.getTransactionId());
        System.out.println("   As Transaction - Amount: " + transaction.getAmount());
        System.out.println("   As Transaction - Type: " + transaction.getType());
        
        System.out.println("\n✅ 5. Can cast back to MobileRecharge:");
        if (transaction instanceof MobileRecharge) {
            MobileRecharge castedBack = (MobileRecharge) transaction;
            System.out.println("   Phone Number: " + castedBack.getPhoneNumber());
            System.out.println("   Operator: " + castedBack.getOperator());
        }
        
        System.out.println("\n✅ 6. Class hierarchy:");
        System.out.println("   Class: " + mobileRecharge.getClass().getSimpleName());
        System.out.println("   Superclass: " + mobileRecharge.getClass().getSuperclass().getSimpleName());
        
        System.out.println("\n🎉 INHERITANCE IS WORKING PERFECTLY!");
        System.out.println("   - MobileRecharge extends Transaction ✅");
        System.out.println("   - Can access all Transaction properties ✅");
        System.out.println("   - Has its own specific properties ✅");
        System.out.println("   - Polymorphism works correctly ✅");
        
        // Assertions to verify everything works
        assertTrue(mobileRecharge instanceof Transaction);
        assertEquals("MR001", mobileRecharge.getTransactionId());
        assertEquals(new BigDecimal("50.00"), mobileRecharge.getAmount());
        assertEquals(TransactionType.MOBILE_RECHARGE, mobileRecharge.getType());
        assertEquals("0612345678", mobileRecharge.getPhoneNumber());
        assertEquals("Orange", mobileRecharge.getOperator());
    }

    @Test
    void demonstrateDataRechargeScenario() {
        System.out.println("\n📱 DEMONSTRATING MOBILE DATA RECHARGE SCENARIO");
        System.out.println("===============================================");
        
        // Create a mobile data recharge using constructor
        MobileRecharge dataRecharge = new MobileRecharge(
            "DATA001", 
            new BigDecimal("100.00"), 
            "Mobile data recharge 10GB", 
            "0612345678", 
            "Maroc Telecom", 
            "PREPAID"
        );
        
        System.out.println("✅ Created mobile data recharge with constructor");
        System.out.println("   Transaction ID: " + dataRecharge.getTransactionId());
        System.out.println("   Amount: " + dataRecharge.getAmount() + " MAD");
        System.out.println("   Description: " + dataRecharge.getDescription());
        System.out.println("   Phone: " + dataRecharge.getPhoneNumber());
        System.out.println("   Operator: " + dataRecharge.getOperator());
        System.out.println("   Type: " + dataRecharge.getRechargeType());
        
        // Verify it's a valid Transaction
        System.out.println("\n✅ Verification:");
        System.out.println("   Is Transaction: " + (dataRecharge instanceof Transaction));
        System.out.println("   Transaction Type: " + dataRecharge.getType());
        System.out.println("   Has Transaction ID: " + (dataRecharge.getTransactionId() != null));
        System.out.println("   Has Amount: " + (dataRecharge.getAmount() != null));
        System.out.println("   Has Phone Number: " + (dataRecharge.getPhoneNumber() != null));
        
        // Test setting additional Transaction properties
        dataRecharge.setStatus(TransactionStatus.COMPLETED);
        dataRecharge.setVerificationNotes("Data recharge verified and completed");
        
        System.out.println("\n✅ Additional Transaction properties:");
        System.out.println("   Status: " + dataRecharge.getStatus());
        System.out.println("   Verification Notes: " + dataRecharge.getVerificationNotes());
        System.out.println("   Created Date: " + dataRecharge.getCreatedDate());
        
        System.out.println("\n🎉 MOBILE DATA RECHARGE WORKS PERFECTLY WITH INHERITANCE!");
        
        // Assertions
        assertTrue(dataRecharge instanceof Transaction);
        assertEquals(TransactionType.MOBILE_RECHARGE, dataRecharge.getType());
        assertEquals("Mobile data recharge 10GB", dataRecharge.getDescription());
        assertEquals("Maroc Telecom", dataRecharge.getOperator());
        assertNotNull(dataRecharge.getCreatedDate());
    }

    @Test
    void demonstrateAllTransactionProperties() {
        System.out.println("\n🔍 DEMONSTRATING ALL INHERITED TRANSACTION PROPERTIES");
        System.out.println("=====================================================");
        
        MobileRecharge mobileRecharge = new MobileRecharge();
        
        // Test ALL Transaction properties are accessible
        System.out.println("✅ Setting all Transaction properties through MobileRecharge:");
        
        mobileRecharge.setId(1L);
        mobileRecharge.setTransactionId("MR123");
        mobileRecharge.setAmount(new BigDecimal("75.50"));
        mobileRecharge.setDate(LocalDateTime.now());
        mobileRecharge.setStatus(TransactionStatus.PENDING);
        mobileRecharge.setDescription("Test recharge");
        mobileRecharge.setType(TransactionType.MOBILE_RECHARGE);
        mobileRecharge.setCreatedDate(LocalDateTime.now());
        mobileRecharge.setUpdatedDate(LocalDateTime.now());
        mobileRecharge.setVerificationDate(LocalDateTime.now());
        mobileRecharge.setVerificationNotes("Test notes");
        
        System.out.println("   ID: " + mobileRecharge.getId());
        System.out.println("   Transaction ID: " + mobileRecharge.getTransactionId());
        System.out.println("   Amount: " + mobileRecharge.getAmount());
        System.out.println("   Date: " + mobileRecharge.getDate());
        System.out.println("   Status: " + mobileRecharge.getStatus());
        System.out.println("   Description: " + mobileRecharge.getDescription());
        System.out.println("   Type: " + mobileRecharge.getType());
        System.out.println("   Created Date: " + mobileRecharge.getCreatedDate());
        System.out.println("   Updated Date: " + mobileRecharge.getUpdatedDate());
        System.out.println("   Verification Date: " + mobileRecharge.getVerificationDate());
        System.out.println("   Verification Notes: " + mobileRecharge.getVerificationNotes());
        
        System.out.println("\n🎉 ALL TRANSACTION PROPERTIES ARE ACCESSIBLE!");
        
        // Verify all properties are set correctly
        assertEquals(Long.valueOf(1L), mobileRecharge.getId());
        assertEquals("MR123", mobileRecharge.getTransactionId());
        assertEquals(new BigDecimal("75.50"), mobileRecharge.getAmount());
        assertEquals(TransactionStatus.PENDING, mobileRecharge.getStatus());
        assertEquals("Test recharge", mobileRecharge.getDescription());
        assertEquals(TransactionType.MOBILE_RECHARGE, mobileRecharge.getType());
        assertNotNull(mobileRecharge.getCreatedDate());
        assertNotNull(mobileRecharge.getUpdatedDate());
        assertNotNull(mobileRecharge.getVerificationDate());
        assertEquals("Test notes", mobileRecharge.getVerificationNotes());
    }
}
