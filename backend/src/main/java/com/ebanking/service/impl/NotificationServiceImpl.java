package com.ebanking.service.impl;

import com.ebanking.dto.response.NotificationDTO;
import com.ebanking.entity.Notification;
import com.ebanking.entity.User;
import com.ebanking.repository.NotificationRepository;
import com.ebanking.repository.UserRepository;
import com.ebanking.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of NotificationService
 */
@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public NotificationDTO createNotification(User user, String title, String message, 
                                            Notification.NotificationType type, 
                                            Notification.NotificationPriority priority) {
        logger.info("Creating notification for user: {} with title: {}", user.getId(), title);

        String notificationId = "NOT" + System.currentTimeMillis();
        
        Notification notification = new Notification(notificationId, user, title, message, type, priority);
        
        // Set expiration date (30 days from now for normal notifications)
        if (priority != Notification.NotificationPriority.URGENT) {
            notification.setExpiresDate(LocalDateTime.now().plusDays(30));
        }

        Notification savedNotification = notificationRepository.save(notification);
        logger.info("Notification created successfully: {}", notificationId);

        return convertToNotificationDTO(savedNotification);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> getUserNotifications(Long userId) {
        logger.debug("Fetching notifications for user: {}", userId);

        List<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedDateDesc(userId);
        
        return notifications.stream()
                .map(this::convertToNotificationDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> getUnreadNotifications(Long userId) {
        logger.debug("Fetching unread notifications for user: {}", userId);

        List<Notification> notifications = notificationRepository.findUnreadByUserIdOrderByCreatedDateDesc(userId);
        
        return notifications.stream()
                .map(this::convertToNotificationDTO)
                .collect(Collectors.toList());
    }

    @Override
    public NotificationDTO markAsRead(String notificationId, Long userId) {
        logger.info("Marking notification as read: {} for user: {}", notificationId, userId);

        Notification notification = notificationRepository.findByNotificationId(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found with ID: " + notificationId));

        // Security check: ensure notification belongs to the user
        if (!notification.getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied: Notification does not belong to user");
        }

        notification.markAsRead();
        Notification updatedNotification = notificationRepository.save(notification);
        
        logger.info("Notification marked as read successfully: {}", notificationId);
        return convertToNotificationDTO(updatedNotification);
    }

    @Override
    public boolean deleteNotification(String notificationId, Long userId) {
        logger.info("Deleting notification: {} for user: {}", notificationId, userId);

        Notification notification = notificationRepository.findByNotificationId(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found with ID: " + notificationId));

        // Security check: ensure notification belongs to the user
        if (!notification.getUser().getId().equals(userId)) {
            throw new RuntimeException("Access denied: Notification does not belong to user");
        }

        notificationRepository.delete(notification);
        logger.info("Notification deleted successfully: {}", notificationId);
        
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public long getNotificationCount(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedDateDesc(userId).size();
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadNotificationCount(Long userId) {
        return notificationRepository.countUnreadByUserId(userId);
    }

    @Override
    public NotificationDTO createTransactionNotification(User user, String transactionId,
                                                       String amount, String type) {
        String title;
        String message;

        // Create user-friendly messages for different transaction types
        switch (type) {
            case "TRANSFER_SENT":
                title = "Transfer Sent";
                message = String.format("You have successfully sent %s MAD to another account. Transaction ID: %s",
                                       amount, transactionId);
                break;
            case "TRANSFER_RECEIVED":
                title = "Transfer Received";
                message = String.format("You have received %s MAD from another account. Transaction ID: %s",
                                       amount, transactionId);
                break;
            case "CRYPTO_TRANSFER_SENT":
                title = "Crypto Transfer Sent";
                message = String.format("You have successfully sent %s cryptocurrency. Transaction ID: %s",
                                       amount, transactionId);
                break;
            case "CRYPTO_TRANSFER_RECEIVED":
                title = "Crypto Transfer Received";
                message = String.format("You have received %s cryptocurrency. Transaction ID: %s",
                                       amount, transactionId);
                break;
            default:
                title = "Transaction " + type;
                message = String.format("Your %s transaction of %s MAD has been processed. Transaction ID: %s",
                                       type.toLowerCase(), amount, transactionId);
                break;
        }

        return createNotification(user, title, message,
                                Notification.NotificationType.TRANSACTION,
                                Notification.NotificationPriority.NORMAL);
    }

    @Override
    public NotificationDTO createSecurityNotification(User user, String action, String details) {
        String title = "Security Alert: " + action;
        String message = String.format("Security action detected: %s. Details: %s. If this wasn't you, please contact support immediately.", 
                                     action, details);
        
        return createNotification(user, title, message, 
                                Notification.NotificationType.SECURITY, 
                                Notification.NotificationPriority.HIGH);
    }

    @Override
    public int cleanupExpiredNotifications() {
        logger.info("Cleaning up expired notifications");
        
        int deletedCount = notificationRepository.deleteExpiredNotifications(LocalDateTime.now());
        
        logger.info("Cleaned up {} expired notifications", deletedCount);
        return deletedCount;
    }

    /**
     * Convert Notification entity to NotificationDTO
     */
    private NotificationDTO convertToNotificationDTO(Notification notification) {
        NotificationDTO dto = new NotificationDTO();
        dto.setId(notification.getId());
        dto.setNotificationId(notification.getNotificationId());
        dto.setTitle(notification.getTitle());
        dto.setMessage(notification.getMessage());
        dto.setType(notification.getType().name());
        dto.setPriority(notification.getPriority().name());
        dto.setIsRead(notification.getIsRead());
        dto.setReadDate(notification.getReadDate());
        dto.setCreatedDate(notification.getCreatedDate());
        dto.setExpiresDate(notification.getExpiresDate());
        dto.setActionUrl(notification.getActionUrl());
        dto.setActionLabel(notification.getActionLabel());
        
        return dto;
    }
}
