package com.example.bibliotech.DTO;

public class SaleBookDTO {
    private int bookId;
    private String coverImageUrl;
    private String title;
    private double originalPrice;
    private double discountedPrice;

    public SaleBookDTO(int bookId, String coverImageUrl, String title,
                       double originalPrice, double discountedPrice) {
        this.bookId = bookId;
        this.coverImageUrl = coverImageUrl;
        this.title = title;
        this.originalPrice = originalPrice;
        this.discountedPrice = discountedPrice;
    }

    // Getters
    public int getBookId() { return bookId; }
    public String getCoverImageUrl() { return coverImageUrl; }
    public String getTitle() { return title; }
    public double getOriginalPrice() { return originalPrice; }
    public double getDiscountedPrice() { return discountedPrice; }
}