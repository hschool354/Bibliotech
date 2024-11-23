package com.example.bibliotech.dao;

import com.example.bibliotech.config.DatabaseConfig;
import com.example.bibliotech.exception.DatabaseException;
import com.example.bibliotech.model.PremiumTransactions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PremiumTransactionDao {

    public void createPremiumTransaction(PremiumTransactions premiumTx) throws DatabaseException {
        String sql = "INSERT INTO PremiumTransactions (transaction_id, package_id, subscription_status) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, premiumTx.getTransactionId());
            stmt.setInt(2, premiumTx.getPackageId());
            stmt.setString(3, premiumTx.getSubscriptionStatus());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Error creating premium transaction", e);
        }
    }

    public PremiumTransactions getActivePremiumTransactionByUserId(int userId) throws DatabaseException {
        String sql = """
            SELECT pt.* 
            FROM PremiumTransactions pt
            JOIN Transactions t ON pt.transaction_id = t.transaction_id
            WHERE t.user_id = ? 
            AND pt.subscription_status = 'ACTIVE'
            ORDER BY pt.created_at DESC 
            LIMIT 1
            """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new PremiumTransactions(
                        rs.getInt("transaction_id"),
                        rs.getInt("package_id")
                );
            }
            return null;
        } catch (SQLException e) {
            throw new DatabaseException("Error getting active premium transaction", e);
        }
    }

    public void updateSubscriptionStatus(int premiumTransactionId, String status) throws DatabaseException {
        String sql = "UPDATE PremiumTransactions SET subscription_status = ? WHERE premium_transaction_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setInt(2, premiumTransactionId);

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Error updating subscription status", e);
        }
    }
}
