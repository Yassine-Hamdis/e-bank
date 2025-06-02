package com.ebanking.repository;

import com.ebanking.entity.User;
import com.ebanking.entity.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User entity
 * Provides CRUD operations and custom queries for User management
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find user by username
     * @param username the username to search for
     * @return Optional containing the user if found
     */
    Optional<User> findByUsername(String username);

    /**
     * Find user by email
     * @param email the email to search for
     * @return Optional containing the user if found
     */
    Optional<User> findByEmail(String email);

    /**
     * Check if username exists
     * @param username the username to check
     * @return true if username exists, false otherwise
     */
    boolean existsByUsername(String username);

    /**
     * Check if email exists
     * @param email the email to check
     * @return true if email exists, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Find users by status
     * @param status the user status
     * @return list of users with the specified status
     */
    List<User> findByStatus(UserStatus status);

    /**
     * Find users by role
     * @param role the user role
     * @return list of users with the specified role
     */
    List<User> findByRole(String role);

    /**
     * Find users by role and status
     * @param role the user role
     * @param status the user status
     * @return list of users with the specified role and status
     */
    List<User> findByRoleAndStatus(String role, UserStatus status);

    /**
     * Count users by role
     * @param role the user role
     * @return count of users with the specified role
     */
    long countByRole(String role);

    /**
     * Find users by username containing (case insensitive)
     * @param username partial username to search for
     * @return list of users whose username contains the search term
     */
    @Query("SELECT u FROM User u WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :username, '%'))")
    List<User> findByUsernameContainingIgnoreCase(@Param("username") String username);

    /**
     * Find users by email containing (case insensitive)
     * @param email partial email to search for
     * @return list of users whose email contains the search term
     */
    @Query("SELECT u FROM User u WHERE LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%'))")
    List<User> findByEmailContainingIgnoreCase(@Param("email") String email);
}
