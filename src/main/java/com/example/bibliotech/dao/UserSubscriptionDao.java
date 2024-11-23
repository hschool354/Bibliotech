package com.example.bibliotech.dao;

import com.example.bibliotech.config.DatabaseConfig;
import com.example.bibliotech.exception.DatabaseException;
import com.example.bibliotech.model.UserSubscriptions;

import java.sql.*;
import java.time.LocalDate;

public class UserSubscriptionDao {
    public UserSubscriptions getUserSubscription(int userId) throws DatabaseException {
        String sql = """
            SELECT us.*, pp.package_name, pp.features 
            FROM UserSubscriptions us
            JOIN PremiumPackages pp ON us.package_id = pp.package_id
            WHERE us.user_id = ?
        """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new UserSubscriptions(
                        rs.getInt("user_id"),
                        rs.getInt("package_id"),
                        rs.getString("package_name"),
                        rs.getDate("start_date").toLocalDate(),
                        rs.getDate("end_date").toLocalDate(),
                        rs.getInt("duration"),
                        rs.getBoolean("auto_renew"),
                        rs.getInt("free_reads_remaining"),
                        rs.getString("features")
                );
            }
            return null;
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving user subscription", e);
        }
    }

    public void updateAutoRenew(int userId, int packageId, boolean autoRenew) throws DatabaseException {
        String sql = "UPDATE UserSubscriptions SET auto_renew = ? WHERE user_id = ? AND package_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBoolean(1, autoRenew);
            stmt.setInt(2, userId);
            stmt.setInt(3, packageId);

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Error updating auto-renew status", e);
        }
    }

    public void createSubscription(UserSubscriptions subscription) throws DatabaseException {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO UserSubscriptions (user_id, package_id, start_date, duration, auto_renew, free_reads_remaining) " +
                             "VALUES (?, ?, ?, ?, ?, ?)"
             )) {

            stmt.setInt(1, subscription.getUserId());
            stmt.setInt(2, subscription.getPackageId());
            stmt.setDate(3, java.sql.Date.valueOf(subscription.getStartDate()));
            stmt.setInt(4, subscription.getDuration());
            stmt.setBoolean(5, subscription.isAutoRenew());
            stmt.setInt(6, subscription.getFreeReadsRemaining());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to create subscription", e);
        }
    }
}