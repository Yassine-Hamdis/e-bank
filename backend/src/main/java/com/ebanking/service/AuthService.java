package com.ebanking.service;

import com.ebanking.dto.request.LoginRequest;
import com.ebanking.dto.response.AuthResponse;

/**
 * Service interface for authentication operations
 */
public interface AuthService {

    /**
     * Authenticate user and generate JWT token
     * @param loginRequest the login request containing username and password
     * @return AuthResponse containing JWT token and user information
     * @throws RuntimeException if authentication fails
     */
    AuthResponse login(LoginRequest loginRequest);

    /**
     * Validate JWT token
     * @param token the JWT token to validate
     * @return true if token is valid, false otherwise
     */
    boolean validateToken(String token);

    /**
     * Extract username from JWT token
     * @param token the JWT token
     * @return username extracted from token
     */
    String getUsernameFromToken(String token);

    /**
     * Refresh JWT token
     * @param token the current JWT token
     * @return new AuthResponse with refreshed token
     * @throws RuntimeException if token refresh fails
     */
    AuthResponse refreshToken(String token);
}
