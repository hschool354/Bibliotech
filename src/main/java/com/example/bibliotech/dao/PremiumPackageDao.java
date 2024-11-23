package com.example.bibliotech.dao;

import com.example.bibliotech.config.DatabaseConfig;
import com.example.bibliotech.exception.DatabaseException;
import com.example.bibliotech.model.PremiumPackages;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class PremiumPackageDao {
    // Lấy tất cả các gói premium
    public List<PremiumPackages> getAllPremiumPackages() throws DatabaseException {
        String sql = "SELECT * FROM PremiumPackages ORDER BY price";
        List<PremiumPackages> packages = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                packages.add(mapResultSetToPackage(rs));
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving premium packages", e);
        }

        return packages;
    }

    // Lấy gói premium theo ID
    public PremiumPackages getPackageById(int packageId) throws DatabaseException {
        String sql = "SELECT * FROM PremiumPackages WHERE package_id = ?";
        PremiumPackages premiumPackage = null;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, packageId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                premiumPackage = mapResultSetToPackage(rs);
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving premium package", e);
        }

        return premiumPackage;
    }

    /*// Thêm gói premium mới
    public void addPremiumPackage(PremiumPackages packages) throws DatabaseException {
        String sql = "INSERT INTO PremiumPackages (package_name, price, duration, billing_cycle, features) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, packages.getPackageName());
            stmt.setBigDecimal(2, packages.getPrice());
            stmt.setInt(3, packages.getDuration());
            stmt.setString(4, packages.getBillingCycle().name());
            stmt.setString(5, String.join("\n", packages.getFeatures()));

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new DatabaseException("Error adding premium package", e);
        }
    }

    // Cập nhật gói premium
    public void updatePremiumPackage(PremiumPackages packages) throws DatabaseException {
        String sql = "UPDATE PremiumPackages SET package_name = ?, price = ?, duration = ?, " +
                "billing_cycle = ?, features = ? WHERE package_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, package.getPackageName());
            stmt.setBigDecimal(2, package.getPrice());
            stmt.setInt(3, package.getDuration());
            stmt.setString(4, package.getBillingCycle().name());
            stmt.setString(5, String.join("\n", package.getFeatures()));
            stmt.setInt(6, package.getPackageId());

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated == 0) {
                throw new DatabaseException("No package found with ID: " + package.getPackageId());
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error updating premium package", e);
        }
    }

    // Xóa gói premium
    public void deletePremiumPackage(int packageId) throws DatabaseException {
        String sql = "DELETE FROM PremiumPackages WHERE package_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, packageId);

            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted == 0) {
                throw new DatabaseException("No package found with ID: " + packageId);
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error deleting premium package", e);
        }
    }


    */

    // Lấy gói premium theo billing cycle
    public List<PremiumPackages> getPackagesByBillingCycle(String billingCycle) throws DatabaseException {
        String sql = "SELECT * FROM PremiumPackages WHERE billing_cycle = ? ORDER BY price";
        List<PremiumPackages> packages = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, billingCycle);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                packages.add(mapResultSetToPackage(rs));
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving premium packages by billing cycle", e);
        }

        return packages;
    }
    // Helper method để map ResultSet thành đối tượng PremiumPackage
    private PremiumPackages mapResultSetToPackage(ResultSet rs) throws SQLException {
        return new PremiumPackages(
                rs.getInt("package_id"),
                rs.getString("package_name"),
                rs.getBigDecimal("price"),
                rs.getInt("duration"),
                PremiumPackages.BillingCycle.valueOf(rs.getString("billing_cycle")),
                rs.getString("features")
        );
    }
}
