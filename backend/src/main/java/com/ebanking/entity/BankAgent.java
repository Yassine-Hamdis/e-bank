package com.ebanking.entity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * BankAgent entity extending User
 * Represents bank agents who manage clients
 */
@Entity
@Table(name = "bank_agents")
@DiscriminatorValue("AGENT")
@PrimaryKeyJoinColumn(name = "user_id")
public class BankAgent extends User {

    @Column(name = "employee_id", unique = true, nullable = false, length = 20)
    private String employeeId;

    @Column(name = "branch", length = 100)
    private String branch;

    @Column(name = "position", length = 50)
    private String position = "Agent";

    @OneToMany(mappedBy = "managingAgent", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private Set<Client> managedClients = new HashSet<>();

    // Constructors
    public BankAgent() {
        super();
        setRole("ROLE_AGENT");
    }

    public BankAgent(String username, String password, String email, String phoneNumber, 
                    String employeeId, String branch) {
        super(username, password, email, phoneNumber, "ROLE_AGENT");
        this.employeeId = employeeId;
        this.branch = branch;
    }

    public BankAgent(String username, String password, String email, String phoneNumber, 
                    String employeeId, String branch, String position) {
        super(username, password, email, phoneNumber, "ROLE_AGENT");
        this.employeeId = employeeId;
        this.branch = branch;
        this.position = position;
    }

    // Getters and Setters
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

    public Set<Client> getManagedClients() {
        return managedClients;
    }

    public void setManagedClients(Set<Client> managedClients) {
        this.managedClients = managedClients;
    }

    // Helper methods
    public void addManagedClient(Client client) {
        managedClients.add(client);
        client.setManagingAgent(this);
    }

    public void removeManagedClient(Client client) {
        managedClients.remove(client);
        client.setManagingAgent(null);
    }

    @Override
    public String toString() {
        return "BankAgent{" +
                "id=" + getId() +
                ", username='" + getUsername() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", employeeId='" + employeeId + '\'' +
                ", branch='" + branch + '\'' +
                ", position='" + position + '\'' +
                ", status=" + getStatus() +
                ", managedClientsCount=" + managedClients.size() +
                '}';
    }
}
