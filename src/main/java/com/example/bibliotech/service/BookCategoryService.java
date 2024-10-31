package com.example.bibliotech.service;


import com.example.bibliotech.DTO.BookCategoryDTO;
import com.example.bibliotech.dao.BookCategoryDao;
import com.example.bibliotech.exception.BookServiceException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class BookCategoryService {
    private final BookCategoryDao bookCategoryDAO;

    public BookCategoryService() {
        this.bookCategoryDAO = new BookCategoryDao();
    }

    public BookCategoryService(Connection connection) {
        this.bookCategoryDAO = new BookCategoryDao(connection);
    }

    /*public BookCategoryDTO getBookCategory(int bookId, int categoryId) {
        return bookCategoryDAO.getBookCategoryById(bookId, categoryId);
    }*/

    public List<BookCategoryDTO> getBookCategories(int bookId) throws BookServiceException {
        try {
            return bookCategoryDAO.getBookCategoriesByBookId(bookId);
        } catch (SQLException e) {
            throw new BookServiceException("Failed to retrieve book categories: " + e.getMessage(), e);
        }
    }

    public void updateBookCategories(int bookId, List<Integer> categoryIds) throws BookServiceException {
        if (categoryIds == null) {
            throw new IllegalArgumentException("Category IDs list cannot be null");
        }

        try {
            bookCategoryDAO.updateBookCategories(bookId, categoryIds);
        } catch (SQLException e) {
            throw new BookServiceException("Failed to update book categories: " + e.getMessage(), e);
        }
    }

    public void removeAllCategories(int bookId) throws BookServiceException {
        try {
            bookCategoryDAO.deleteBookCategories(bookId);
        } catch (SQLException e) {
            throw new BookServiceException("Failed to remove book categories: " + e.getMessage(), e);
        }
    }

    public void addCategory(int bookId, int categoryId) throws BookServiceException {
        try {
            bookCategoryDAO.addBookCategory(bookId, categoryId);
        } catch (SQLException e) {
            throw new BookServiceException("Failed to add book category: " + e.getMessage(), e);
        }
    }
}