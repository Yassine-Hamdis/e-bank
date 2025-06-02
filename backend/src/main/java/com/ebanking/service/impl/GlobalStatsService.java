package com.ebanking.service;

/**
 * Service interface for global application statistics
 */
public interface GlobalStatsService {

    /**
     * Get global application statistics
     * @return GlobalApplicationStatistics containing all statistics
     */
    GlobalApplicationStatistics getGlobalStatistics();

    /**
     * Inner class for global application statistics
     */
    class GlobalApplicationStatistics {
        private long totalUsers;
        private long totalClients;
        private long totalBankAgents;
        private long totalAdministrators;
        private long activeUsers;
        private long inactiveUsers;
        private long totalAccounts;
        private long totalTransactions;
        private long pendingTransactions;
        private long completedTransactions;
        private long failedTransactions;
        private long totalCurrencies;
        private long activeCurrencies;
        private long totalNotifications;
        private long unreadNotifications;
        private String systemStatus;
        private long timestamp;

        public GlobalApplicationStatistics() {
            this.timestamp = System.currentTimeMillis();
            this.systemStatus = "OPERATIONAL";
        }

        public GlobalApplicationStatistics(long totalUsers, long totalClients, long totalBankAgents, 
                                         long totalAdministrators, long activeUsers, long inactiveUsers,
                                         long totalAccounts, long totalTransactions, long pendingTransactions,
                                         long completedTransactions, long failedTransactions, long totalCurrencies,
                                         long activeCurrencies, long totalNotifications, long unreadNotifications) {
            this();
            this.totalUsers = totalUsers;
            this.totalClients = totalClients;
            this.totalBankAgents = totalBankAgents;
            this.totalAdministrators = totalAdministrators;
            this.activeUsers = activeUsers;
            this.inactiveUsers = inactiveUsers;
            this.totalAccounts = totalAccounts;
            this.totalTransactions = totalTransactions;
            this.pendingTransactions = pendingTransactions;
            this.completedTransactions = completedTransactions;
            this.failedTransactions = failedTransactions;
            this.totalCurrencies = totalCurrencies;
            this.activeCurrencies = activeCurrencies;
            this.totalNotifications = totalNotifications;
            this.unreadNotifications = unreadNotifications;
        }

        // Getters and Setters
        public long getTotalUsers() {
            return totalUsers;
        }

        public void setTotalUsers(long totalUsers) {
            this.totalUsers = totalUsers;
        }

        public long getTotalClients() {
            return totalClients;
        }

        public void setTotalClients(long totalClients) {
            this.totalClients = totalClients;
        }

        public long getTotalBankAgents() {
            return totalBankAgents;
        }

        public void setTotalBankAgents(long totalBankAgents) {
            this.totalBankAgents = totalBankAgents;
        }

        public long getTotalAdministrators() {
            return totalAdministrators;
        }

        public void setTotalAdministrators(long totalAdministrators) {
            this.totalAdministrators = totalAdministrators;
        }

        public long getActiveUsers() {
            return activeUsers;
        }

        public void setActiveUsers(long activeUsers) {
            this.activeUsers = activeUsers;
        }

        public long getInactiveUsers() {
            return inactiveUsers;
        }

        public void setInactiveUsers(long inactiveUsers) {
            this.inactiveUsers = inactiveUsers;
        }

        public long getTotalAccounts() {
            return totalAccounts;
        }

        public void setTotalAccounts(long totalAccounts) {
            this.totalAccounts = totalAccounts;
        }

        public long getTotalTransactions() {
            return totalTransactions;
        }

        public void setTotalTransactions(long totalTransactions) {
            this.totalTransactions = totalTransactions;
        }

        public long getPendingTransactions() {
            return pendingTransactions;
        }

        public void setPendingTransactions(long pendingTransactions) {
            this.pendingTransactions = pendingTransactions;
        }

        public long getCompletedTransactions() {
            return completedTransactions;
        }

        public void setCompletedTransactions(long completedTransactions) {
            this.completedTransactions = completedTransactions;
        }

        public long getFailedTransactions() {
            return failedTransactions;
        }

        public void setFailedTransactions(long failedTransactions) {
            this.failedTransactions = failedTransactions;
        }

        public long getTotalCurrencies() {
            return totalCurrencies;
        }

        public void setTotalCurrencies(long totalCurrencies) {
            this.totalCurrencies = totalCurrencies;
        }

        public long getActiveCurrencies() {
            return activeCurrencies;
        }

        public void setActiveCurrencies(long activeCurrencies) {
            this.activeCurrencies = activeCurrencies;
        }

        public long getTotalNotifications() {
            return totalNotifications;
        }

        public void setTotalNotifications(long totalNotifications) {
            this.totalNotifications = totalNotifications;
        }

        public long getUnreadNotifications() {
            return unreadNotifications;
        }

        public void setUnreadNotifications(long unreadNotifications) {
            this.unreadNotifications = unreadNotifications;
        }

        public String getSystemStatus() {
            return systemStatus;
        }

        public void setSystemStatus(String systemStatus) {
            this.systemStatus = systemStatus;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }
    }
}
