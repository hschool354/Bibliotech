package com.example.bibliotech.dao;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {
    public Connection databaseLink;

    public Connection getConnection() {
        String databaseName ="Test";
        String databaseUser ="root";
        String datasePasword = "030504";
        String url = "jdbc:mysql://localhost:3306/" + databaseName;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            databaseLink = DriverManager.getConnection(url,databaseUser,datasePasword);

            System.out.println("Connected to database");
        }catch (Exception e) {
            System.out.println(e);

            e.printStackTrace();
        }

        return databaseLink;
    }
}