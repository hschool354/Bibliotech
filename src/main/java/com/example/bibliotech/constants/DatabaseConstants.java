package com.example.bibliotech.constants;

public class DatabaseConstants {
    // SQL Queries
    public static final String LOGIN_QUERY =
            "SELECT * FROM Users WHERE username = ?";
    public static final String GET_USER_BY_USERNAME =
            "SELECT * FROM Users WHERE username = ?";

    // Error Messages
    public static final String CONNECTION_ERROR = "Error connecting to database";
    public static final String INVALID_CREDENTIALS = "Invalid username or password";
    public static final String USER_NOT_FOUND = "User not found";

    // Success Messages
    public static final String LOGIN_SUCCESS = "Login successful";

    // Column Names
    public static final String COLUMN_ID = "user_id";  // Thay 'id' thành 'user_id'
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_IS_ADMIN = "is_admin";
}