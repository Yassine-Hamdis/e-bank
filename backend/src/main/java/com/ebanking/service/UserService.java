package com.ebanking.service;

import com.ebanking.dto.request.PasswordChangeRequest;
import com.ebanking.dto.response.PasswordChangeResponse;

/**
 * Service interface for user operations
 */
public interface UserService {

    /**
     * Change user password
     * @param request the password change request containing current and new passwords
     * @param userId the ID of the user requesting password change
     * @return PasswordChangeResponse indicating success or failure
     * @throws RuntimeException if password change fails
     */
    PasswordChangeResponse changePassword(PasswordChangeRequest request, Long userId);

    /**
     * Validate current password for a user
     * @param userId the user ID
     * @param currentPassword the current password to validate
     * @return true if password is valid, false otherwise
     */
    boolean validateCurrentPassword(Long userId, String currentPassword);
}
