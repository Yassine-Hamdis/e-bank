package com.ebanking.controller;

import com.ebanking.dto.request.LoginRequest;
import com.ebanking.dto.response.AuthResponse;
import com.ebanking.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * REST Controller for authentication operations
 */
@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = {"http://localhost:45571", "http://localhost:4200"}, maxAge = 3600)
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    /**
     * Authenticate user and return JWT token
     *
     * @param loginRequest the login request containing username and password
     * @return ResponseEntity containing AuthResponse with JWT token
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        logger.info("Login attempt for username: {}", loginRequest.getUsername());

        try {
            AuthResponse authResponse = authService.login(loginRequest);
            logger.info("Login successful for username: {}", loginRequest.getUsername());
            return ResponseEntity.ok(authResponse);

        } catch (Exception e) {
            logger.error("Login failed for username: {}: {}", loginRequest.getUsername(), e.getMessage());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Authentication failed");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }

    /**
     * Validate JWT token
     *
     * @param token the JWT token to validate
     * @return ResponseEntity indicating token validity
     */
    @PostMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestParam String token) {
        logger.debug("Token validation request received");

        try {
            boolean isValid = authService.validateToken(token);

            Map<String, Object> response = new HashMap<>();
            response.put("valid", isValid);
            response.put("timestamp", System.currentTimeMillis());

            if (isValid) {
                String username = authService.getUsernameFromToken(token);
                response.put("username", username);
                logger.debug("Token validation successful for user: {}", username);
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "Invalid token");
                logger.debug("Token validation failed");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

        } catch (Exception e) {
            logger.error("Token validation error: {}", e.getMessage());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("valid", false);
            errorResponse.put("error", "Token validation failed");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }

    /**
     * Refresh JWT token
     *
     * @param token the current JWT token
     * @return ResponseEntity containing new AuthResponse with refreshed token
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestParam String token) {
        logger.debug("Token refresh request received");

        try {
            AuthResponse authResponse = authService.refreshToken(token);
            logger.info("Token refresh successful for user: {}", authResponse.getUsername());
            return ResponseEntity.ok(authResponse);

        } catch (Exception e) {
            logger.error("Token refresh failed: {}", e.getMessage());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Token refresh failed");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }

    /**
     * Debug endpoint to check current authentication
     *
     * @return ResponseEntity containing current user information
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        logger.debug("Current user request received");

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            Map<String, Object> response = new HashMap<>();

            if (authentication != null && authentication.isAuthenticated()) {
                response.put("authenticated", true);
                response.put("username", authentication.getName());
                response.put("authorities", authentication.getAuthorities());
                response.put("principal", authentication.getPrincipal().getClass().getSimpleName());
                logger.debug("Current user: {} with authorities: {}",
                           authentication.getName(), authentication.getAuthorities());
            } else {
                response.put("authenticated", false);
                response.put("message", "No authentication found");
                logger.debug("No authentication found in security context");
            }

            response.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error getting current user: {}", e.getMessage());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to get current user");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Health check endpoint for authentication service
     *
     * @return ResponseEntity indicating service status
     */
    @GetMapping("/health")
    public ResponseEntity<?> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Authentication Service");
        response.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.ok(response);
    }
}
