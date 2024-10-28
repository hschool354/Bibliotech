package com.example.bibliotech.service;

import com.example.bibliotech.config.DatabaseConfig;
import com.example.bibliotech.dao.BooksDao;
import com.example.bibliotech.model.Books;
import com.example.bibliotech.exception.BookServiceException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class BookService {
    private static final String BOOK_COVERS_DIR = "src/main/resources/book-covers/";
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final String[] ALLOWED_EXTENSIONS = {".jpg", ".jpeg", ".png"};

    private final BooksDao bookDAO;

    public BookService() {
        this.bookDAO = new BooksDao();
    }

    public boolean addBook(Books book, List<Integer> categoryIds, File coverImage) throws BookServiceException {
        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);

            // Xử lý ảnh bìa sách nếu có
            if (coverImage != null) {
                // Validate file
                validateImageFile(coverImage);

                // Ensure directory exists
                createBookCoversDirectoryIfNotExists();

                // Generate filename keeping original name but replacing spaces with underscores
                String originalFileName = coverImage.getName();
                String cleanFileName = originalFileName.replaceAll("\\s+", "_");

                // Save the file
                saveBookCover(coverImage, cleanFileName);

                // Update book object with the image URL
                book.setCoverImageUrl(cleanFileName);
            }

            // Insert book và lấy ID
            int bookId = bookDAO.insertBook(book);

            // Insert book categories nếu có
            if (categoryIds != null && !categoryIds.isEmpty()) {
                bookDAO.insertBookCategories(bookId, categoryIds);
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new BookServiceException("Error adding book: " + e.getMessage(), e);
        } catch (IOException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new BookServiceException("Error processing cover image: " + e.getMessage(), e);
        } finally {
            closeConnection(conn);
        }
    }

    public List<Books> getAllBooks() {
        return bookDAO.getAllBooksForDisplay();
    }

    private void validateImageFile(File file) throws BookServiceException {
        if (file == null) {
            throw new BookServiceException("No file provided");
        }

        // Check file size
        if (file.length() > MAX_FILE_SIZE) {
            throw new BookServiceException("File size exceeds maximum limit of 5MB");
        }

        // Check file extension
        String extension = getFileExtension(file.getName()).toLowerCase();
        boolean validExtension = false;
        for (String allowedExt : ALLOWED_EXTENSIONS) {
            if (allowedExt.equals(extension)) {
                validExtension = true;
                break;
            }
        }
        if (!validExtension) {
            throw new BookServiceException("Invalid file type. Allowed types: JPG, JPEG, PNG");
        }
    }

    private void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.setAutoCommit(true);
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void createBookCoversDirectoryIfNotExists() throws BookServiceException {
        Path directory = Paths.get(BOOK_COVERS_DIR);
        try {
            if (!Files.exists(directory)) {
                Files.createDirectories(directory);
            }
        } catch (IOException e) {
            throw new BookServiceException("Error creating book covers directory", e);
        }
    }

    private void saveBookCover(File sourceFile, String targetFileName) throws IOException {
        Path targetPath = Paths.get(BOOK_COVERS_DIR, targetFileName);
        Files.copy(sourceFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
    }

    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf(".");
        return lastDotIndex > 0 ? fileName.substring(lastDotIndex) : "";
    }

    public void updateBook(Books book, File newCoverImage) throws BookServiceException {
        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);

            // Handle new cover image if provided
            if (newCoverImage != null) {
                validateImageFile(newCoverImage);
                createBookCoversDirectoryIfNotExists();

                // Generate filename
                String cleanFileName = newCoverImage.getName().replaceAll("\\s+", "_");
                saveBookCover(newCoverImage, cleanFileName);
                book.setCoverImageUrl(cleanFileName);
            }

            // Update book in database
            bookDAO.updateBook(book);

            conn.commit();
        } catch (SQLException | IOException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new BookServiceException("Error updating book: " + e.getMessage(), e);
        } finally {
            closeConnection(conn);
        }
    }

    public Books getBookById(int bookId) throws BookServiceException {
        try {
            Books book = bookDAO.getBookById(bookId);
            if (book == null) {
                throw new BookServiceException("Book not found with ID: " + bookId);
            }
            return book;
        } catch (Exception e) {
            throw new BookServiceException("Error fetching book details: " + e.getMessage(), e);
        }
    }

}