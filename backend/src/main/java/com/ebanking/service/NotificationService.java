package com.ebanking.service;

import com.ebanking.dto.response.NotificationDTO;
import com.ebanking.entity.Notification;
import com.ebanking.entity.User;

import java.util.List;

/**
 * Service interface for notification operations
 */
public interface NotificationService {

    /**
     * Create a new notification for a user
     * @param user the user to notify
     * @param title the notification title
     * @param message the notification message
     * @param type the notification type
     * @param priority the notification priority
     * @return the created notification DTO
     */
    NotificationDTO createNotification(User user, String title, String message, 
                                     Notification.NotificationType type, 
                                     Notification.NotificationPriority priority);

    /**
     * Get all notifications for a user
     * @param userId the user ID
     * @return list of notification DTOs
     */
    List<NotificationDTO> getUserNotifications(Long userId);

    /**
     * Get unread notifications for a user
     * @param userId the user ID
     * @return list of unread notification DTOs
     */
    List<NotificationDTO> getUnreadNotifications(Long userId);

    /**
     * Mark a notification as read
     * @param notificationId the notification ID
     * @param userId the user ID (for security check)
     * @return the updated notification DTO
     */
    NotificationDTO markAsRead(String notificationId, Long userId);

    /**
     * Delete a notification
     * @param notificationId the notification ID
     * @param userId the user ID (for security check)
     * @return true if deleted successfully
     */
    boolean deleteNotification(String notificationId, Long userId);

    /**
     * Get notification count for a user
     * @param userId the user ID
     * @return total number of notifications
     */
    long getNotificationCount(Long userId);

    /**
     * Get unread notification count for a user
     * @param userId the user ID
     * @return number of unread notifications
     */
    long getUnreadNotificationCount(Long userId);

    /**
     * Create transaction notification
     * @param user the user
     * @param transactionId the transaction ID
     * @param amount the transaction amount
     * @param type the transaction type
     * @return the created notification DTO
     */
    NotificationDTO createTransactionNotification(User user, String transactionId, 
                                                String amount, String type);

    /**
     * Create security notification
     * @param user the user
     * @param action the security action
     * @param details additional details
     * @return the created notification DTO
     */
    NotificationDTO createSecurityNotification(User user, String action, String details);

    /**
     * Clean up expired notifications
     * @return number of deleted notifications
     */
    int cleanupExpiredNotifications();
}
