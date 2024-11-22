package com.example.bibliotech.model;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transactions {
    private int transactionId;
    private int userId;
    private Integer bookId; // book_id có thể null
    private String transactionType;
    private BigDecimal amount;
    private TransactionStatus status;
    private LocalDateTime transactionDate;

    // Constructor
    public Transactions(int transactionId, int userId, Integer bookId, String transactionType, BigDecimal amount, TransactionStatus status, LocalDateTime transactionDate) {
        this.transactionId = transactionId;
        this.userId = userId;
        this.bookId = bookId;
        this.transactionType = transactionType;
        this.amount = amount;
        this.status = status;
        this.transactionDate = transactionDate;
    }

    public Transactions(int userId, Integer bookId, String transactionType,
                        BigDecimal amount, TransactionStatus status) {
        this.userId = userId;
        this.bookId = bookId;
        this.transactionType = transactionType;
        this.amount = amount;
        this.status = status;
    }

    // Getter and Setter methods
    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }
}

