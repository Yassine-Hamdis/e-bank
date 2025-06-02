package com.ebanking.controller;

import com.ebanking.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller for testing email functionality
 */
@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = {"http://localhost:45571", "http://localhost:4200"}, maxAge = 3600)
public class EmailTestController {

    private static final Logger logger = LoggerFactory.getLogger(EmailTestController.class);

    @Autowired
    private EmailService emailService;

    /**
     * Test email sending functionality
     */
    @PostMapping("/email")
    public ResponseEntity<Map<String, Object>> testEmail(@RequestParam String email) {
        logger.info("Testing email sending to: {}", email);

        Map<String, Object> response = new HashMap<>();

        try {
            // Test simple email
            emailService.sendSimpleEmail(
                email,
                "Test Email from E-Banking System",
                "This is a test email to verify email configuration is working correctly.\n\n" +
                "If you receive this email, the email service is configured properly.\n\n" +
                "Best regards,\nE-Banking System"
            );

            response.put("success", true);
            response.put("message", "Test email sent successfully to " + email);
            logger.info("Test email sent successfully to: {}", email);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Failed to send test email to: {}", email, e);
            response.put("success", false);
            response.put("message", "Failed to send test email: " + e.getMessage());
            response.put("error", e.getClass().getSimpleName());

            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Test bank agent credentials email
     */
    @PostMapping("/email/credentials")
    public ResponseEntity<Map<String, Object>> testCredentialsEmail(@RequestParam String email) {
        logger.info("Testing bank agent credentials email to: {}", email);

        Map<String, Object> response = new HashMap<>();

        try {
            // Test bank agent credentials email
            emailService.sendBankAgentCredentials(
                email,
                "Test Agent",
                "testagent",
                "TestPassword123!",
                "EMP999",
                "Test Branch"
            );

            response.put("success", true);
            response.put("message", "Bank agent credentials email sent successfully to " + email);
            logger.info("Bank agent credentials email sent successfully to: {}", email);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Failed to send bank agent credentials email to: {}", email, e);
            response.put("success", false);
            response.put("message", "Failed to send credentials email: " + e.getMessage());
            response.put("error", e.getClass().getSimpleName());

            return ResponseEntity.status(500).body(response);
        }
    }
}
