package com.example.bibliotech.dao;

import com.example.bibliotech.config.DatabaseConfig;
import com.example.bibliotech.constants.DatabaseConstants;
import com.example.bibliotech.exception.DatabaseException;
import com.example.bibliotech.model.Users;

import java.sql.*;

public class UserDao {
    public Users checkLogin(String username, String password) throws DatabaseException {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DatabaseConstants.LOGIN_QUERY)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String adminValue = rs.getString(DatabaseConstants.COLUMN_IS_ADMIN);
                    System.out.println("Admin value from database: " + adminValue); // Debug line

                    Users user = new Users(
                            rs.getInt(DatabaseConstants.COLUMN_ID),
                            rs.getString(DatabaseConstants.COLUMN_USERNAME),
                            rs.getString(DatabaseConstants.COLUMN_PASSWORD),
                            adminValue
                    );
                    System.out.println("Is admin after conversion: " + user.isAdmin()); // Debug line
                    return user;
                }
                return null;
            }
        } catch (SQLException e) {
            throw new DatabaseException(DatabaseConstants.CONNECTION_ERROR + ": " + e.getMessage(), e);
        }
    }

    public Users getUserByUsername(String username) throws DatabaseException {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DatabaseConstants.GET_USER_BY_USERNAME)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Users(
                            rs.getInt(DatabaseConstants.COLUMN_ID),
                            rs.getString(DatabaseConstants.COLUMN_USERNAME),
                            rs.getString(DatabaseConstants.COLUMN_PASSWORD),
                            rs.getString(DatabaseConstants.COLUMN_IS_ADMIN)
                    );
                }
                return null;
            }
        } catch (SQLException e) {
            throw new DatabaseException(DatabaseConstants.CONNECTION_ERROR + ": " + e.getMessage(), e);

        }
    }
}
