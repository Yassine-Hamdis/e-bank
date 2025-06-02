package com.ebanking.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for Client response
 */
public class ClientDTO {

    private Long id;
    private String clientId;
    private String address;
    private String identificationNumber;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime enrollmentDate;
    
    private String username;
    private String email;
    private String phoneNumber;
    private String nationalId;
    private LocalDate dateOfBirth;
    private String status;
    
    // Client attributes from diagram
    private List<String> viewAccount;
    private boolean makeTransfer;
    private List<String> viewHistory;

    // Constructors
    public ClientDTO() {}

    public ClientDTO(Long id, String clientId, String address, String identificationNumber, 
                    LocalDateTime enrollmentDate, String username, String email) {
        this.id = id;
        this.clientId = clientId;
        this.address = address;
        this.identificationNumber = identificationNumber;
        this.enrollmentDate = enrollmentDate;
        this.username = username;
        this.email = email;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getViewAccount() {
        return viewAccount;
    }

    public void setViewAccount(List<String> viewAccount) {
        this.viewAccount = viewAccount;
    }

    public boolean isMakeTransfer() {
        return makeTransfer;
    }

    public void setMakeTransfer(boolean makeTransfer) {
        this.makeTransfer = makeTransfer;
    }

    public List<String> getViewHistory() {
        return viewHistory;
    }

    public void setViewHistory(List<String> viewHistory) {
        this.viewHistory = viewHistory;
    }

    @Override
    public String toString() {
        return "ClientDTO{" +
                "id=" + id +
                ", clientId='" + clientId + '\'' +
                ", address='" + address + '\'' +
                ", identificationNumber='" + identificationNumber + '\'' +
                ", enrollmentDate=" + enrollmentDate +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
