package com.ebanking.controller;

import com.ebanking.dto.request.PasswordChangeRequest;
import com.ebanking.dto.response.PasswordChangeResponse;
import com.ebanking.entity.Administrator;
import com.ebanking.entity.BankAgent;
import com.ebanking.entity.Client;
import com.ebanking.service.UserService;
import com.ebanking.service.impl.UserDetailsServiceImpl;
import com.ebanking.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for UserController
 */
@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private UserDetailsServiceImpl.UserPrincipal adminPrincipal;
    private UserDetailsServiceImpl.UserPrincipal agentPrincipal;
    private UserDetailsServiceImpl.UserPrincipal clientPrincipal;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        objectMapper = new ObjectMapper();

        // Create mock users
        Administrator mockAdmin = new Administrator("admin", "password", "admin@test.com", "1234567890");
        mockAdmin.setId(1L);
        adminPrincipal = new UserDetailsServiceImpl.UserPrincipal(mockAdmin);

        BankAgent mockAgent = new BankAgent("agent", "password", "agent@test.com", "1234567890", "EMP001", "Main Branch");
        mockAgent.setId(2L);
        agentPrincipal = new UserDetailsServiceImpl.UserPrincipal(mockAgent);

        Client mockClient = new Client("client", "password", "client@test.com", "1234567890", "CLT001");
        mockClient.setId(3L);
        clientPrincipal = new UserDetailsServiceImpl.UserPrincipal(mockClient);
    }

    @Test
    void testChangePassword_Success_Admin() throws Exception {
        // Arrange
        setupSecurityContext(adminPrincipal);
        
        PasswordChangeRequest request = new PasswordChangeRequest("oldPassword", "newPassword123", "newPassword123");
        PasswordChangeResponse response = new PasswordChangeResponse(true, "Password changed successfully", "admin");

        when(jwtUtil.extractUserId("test-token")).thenReturn(1L);
        when(userService.changePassword(any(PasswordChangeRequest.class), eq(1L))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(put("/api/user/change-password")
                .header("Authorization", "Bearer test-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Password changed successfully"))
                .andExpect(jsonPath("$.username").value("admin"));

        verify(userService).changePassword(any(PasswordChangeRequest.class), eq(1L));
    }

    @Test
    void testChangePassword_Success_Agent() throws Exception {
        // Arrange
        setupSecurityContext(agentPrincipal);
        
        PasswordChangeRequest request = new PasswordChangeRequest("oldPassword", "newPassword123", "newPassword123");
        PasswordChangeResponse response = new PasswordChangeResponse(true, "Password changed successfully", "agent");

        when(jwtUtil.extractUserId("test-token")).thenReturn(2L);
        when(userService.changePassword(any(PasswordChangeRequest.class), eq(2L))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(put("/api/user/change-password")
                .header("Authorization", "Bearer test-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Password changed successfully"))
                .andExpect(jsonPath("$.username").value("agent"));

        verify(userService).changePassword(any(PasswordChangeRequest.class), eq(2L));
    }

    @Test
    void testChangePassword_Success_Client() throws Exception {
        // Arrange
        setupSecurityContext(clientPrincipal);
        
        PasswordChangeRequest request = new PasswordChangeRequest("oldPassword", "newPassword123", "newPassword123");
        PasswordChangeResponse response = new PasswordChangeResponse(true, "Password changed successfully", "client");

        when(jwtUtil.extractUserId("test-token")).thenReturn(3L);
        when(userService.changePassword(any(PasswordChangeRequest.class), eq(3L))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(put("/api/user/change-password")
                .header("Authorization", "Bearer test-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Password changed successfully"))
                .andExpect(jsonPath("$.username").value("client"));

        verify(userService).changePassword(any(PasswordChangeRequest.class), eq(3L));
    }

    @Test
    void testChangePassword_Failure_WrongCurrentPassword() throws Exception {
        // Arrange
        setupSecurityContext(adminPrincipal);
        
        PasswordChangeRequest request = new PasswordChangeRequest("wrongPassword", "newPassword123", "newPassword123");
        PasswordChangeResponse response = new PasswordChangeResponse(false, "Current password is incorrect", "admin");

        when(jwtUtil.extractUserId("test-token")).thenReturn(1L);
        when(userService.changePassword(any(PasswordChangeRequest.class), eq(1L))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(put("/api/user/change-password")
                .header("Authorization", "Bearer test-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Current password is incorrect"))
                .andExpect(jsonPath("$.username").value("admin"));

        verify(userService).changePassword(any(PasswordChangeRequest.class), eq(1L));
    }

    @Test
    void testChangePassword_Failure_InvalidToken() throws Exception {
        // Arrange
        setupSecurityContext(adminPrincipal);
        
        PasswordChangeRequest request = new PasswordChangeRequest("oldPassword", "newPassword123", "newPassword123");

        when(jwtUtil.extractUserId("invalid-token")).thenReturn(null);

        // Act & Assert
        mockMvc.perform(put("/api/user/change-password")
                .header("Authorization", "Bearer invalid-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Unable to authenticate user"));

        verify(userService, never()).changePassword(any(PasswordChangeRequest.class), anyLong());
    }

    @Test
    void testGetCurrentUser_Success() throws Exception {
        // Arrange
        setupSecurityContext(adminPrincipal);

        // Act & Assert
        mockMvc.perform(get("/api/user/profile")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("admin"))
                .andExpect(jsonPath("$.email").value("admin@test.com"))
                .andExpect(jsonPath("$.role").value("ROLE_ADMIN"));
    }

    private void setupSecurityContext(UserDetailsServiceImpl.UserPrincipal userPrincipal) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userPrincipal, null, userPrincipal.getAuthorities()
        );

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }
}
