package com.ebanking.service;

import com.ebanking.dto.request.PasswordChangeRequest;
import com.ebanking.dto.response.PasswordChangeResponse;
import com.ebanking.entity.Administrator;
import com.ebanking.entity.BankAgent;
import com.ebanking.entity.Client;
import com.ebanking.entity.User;
import com.ebanking.repository.UserRepository;
import com.ebanking.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UserService
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private Administrator testAdmin;
    private BankAgent testAgent;
    private Client testClient;
    private PasswordChangeRequest validRequest;
    private PasswordChangeRequest invalidRequest;

    @BeforeEach
    void setUp() {
        // Create test users
        testAdmin = new Administrator("admin", "encodedOldPassword", "admin@test.com", "1234567890");
        testAdmin.setId(1L);

        testAgent = new BankAgent("agent", "encodedOldPassword", "agent@test.com", "1234567890", "EMP001", "Main Branch");
        testAgent.setId(2L);

        testClient = new Client("client", "encodedOldPassword", "client@test.com", "1234567890", "CLT001");
        testClient.setId(3L);

        // Create test requests
        validRequest = new PasswordChangeRequest("oldPassword", "newPassword123", "newPassword123");
        invalidRequest = new PasswordChangeRequest("wrongPassword", "newPassword123", "differentPassword");
    }

    @Test
    void testChangePassword_Success_Admin() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testAdmin));
        when(passwordEncoder.matches("oldPassword", "encodedOldPassword")).thenReturn(true);
        when(passwordEncoder.matches("newPassword123", "encodedOldPassword")).thenReturn(false);
        when(passwordEncoder.encode("newPassword123")).thenReturn("encodedNewPassword");
        when(userRepository.save(any(User.class))).thenReturn(testAdmin);

        // Act
        PasswordChangeResponse response = userService.changePassword(validRequest, 1L);

        // Assert
        assertTrue(response.isSuccess());
        assertEquals("Password changed successfully", response.getMessage());
        assertEquals("admin", response.getUsername());
        verify(userRepository).save(testAdmin);
    }

    @Test
    void testChangePassword_Success_Agent() {
        // Arrange
        when(userRepository.findById(2L)).thenReturn(Optional.of(testAgent));
        when(passwordEncoder.matches("oldPassword", "encodedOldPassword")).thenReturn(true);
        when(passwordEncoder.matches("newPassword123", "encodedOldPassword")).thenReturn(false);
        when(passwordEncoder.encode("newPassword123")).thenReturn("encodedNewPassword");
        when(userRepository.save(any(User.class))).thenReturn(testAgent);

        // Act
        PasswordChangeResponse response = userService.changePassword(validRequest, 2L);

        // Assert
        assertTrue(response.isSuccess());
        assertEquals("Password changed successfully", response.getMessage());
        assertEquals("agent", response.getUsername());
        verify(userRepository).save(testAgent);
    }

    @Test
    void testChangePassword_Success_Client() {
        // Arrange
        when(userRepository.findById(3L)).thenReturn(Optional.of(testClient));
        when(passwordEncoder.matches("oldPassword", "encodedOldPassword")).thenReturn(true);
        when(passwordEncoder.matches("newPassword123", "encodedOldPassword")).thenReturn(false);
        when(passwordEncoder.encode("newPassword123")).thenReturn("encodedNewPassword");
        when(userRepository.save(any(User.class))).thenReturn(testClient);

        // Act
        PasswordChangeResponse response = userService.changePassword(validRequest, 3L);

        // Assert
        assertTrue(response.isSuccess());
        assertEquals("Password changed successfully", response.getMessage());
        assertEquals("client", response.getUsername());
        verify(userRepository).save(testClient);
    }

    @Test
    void testChangePassword_Failure_PasswordMismatch() {
        // Act
        PasswordChangeResponse response = userService.changePassword(invalidRequest, 1L);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals("New password and confirm password do not match", response.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testChangePassword_Failure_WrongCurrentPassword() {
        // Arrange
        PasswordChangeRequest wrongPasswordRequest = new PasswordChangeRequest("wrongPassword", "newPassword123", "newPassword123");
        when(userRepository.findById(1L)).thenReturn(Optional.of(testAdmin));
        when(passwordEncoder.matches("wrongPassword", "encodedOldPassword")).thenReturn(false);

        // Act
        PasswordChangeResponse response = userService.changePassword(wrongPasswordRequest, 1L);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals("Current password is incorrect", response.getMessage());
        assertEquals("admin", response.getUsername());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testChangePassword_Failure_SamePassword() {
        // Arrange
        PasswordChangeRequest samePasswordRequest = new PasswordChangeRequest("oldPassword", "oldPassword", "oldPassword");
        when(userRepository.findById(1L)).thenReturn(Optional.of(testAdmin));
        when(passwordEncoder.matches("oldPassword", "encodedOldPassword")).thenReturn(true);
        when(passwordEncoder.matches("oldPassword", "encodedOldPassword")).thenReturn(true);

        // Act
        PasswordChangeResponse response = userService.changePassword(samePasswordRequest, 1L);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals("New password must be different from current password", response.getMessage());
        assertEquals("admin", response.getUsername());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testChangePassword_Failure_UserNotFound() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            userService.changePassword(validRequest, 999L);
        });
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testValidateCurrentPassword_Success() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testAdmin));
        when(passwordEncoder.matches("oldPassword", "encodedOldPassword")).thenReturn(true);

        // Act
        boolean result = userService.validateCurrentPassword(1L, "oldPassword");

        // Assert
        assertTrue(result);
    }

    @Test
    void testValidateCurrentPassword_Failure() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testAdmin));
        when(passwordEncoder.matches("wrongPassword", "encodedOldPassword")).thenReturn(false);

        // Act
        boolean result = userService.validateCurrentPassword(1L, "wrongPassword");

        // Assert
        assertFalse(result);
    }

    @Test
    void testValidateCurrentPassword_UserNotFound() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        boolean result = userService.validateCurrentPassword(999L, "anyPassword");

        // Assert
        assertFalse(result);
    }
}
