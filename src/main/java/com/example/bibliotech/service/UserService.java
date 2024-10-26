package com.example.bibliotech.service;

import com.example.bibliotech.dao.UserDao;
import com.example.bibliotech.exception.DatabaseException;
import com.example.bibliotech.exception.LoginException;
import com.example.bibliotech.model.Users;
import com.example.bibliotech.constants.DatabaseConstants;

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

}

