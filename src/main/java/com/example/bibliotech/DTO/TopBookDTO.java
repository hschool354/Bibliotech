package com.example.bibliotech.DTO;

public class TopBookDTO {
    private int bookId;
    private String title;
    private String coverImageUrl;
    private double displayPrice;

    public TopBookDTO(int bookId, String title, String coverImageUrl, double displayPrice) {
        this.bookId = bookId;
        this.title = title;
        this.coverImageUrl = coverImageUrl;
        this.displayPrice = displayPrice;
    }

    // Getters
    public int getBookId() { return bookId; }
    public String getTitle() { return title; }
    public String getCoverImageUrl() { return coverImageUrl; }
    public double getDisplayPrice() { return displayPrice; }
}