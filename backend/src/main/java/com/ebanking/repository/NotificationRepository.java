package com.ebanking.repository;

import com.ebanking.entity.Notification;
import com.ebanking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Notification entity
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * Find notification by notification ID
     * @param notificationId the notification ID
     * @return Optional containing the notification if found
     */
    Optional<Notification> findByNotificationId(String notificationId);

    /**
     * Find all notifications for a specific user
     * @param user the user
     * @return list of notifications for the user
     */
    List<Notification> findByUserOrderByCreatedDateDesc(User user);

    /**
     * Find all notifications for a specific user ID
     * @param userId the user ID
     * @return list of notifications for the user
     */
    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId ORDER BY n.createdDate DESC")
    List<Notification> findByUserIdOrderByCreatedDateDesc(@Param("userId") Long userId);

    /**
     * Find unread notifications for a specific user
     * @param user the user
     * @return list of unread notifications for the user
     */
    List<Notification> findByUserAndIsReadFalseOrderByCreatedDateDesc(User user);

    /**
     * Find unread notifications for a specific user ID
     * @param userId the user ID
     * @return list of unread notifications for the user
     */
    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId AND n.isRead = false ORDER BY n.createdDate DESC")
    List<Notification> findUnreadByUserIdOrderByCreatedDateDesc(@Param("userId") Long userId);

    /**
     * Find notifications by type for a specific user
     * @param user the user
     * @param type the notification type
     * @return list of notifications of the specified type for the user
     */
    List<Notification> findByUserAndTypeOrderByCreatedDateDesc(User user, Notification.NotificationType type);

    /**
     * Find notifications by priority for a specific user
     * @param user the user
     * @param priority the notification priority
     * @return list of notifications with the specified priority for the user
     */
    List<Notification> findByUserAndPriorityOrderByCreatedDateDesc(User user, Notification.NotificationPriority priority);

    /**
     * Find expired notifications
     * @param currentTime the current time
     * @return list of expired notifications
     */
    @Query("SELECT n FROM Notification n WHERE n.expiresDate IS NOT NULL AND n.expiresDate < :currentTime")
    List<Notification> findExpiredNotifications(@Param("currentTime") LocalDateTime currentTime);

    /**
     * Find notifications created within a date range for a user
     * @param user the user
     * @param startDate the start date
     * @param endDate the end date
     * @return list of notifications created within the date range
     */
    @Query("SELECT n FROM Notification n WHERE n.user = :user AND n.createdDate BETWEEN :startDate AND :endDate ORDER BY n.createdDate DESC")
    List<Notification> findByUserAndCreatedDateBetween(@Param("user") User user, 
                                                      @Param("startDate") LocalDateTime startDate, 
                                                      @Param("endDate") LocalDateTime endDate);

    /**
     * Count unread notifications for a specific user
     * @param user the user
     * @return number of unread notifications for the user
     */
    long countByUserAndIsReadFalse(User user);

    /**
     * Count unread notifications for a specific user ID
     * @param userId the user ID
     * @return number of unread notifications for the user
     */
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.user.id = :userId AND n.isRead = false")
    long countUnreadByUserId(@Param("userId") Long userId);

    /**
     * Check if notification ID exists
     * @param notificationId the notification ID
     * @return true if exists, false otherwise
     */
    boolean existsByNotificationId(String notificationId);

    /**
     * Delete expired notifications
     * @param currentTime the current time
     * @return number of deleted notifications
     */
    @Query("DELETE FROM Notification n WHERE n.expiresDate IS NOT NULL AND n.expiresDate < :currentTime")
    int deleteExpiredNotifications(@Param("currentTime") LocalDateTime currentTime);

    /**
     * Find recent notifications (last 7 days) for a user
     * @param user the user
     * @param sevenDaysAgo the date 7 days ago
     * @return list of recent notifications
     */
    @Query("SELECT n FROM Notification n WHERE n.user = :user AND n.createdDate >= :sevenDaysAgo ORDER BY n.createdDate DESC")
    List<Notification> findRecentNotifications(@Param("user") User user, @Param("sevenDaysAgo") LocalDateTime sevenDaysAgo);

    /**
     * Find high priority unread notifications for a user
     * @param user the user
     * @return list of high priority unread notifications
     */
    @Query("SELECT n FROM Notification n WHERE n.user = :user AND n.isRead = false AND (n.priority = 'HIGH' OR n.priority = 'URGENT') ORDER BY n.priority DESC, n.createdDate DESC")
    List<Notification> findHighPriorityUnreadNotifications(@Param("user") User user);
}
