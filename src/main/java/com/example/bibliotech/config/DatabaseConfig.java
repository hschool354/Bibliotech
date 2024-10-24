package com.example.bibliotech.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfig {
    private Connection databaseLink;

    public Connection getConnection() {
        String databaseName = "Test";
        String databaseUser = "root";
        String databasePassword = "030504";
        String url = "jdbc:mysql://localhost:3306/" + databaseName + "?useSSL=false&serverTimezone=UTC";

        try {
            // Tải driver MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
            databaseLink = DriverManager.getConnection(url, databaseUser, databasePassword);
            System.out.println("Connected to database");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL Driver not found.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Connection failed! Check output console.");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("An unexpected error occurred.");
            e.printStackTrace();
        }

        return databaseLink;
    }

    // Phương thức để đóng kết nối
    public void closeConnection() {
        if (databaseLink != null) {
            try {
                databaseLink.close();
                System.out.println("Database connection closed.");
            } catch (SQLException e) {
                System.err.println("Failed to close the database connection.");
                e.printStackTrace();
            }
        }
    }
}
