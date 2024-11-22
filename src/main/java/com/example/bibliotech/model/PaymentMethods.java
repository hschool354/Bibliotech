package com.example.bibliotech.model;

import java.time.LocalDateTime;

public class PaymentMethods {
    private int paymentMethodId;
    private int transactionId;
    private String methodType;
    private String cardNumber;
    private String cardHolder;
    private String expirationDate;
    private String cvv;
    private LocalDateTime createdAt;

    // Constructor
    public PaymentMethods(int paymentMethodId, int transactionId, String methodType, String cardNumber, String cardHolder, String expirationDate, String cvv, LocalDateTime createdAt) {
        this.paymentMethodId = paymentMethodId;
        this.transactionId = transactionId;
        this.methodType = methodType;
        this.cardNumber = cardNumber;
        this.cardHolder = cardHolder;
        this.expirationDate = expirationDate;
        this.cvv = cvv;
        this.createdAt = createdAt;
    }

    // Getter and Setter methods
    public int getPaymentMethodId() {
        return paymentMethodId;
    }

    public void setPaymentMethodId(int paymentMethodId) {
        this.paymentMethodId = paymentMethodId;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public String getMethodType() {
        return methodType;
    }

    public void setMethodType(String methodType) {
        this.methodType = methodType;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardHolder() {
        return cardHolder;
    }

    public void setCardHolder(String cardHolder) {
        this.cardHolder = cardHolder;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
