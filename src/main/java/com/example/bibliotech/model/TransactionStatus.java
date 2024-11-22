package com.example.bibliotech.model;

public enum TransactionStatus {
    PENDING("PENDING"),
    COMPLETED("COMPLETED"),
    FAILED("FAILED");

    private final String value;

    TransactionStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static TransactionStatus fromString(String value) {
        for (TransactionStatus status : TransactionStatus.values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid transaction status: " + value);
    }
}