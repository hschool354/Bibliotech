package com.example.bibliotech.utils;

import com.example.bibliotech.model.Books;

public class DataManager {
    private static volatile DataManager instance;
    private Books selectedBook;

    private DataManager() {
        // Private constructor for Singleton
    }

    public static DataManager getInstance() {
        if (instance == null) {
            synchronized (DataManager.class) {
                if (instance == null) {
                    instance = new DataManager();
                }
            }
        }
        return instance;
    }

    public void setSelectedBook(Books book) {
        this.selectedBook = book;
    }

    public Books getSelectedBook() {
        return selectedBook;
    }

    public void clearSelectedBook() {
        this.selectedBook = null;
    }

    public int getSelectedBookId() {
        return selectedBook != null ? selectedBook.getBookId() : -1;
    }

    public boolean hasValidSelectedBook() {
        return selectedBook != null;
    }
}