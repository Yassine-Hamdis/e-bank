package com.ebanking.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO for Client profile response
 */
public class ClientProfileDTO {

    private String clientId;
    private String address;
    private String identificationNumber;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime enrollmentDate;
    
    // User attributes
    private String username;
    private String email;
    private String phoneNumber;
    private String status;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime lastLogin;
    
    private String nationalId;
    private LocalDate dateOfBirth;

    // Constructors
    public ClientProfileDTO() {}

    public ClientProfileDTO(String clientId, String address, String identificationNumber, 
                           LocalDateTime enrollmentDate, String username, String email, 
                           String phoneNumber, String status, LocalDateTime lastLogin) {
        this.clientId = clientId;
        this.address = address;
        this.identificationNumber = identificationNumber;
        this.enrollmentDate = enrollmentDate;
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.status = status;
        this.lastLogin = lastLogin;
    }

    // Getters and Setters
    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getIdentificationNumber() {
        return identificationNumber;
    }

    public void setIdentificationNumber(String identificationNumber) {
        this.identificationNumber = identificationNumber;
    }

    public LocalDateTime getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(LocalDateTime enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public String getNationalId() {
        return nationalId;
    }

    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    @Override
    public String toString() {
        return "ClientProfileDTO{" +
                "clientId='" + clientId + '\'' +
                ", address='" + address + '\'' +
                ", identificationNumber='" + identificationNumber + '\'' +
                ", enrollmentDate=" + enrollmentDate +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", status='" + status + '\'' +
                ", lastLogin=" + lastLogin +
                ", nationalId='" + nationalId + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                '}';
    }
}
