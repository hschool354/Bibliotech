package com.example.bibliotech.model;

import java.time.LocalDate;

public class UserSubscriptions {
    private int userId;
    private int packageId;
    private String packageName;
    private LocalDate startDate;
    private LocalDate endDate;
    private int duration;
    private boolean autoRenew;
    private int freeReadsRemaining;
    private String features;

    // Constructor
    public UserSubscriptions(int userId, int packageId, String packageName, LocalDate startDate,
                             LocalDate endDate, int duration, boolean autoRenew, int freeReadsRemaining, String features) {
        this.userId = userId;
        this.packageId = packageId;
        this.packageName = packageName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.duration = duration;
        this.autoRenew = autoRenew;
        this.freeReadsRemaining = freeReadsRemaining;
        this.features = features;
    }

    // Default constructor
    public UserSubscriptions() {
    }

    // Getters and Setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getPackageId() {
        return packageId;
    }

    public void setPackageId(int packageId) {
        this.packageId = packageId;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public boolean isAutoRenew() {
        return autoRenew;
    }

    public void setAutoRenew(boolean autoRenew) {
        this.autoRenew = autoRenew;
    }

    public int getFreeReadsRemaining() {
        return freeReadsRemaining;
    }

    public void setFreeReadsRemaining(int freeReadsRemaining) {
        this.freeReadsRemaining = freeReadsRemaining;
    }

    public String getFeatures() {
        return features;
    }

    public void setFeatures(String features) {
        this.features = features;
    }

    // toString method
    @Override
    public String toString() {
        return "UserSubscriptions{" +
                "userId=" + userId +
                ", packageId=" + packageId +
                ", packageName='" + packageName + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", duration=" + duration +
                ", autoRenew=" + autoRenew +
                ", freeReadsRemaining=" + freeReadsRemaining +
                ", features='" + features + '\'' +
                '}';
    }
}
