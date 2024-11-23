package com.example.bibliotech.model;

import java.time.LocalDateTime;

public class PremiumTransactions {
    private int premiumTransactionId;
    private int transactionId;
    private int packageId;
    private String subscriptionStatus;
    private LocalDateTime createdAt;

    // Constructor with transactionId and packageId (default status "ACTIVE")
    public PremiumTransactions(int transactionId, int packageId) {
        this.transactionId = transactionId;
        this.packageId = packageId;
        this.subscriptionStatus = "ACTIVE";
    }

    // Default constructor
    public PremiumTransactions() {
    }

    // Getters and Setters
    public int getPremiumTransactionId() {
        return premiumTransactionId;
    }

    public void setPremiumTransactionId(int premiumTransactionId) {
        this.premiumTransactionId = premiumTransactionId;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public int getPackageId() {
        return packageId;
    }

    public void setPackageId(int packageId) {
        this.packageId = packageId;
    }

    public String getSubscriptionStatus() {
        return subscriptionStatus;
    }

    public void setSubscriptionStatus(String subscriptionStatus) {
        this.subscriptionStatus = subscriptionStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // toString method
    @Override
    public String toString() {
        return "PremiumTransactions{" +
                "premiumTransactionId=" + premiumTransactionId +
                ", transactionId=" + transactionId +
                ", packageId=" + packageId +
                ", subscriptionStatus='" + subscriptionStatus + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
