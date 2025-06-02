package com.ebanking.service.impl;

import com.ebanking.entity.TransactionStatus;
import com.ebanking.entity.UserStatus;
import com.ebanking.repository.*;
import com.ebanking.service.GlobalStatsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of GlobalStatsService for aggregating application statistics
 */
@Service
@Transactional(readOnly = true)
public class GlobalStatsServiceImpl implements GlobalStatsService {

    private static final Logger logger = LoggerFactory.getLogger(GlobalStatsServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private BankAgentRepository bankAgentRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CurrencyRepository currencyRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Override
    public GlobalApplicationStatistics getGlobalStatistics() {
        logger.debug("Fetching global application statistics");

        try {
            // User statistics
            long totalUsers = userRepository.count();
            long totalClients = clientRepository.count();
            long totalBankAgents = bankAgentRepository.count();
            
            // Calculate administrators count (total users - clients - bank agents)
            long totalAdministrators = totalUsers - totalClients - totalBankAgents;
            
            // Active/Inactive users
            long activeUsers = clientRepository.countByStatus(UserStatus.ACTIVE) + 
                              countActiveBankAgents() + 
                              countActiveAdministrators();
            long inactiveUsers = totalUsers - activeUsers;

            // Account statistics
            long totalAccounts = accountRepository.count();

            // Transaction statistics
            long totalTransactions = transactionRepository.count();
            long pendingTransactions = transactionRepository.countByStatus(TransactionStatus.PENDING);
            long completedTransactions = transactionRepository.countByStatus(TransactionStatus.COMPLETED);
            long failedTransactions = transactionRepository.countByStatus(TransactionStatus.FAILED);

            // Currency statistics
            long totalCurrencies = currencyRepository.count();
            long activeCurrencies = currencyRepository.countActiveCurrencies();

            // Notification statistics
            long totalNotifications = notificationRepository.count();
            long unreadNotifications = countUnreadNotifications();

            GlobalApplicationStatistics stats = new GlobalApplicationStatistics(
                totalUsers, totalClients, totalBankAgents, totalAdministrators,
                activeUsers, inactiveUsers, totalAccounts, totalTransactions,
                pendingTransactions, completedTransactions, failedTransactions,
                totalCurrencies, activeCurrencies, totalNotifications, unreadNotifications
            );

            logger.debug("Global statistics retrieved successfully");
            return stats;

        } catch (Exception e) {
            logger.error("Failed to fetch global statistics: {}", e.getMessage(), e);
            
            // Return empty statistics in case of error
            GlobalApplicationStatistics errorStats = new GlobalApplicationStatistics();
            errorStats.setSystemStatus("ERROR");
            return errorStats;
        }
    }

    /**
     * Count active bank agents
     */
    private long countActiveBankAgents() {
        try {
            return bankAgentRepository.findAll().stream()
                    .filter(agent -> agent.getStatus() == UserStatus.ACTIVE)
                    .count();
        } catch (Exception e) {
            logger.warn("Failed to count active bank agents: {}", e.getMessage());
            return 0;
        }
    }

    /**
     * Count active administrators
     * Note: This is a simplified implementation. In a real scenario, you might want to
     * add a specific repository method or query for administrators.
     */
    private long countActiveAdministrators() {
        try {
            // For now, assume all administrators are active
            // You can enhance this by adding specific queries for administrators
            long totalUsers = userRepository.count();
            long totalClients = clientRepository.count();
            long totalBankAgents = bankAgentRepository.count();
            return Math.max(0, totalUsers - totalClients - totalBankAgents);
        } catch (Exception e) {
            logger.warn("Failed to count active administrators: {}", e.getMessage());
            return 0;
        }
    }

    /**
     * Count unread notifications across all users
     */
    private long countUnreadNotifications() {
        try {
            // This is a simplified count. You might want to implement a more specific query
            // in the NotificationRepository for better performance
            return notificationRepository.findAll().stream()
                    .filter(notification -> !notification.getIsRead())
                    .count();
        } catch (Exception e) {
            logger.warn("Failed to count unread notifications: {}", e.getMessage());
            return 0;
        }
    }
}
