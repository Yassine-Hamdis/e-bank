package com.ebanking.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Utility class for generating unique IDs for various entities
 */
@Component
public class IdGeneratorUtil {

    private static final SecureRandom random = new SecureRandom();
    private static final AtomicLong clientCounter = new AtomicLong(1000);
    private static final AtomicLong identificationCounter = new AtomicLong(100000);

    /**
     * Generate a unique client ID
     * Format: CLT + 6-digit sequential number (e.g., CLT001001)
     * 
     * @return unique client ID
     */
    public String generateClientId() {
        long counter = clientCounter.incrementAndGet();
        return String.format("CLT%06d", counter);
    }

    /**
     * Generate a unique identification number
     * Format: ID + current year + 8-digit sequential number (e.g., ID2024100001)
     * 
     * @return unique identification number
     */
    public String generateIdentificationNumber() {
        String year = String.valueOf(LocalDateTime.now().getYear());
        long counter = identificationCounter.incrementAndGet();
        return String.format("ID%s%08d", year, counter);
    }

    /**
     * Generate a unique account ID
     * Format: ACC + 6-digit random number (e.g., ACC123456)
     * 
     * @return unique account ID
     */
    public String generateAccountId() {
        int randomNumber = random.nextInt(900000) + 100000; // 6-digit number
        return "ACC" + randomNumber;
    }

    /**
     * Generate a unique transaction ID
     * Format: TXN + current timestamp (e.g., TXN1703123456789)
     * 
     * @return unique transaction ID
     */
    public String generateTransactionId() {
        return "TXN" + System.currentTimeMillis();
    }

    /**
     * Generate a unique wallet address
     * Format: Bitcoin-like address starting with '1' followed by 32 random characters
     * 
     * @return unique wallet address
     */
    public String generateWalletAddress() {
        StringBuilder address = new StringBuilder("1");
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        
        for (int i = 0; i < 32; i++) {
            address.append(chars.charAt(random.nextInt(chars.length())));
        }
        
        return address.toString();
    }

    /**
     * Generate a unique employee ID for bank agents
     * Format: EMP + current year + 4-digit random number (e.g., EMP20241234)
     * 
     * @return unique employee ID
     */
    public String generateEmployeeId() {
        String year = String.valueOf(LocalDateTime.now().getYear());
        int randomNumber = random.nextInt(9000) + 1000; // 4-digit number
        return "EMP" + year + randomNumber;
    }

    /**
     * Generate a unique reference number for transactions
     * Format: REF + timestamp + 3-digit random number (e.g., REF1703123456789123)
     * 
     * @return unique reference number
     */
    public String generateReferenceNumber() {
        long timestamp = System.currentTimeMillis();
        int randomSuffix = random.nextInt(900) + 100; // 3-digit number
        return "REF" + timestamp + randomSuffix;
    }

    /**
     * Reset counters (useful for testing)
     * Note: This method should only be used in test environments
     */
    public void resetCounters() {
        clientCounter.set(1000);
        identificationCounter.set(100000);
    }
}
