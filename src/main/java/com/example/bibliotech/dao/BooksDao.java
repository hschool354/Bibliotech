package com.example.bibliotech.dao;

import com.example.bibliotech.config.DatabaseConfig;
import com.example.bibliotech.model.Books;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BooksDao {

    public List<Books> getAllBooksForDisplay() {
        List<Books> books = new ArrayList<>();
        // Điều chỉnh query theo đúng tên cột trong database
        String query = "SELECT book_id, cover_image_url, title, author, language, " +
                "original_price, discounted_price, stock_quantity " +
                "FROM Books";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Books book = new Books(
                        rs.getInt("book_id"),           // ID
                        rs.getString("title"),          // Title
                        rs.getString("author"),         // Author
                        "",                             // isbn (không cần cho hiển thị)
                        rs.getDouble("original_price"), // Original Price
                        rs.getDouble("discounted_price"), // Discounted Price
                        0,                              // publicationYear (không cần cho hiển thị)
                        rs.getString("language"),       // Language
                        0,                              // pageCount (không cần cho hiển thị)
                        0.0,                            // averageRating (không cần cho hiển thị)
                        0,                              // ratingCount (không cần cho hiển thị)
                        "",                             // description (không cần cho hiển thị)
                        rs.getString("cover_image_url"),// Cover Image URL
                        rs.getInt("stock_quantity"),    // Stock Quantity
                        null,                           // dealId (không cần cho hiển thị)
                        "",                             // readingDifficulty (không cần cho hiển thị)
                        0,                              // estimatedReadingTime (không cần cho hiển thị)
                        ""                              // contentRating (không cần cho hiển thị)
                );
                books.add(book);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching books: " + e.getMessage());
            e.printStackTrace();
        }
        return books;
    }
}