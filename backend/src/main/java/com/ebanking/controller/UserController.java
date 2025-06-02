package com.ebanking.controller;

import com.ebanking.dto.request.PasswordChangeRequest;
import com.ebanking.dto.response.PasswordChangeResponse;
import com.ebanking.service.UserService;
import com.ebanking.service.impl.UserDetailsServiceImpl;
import com.ebanking.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * REST Controller for user operations
 * Handles user-related endpoints accessible to all authenticated users
 */
@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Change user password
     * Accessible to all authenticated users (ADMIN, AGENT, CLIENT)
     * Users can only change their own password
     *
     * @param request the password change request
     * @param httpRequest the HTTP request to extract JWT token
     * @return ResponseEntity containing password change response
     */
    @PutMapping("/change-password")
    @PreAuthorize("hasRole('ADMIN') or hasRole('AGENT') or hasRole('CLIENT')")
    public ResponseEntity<?> changePassword(@Valid @RequestBody PasswordChangeRequest request,
                                          HttpServletRequest httpRequest) {
        logger.info("Password change request received");

        try {
            // Extract user ID from JWT token
            Long userId = getCurrentUserId(httpRequest);
            if (userId == null) {
                logger.error("Unable to extract user ID from token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(createErrorResponse("Unable to authenticate user", "Invalid token"));
            }

            // Change password
            PasswordChangeResponse response = userService.changePassword(request, userId);

            if (response.isSuccess()) {
                logger.info("Password changed successfully for user ID: {}", userId);
                return ResponseEntity.ok(response);
            } else {
                logger.warn("Password change failed for user ID: {}: {}", userId, response.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

        } catch (Exception e) {
            logger.error("Error processing password change request: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to change password", e.getMessage()));
        }
    }

    /**
     * Get current user information
     * Accessible to all authenticated users
     *
     * @return ResponseEntity containing current user information
     */
    @GetMapping("/profile")
    @PreAuthorize("hasRole('ADMIN') or hasRole('AGENT') or hasRole('CLIENT')")
    public ResponseEntity<?> getCurrentUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication != null && authentication.isAuthenticated()) {
                UserDetailsServiceImpl.UserPrincipal userPrincipal = 
                    (UserDetailsServiceImpl.UserPrincipal) authentication.getPrincipal();

                Map<String, Object> userInfo = new HashMap<>();
                userInfo.put("id", userPrincipal.getId());
                userInfo.put("username", userPrincipal.getUsername());
                userInfo.put("email", userPrincipal.getEmail());
                userInfo.put("role", userPrincipal.getRole());
                userInfo.put("status", userPrincipal.getStatus());

                return ResponseEntity.ok(userInfo);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(createErrorResponse("User not authenticated", "No valid authentication found"));
            }

        } catch (Exception e) {
            logger.error("Error getting current user information: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to get user information", e.getMessage()));
        }
    }

    /**
     * Extract user ID from JWT token in the request
     */
    private Long getCurrentUserId(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                return jwtUtil.extractUserId(token);
            }
            return null;
        } catch (Exception e) {
            logger.error("Error extracting user ID from token: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Create error response map
     */
    private Map<String, Object> createErrorResponse(String error, String message) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", error);
        errorResponse.put("message", message);
        errorResponse.put("timestamp", System.currentTimeMillis());
        return errorResponse;
    }
}
