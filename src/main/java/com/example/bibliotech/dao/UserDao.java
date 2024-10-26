package com.example.bibliotech.dao;

import com.example.bibliotech.config.DatabaseConfig;
import com.example.bibliotech.constants.DatabaseConstants;
import com.example.bibliotech.exception.DatabaseException;
import com.example.bibliotech.model.Users;
import java.sql.*;
import com.example.bibliotech.utils.PasswordUtil;

import java.sql.*;

public class UserDao {
    public Users checkLogin(String username, String password) throws DatabaseException {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DatabaseConstants.LOGIN_QUERY)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String storedPassword = rs.getString(DatabaseConstants.COLUMN_PASSWORD);

                    // Verify the password
                    if (PasswordUtil.checkPassword(password, storedPassword)) {
                        String adminValue = rs.getString(DatabaseConstants.COLUMN_IS_ADMIN);
                        Users user = new Users(
                                rs.getInt(DatabaseConstants.COLUMN_ID),
                                rs.getString(DatabaseConstants.COLUMN_USERNAME),
                                storedPassword,
                                adminValue
                        );

                        // If the password was in plain text, update it to BCrypt
                        if (!PasswordUtil.isBCryptHashed(storedPassword)) {
                            updatePasswordToHash(conn, user.getUserId(), password);
                        }

                        return user;
                    }
                }
                return null;
            }
        } catch (SQLException e) {
            throw new DatabaseException(DatabaseConstants.CONNECTION_ERROR + ": " + e.getMessage(), e);
        }
    }

    private void updatePasswordToHash(Connection conn, int userId, String plainTextPassword) throws SQLException {
        String sql = "UPDATE Users SET password = ? WHERE user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, PasswordUtil.hashPassword(plainTextPassword));
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        }
    }

    public void createUser(Users user) throws DatabaseException {
        String sql = "INSERT INTO Users (username, email, password, registration_status) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, PasswordUtil.hashPassword(user.getPassword())); // Mã hóa mật khẩu
            stmt.setString(4, user.getRegistrationStatus()); // Đặt trạng thái đăng ký là PENDING

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Error creating user: " + e.getMessage(), e);
        }
    }
    public boolean checkEmailExists(String email) throws DatabaseException {
        String sql = "SELECT COUNT(*) FROM Users WHERE email = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
                return false;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error checking email existence: " + e.getMessage(), e);
        }
    }

    public boolean checkUsernameExists(String username) throws DatabaseException {
        String sql = "SELECT COUNT(*) FROM Users WHERE username = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
                return false;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error checking username existence: " + e.getMessage(), e);
        }
    }


}