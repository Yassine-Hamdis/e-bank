package com.ebanking.dto.request;

import javax.validation.constraints.Size;

/**
 * DTO for client update request
 * All fields are optional for partial updates
 */
public class ClientUpdateRequest {

    @Size(max = 500, message = "Address must not exceed 500 characters")
    private String address;

    @Size(max = 20, message = "Identification number must not exceed 20 characters")
    private String identificationNumber;

    // Optional fields for complete client updates
    private String username;
    private String email;
    private String phoneNumber;
    private String nationalId;

    // Constructors
    public ClientUpdateRequest() {}

    public ClientUpdateRequest(String address, String identificationNumber) {
        this.address = address;
        this.identificationNumber = identificationNumber;
    }

    // Getters and Setters
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

    @Override
    public String toString() {
        return "ClientUpdateRequest{" +
                "address='" + address + '\'' +
                ", identificationNumber='" + identificationNumber + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", nationalId='" + nationalId + '\'' +
                '}';
    }
}
