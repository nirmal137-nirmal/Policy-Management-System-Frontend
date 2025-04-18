package com.pms.entity;

import java.time.LocalDate;

public class BoughtPolicy {

    private Long boughtPolicyId;
    private Policy policy;
    private Customer customer;
    private LocalDate startDate;
    private Double totalPremiumAmount;
    private Double maturityAmount;
    private Integer policyTerm;
    private Policy.PolicyStatus policyStatus;
    private Policy.AnnuityTerm annuityTerm;

    public Long getBoughtPolicyId() {
        return boughtPolicyId;
    }

    public void setBoughtPolicyId(Long boughtPolicyId) {
        this.boughtPolicyId = boughtPolicyId;
    }

    public Policy getPolicy() {
        return policy;
    }

    public void setPolicy(Policy policy) {
        this.policy = policy;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
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

    public Policy.PolicyStatus getPolicyStatus() {
        return policyStatus;
    }

    public void setPolicyStatus(Policy.PolicyStatus policyStatus) {
        this.policyStatus = policyStatus;
    }

    public Policy.AnnuityTerm getAnnuityTerm() {
        return annuityTerm;
    }

    public void setAnnuityTerm(Policy.AnnuityTerm annuityTerm) {
        this.annuityTerm = annuityTerm;
    }
}
