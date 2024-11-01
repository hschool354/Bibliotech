package com.example.bibliotech.DTO;

public class DetailedBookDTO {
    private int bookId;
    private String coverImageUrl;
    private String title;
    private String author;
    private int pageCount;
    private double averageRating;
    private int ratingCount;
    private String description;
    private String language;
    private int publicationYear;
    private String readingDifficulty;
    private Double originalPrice;
    private Double discountedPrice;

    // Constructor
    public DetailedBookDTO(int bookId, String coverImageUrl, String title, String author,
                           int pageCount, double averageRating, int ratingCount, String description,
                           String language, int publicationYear, String readingDifficulty,
                           Double originalPrice, Double discountedPrice) {
        this.bookId = bookId;
        this.coverImageUrl = coverImageUrl;
        this.title = title;
        this.author = author;
        this.pageCount = pageCount;
        this.averageRating = averageRating;
        this.ratingCount = ratingCount;
        this.description = description;
        this.language = language;
        this.publicationYear = publicationYear;
        this.readingDifficulty = readingDifficulty;
        this.originalPrice = originalPrice;
        this.discountedPrice = discountedPrice;
    }

    // Getters
    public int getBookId() { return bookId; }
    public String getCoverImageUrl() { return coverImageUrl; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public int getPageCount() { return pageCount; }
    public double getAverageRating() { return averageRating; }
    public int getRatingCount() { return ratingCount; }
    public String getDescription() { return description; }
    public String getLanguage() { return language; }
    public int getPublicationYear() { return publicationYear; }
    public String getReadingDifficulty() { return readingDifficulty; }
    public Double getOriginalPrice() { return originalPrice; }
    public Double getDiscountedPrice() { return discountedPrice; }
}