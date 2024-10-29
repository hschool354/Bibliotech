package com.example.bibliotech.DTO;

public class BriefBookDTO {
    private int bookId;
    private String coverImageUrl;
    private String title;
    private String author;
    private int pageCount;
    private double averageRating;
    private int ratingCount;
    private String description;

    // Constructor
    public BriefBookDTO(int bookId, String coverImageUrl, String title, String author,
                        int pageCount, double averageRating, int ratingCount, String description) {
        this.bookId = bookId;
        this.coverImageUrl = coverImageUrl;
        this.title = title;
        this.author = author;
        this.pageCount = pageCount;
        this.averageRating = averageRating;
        this.ratingCount = ratingCount;
        this.description = description;
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
}