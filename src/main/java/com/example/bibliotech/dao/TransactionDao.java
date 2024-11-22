package com.example.bibliotech.dao;

import com.example.bibliotech.config.DatabaseConfig;
import com.example.bibliotech.exception.DatabaseException;
import com.example.bibliotech.model.TransactionStatus;
import com.example.bibliotech.model.Transactions;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionDao {
    // Lấy tất cả giao dịch
    public List<Transactions> getAllTransactions() throws DatabaseException {
        String sql = "SELECT * FROM Transactions";
        List<Transactions> transactions = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int transactionId = rs.getInt("transaction_id");
                int userId = rs.getInt("user_id");
                Integer bookId = rs.getObject("book_id", Integer.class);
                String transactionType = rs.getString("transaction_type");
                BigDecimal amount = rs.getBigDecimal("amount");
                String statusStr = rs.getString("status");
                TransactionStatus status = TransactionStatus.fromString(statusStr);
                Timestamp transactionDate = rs.getTimestamp("transaction_date");
                transactions.add(new Transactions(transactionId, userId, bookId,
                        transactionType, amount, status, transactionDate.toLocalDateTime()));            }

        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving transactions", e);
        }

        return transactions;
    }

    // Thêm giao dịch mới
    public void addTransaction(Transactions transaction) throws DatabaseException {
        String sql = "INSERT INTO Transactions (user_id, book_id, transaction_type, amount, status) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, transaction.getUserId());
            stmt.setObject(2, transaction.getBookId(), Types.INTEGER); // book_id có thể null
            stmt.setString(3, transaction.getTransactionType());
            stmt.setBigDecimal(4, transaction.getAmount());
            stmt.setString(5, transaction.getStatus().getValue());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new DatabaseException("Error adding transaction", e);
        }
    }

    // Lấy giao dịch theo ID
    public Transactions getTransactionById(int transactionId) throws DatabaseException {
        String sql = "SELECT * FROM Transactions WHERE transaction_id = ?";
        Transactions transaction = null;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, transactionId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int userId = rs.getInt("user_id");
                Integer bookId = rs.getObject("book_id", Integer.class);
                String transactionType = rs.getString("transaction_type");
                BigDecimal amount = rs.getBigDecimal("amount");
                String statusStr = rs.getString("status");
                TransactionStatus status = TransactionStatus.fromString(statusStr);
                Timestamp transactionDate = rs.getTimestamp("transaction_date");
                transaction = new Transactions(transactionId, userId, bookId,
                        transactionType, amount, status, transactionDate.toLocalDateTime());            }

        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving transaction", e);
        }

        return transaction;
    }

    public int createTransactionAndGetId(Transactions transaction) throws DatabaseException {
        String sql = "INSERT INTO Transactions (user_id, book_id, transaction_type, amount, status) VALUES (?, ?, ?, ?, ?)";
        int transactionId = -1;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, transaction.getUserId());
            stmt.setObject(2, transaction.getBookId(), Types.INTEGER); // book_id có thể null
            stmt.setString(3, transaction.getTransactionType());
            stmt.setBigDecimal(4, transaction.getAmount());
            stmt.setString(5, transaction.getStatus().getValue());

            stmt.executeUpdate();

            // Lấy transaction_id vừa được tạo
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    transactionId = rs.getInt(1);
                }
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error creating transaction", e);
        }

        return transactionId;
    }

    public void updateTransactionStatus(int transactionId, TransactionStatus status) throws DatabaseException {
        String sql = "UPDATE Transactions SET status = ? WHERE transaction_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            conn.setAutoCommit(true); // Ensure autocommit is on for single updates
            stmt.setString(1, status.getValue());
            stmt.setInt(2, transactionId);

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated == 0) {
                throw new DatabaseException("No transaction found with ID: " + transactionId);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error updating transaction status", e);
        }
    }



}
