package com.pms.entity;

import java.time.LocalDate;

public class Policy {
    private String policyId;
    private String policyName; // New field
    private LocalDate startDate;
    private Double totalPremiumAmount;
    private Double maturityAmount;
    private Integer policyTerm;
    private PolicyStatus policyStatus;
    private AnnuityTerm annuityTerm;
    private Customer customer;

    public enum PolicyStatus {
        ACTIVE, INACTIVE, CLOSED
    }

    public enum AnnuityTerm {
        QUARTERLY, HALF_YEARLY, ANNUAL, ONE_TIME
    }

    // Getters and setters

    public String getPolicyId() {
        return policyId;
    }
    public void setPolicyId(String policyId2) {
        this.policyId = policyId2;
    }

    public String getPolicyName() {
        return policyName;
    }
    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }

    public LocalDate getStartDate() {
        return startDate;
    }
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    public Double getTotalPremiumAmount() {
        return totalPremiumAmount;
    }
    public void setTotalPremiumAmount(Double totalPremiumAmount) {
        this.totalPremiumAmount = totalPremiumAmount;
    }
    public Double getMaturityAmount() {
        return maturityAmount;
    }
    public void setMaturityAmount(Double maturityAmount) {
        this.maturityAmount = maturityAmount;
    }
    public Integer getPolicyTerm() {
        return policyTerm;
    }
    public void setPolicyTerm(Integer policyTerm) {
        this.policyTerm = policyTerm;
    }
    public PolicyStatus getPolicyStatus() {
        return policyStatus;
    }
    public void setPolicyStatus(PolicyStatus policyStatus) {
        this.policyStatus = policyStatus;
    }
    public AnnuityTerm getAnnuityTerm() {
        return annuityTerm;
    }
    public void setAnnuityTerm(AnnuityTerm annuityTerm) {
        this.annuityTerm = annuityTerm;
    }
    public Customer getCustomer() {
        return customer;
    }
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}
