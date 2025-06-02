package com.ebanking.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for agent deposit statistics
 */
public class AgentDepositStatisticsDTO {

    private String agentId;
    private String agentEmployeeId;
    private String agentName;
    private String branch;
    private long totalDeposits;
    private BigDecimal totalDepositAmount;
    private long depositsToday;
    private BigDecimal depositAmountToday;
    private long depositsThisWeek;
    private BigDecimal depositAmountThisWeek;
    private long depositsThisMonth;
    private BigDecimal depositAmountThisMonth;
    private BigDecimal averageDepositAmount;
    private BigDecimal largestDeposit;
    private BigDecimal smallestDeposit;
    private LocalDateTime lastDepositDate;
    private long managedClientsCount;
    private long timestamp;

    // Constructors
    public AgentDepositStatisticsDTO() {
        this.timestamp = System.currentTimeMillis();
    }

    public AgentDepositStatisticsDTO(String agentId, String agentEmployeeId, String agentName, String branch,
                                   long totalDeposits, BigDecimal totalDepositAmount, long depositsToday,
                                   BigDecimal depositAmountToday, long depositsThisWeek, BigDecimal depositAmountThisWeek,
                                   long depositsThisMonth, BigDecimal depositAmountThisMonth, BigDecimal averageDepositAmount,
                                   BigDecimal largestDeposit, BigDecimal smallestDeposit, LocalDateTime lastDepositDate,
                                   long managedClientsCount) {
        this();
        this.agentId = agentId;
        this.agentEmployeeId = agentEmployeeId;
        this.agentName = agentName;
        this.branch = branch;
        this.totalDeposits = totalDeposits;
        this.totalDepositAmount = totalDepositAmount;
        this.depositsToday = depositsToday;
        this.depositAmountToday = depositAmountToday;
        this.depositsThisWeek = depositsThisWeek;
        this.depositAmountThisWeek = depositAmountThisWeek;
        this.depositsThisMonth = depositsThisMonth;
        this.depositAmountThisMonth = depositAmountThisMonth;
        this.averageDepositAmount = averageDepositAmount;
        this.largestDeposit = largestDeposit;
        this.smallestDeposit = smallestDeposit;
        this.lastDepositDate = lastDepositDate;
        this.managedClientsCount = managedClientsCount;
    }

    // Getters and Setters
    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getAgentEmployeeId() {
        return agentEmployeeId;
    }

    public void setAgentEmployeeId(String agentEmployeeId) {
        this.agentEmployeeId = agentEmployeeId;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public long getTotalDeposits() {
        return totalDeposits;
    }

    public void setTotalDeposits(long totalDeposits) {
        this.totalDeposits = totalDeposits;
    }

    public BigDecimal getTotalDepositAmount() {
        return totalDepositAmount;
    }

    public void setTotalDepositAmount(BigDecimal totalDepositAmount) {
        this.totalDepositAmount = totalDepositAmount;
    }

    public long getDepositsToday() {
        return depositsToday;
    }

    public void setDepositsToday(long depositsToday) {
        this.depositsToday = depositsToday;
    }

    public BigDecimal getDepositAmountToday() {
        return depositAmountToday;
    }

    public void setDepositAmountToday(BigDecimal depositAmountToday) {
        this.depositAmountToday = depositAmountToday;
    }

    public long getDepositsThisWeek() {
        return depositsThisWeek;
    }

    public void setDepositsThisWeek(long depositsThisWeek) {
        this.depositsThisWeek = depositsThisWeek;
    }

    public BigDecimal getDepositAmountThisWeek() {
        return depositAmountThisWeek;
    }

    public void setDepositAmountThisWeek(BigDecimal depositAmountThisWeek) {
        this.depositAmountThisWeek = depositAmountThisWeek;
    }

    public long getDepositsThisMonth() {
        return depositsThisMonth;
    }

    public void setDepositsThisMonth(long depositsThisMonth) {
        this.depositsThisMonth = depositsThisMonth;
    }

    public BigDecimal getDepositAmountThisMonth() {
        return depositAmountThisMonth;
    }

    public void setDepositAmountThisMonth(BigDecimal depositAmountThisMonth) {
        this.depositAmountThisMonth = depositAmountThisMonth;
    }

    public BigDecimal getAverageDepositAmount() {
        return averageDepositAmount;
    }

    public void setAverageDepositAmount(BigDecimal averageDepositAmount) {
        this.averageDepositAmount = averageDepositAmount;
    }

    public BigDecimal getLargestDeposit() {
        return largestDeposit;
    }

    public void setLargestDeposit(BigDecimal largestDeposit) {
        this.largestDeposit = largestDeposit;
    }

    public BigDecimal getSmallestDeposit() {
        return smallestDeposit;
    }

    public void setSmallestDeposit(BigDecimal smallestDeposit) {
        this.smallestDeposit = smallestDeposit;
    }

    public LocalDateTime getLastDepositDate() {
        return lastDepositDate;
    }

    public void setLastDepositDate(LocalDateTime lastDepositDate) {
        this.lastDepositDate = lastDepositDate;
    }

    public long getManagedClientsCount() {
        return managedClientsCount;
    }

    public void setManagedClientsCount(long managedClientsCount) {
        this.managedClientsCount = managedClientsCount;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "AgentDepositStatisticsDTO{" +
                "agentId='" + agentId + '\'' +
                ", agentEmployeeId='" + agentEmployeeId + '\'' +
                ", agentName='" + agentName + '\'' +
                ", branch='" + branch + '\'' +
                ", totalDeposits=" + totalDeposits +
                ", totalDepositAmount=" + totalDepositAmount +
                ", depositsToday=" + depositsToday +
                ", depositAmountToday=" + depositAmountToday +
                ", depositsThisWeek=" + depositsThisWeek +
                ", depositAmountThisWeek=" + depositAmountThisWeek +
                ", depositsThisMonth=" + depositsThisMonth +
                ", depositAmountThisMonth=" + depositAmountThisMonth +
                ", averageDepositAmount=" + averageDepositAmount +
                ", largestDeposit=" + largestDeposit +
                ", smallestDeposit=" + smallestDeposit +
                ", lastDepositDate=" + lastDepositDate +
                ", managedClientsCount=" + managedClientsCount +
                ", timestamp=" + timestamp +
                '}';
    }
}
