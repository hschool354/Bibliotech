package com.example.bibliotech.presentation.control;

import com.example.bibliotech.BookItem;
import com.example.bibliotech.DTO.TopBookDTO;
import com.example.bibliotech.dao.BooksDao;
import com.example.bibliotech.utils.SceneCache;
import com.example.bibliotech.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;

public class MyBookItem {

    @FXML
    private ImageView bookCover;


    private void loadBookCover(ImageView imageView, String coverImageUrl) {
        if (coverImageUrl != null && !coverImageUrl.isEmpty()) {
            try {
                String resourcePath = "/book-covers/" + coverImageUrl;
                URL imageUrl = getClass().getResource(resourcePath);

                if (imageUrl != null) {
                    Image image = new Image(imageUrl.toExternalForm());
                    imageView.setImage(image);
                    imageView.setPreserveRatio(false); // Tắt PreserveRatio để ảnh full khung
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


    public void setBookData(String coverImageUrl) {
        // Sử dụng phương thức loadBookCover để tải ảnh bìa
        loadBookCover(bookCover, coverImageUrl);

    }

}
