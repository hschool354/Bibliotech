    package com.example.bibliotech.dao;
    
    import com.example.bibliotech.DTO.BriefBookDTO;
    import com.example.bibliotech.DTO.SaleBookDTO;
    import com.example.bibliotech.DTO.TopBookDTO;
    import com.example.bibliotech.config.DatabaseConfig;
    import com.example.bibliotech.model.Books;
    import javafx.scene.image.Image;
    import javafx.scene.image.ImageView;
    import javafx.scene.shape.Circle;

    import java.net.URL;
    import java.sql.*;
    import java.util.ArrayList;
    import java.util.List;
    import java.util.Optional;
    import java.util.logging.Level;
    import java.util.logging.Logger;


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

        public int getMaxBookId() throws SQLException {
            String query = "SELECT MAX(book_id) FROM Books";
            try (Connection conn = DatabaseConfig.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return 0;
            }
        }

        public int insertBook(Books book) throws SQLException {
            int newId = getMaxBookId() + 1;

            String insertBookSQL = "INSERT INTO Books (book_id, title, author, isbn, original_price, " +
                    "discounted_price, publication_year, language, page_count, " +
                    "average_rating, rating_count, description, cover_image_url, " +
                    "stock_quantity, deal_id, reading_difficulty, estimated_reading_time, " +
                    "content_rating, created_at, updated_at) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            try (Connection conn = DatabaseConfig.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(insertBookSQL)) {

                validateBookData(book);

                Timestamp currentTime = new Timestamp(System.currentTimeMillis());

                stmt.setInt(1, newId);
                stmt.setString(2, book.getTitle());
                stmt.setString(3, book.getAuthor());
                stmt.setString(4, book.getIsbn());
                stmt.setDouble(5, book.getOriginalPrice());
                stmt.setObject(6, book.getDiscountedPrice());
                stmt.setInt(7, book.getPublicationYear());
                stmt.setString(8, book.getLanguage());
                stmt.setInt(9, book.getPageCount());
                stmt.setDouble(10, book.getAverageRating());
                stmt.setInt(11, book.getRatingCount());
                stmt.setString(12, book.getDescription());
                stmt.setString(13, book.getCoverImageUrl());
                stmt.setInt(14, book.getStockQuantity());
                stmt.setObject(15, book.getDealId());
                stmt.setString(16, book.getReadingDifficulty());
                stmt.setInt(17, book.getEstimatedReadingTime());
                stmt.setString(18, book.getContentRating());
                // Set current timestamp for both created_at and updated_at
                stmt.setTimestamp(19, currentTime);
                stmt.setTimestamp(20, currentTime);

                int affectedRows = stmt.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Creating book failed, no rows affected.");
                }

                return newId;
            }
        }

        private void validateBookData(Books book) throws SQLException {
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
            if (!isValidLanguage(book.getLanguage())) {
                throw new SQLException("Invalid language value");
            }
            if (book.getReadingDifficulty() != null && !isValidReadingDifficulty(book.getReadingDifficulty())) {
                throw new SQLException("Invalid reading difficulty value");
            }
            if (book.getContentRating() != null && !isValidContentRating(book.getContentRating())) {
                throw new SQLException("Invalid content rating value");
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

        public void updateBook(Books book) throws SQLException {
            String updateSQL = "UPDATE Books SET title = ?, author = ?, isbn = ?, " +
                    "original_price = ?, discounted_price = ?, publication_year = ?, " +
                    "language = ?, page_count = ?, average_rating = ?, rating_count = ?, " +
                    "description = ?, cover_image_url = ?, stock_quantity = ?, " +
                    "deal_id = ?, reading_difficulty = ?, estimated_reading_time = ?, " +
                    "content_rating = ?, updated_at = ? WHERE book_id = ?";

            try (Connection conn = DatabaseConfig.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(updateSQL)) {

                // Validate dữ liệu trước khi update
                validateBookData(book);

                // Set các giá trị cho PreparedStatement
                stmt.setString(1, book.getTitle());
                stmt.setString(2, book.getAuthor());
                stmt.setString(3, book.getIsbn());
                stmt.setDouble(4, book.getOriginalPrice());
                stmt.setObject(5, book.getDiscountedPrice());
                stmt.setInt(6, book.getPublicationYear());
                stmt.setString(7, book.getLanguage());
                stmt.setInt(8, book.getPageCount());
                stmt.setDouble(9, book.getAverageRating());
                stmt.setInt(10, book.getRatingCount());
                stmt.setString(11, book.getDescription());
                stmt.setString(12, book.getCoverImageUrl());
                stmt.setInt(13, book.getStockQuantity());
                stmt.setObject(14, book.getDealId());
                stmt.setString(15, book.getReadingDifficulty());
                stmt.setInt(16, book.getEstimatedReadingTime());
                stmt.setString(17, book.getContentRating());
                // Set current timestamp for updated_at
                stmt.setTimestamp(18, new Timestamp(System.currentTimeMillis()));
                stmt.setInt(19, book.getBookId());

                int affectedRows = stmt.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Updating book failed, no rows affected.");
                }
            }
        }

        public Books getBookById(int bookId) {
            String query = "SELECT book_id, title, author, isbn, original_price, " +
                    "discounted_price, publication_year, language, page_count, " +
                    "average_rating, rating_count, description, cover_image_url, " +
                    "stock_quantity, deal_id, reading_difficulty, estimated_reading_time, " +
                    "content_rating, created_at, updated_at " +
                    "FROM Books WHERE book_id = ?";

            try (Connection conn = DatabaseConfig.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setInt(1, bookId);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        Books book = new Books(
                                rs.getInt("book_id"),
                                rs.getString("title"),
                                rs.getString("author"),
                                rs.getString("isbn"),
                                rs.getDouble("original_price"),
                                rs.getObject("discounted_price") != null ? rs.getDouble("discounted_price") : null,
                                rs.getInt("publication_year"),
                                rs.getString("language"),
                                rs.getInt("page_count"),
                                rs.getDouble("average_rating"),
                                rs.getInt("rating_count"),
                                rs.getString("description"),
                                rs.getString("cover_image_url"),
                                rs.getInt("stock_quantity"),
                                rs.getObject("deal_id") != null ? rs.getInt("deal_id") : null,
                                rs.getString("reading_difficulty"),
                                rs.getInt("estimated_reading_time"),
                                rs.getString("content_rating")
                        );
                        // Set timestamps
                        book.setCreatedAt(rs.getTimestamp("created_at"));
                        book.setUpdatedAt(rs.getTimestamp("updated_at"));
                        return book;
                    }
                }
            } catch (SQLException e) {
                System.err.println("Error fetching book details: " + e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        public List<TopBookDTO> getTopRatedBooks(int limit) throws SQLException {
            List<TopBookDTO> popularBooks = new ArrayList<>();
            String query = """
                SELECT 
                    book_id,
                    title,
                    cover_image_url,
                    COALESCE(discounted_price, original_price) as display_price
                FROM Books 
                WHERE stock_quantity > 0
                ORDER BY average_rating DESC, rating_count DESC
                LIMIT ?
                """;

            try (Connection conn = DatabaseConfig.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(query)) {

                pstmt.setInt(1, limit);

                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        TopBookDTO book = new TopBookDTO(
                                rs.getInt("book_id"),
                                rs.getString("title"),
                                rs.getString("cover_image_url"),
                                rs.getDouble("display_price")
                        );
                        popularBooks.add(book);
                    }
                }
            }
            return popularBooks;
        }

        public List<BriefBookDTO> getBriefBooks(int limit) throws SQLException {
            List<BriefBookDTO> briefBooks = new ArrayList<>();
            String query = """
            SELECT 
                book_id,
                cover_image_url,
                title,
                author,
                page_count,
                average_rating,
                rating_count,
                description
            FROM Books 
            WHERE stock_quantity > 0
            ORDER BY average_rating DESC, rating_count DESC
            LIMIT ?
            """;

            try (Connection conn = DatabaseConfig.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(query)) {

                pstmt.setInt(1, limit);

                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        BriefBookDTO book = new BriefBookDTO(
                                rs.getInt("book_id"),
                                rs.getString("cover_image_url"),
                                rs.getString("title"),
                                rs.getString("author"),
                                rs.getInt("page_count"),
                                rs.getDouble("average_rating"),
                                rs.getInt("rating_count"),
                                rs.getString("description")
                        );
                        briefBooks.add(book);
                    }
                }
            }
            return briefBooks;
        }

        public Optional<BriefBookDTO> findBriefBookById(int bookId) throws SQLException {
            String query = """
            SELECT 
                book_id,
                cover_image_url,
                title,
                author,
                page_count,
                average_rating,
                rating_count,
                description
            FROM Books 
            WHERE book_id = ?
            """;

            try (Connection conn = DatabaseConfig.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(query)) {

                pstmt.setInt(1, bookId);

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return Optional.of(new BriefBookDTO(
                                rs.getInt("book_id"),
                                rs.getString("cover_image_url"),
                                rs.getString("title"),
                                rs.getString("author"),
                                rs.getInt("page_count"),
                                rs.getDouble("average_rating"),
                                rs.getInt("rating_count"),
                                rs.getString("description")
                        ));
                    }
                }
            }
            return Optional.empty();
        }

        public List<SaleBookDTO> getRandomSaleBooks(int limit) throws SQLException {
            List<SaleBookDTO> saleBooks = new ArrayList<>();
            String query = """
        SELECT 
            book_id,
            cover_image_url,
            title,
            original_price,
            discounted_price
        FROM Books 
        WHERE discounted_price IS NOT NULL 
        AND discounted_price < original_price 
        AND stock_quantity > 0
        ORDER BY RAND()
        LIMIT ?
        """;

            try (Connection conn = DatabaseConfig.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(query)) {

                pstmt.setInt(1, limit);

                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        SaleBookDTO book = new SaleBookDTO(
                                rs.getInt("book_id"),
                                rs.getString("cover_image_url"),
                                rs.getString("title"),
                                rs.getDouble("original_price"),
                                rs.getDouble("discounted_price")
                        );
                        saleBooks.add(book);
                    }
                }
            }
            return saleBooks;
        }

    }