package com.example.bibliotech.dao;

import com.example.bibliotech.DTO.BookCategoryDTO;
import com.example.bibliotech.config.DatabaseConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BookCategoryDao {
    private Connection connection;

    public BookCategoryDao() {
    }

    public BookCategoryDao(Connection connection) {
        this.connection = connection;
    }

    public List<BookCategoryDTO> getBookCategoriesByBookId(int bookId) throws SQLException {
        List<BookCategoryDTO> categories = new ArrayList<>();
        // Sửa 'c.name' thành 'c.category_name' nếu trong bảng Categories
        // tên cột là 'category_name'
        String query = """
            SELECT bc.book_id, bc.category_id, c.category_name 
            FROM BookCategories bc 
            JOIN Categories c ON bc.category_id = c.category_id 
            WHERE bc.book_id = ?
            """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, bookId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    categories.add(new BookCategoryDTO(
                            rs.getInt("book_id"),
                            rs.getInt("category_id"),
                            rs.getString("category_name") // Sửa tên cột ở đây nữa
                    ));
                }
            }
        }
        return categories;
    }

    public void deleteBookCategories(int bookId) throws SQLException {
        String sql = "DELETE FROM BookCategories WHERE book_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, bookId);
            stmt.executeUpdate();
        }
    }

    public void addBookCategory(int bookId, int categoryId) throws SQLException {
        String sql = "INSERT INTO BookCategories (book_id, category_id) VALUES (?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, bookId);
            stmt.setInt(2, categoryId);
            stmt.executeUpdate();
        }
    }

    public void updateBookCategories(int bookId, List<Integer> categoryIds) throws SQLException {
        try (Connection conn = DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Delete existing categories
                deleteBookCategories(bookId);

                // Add new categories
                String sql = "INSERT INTO BookCategories (book_id, category_id) VALUES (?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    for (Integer categoryId : categoryIds) {
                        stmt.setInt(1, bookId);
                        stmt.setInt(2, categoryId);
                        stmt.executeUpdate();
                    }
                }

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }
}