package com.ebanking.service;

/**
 * Service interface for email operations
 */
public interface EmailService {

    /**
     * Send bank agent credentials via email
     *
     * @param email the recipient email address
     * @param agentName the bank agent's name/username
     * @param username the login username
     * @param password the generated password
     * @param employeeId the employee ID
     * @param branch the branch name
     */
    void sendBankAgentCredentials(String email, String agentName, String username,
                                 String password, String employeeId, String branch);

    /**
     * Send client credentials via email
     * @param email the client's email address
     * @param clientName the client's name
     * @param username the client's username
     * @param password the client's password
     * @param clientId the client's ID
     * @param accountId the client's account ID
     */
    void sendClientCredentials(String email, String clientName, String username,
                             String password, String clientId, String accountId);

    /**
     * Send a simple email
     *
     * @param to recipient email address
     * @param subject email subject
     * @param content email content
     */
    void sendSimpleEmail(String to, String subject, String content);
}
