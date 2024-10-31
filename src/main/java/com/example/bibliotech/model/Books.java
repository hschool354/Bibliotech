package com.example.bibliotech.model;

public class Books {
    private int book_id;  // mã sách
    private String title; // tiếu đề sách
    private String author; // tác giả
    private String isbn; // mã số sách tiêu chuẩn
    private double originalPrice; // giá gốc của sách
    private Double discountedPrice; // giá sau khi giảm của sách
    private int publicationYear; // năm xuất bản sách
    private String language; // ngôn ngữ
    private int pageCount; // số trang
    private double averageRating; // điểm đánh giá trung bình của sách
    private int ratingCount; // số lượng đánh giá của sách
    private String description; // mô tả của sách
    private String coverImageUrl; // ảnh bìa sách
    private int stockQuantity; // số lượng sách tồn khoth
    private Integer dealId; // mã khuyến mãi liên kết với sách
    private String readingDifficulty; // độ khó đọc
    private int estimatedReadingTime; // thời gian đọc ước tính
    private String contentRating; // đánh giá nội dung
    private java.sql.Timestamp createdAt; // thời gian tạo sách
    private java.sql.Timestamp updatedAt; // thời gian cập nhật sách


    public Books(int book_id, String title, String author, String isbn, double originalPrice,
                Double discountedPrice, int publicationYear, String language, int pageCount,
                double averageRating, int ratingCount, String description,
                String coverImageUrl, int stockQuantity, Integer dealId,
                String readingDifficulty, int estimatedReadingTime, String contentRating) {
        this.book_id = book_id;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.originalPrice = originalPrice;
        this.discountedPrice = discountedPrice;
        this.publicationYear = publicationYear;
        this.language = language;
        this.pageCount = pageCount;
        this.averageRating = averageRating;
        this.ratingCount = ratingCount;
        this.description = description;
        this.coverImageUrl = coverImageUrl;
        this.stockQuantity = stockQuantity;
        this.dealId = dealId;
        this.readingDifficulty = readingDifficulty;
        this.estimatedReadingTime = estimatedReadingTime;
        this.contentRating = contentRating;
        this.createdAt = new java.sql.Timestamp(System.currentTimeMillis());
        this.updatedAt = new java.sql.Timestamp(System.currentTimeMillis());
    }

    public int getBookId() { return book_id; }
    public void setBookId(int book_id) { this.book_id = this.book_id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public double getOriginalPrice() { return originalPrice; }
    public void setOriginalPrice(double originalPrice) { this.originalPrice = originalPrice; }

    public Double getDiscountedPrice() { return discountedPrice; }
    public void setDiscountedPrice(Double discountedPrice) { this.discountedPrice = discountedPrice; }

    public int getPublicationYear() { return publicationYear; }
    public void setPublicationYear(int publicationYear) { this.publicationYear = publicationYear; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public int getPageCount() { return pageCount; }
    public void setPageCount(int pageCount) { this.pageCount = pageCount; }

    public double getAverageRating() { return averageRating; }
    public void setAverageRating(double averageRating) { this.averageRating = averageRating; }

    public int getRatingCount() { return ratingCount; }
    public void setRatingCount(int ratingCount) { this.ratingCount = ratingCount; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCoverImageUrl() { return coverImageUrl; }
    public void setCoverImageUrl(String coverImageUrl) { this.coverImageUrl = coverImageUrl; }

    public int getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }

    public Integer getDealId() { return dealId; }
    public void setDealId(Integer dealId) { this.dealId = dealId; }

    public String getReadingDifficulty() { return readingDifficulty; }
    public void setReadingDifficulty(String readingDifficulty) { this.readingDifficulty = readingDifficulty; }

    public int getEstimatedReadingTime() { return estimatedReadingTime; }
    public void setEstimatedReadingTime(int estimatedReadingTime) { this.estimatedReadingTime = estimatedReadingTime; }

    public String getContentRating() { return contentRating; }
    public void setContentRating(String contentRating) { this.contentRating = contentRating; }

    public java.sql.Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(java.sql.Timestamp createdAt) { this.createdAt = createdAt; }

    public java.sql.Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(java.sql.Timestamp updatedAt) { this.updatedAt = updatedAt; }
}
