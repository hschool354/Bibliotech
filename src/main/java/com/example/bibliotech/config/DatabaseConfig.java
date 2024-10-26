package com.example.bibliotech.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfig {
    private static final String URL = "jdbc:mysql://localhost:3306/ThuVien";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "030504";


    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Attempting to connect to database...");
            Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("Connection successful!");
            return conn;
        } catch (ClassNotFoundException e) {
            System.err.println("Database Driver not found.");
            throw new SQLException("Database Driver not found", e);
        } catch (SQLException e) {
            System.err.println("SQL Exception: " + e.getMessage());
            throw new SQLException("Error connecting to database", e);
        }
    }

    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }
}