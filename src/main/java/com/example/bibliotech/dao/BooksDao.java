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

        public int insertBook(Books book) throws SQLException {
            String insertBookSQL = "INSERT INTO Books (title, author, isbn, original_price, " +
                    "discounted_price, publication_year, language, page_count, " +
                    "average_rating, rating_count, description, cover_image_url, " +
                    "stock_quantity, deal_id, reading_difficulty, estimated_reading_time, " +
                    "content_rating) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            try (Connection conn = DatabaseConfig.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(insertBookSQL, Statement.RETURN_GENERATED_KEYS)) {

                // Basic validations based on the new schema constraints
                if (book.getOriginalPrice() < 0) {
                    throw new SQLException("Original price cannot be negative");
                }
                if (book.getDiscountedPrice() != null && book.getDiscountedPrice() < 0) {
                    throw new SQLException("Discounted price cannot be negative");
                }
                if (book.getPublicationYear() < 1600) {
                    throw new SQLException("Publication year must be 1600 or later");
                }
                if (book.getPageCount() <= 0) {
                    throw new SQLException("Page count must be greater than 0");
                }
                if (book.getRatingCount() < 0) {
                    throw new SQLException("Rating count cannot be negative");
                }

                stmt.setString(1, book.getTitle());
                stmt.setString(2, book.getAuthor());
                stmt.setString(3, book.getIsbn());
                stmt.setDouble(4, book.getOriginalPrice());
                stmt.setObject(5, book.getDiscountedPrice());
                stmt.setInt(6, book.getPublicationYear());
                // Validate language enum
                String language = book.getLanguage();
                if (!isValidLanguage(language)) {
                    throw new SQLException("Invalid language value");
                }
                stmt.setString(7, language);
                stmt.setInt(8, book.getPageCount());
                stmt.setDouble(9, book.getAverageRating());
                stmt.setInt(10, book.getRatingCount());
                stmt.setString(11, book.getDescription());
                stmt.setString(12, book.getCoverImageUrl());
                stmt.setInt(13, book.getStockQuantity());
                stmt.setObject(14, book.getDealId());
                // Validate reading difficulty enum
                String readingDifficulty = book.getReadingDifficulty();
                if (readingDifficulty != null && !isValidReadingDifficulty(readingDifficulty)) {
                    throw new SQLException("Invalid reading difficulty value");
                }
                stmt.setString(15, readingDifficulty);
                stmt.setInt(16, book.getEstimatedReadingTime());
                // Validate content rating enum
                String contentRating = book.getContentRating();
                if (contentRating != null && !isValidContentRating(contentRating)) {
                    throw new SQLException("Invalid content rating value");
                }
                stmt.setString(17, contentRating);

                int affectedRows = stmt.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Creating book failed, no rows affected.");
                }

                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    } else {
                        throw new SQLException("Creating book failed, no ID obtained.");
                    }
                }
            }
        }

        private boolean isValidLanguage(String language) {
            String[] validLanguages = {"Vietnamese", "English", "French", "German", "Spanish", "Chinese", "Japanese", "Korean", "Other"};
            for (String valid : validLanguages) {
                if (valid.equals(language)) {
                    return true;
                }
            }
            return false;
        }

        private boolean isValidReadingDifficulty(String difficulty) {
            String[] validDifficulties = {"EASY", "MEDIUM", "HARD"};
            for (String valid : validDifficulties) {
                if (valid.equals(difficulty)) {
                    return true;
                }
            }
            return false;
        }

        private boolean isValidContentRating(String rating) {
            String[] validRatings = {"EVERYONE", "TEEN", "MATURE"};
            for (String valid : validRatings) {
                if (valid.equals(rating)) {
                    return true;
                }
            }
            return false;
        }

        public void insertBookCategories(int bookId, List<Integer> categoryIds) throws SQLException {
            String insertBookCategorySQL = "INSERT INTO BookCategories (book_id, category_id) VALUES (?, ?)";

            try (Connection conn = DatabaseConfig.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(insertBookCategorySQL)) {

                for (Integer categoryId : categoryIds) {
                    stmt.setInt(1, bookId);
                    stmt.setInt(2, categoryId);
                    stmt.executeUpdate();
                }
            }
        }
    }