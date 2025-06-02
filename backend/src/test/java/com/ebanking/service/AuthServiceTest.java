package com.ebanking.service;

import com.ebanking.dto.request.LoginRequest;
import com.ebanking.dto.response.AuthResponse;
import com.ebanking.entity.Administrator;
import com.ebanking.repository.UserRepository;
import com.ebanking.service.impl.AuthServiceImpl;
import com.ebanking.service.impl.UserDetailsServiceImpl;
import com.ebanking.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthServiceImpl authService;

    private Administrator testAdmin;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        testAdmin = new Administrator("admin", encoder.encode("admin123"), "admin@test.com", "1234567890");
        testAdmin.setId(1L);
        
        loginRequest = new LoginRequest("admin", "admin123");
    }

    @Test
    void testLoginSuccess() {
        // Arrange
        UserDetailsServiceImpl.UserPrincipal userPrincipal = new UserDetailsServiceImpl.UserPrincipal(testAdmin);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtUtil.generateToken(any())).thenReturn("test-jwt-token");
        when(jwtUtil.getExpirationTime()).thenReturn(86400L);

        // Act
        AuthResponse response = authService.login(loginRequest);

        // Assert
        assertNotNull(response);
        assertEquals("test-jwt-token", response.getToken());
        assertEquals("admin", response.getUsername());
        assertEquals("admin@test.com", response.getEmail());
        assertTrue(response.getRoles().contains("ROLE_ADMIN"));
        assertEquals(86400L, response.getExpiresIn());
    }

    @Test
    void testValidateToken() {
        // Arrange
        String token = "valid-token";
        when(jwtUtil.validateToken(token)).thenReturn(true);

        // Act
        boolean isValid = authService.validateToken(token);

        // Assert
        assertTrue(isValid);
        verify(jwtUtil).validateToken(token);
    }

    @Test
    void testGetUsernameFromToken() {
        // Arrange
        String token = "valid-token";
        String expectedUsername = "admin";
        when(jwtUtil.extractUsername(token)).thenReturn(expectedUsername);

        // Act
        String username = authService.getUsernameFromToken(token);

        // Assert
        assertEquals(expectedUsername, username);
        verify(jwtUtil).extractUsername(token);
    }
}
