package com.example.bibliotech.model;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

public class PremiumPackages {
    private int packageId;
    private String packageName;
    private BigDecimal price;
    private int duration;
    private BillingCycle billingCycle;
    private List<String> features;

    public enum BillingCycle {
        MONTHLY, YEARLY
    }

    public PremiumPackages(int packageId, String packageName, BigDecimal price,
                          int duration, BillingCycle billingCycle, String features) {
        this.packageId = packageId;
        this.packageName = packageName;
        this.price = price;
        this.duration = duration;
        this.billingCycle = billingCycle;
        this.features = Arrays.asList(features.split("\n"));
    }

    // Getters and setters
    public int getPackageId() { return packageId; }
    public String getPackageName() { return packageName; }
    public BigDecimal getPrice() { return price; }
    public int getDuration() { return duration; }
    public BillingCycle getBillingCycle() { return billingCycle; }
    public List<String> getFeatures() { return features; }
}