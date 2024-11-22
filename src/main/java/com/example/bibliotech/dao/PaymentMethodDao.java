package com.example.bibliotech.dao;

import com.example.bibliotech.config.DatabaseConfig;
import com.example.bibliotech.exception.DatabaseException;
import com.example.bibliotech.model.PaymentMethods;
import com.example.bibliotech.model.TransactionStatus;
import com.example.bibliotech.model.Transactions;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PaymentMethodDao {
    // Lấy tất cả phương thức thanh toán
    public List<PaymentMethods> getAllPaymentMethods() throws DatabaseException {
        String sql = "SELECT * FROM PaymentMethods";
        List<PaymentMethods> paymentMethods = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int paymentMethodId = rs.getInt("payment_method_id");
                int transactionId = rs.getInt("transaction_id");
                String methodType = rs.getString("method_type");
                String cardNumber = rs.getString("card_number");
                String cardHolder = rs.getString("card_holder");
                String expirationDate = rs.getString("expiration_date");
                String cvv = rs.getString("cvv");
                Timestamp createdAt = rs.getTimestamp("created_at");

                paymentMethods.add(new PaymentMethods(paymentMethodId, transactionId, methodType, cardNumber, cardHolder, expirationDate, cvv, createdAt.toLocalDateTime()));
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving payment methods", e);
        }

        return paymentMethods;
    }

    // Thêm phương thức thanh toán mới
    public void addPaymentMethod(PaymentMethods paymentMethod) throws DatabaseException {
        String sql = "INSERT INTO PaymentMethods (transaction_id, method_type, card_number, card_holder, expiration_date, cvv) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, paymentMethod.getTransactionId());
            stmt.setString(2, paymentMethod.getMethodType());
            stmt.setString(3, paymentMethod.getCardNumber());
            stmt.setString(4, paymentMethod.getCardHolder());
            stmt.setString(5, paymentMethod.getExpirationDate());
            stmt.setString(6, paymentMethod.getCvv());

            System.out.println("Executing SQL: " + stmt.toString()); // Debugging SQL query
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new DatabaseException("Error adding payment method: " + e.getMessage(), e);
        }
    }


    // Lấy phương thức thanh toán theo ID
    public PaymentMethods getPaymentMethodById(int paymentMethodId) throws DatabaseException {
        String sql = "SELECT * FROM PaymentMethods WHERE payment_method_id = ?";
        PaymentMethods paymentMethod = null;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, paymentMethodId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int transactionId = rs.getInt("transaction_id");
                String methodType = rs.getString("method_type");
                String cardNumber = rs.getString("card_number");
                String cardHolder = rs.getString("card_holder");
                String expirationDate = rs.getString("expiration_date");
                String cvv = rs.getString("cvv");
                Timestamp createdAt = rs.getTimestamp("created_at");

                paymentMethod = new PaymentMethods(paymentMethodId, transactionId, methodType, cardNumber, cardHolder, expirationDate, cvv, createdAt.toLocalDateTime());
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving payment method", e);
        }

        return paymentMethod;
    }

    public int addPaymentMethodWithTransaction(PaymentMethods paymentMethod, Transactions transaction) throws DatabaseException {
        Connection conn = null;
        int transactionId = -1;

        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);

            // 1. Create transaction first
            String transactionSql = "INSERT INTO Transactions (user_id, book_id, transaction_type, amount, status) " +
                    "VALUES (?, ?, ?, ?, ?)";

            try (PreparedStatement stmt = conn.prepareStatement(transactionSql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setInt(1, transaction.getUserId());
                stmt.setObject(2, transaction.getBookId(), Types.INTEGER);
                stmt.setString(3, transaction.getTransactionType());
                stmt.setBigDecimal(4, transaction.getAmount());
                stmt.setString(5, TransactionStatus.PENDING.getValue());

                stmt.executeUpdate();

                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        transactionId = rs.getInt(1);
                    }
                }
            }

            // 2. Add payment method
            if (transactionId > 0) {
                String paymentSql = "INSERT INTO PaymentMethods (transaction_id, method_type, card_number, " +
                        "card_holder, expiration_date, cvv) VALUES (?, ?, ?, ?, ?, ?)";

                try (PreparedStatement stmt = conn.prepareStatement(paymentSql)) {
                    stmt.setInt(1, transactionId);
                    stmt.setString(2, paymentMethod.getMethodType());
                    stmt.setString(3, paymentMethod.getCardNumber());
                    stmt.setString(4, paymentMethod.getCardHolder());
                    stmt.setString(5, paymentMethod.getExpirationDate());
                    stmt.setString(6, paymentMethod.getCvv());

                    stmt.executeUpdate();
                }

                // 3. Update transaction status to COMPLETED
                String updateSql = "UPDATE Transactions SET status = ? WHERE transaction_id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
                    stmt.setString(1, TransactionStatus.COMPLETED.getValue());
                    stmt.setInt(2, transactionId);
                    stmt.executeUpdate();
                }

                conn.commit();
            } else {
                conn.rollback();
                throw new DatabaseException("Failed to create transaction record");
            }

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    throw new DatabaseException("Error rolling back transaction", ex);
                }
            }
            throw new DatabaseException("Database error: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Error closing connection: " + e.getMessage());
                }
            }
        }

        return transactionId;
    }

}
