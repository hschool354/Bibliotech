package com.example.bibliotech.utils;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {
    // Check if a string looks like a BCrypt hash
    public static boolean isBCryptHashed(String password) {
        return password != null && password.startsWith("$2a$") && password.length() == 60;
    }

    // Generate a salt and hash the password
    public static String hashPassword(String plainTextPassword) {
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt(12));
    }

    // Verify password with migration support
    public static boolean checkPassword(String plainTextPassword, String storedPassword) {
        // If the stored password is not in BCrypt format, compare directly
        if (!isBCryptHashed(storedPassword)) {
            return plainTextPassword.equals(storedPassword);
        }
        // If it is BCrypt hashed, use BCrypt to verify
        return BCrypt.checkpw(plainTextPassword, storedPassword);
    }
}