package com.ebanking.service;

import com.ebanking.service.impl.EmailServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for EmailService
 */
@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailServiceImpl emailService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(emailService, "fromEmail", "noreply@ebanking.com");
        ReflectionTestUtils.setField(emailService, "fromName", "E-Banking System");
    }

    @Test
    void testSendBankAgentCredentials() {
        // Given
        String email = "agent@test.com";
        String agentName = "John Doe";
        String username = "johndoe";
        String password = "TempPass123!";
        String employeeId = "EMP001";
        String branch = "Main Branch";

        // When
        emailService.sendBankAgentCredentials(email, agentName, username, password, employeeId, branch);

        // Then
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void testSendSimpleEmail() {
        // Given
        String to = "test@example.com";
        String subject = "Test Subject";
        String content = "Test Content";

        // When
        emailService.sendSimpleEmail(to, subject, content);

        // Then
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }
}
