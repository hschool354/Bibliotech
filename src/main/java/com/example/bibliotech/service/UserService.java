package com.example.bibliotech.service;

import com.example.bibliotech.config.DatabaseConfig;
import com.example.bibliotech.constants.UserConstants;
import com.example.bibliotech.dao.UserDao;
import com.example.bibliotech.exception.BookServiceException;
import com.example.bibliotech.exception.DatabaseException;
import com.example.bibliotech.exception.LoginException;
import com.example.bibliotech.exception.UserServiceException;
import com.example.bibliotech.model.Books;
import com.example.bibliotech.model.Users;
import com.example.bibliotech.constants.DatabaseConstants;
import com.example.bibliotech.utils.PasswordUtil;
import com.example.bibliotech.utils.SessionManager;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public class UserService {
    private static final String USER_AVATARS_DIR = "src/main/resources/Profile_Picture/";
    private static final long MAX_FILE_SIZE = 2 * 1024 * 1024; // 2MB
    private static final String[] ALLOWED_EXTENSIONS = {".jpg", ".jpeg", ".png"};

    private final UserDao userDAO;

    public UserService() {
        this.userDAO = new UserDao();
    }

    public Users login(String username, String password) throws LoginException {
        try {
            if (username == null || username.trim().isEmpty()
                    || password == null || password.trim().isEmpty()) {
                throw new LoginException(DatabaseConstants.INVALID_CREDENTIALS);
            }

            Users user = userDAO.checkLogin(username, password);
            if (user == null) {
                throw new LoginException(DatabaseConstants.INVALID_CREDENTIALS);
            }

            System.out.println("Login successful for user: " + user.getUsername());

            SessionManager.getInstance().setCurrentUser(user);

            return user;
        } catch (DatabaseException e) {
            System.err.println("Database error occurred: " + e.getMessage());
            throw new LoginException(DatabaseConstants.CONNECTION_ERROR, e);
        }
    }


    public boolean isEmailExists(String email) throws DatabaseException {
        return userDAO.checkEmailExists(email);
    }

    public boolean isUsernameExists(String username) throws DatabaseException {
        return userDAO.checkUsernameExists(username);
    }

    public void createUser(Users user) throws DatabaseException {
        if (user == null) {
            throw new DatabaseException("User cannot be null");
        }

        // Validate user data
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new DatabaseException("Username cannot be empty");
        }
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new DatabaseException("Email cannot be empty");
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new DatabaseException("Password cannot be empty");
        }

        user.setRegistrationStatus("PENDING");
        userDAO.createUser(user);
    }

    public void updateUserProfile(int userId, String fullName, String phone, Date dob,
                                  String gender, String address, String nationality,
                                  String bio) throws DatabaseException {
        try {
            userDAO.updateUserProfile(
                    userId,
                    fullName,
                    phone,
                    dob,
                    gender,
                    address,
                    nationality,
                    bio,
                    UserConstants.REGISTRATION_STATUS_COMPLETED
            );
        } catch (DatabaseException e) {
            System.err.println("Error updating user profile: " + e.getMessage());
            throw e;
        }
    }

    public void skipProfileUpdate(int userId) throws DatabaseException {
        try {
            userDAO.updateRegistrationStatus(userId, "COMPLETED");
        } catch (DatabaseException e) {
            System.err.println("Error updating registration status: " + e.getMessage());
            throw new DatabaseException("Failed to update registration status: " + e.getMessage(), e);
        }
    }

    private void validateProfileData(String fullName, String phone, Date dob) throws DatabaseException {
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new DatabaseException("Full name cannot be empty");
        }

        if (phone == null || !phone.matches("^[0-9]{10,15}$")) {
            throw new DatabaseException("Phone number must be between 10 and 15 digits");
        }

        if (dob == null) {
            throw new DatabaseException("Date of birth cannot be empty");
        }

        // Add additional validation if needed
        // For example, check if date of birth is not in the future
        if (dob.after(new Date(System.currentTimeMillis()))) {
            throw new DatabaseException("Date of birth cannot be in the future");
        }
    }

    private void validateUser(Users user) throws DatabaseException {
        if (user == null) {
            throw new DatabaseException("User cannot be null");
        }

        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new DatabaseException("Username cannot be empty");
        }
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new DatabaseException("Email cannot be empty");
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new DatabaseException("Password cannot be empty");
        }
    }

    public List<Users> getAllUsers() throws DatabaseException {
        return userDAO.getAllUsers();
    }


    public void addAdmin(Users admin) throws DatabaseException {
        validateUser(admin);
        admin.setAdmin(true);
        admin.setRegistrationStatus("COMPLETED");
        userDAO.addAdmin(admin);
    }

    public Users loadSettingProfile(int userId) throws DatabaseException {
        try {
            return userDAO.getUserProfileSettingByID(userId);
        } catch (SQLException e) {
            throw new DatabaseException("Error loading user profile: " + e.getMessage(), e);
        }
    }

    private void validateImageFile(File file) throws UserServiceException {
        if (file == null) {
            throw new UserServiceException("No file provided");
        }

        // Check file size
        if (file.length() > MAX_FILE_SIZE) {
            throw new UserServiceException("File size exceeds maximum limit of 5MB");
        }

        // Check file extension
        String extension = getFileExtension(file.getName()).toLowerCase();
        boolean validExtension = false;
        for (String allowedExt : ALLOWED_EXTENSIONS) {
            if (allowedExt.equals(extension)) {
                validExtension = true;
                break;
            }
        }
        if (!validExtension) {
            throw new UserServiceException("Invalid file type. Allowed types: JPG, JPEG, PNG");
        }
    }

    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf(".");
        return lastDotIndex > 0 ? fileName.substring(lastDotIndex) : "";
    }

    private void createBookCoversDirectoryIfNotExists() throws UserServiceException {
        Path directory = Paths.get(USER_AVATARS_DIR);
        try {
            if (!Files.exists(directory)) {
                Files.createDirectories(directory);
            }
        } catch (IOException e) {
            throw new UserServiceException("Error creating book covers directory", e);
        }
    }

    private void saveUserPicture(File sourceFile, String targetFileName) throws IOException {
        Path targetPath = Paths.get(USER_AVATARS_DIR, targetFileName);
        Files.copy(sourceFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
    }

    public void updateProfileSetting(Users users , File newCoverImage) throws UserServiceException {
        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);

            if (newCoverImage != null) {
                validateImageFile(newCoverImage);
                createBookCoversDirectoryIfNotExists();

                // Generate filename
                String cleanFileName = newCoverImage.getName().replaceAll("\\s+", "_");
                saveUserPicture(newCoverImage, cleanFileName);
                users.setProfilePictureUrl(cleanFileName);
            }

            userDAO.updateProfileSetting(users);

            conn.commit();
        } catch (SQLException | IOException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new UserServiceException("Error updating users: " + e.getMessage(), e);
        } finally {
            closeConnection(conn);
        }
    }

    private void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.setAutoCommit(true);
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public BigDecimal getUserBalance(int userId) {
        return userDAO.getBalanceByUserId(userId);
    }

    public boolean updateUserBalance(int userId, BigDecimal newBalance) {
        return userDAO.updateBalance(userId, newBalance);
    }
}



