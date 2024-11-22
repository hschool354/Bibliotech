package com.example.bibliotech.presentation.control;

import com.example.bibliotech.BookItem;
import com.example.bibliotech.DTO.TopBookDTO;
import com.example.bibliotech.dao.BooksDao;
import com.example.bibliotech.model.Books;
import com.example.bibliotech.utils.SceneCache;
import com.example.bibliotech.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;

public class MyLibraryController {
    @FXML
    private HBox MyBook;

    private BooksDao bookDAO;

    @FXML
    public void initialize() {
        this.bookDAO = new BooksDao();
        loadMyBoook();
    }

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

    private void loadMyBoook() {
        try {
            System.out.println("Current User ID: " + SessionManager.getInstance().getCurrentUserId());

            // Lấy danh sách sách đã mua
            List<Books> purchasedBooks = bookDAO.getBooksPurchasedByUser(SessionManager.getInstance().getCurrentUserId());

            // Xóa các phần tử cũ (nếu có)
            MyBook.getChildren().clear();

            // Tạo ImageView cho từng sách và hiển thị
            for (Books book : purchasedBooks) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/bibliotech/MyBookItem.fxml"));
                Node bookNode = loader.load();

                // Cập nhật dữ liệu sách cho BookItem
                MyBookItem myBookController = loader.getController();
                myBookController.setBookData(book.getCoverImageUrl());

                // Gắn sự kiện nhấn vào cuốn sách
                bookNode.setOnMouseClicked(event -> {
                    handleBookClick(book.getBookId()); // Hàm xử lý chuyển trang
                });

                // Thêm vào container
                MyBook.getChildren().add(bookNode);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error loading purchased books");
        }
    }

    private void handleBookClick(int bookId) {
//        try {
//            System.out.println("Book clicked with ID: " + bookId);
//
//            // Lưu ID của sách vào SessionManager
//            SessionManager.getInstance().setSelectedBookId(bookId);
//            System.out.println("Saved book ID to SessionManager: " + SessionManager.getInstance().getSelectedBookId());
//
//            // Đánh dấu là cần reload dữ liệu
//            InformationBookController.setNeedsReload(true);
//
//            // Chuyển sang trang thông tin sách
//            Stage stage = (Stage) scrollPane.getScene().getWindow();
//
//            // Xóa cache của scene information_Book
//            SceneCache.clearCache("/com/example/bibliotech/information_Book.fxml");
//
//            Scene scene = SceneCache.getScene("/com/example/bibliotech/information_Book.fxml");
//            stage.setScene(scene);
//            stage.show();
//
//            System.out.println("Switched to information_Book scene");
//        } catch (IOException e) {
//            System.err.println("Error loading information_Book.fxml: " + e.getMessage());
//            e.printStackTrace();
//        }
    }
}
