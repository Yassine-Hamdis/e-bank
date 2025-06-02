package com.ebanking.service.impl;

import com.ebanking.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Implementation of EmailService for sending emails
 */
@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Autowired
    private JavaMailSender mailSender;

    @Value("${mail.from}")
    private String fromEmail;

    @Value("${mail.from.name}")
    private String fromName;

    @Override
    public void sendBankAgentCredentials(String email, String agentName, String username,
                                       String password, String employeeId, String branch) {
        logger.info("Sending credentials email to bank agent: {}", email);

        try {
            String subject = "Welcome to E-Banking System - Your Login Credentials";
            String content = buildAgentCredentialsEmailContent(agentName, username, password, employeeId, branch);

            sendSimpleEmail(email, subject, content);

            logger.info("Credentials email sent successfully to: {}", email);
        } catch (Exception e) {
            logger.error("Failed to send credentials email to: {}", email, e);
            throw new RuntimeException("Failed to send credentials email: " + e.getMessage(), e);
        }
    }

    @Override
    public void sendClientCredentials(String email, String clientName, String username,
                                    String password, String clientId, String accountId) {
        logger.info("Sending credentials email to client: {}", email);

        try {
            String subject = "Welcome to E-Banking - Your Account is Ready!";
            String content = buildClientCredentialsEmailContent(clientName, username, password, clientId, accountId);

            sendSimpleEmail(email, subject, content);

            logger.info("Client credentials email sent successfully to: {}", email);
        } catch (Exception e) {
            logger.error("Failed to send client credentials email to: {}", email, e);
            throw new RuntimeException("Failed to send client credentials email: " + e.getMessage(), e);
        }
    }

    @Override
    public void sendSimpleEmail(String to, String subject, String content) {
        logger.debug("Sending email to: {} with subject: {}", to, subject);

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content);

            mailSender.send(message);

            logger.debug("Email sent successfully to: {}", to);
        } catch (Exception e) {
            logger.error("Failed to send email to: {}", to, e);
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
    }

    /**
     * Build the email content for bank agent credentials
     */
    private String buildAgentCredentialsEmailContent(String agentName, String username,
                                                    String password, String employeeId, String branch) {
        StringBuilder content = new StringBuilder();

        content.append("Dear ").append(agentName).append(",\n\n");
        content.append("Welcome to the E-Banking System! Your account has been successfully created.\n\n");
        content.append("Here are your login credentials:\n\n");
        content.append("Employee ID: ").append(employeeId).append("\n");
        content.append("Username: ").append(username).append("\n");
        content.append("Password: ").append(password).append("\n");
        content.append("Branch: ").append(branch).append("\n\n");
        content.append("IMPORTANT SECURITY NOTES:\n");
        content.append("- Please change your password after your first login\n");
        content.append("- Do not share your credentials with anyone\n");
        content.append("- Always log out when you finish your session\n");
        content.append("- Contact IT support if you suspect any unauthorized access\n\n");
        content.append("You can access the system at: [Your System URL]\n\n");
        content.append("If you have any questions or need assistance, please contact the IT department.\n\n");
        content.append("Best regards,\n");
        content.append("E-Banking System Administration\n");
        content.append(fromName);

        return content.toString();
    }

    /**
     * Build the email content for client credentials
     */
    private String buildClientCredentialsEmailContent(String clientName, String username,
                                                     String password, String clientId, String accountId) {
        StringBuilder content = new StringBuilder();

        content.append("Dear ").append(clientName).append(",\n\n");
        content.append("Welcome to E-Banking! Your account has been successfully created by our bank agent.\n\n");
        content.append("Here are your login credentials:\n\n");
        content.append("Client ID: ").append(clientId).append("\n");
        content.append("Username: ").append(username).append("\n");
        content.append("Password: ").append(password).append("\n");
        content.append("Account ID: ").append(accountId).append("\n\n");
        content.append("You can now access your account at: http://localhost:8080\n\n");
        content.append("IMPORTANT SECURITY NOTES:\n");
        content.append("- Please change your password after your first login\n");
        content.append("- Do not share your credentials with anyone\n");
        content.append("- Always log out when you finish your session\n");
        content.append("- Contact us immediately if you notice any suspicious activity\n\n");
        content.append("Available Services:\n");
        content.append("- View account balance and transaction history\n");
        content.append("- Transfer money between accounts\n");
        content.append("- Mobile phone recharge\n");
        content.append("- Cryptocurrency trading\n");
        content.append("- Real-time notifications\n\n");
        content.append("For support, please contact your bank agent or our customer service.\n\n");
        content.append("Thank you for choosing E-Banking!\n\n");
        content.append("Best regards,\n");
        content.append("E-Banking Team");

        return content.toString();
    }
}
