package com.ebanking.entity;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Client entity extending User
 * Represents bank clients managed by bank agents
 */
@Entity
@Table(name = "clients")
@DiscriminatorValue("CLIENT")
@PrimaryKeyJoinColumn(name = "user_id")
public class Client extends User {

    @Column(name = "client_id", unique = true, nullable = false, length = 20)
    private String clientId;

    @Column(name = "enrollment_date")
    private LocalDate enrollmentDate;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "address", length = 500)
    private String address;

    @Column(name = "national_id", unique = true, length = 20)
    private String nationalId;

    @Column(name = "identification_number", unique = true, length = 20)
    private String identificationNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "managing_agent_id")
    private BankAgent managingAgent;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Account> accounts = new HashSet<>();

    @OneToOne(mappedBy = "client", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private CryptoWallet cryptoWallet;

    // Constructors
    public Client() {
        super();
        setRole("ROLE_CLIENT");
        this.enrollmentDate = LocalDate.now();
    }

    public Client(String username, String password, String email, String phoneNumber,
                 String clientId) {
        super(username, password, email, phoneNumber, "ROLE_CLIENT");
        this.clientId = clientId;
        this.enrollmentDate = LocalDate.now();
    }

    public Client(String username, String password, String email, String phoneNumber,
                 String clientId, LocalDate dateOfBirth, String address, String nationalId) {
        super(username, password, email, phoneNumber, "ROLE_CLIENT");
        this.clientId = clientId;
        this.enrollmentDate = LocalDate.now();
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.nationalId = nationalId;
    }

    // Getters and Setters
    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public LocalDate getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(LocalDate enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNationalId() {
        return nationalId;
    }

    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
    }

    public BankAgent getManagingAgent() {
        return managingAgent;
    }

    public void setManagingAgent(BankAgent managingAgent) {
        this.managingAgent = managingAgent;
    }

    public String getIdentificationNumber() {
        return identificationNumber;
    }

    public void setIdentificationNumber(String identificationNumber) {
        this.identificationNumber = identificationNumber;
    }

    public Set<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(Set<Account> accounts) {
        this.accounts = accounts;
    }

    public CryptoWallet getCryptoWallet() {
        return cryptoWallet;
    }

    public void setCryptoWallet(CryptoWallet cryptoWallet) {
        this.cryptoWallet = cryptoWallet;
    }

    // Helper methods
    public void addAccount(Account account) {
        accounts.add(account);
        account.setClient(this);
    }

    public void removeAccount(Account account) {
        accounts.remove(account);
        account.setClient(null);
    }

    @Override
    public String toString() {
        return "Client{" +
                "id=" + getId() +
                ", username='" + getUsername() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", clientId='" + clientId + '\'' +
                ", enrollmentDate=" + enrollmentDate +
                ", dateOfBirth=" + dateOfBirth +
                ", nationalId='" + nationalId + '\'' +
                ", status=" + getStatus() +
                ", managingAgent=" + (managingAgent != null ? managingAgent.getUsername() : "None") +
                '}';
    }
}
