package com.example.bibliotech.dao;

import com.example.bibliotech.config.DatabaseConfig;
import com.example.bibliotech.constants.DatabaseConstants;
import com.example.bibliotech.constants.UserConstants;
import com.example.bibliotech.exception.DatabaseException;
import com.example.bibliotech.model.Users;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.example.bibliotech.utils.PasswordUtil;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;

import static com.example.bibliotech.config.DatabaseConfig.closeConnection;

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

    public Users getUserProfileSettingByID(int user_id) throws SQLException {
        if (user_id <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }

        String query = "SELECT u.*, " +
                "COALESCE(u.full_name, '') as full_name, " +
                "COALESCE(u.phone, '') as phone, " +
                "COALESCE(u.address, '') as address, " +
                "COALESCE(u.nationality, '') as nationality, " +
                "COALESCE(u.bio, '') as bio, " +
                "COALESCE(u.profile_picture_url, '') as profile_picture_url " +
                "FROM Users u WHERE u.user_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, user_id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Users user = new Users();


                    // Set all fields from ResultSet with null checks
                    user.setUserId(rs.getInt("user_id"));
                    user.setUsername(rs.getString("username"));

                    System.out.println("Loading user data from database:");
                    System.out.println("Full name: " + rs.getString("full_name"));
                    System.out.println("Phone: " + rs.getString("phone"));
                    System.out.println("DOB: " + rs.getDate("dob"));

                    user.setFullName(rs.getString("full_name"));
                    user.setPhone(rs.getString("phone"));

                    // Handle date carefully
                    java.sql.Date dobSQL = rs.getDate("dob");
                    if (dobSQL != null) {
                        user.setDob(new Date(dobSQL.getTime()));
                    }

                    user.setGender(rs.getString("gender"));
                    user.setAddress(rs.getString("address"));
                    user.setNationality(rs.getString("nationality"));
                    user.setBio(rs.getString("bio"));
                    user.setProfilePictureUrl(rs.getString("profile_picture_url"));

                    return user;
                } else {
                    // No user found with given ID
                    return null;
                }
            }
        } catch (SQLException e) {
            Logger.getLogger(getClass().getName()).severe(
                    String.format("Error fetching user profile for ID %d: %s", user_id, e.getMessage())
            );
            throw new SQLException("Failed to fetch user profile: " + e.getMessage(), e);
        }
    }

    // Optional: Add a method to check if user exists
    private boolean doesUserExist(int userId, Connection conn) throws SQLException {
        String checkQuery = "SELECT 1 FROM Users WHERE user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(checkQuery)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
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

    public void addAdmin(Users admin) throws DatabaseException {
        String sql = "INSERT INTO Users (username, email, password, full_name, phone, dob, gender, address, nationality, bio, is_admin, registration_status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, admin.getUsername());
            stmt.setString(2, admin.getEmail());
            stmt.setString(3, PasswordUtil.hashPassword(admin.getPassword()));
            stmt.setString(4, admin.getFullName()); // Chắc chắn rằng full_name được thiết lập
            stmt.setString(5, admin.getPhone()); // Chắc chắn rằng phone được thiết lập
            stmt.setDate(6, admin.getDob() != null ? new java.sql.Date(admin.getDob().getTime()) : null); // Chắc chắn rằng dob được thiết lập
            stmt.setString(7, admin.getGender());
            stmt.setString(8, admin.getAddress());
            stmt.setString(9, admin.getNationality());
            stmt.setString(10, admin.getBio());
            stmt.setBoolean(11, true); // Đặt is_admin là true
            stmt.setString(12, UserConstants.REGISTRATION_STATUS_COMPLETED); // Trạng thái đăng ký là hoàn thành

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Error adding admin: " + e.getMessage(), e);
        }
    }

    public void loadProfilePicture(ImageView imageView, String profilePictureUrl) {
        try {
            if (profilePictureUrl != null && !profilePictureUrl.trim().isEmpty()) {
                // Xử lý URL tùy theo nguồn ảnh
                if (profilePictureUrl.startsWith("http")) {
                    // Load từ URL internet
                    Image image = new Image(profilePictureUrl,
                            true); // true để load bất đồng bộ
                    image.errorProperty().addListener((observable, oldValue, newValue) -> {
                        if (newValue) {
                            loadDefaultProfileImage(imageView);
                        }
                    });
                    imageView.setImage(image);
                } else {
                    // Load từ resources local
                    String resourcePath = "/Profile_Picture/" + profilePictureUrl;
                    URL imageUrl = getClass().getResource(resourcePath);

                    if (imageUrl != null) {
                        Image image = new Image(imageUrl.toExternalForm());
                        imageView.setImage(image);
                    } else {
                        loadDefaultProfileImage(imageView);
                    }
                }
            } else {
                loadDefaultProfileImage(imageView);
            }

        } catch (Exception e) {
            System.out.println("Error loading profile picture: "+ e.getMessage());
            loadDefaultProfileImage(imageView);
        }
    }

    private void loadDefaultProfileImage(ImageView imageView) {
        try {
            URL defaultImageUrl = getClass().getResource("/Profile_Picture/icons_customer.png");
            if (defaultImageUrl != null) {
                Image defaultImage = new Image(defaultImageUrl.toExternalForm());
                imageView.setImage(defaultImage);
            } else {
                System.out.println("Default profile image not found in resources");
            }
        } catch (Exception e) {
            System.out.println("Error loading default profile image"+ e.getMessage());
        }
    }

    public void loadUserName(Label label, int userId) throws DatabaseException {
        String sql = "SELECT username FROM Users WHERE user_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String username = rs.getString("username");
                    label.setText(username);
                } else {
                    label.setText("User not found"); // Nếu không tìm thấy user
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error loading username: " + e.getMessage(), e);
        }
    }

    public void updateProfileSetting(Users users) throws SQLException {
        // Validate input
        if (users == null || users.getUserId() <= 0) {
            throw new IllegalArgumentException("Invalid user data");
        }

        validateProfileData(users);

        String updateSQL = "UPDATE Users SET username = ?, full_name = ?, phone = ?, " +
                "dob = ?, gender = ?, nationality = ?, bio = ?, " +
                "profile_picture_url = ?, updated_at = CURRENT_TIMESTAMP " +
                "WHERE user_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(updateSQL)) {

            // Set parameters with null checks
            stmt.setString(1, users.getUsername());
            stmt.setString(2, users.getFullName());
            stmt.setString(3, users.getPhone());

            // Handle Date conversion safely
            if (users.getDob() != null) {
                stmt.setDate(4, new java.sql.Date(users.getDob().getTime()));
            } else {
                stmt.setNull(4, java.sql.Types.DATE);
            }

            stmt.setString(5, users.getGender());
            stmt.setString(6, users.getNationality());
            stmt.setString(7, users.getBio());
            stmt.setString(8, users.getProfilePictureUrl());
            stmt.setInt(9, users.getUserId());

            // Execute update
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("Update failed, no rows affected. User ID: " + users.getUserId());
            }

        } catch (SQLException e) {
            throw new SQLException("Failed to update user profile: " + e.getMessage(), e);
        }
    }

    // Helper method to validate profile data before update
    private void validateProfileData(Users users) throws IllegalArgumentException {
        if (users.getUsername() == null || users.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }

        if (users.getFullName() != null && users.getFullName().length() > 100) {
            throw new IllegalArgumentException("Full name exceeds maximum length");
        }

        if (users.getPhone() != null) {
            String phonePattern = "^\\+?[0-9]{10,15}$";
            if (!users.getPhone().matches(phonePattern)) {
                throw new IllegalArgumentException("Invalid phone number format");
            }
        }

        if (users.getDob() != null) {
            // Convert java.sql.Date directly to LocalDate
            LocalDate dobLocalDate = ((java.sql.Date) users.getDob()).toLocalDate();

            // Validate date is not in the future
            if (dobLocalDate.isAfter(LocalDate.now())) {
                throw new IllegalArgumentException("Date of birth cannot be in the future");
            }
        }
    }

}