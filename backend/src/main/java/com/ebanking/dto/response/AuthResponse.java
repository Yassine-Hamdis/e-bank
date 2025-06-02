package com.ebanking.dto.response;

import java.util.List;

/**
 * DTO for authentication response
 */
public class AuthResponse {

    private String token;
    private String username;
    private String email;
    private List<String> roles;
    private String tokenType = "Bearer";
    private Long expiresIn; // in seconds

    // Constructors
    public AuthResponse() {}

    public AuthResponse(String token, String username, String email, List<String> roles) {
        this.token = token;
        this.username = username;
        this.email = email;
        this.roles = roles;
    }

    public AuthResponse(String token, String username, String email, List<String> roles, Long expiresIn) {
        this.token = token;
        this.username = username;
        this.email = email;
        this.roles = roles;
        this.expiresIn = expiresIn;
    }

    // Getters and Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    @Override
    public String toString() {
        return "AuthResponse{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", roles=" + roles +
                ", tokenType='" + tokenType + '\'' +
                ", expiresIn=" + expiresIn +
                ", token='[PROTECTED]'" +
                '}';
    }
}
