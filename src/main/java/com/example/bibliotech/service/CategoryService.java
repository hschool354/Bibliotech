package com.example.bibliotech.service;

import com.example.bibliotech.dao.CategoryDao;
import com.example.bibliotech.exception.BookServiceException;
import com.example.bibliotech.exception.DatabaseException;
import com.example.bibliotech.model.Category;

import java.util.List;

public class CategoryService {
    private final CategoryDao categoryDAO;

    public CategoryService() {
        this.categoryDAO = new CategoryDao();
    }

    public List<Category> getCategories() throws BookServiceException {
        try {
            return categoryDAO.getAllCategories();
        } catch (DatabaseException e) {
            throw new BookServiceException("Error getting categories: " + e.getMessage(), e);
        }
    }
}
