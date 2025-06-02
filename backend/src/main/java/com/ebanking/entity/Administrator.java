package com.ebanking.entity;

import javax.persistence.*;

/**
 * Administrator entity extending User
 * Represents system administrators with elevated privileges
 */
@Entity
@Table(name = "administrators")
@DiscriminatorValue("ADMIN")
@PrimaryKeyJoinColumn(name = "user_id")
public class Administrator extends User {

    @Column(name = "admin_level")
    private String adminLevel = "STANDARD";

    @Column(name = "department", length = 100)
    private String department;

    // Constructors
    public Administrator() {
        super();
        setRole("ROLE_ADMIN");
    }

    public Administrator(String username, String password, String email, String phoneNumber) {
        super(username, password, email, phoneNumber, "ROLE_ADMIN");
        this.adminLevel = "STANDARD";
    }

    public Administrator(String username, String password, String email, String phoneNumber, 
                        String adminLevel, String department) {
        super(username, password, email, phoneNumber, "ROLE_ADMIN");
        this.adminLevel = adminLevel;
        this.department = department;
    }

    // Getters and Setters
    public String getAdminLevel() {
        return adminLevel;
    }

    public void setAdminLevel(String adminLevel) {
        this.adminLevel = adminLevel;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    @Override
    public String toString() {
        return "Administrator{" +
                "id=" + getId() +
                ", username='" + getUsername() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", adminLevel='" + adminLevel + '\'' +
                ", department='" + department + '\'' +
                ", status=" + getStatus() +
                '}';
    }
}
