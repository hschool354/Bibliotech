// CategoryDao.java
package com.example.bibliotech.dao;

import com.example.bibliotech.config.DatabaseConfig;
import com.example.bibliotech.exception.DatabaseException;
import com.example.bibliotech.model.Category;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoryDao {
    public List<Category> getAllCategories() throws DatabaseException {
        String sql = "SELECT * FROM Categories";
        List<Category> categories = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("category_id");
                String name = rs.getString("category_name");
                categories.add(new Category(id, name));
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving categories", e);
        }

        return categories;
    }
}
