package com.ebanking.dto.response;

import com.ebanking.entity.UserStatus;
import java.time.LocalDateTime;

/**
 * DTO for BankAgent response
 */
public class BankAgentDTO {

    private Long id;
    private String username;
    private String email;
    private String phoneNumber;
    private String employeeId;
    private String branch;
    private String position;
    private UserStatus status;
    private String role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int managedClientsCount;

    // Constructors
    public BankAgentDTO() {}

    public BankAgentDTO(Long id, String username, String email, String phoneNumber,
                       String employeeId, String branch, String position, UserStatus status,
                       String role, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.employeeId = employeeId;
        this.branch = branch;
        this.position = position;
        this.status = status;
        this.role = role;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int getManagedClientsCount() {
        return managedClientsCount;
    }

    public void setManagedClientsCount(int managedClientsCount) {
        this.managedClientsCount = managedClientsCount;
    }

    @Override
    public String toString() {
        return "BankAgentDTO{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", employeeId='" + employeeId + '\'' +
                ", branch='" + branch + '\'' +
                ", position='" + position + '\'' +
                ", status=" + status +
                ", role='" + role + '\'' +
                ", managedClientsCount=" + managedClientsCount +
                '}';
    }
}
