package com.example.bibliotech;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URL;

public class BookItem {
    @FXML
    private ImageView bookCover;

    @FXML
    private Label bookTitle;

    @FXML
    private Label bookPrice;

    private void loadBookCover(ImageView imageView, String coverImageUrl) {
        if (coverImageUrl != null && !coverImageUrl.isEmpty()) {
            try {
                String resourcePath = "/book-covers/" + coverImageUrl;
                URL imageUrl = getClass().getResource(resourcePath);

                if (imageUrl != null) {
                    Image image = new Image(imageUrl.toExternalForm());
                    imageView.setImage(image);
                    imageView.setFitHeight(170);
                    imageView.setFitWidth(120);
                    imageView.setPreserveRatio(true);
                } else {
                    loadPlaceholderImage(imageView);
                }
            } catch (Exception e) {
                System.err.println("Error loading book cover: " + coverImageUrl);
                e.printStackTrace();
                loadPlaceholderImage(imageView);
            }
        } else {
            loadPlaceholderImage(imageView);
        }
    }

    private void loadPlaceholderImage(ImageView imageView) {
        try {
            URL placeholderUrl = getClass().getResource("/book-covers/placeholder.png");
            if (placeholderUrl != null) {
                Image placeholder = new Image(placeholderUrl.toExternalForm());
                imageView.setImage(placeholder);
            }
        } catch (Exception e) {
            System.err.println("Error loading placeholder image");
            e.printStackTrace();
        }
    }

    public void setBookData(String title, String coverImageUrl, double price) {
        bookTitle.setText(title);
        bookPrice.setText(String.format("%.2f VND", price));

        // Sử dụng phương thức loadBookCover để tải ảnh bìa
        loadBookCover(bookCover, coverImageUrl);
    }

}
