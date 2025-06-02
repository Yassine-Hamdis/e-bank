package com.ebanking.service.impl;

import com.ebanking.dto.request.LoginRequest;
import com.ebanking.dto.response.AuthResponse;
import com.ebanking.service.AuthService;
import com.ebanking.service.impl.UserDetailsServiceImpl.UserPrincipal;
import com.ebanking.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementation of AuthService for authentication operations
 */
@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public AuthResponse login(LoginRequest loginRequest) {
        logger.debug("Attempting to authenticate user: {}", loginRequest.getUsername());

        try {
            // Authenticate the user
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
                )
            );

            // Get user details
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

            // Extract roles
            List<String> roles = userPrincipal.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            // Generate JWT token with user ID claim
            Map<String, Object> claims = new HashMap<>();
            claims.put("userId", userPrincipal.getId());
            claims.put("role", userPrincipal.getRole());
            String token = jwtUtil.generateToken(userPrincipal.getUsername(), claims);

            logger.info("User {} authenticated successfully with ID: {}", loginRequest.getUsername(), userPrincipal.getId());

            return new AuthResponse(
                token,
                userPrincipal.getUsername(),
                userPrincipal.getEmail(),
                roles,
                jwtUtil.getExpirationTime()
            );

        } catch (AuthenticationException e) {
            logger.error("Authentication failed for user {}: {}", loginRequest.getUsername(), e.getMessage());
            throw new BadCredentialsException("Invalid username or password", e);
        }
    }

    @Override
    public boolean validateToken(String token) {
        try {
            return jwtUtil.validateToken(token);
        } catch (Exception e) {
            logger.error("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public String getUsernameFromToken(String token) {
        try {
            return jwtUtil.extractUsername(token);
        } catch (Exception e) {
            logger.error("Failed to extract username from token: {}", e.getMessage());
            throw new RuntimeException("Invalid token", e);
        }
    }

    @Override
    public AuthResponse refreshToken(String token) {
        try {
            if (!jwtUtil.validateToken(token)) {
                throw new RuntimeException("Invalid token for refresh");
            }

            String username = jwtUtil.extractUsername(token);
            UserPrincipal userPrincipal = (UserPrincipal) userDetailsService.loadUserByUsername(username);

            // Extract roles
            List<String> roles = userPrincipal.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            // Generate new token with user ID claim
            Map<String, Object> claims = new HashMap<>();
            claims.put("userId", userPrincipal.getId());
            claims.put("role", userPrincipal.getRole());
            String newToken = jwtUtil.generateToken(userPrincipal.getUsername(), claims);

            logger.info("Token refreshed for user: {}", username);

            return new AuthResponse(
                newToken,
                userPrincipal.getUsername(),
                userPrincipal.getEmail(),
                roles,
                jwtUtil.getExpirationTime()
            );

        } catch (Exception e) {
            logger.error("Token refresh failed: {}", e.getMessage());
            throw new RuntimeException("Token refresh failed", e);
        }
    }
}
