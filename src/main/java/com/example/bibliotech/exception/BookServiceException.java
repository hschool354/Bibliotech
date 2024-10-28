package com.example.bibliotech.exception;

public class BookServiceException extends Exception {
    public BookServiceException(String message) {
        super(message);
    }

    public BookServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}