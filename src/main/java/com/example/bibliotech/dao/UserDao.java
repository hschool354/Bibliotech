package com.example.bibliotech.dao;

import com.example.bibliotech.config.DatabaseConfig;
import com.example.bibliotech.constants.DatabaseConstants;
import com.example.bibliotech.constants.UserConstants;
import com.example.bibliotech.exception.DatabaseException;
import com.example.bibliotech.model.Users;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.example.bibliotech.utils.PasswordUtil;

public class UserDao {
    public Users checkLogin(String username, String password) throws DatabaseException {
        String sql = "SELECT user_id, username, password, is_admin, registration_status FROM Users WHERE username = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String storedPassword = rs.getString("password");

                    // Verify the password
                    if (PasswordUtil.checkPassword(password, storedPassword)) {
                        Users user = new Users(
                                rs.getInt("user_id"),
                                rs.getString("username"),
                                storedPassword,
                                rs.getString("is_admin")
                        );

                        // Set registration status
                        user.setRegistrationStatus(rs.getString("registration_status"));

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
            stmt.setString(3, PasswordUtil.hashPassword(user.getPassword()));
            stmt.setString(4, UserConstants.REGISTRATION_STATUS_PENDING);

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

    public void updateUserProfile(int userId, String fullName, String phone, Date dob,
                                  String gender, String address, String nationality,
                                  String bio, String registrationStatus) throws DatabaseException {


        if (registrationStatus != null && !registrationStatus.matches("PENDING|COMPLETED")) {
            throw new IllegalArgumentException("Invalid registration status. Must be either PENDING or COMPLETED.");
        }

        if (gender != null && !gender.matches("Male|Female|Other")) {
            throw new IllegalArgumentException("Invalid gender value. Allowed values: Male, Female, Other");
        }

        StringBuilder sql = new StringBuilder("UPDATE Users SET ");
        List<Object> parameters = new ArrayList<>();

        // Cập nhật các trường động
        if (fullName != null) {
            sql.append("full_name = ?, ");
            parameters.add(fullName);
        }
        if (phone != null) {
            sql.append("phone = ?, ");
            parameters.add(phone);
        }
        if (dob != null) {
            sql.append("dob = ?, ");
            parameters.add(new java.sql.Date(dob.getTime()));
        }
        if (gender != null) {
            sql.append("gender = ?, ");
            parameters.add(gender);
        }
        if (address != null) {
            sql.append("address = ?, ");
            parameters.add(address);
        }
        if (nationality != null) {
            sql.append("nationality = ?, ");
            parameters.add(nationality);
        }
        if (bio != null) {
            sql.append("bio = ?, ");
            parameters.add(bio);
        }

        // Cập nhật trạng thái đăng ký
        sql.append("registration_status = ? ");
        parameters.add(registrationStatus);

        sql.append("WHERE user_id = ?");
        parameters.add(userId);

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < parameters.size(); i++) {
                Object param = parameters.get(i);
                if (param instanceof String) {
                    stmt.setString(i + 1, (String) param);
                } else if (param instanceof Integer) {
                    stmt.setInt(i + 1, (Integer) param);
                } else if (param instanceof java.sql.Date) {
                    stmt.setDate(i + 1, (java.sql.Date) param);
                }
            }
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Error updating user profile: " + e.getMessage(), e);
        }
    }

    public void updateRegistrationStatus(int userId, String status) throws DatabaseException {
        String sql = "UPDATE Users SET registration_status = ? WHERE user_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setInt(2, userId);

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Error updating registration status: " + e.getMessage(), e);
        }
    }
    public List<Users> getAllUsers() throws DatabaseException {
        String sql = "SELECT user_id, username, email, is_admin, registration_status FROM Users";
        List<Users> users = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Users user = new Users();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                // Sửa lại cách set giá trị isAdmin
                user.setAdmin(rs.getBoolean("is_admin")); // Hoặc
                // user.setAdmin("1".equals(rs.getString("is_admin")) || "true".equalsIgnoreCase(rs.getString("is_admin")));
                user.setRegistrationStatus(rs.getString("registration_status"));
                users.add(user);
            }
            return users;
        } catch (SQLException e) {
            throw new DatabaseException("Error getting all users: " + e.getMessage(), e);
        }
    }


}