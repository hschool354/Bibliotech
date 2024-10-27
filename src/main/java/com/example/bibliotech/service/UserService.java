package com.example.bibliotech.service;

import com.example.bibliotech.constants.UserConstants;
import com.example.bibliotech.dao.UserDao;
import com.example.bibliotech.exception.DatabaseException;
import com.example.bibliotech.exception.LoginException;
import com.example.bibliotech.model.Users;
import com.example.bibliotech.constants.DatabaseConstants;
import com.example.bibliotech.utils.SessionManager;

import java.sql.Date;

public class UserService {
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
}

