package com.ebanking.service.impl;

import com.ebanking.dto.request.PasswordChangeRequest;
import com.ebanking.dto.response.PasswordChangeResponse;
import com.ebanking.entity.User;
import com.ebanking.repository.UserRepository;
import com.ebanking.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Implementation of UserService for user operations
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public PasswordChangeResponse changePassword(PasswordChangeRequest request, Long userId) {
        logger.info("Attempting to change password for user ID: {}", userId);

        try {
            // Validate input
            if (!request.getNewPassword().equals(request.getConfirmPassword())) {
                logger.warn("Password change failed for user {}: New password and confirm password do not match", userId);
                return new PasswordChangeResponse(false, "New password and confirm password do not match", null);
            }

            // Find user
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

            // Validate current password
            if (!validateCurrentPassword(userId, request.getCurrentPassword())) {
                logger.warn("Password change failed for user {}: Current password is incorrect", userId);
                return new PasswordChangeResponse(false, "Current password is incorrect", user.getUsername());
            }

            // Check if new password is different from current password
            if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
                logger.warn("Password change failed for user {}: New password must be different from current password", userId);
                return new PasswordChangeResponse(false, "New password must be different from current password", user.getUsername());
            }

            // Encode new password
            String encodedNewPassword = passwordEncoder.encode(request.getNewPassword());

            // Update password
            user.setPassword(encodedNewPassword);
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);

            logger.info("Password changed successfully for user: {}", user.getUsername());
            return new PasswordChangeResponse(true, "Password changed successfully", user.getUsername());

        } catch (Exception e) {
            logger.error("Error changing password for user {}: {}", userId, e.getMessage());
            throw new RuntimeException("Failed to change password: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean validateCurrentPassword(Long userId, String currentPassword) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

            return passwordEncoder.matches(currentPassword, user.getPassword());

        } catch (Exception e) {
            logger.error("Error validating current password for user {}: {}", userId, e.getMessage());
            return false;
        }
    }
}
