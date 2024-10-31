package com.example.bibliotech.DTO;

public class BookCategoryDTO {
    private int bookId;
    private int categoryId;
    private String categoryName;

    public BookCategoryDTO(int bookId, int categoryId, String categoryName) {
        this.bookId = bookId;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }

    // Getters and setters
    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}