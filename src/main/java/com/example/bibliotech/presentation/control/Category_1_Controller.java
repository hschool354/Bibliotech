package com.example.bibliotech.presentation.control;

import com.example.bibliotech.BookItem;
import com.example.bibliotech.DTO.TopBookDTO;
import com.example.bibliotech.dao.BooksDao;
import com.example.bibliotech.service.BookService;
import com.example.bibliotech.utils.SceneCache;
import com.example.bibliotech.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.io.IOException;


public class Category_1_Controller {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private Button btnLeft;

    @FXML
    private Button btnRight;

    @FXML
    private HBox containerPopular;
    private BooksDao bookDAO;

    @FXML private Button btn_Home,btn_Wallet;


    private List<TopBookDTO> popularBooks;

    @FXML
    public void initialize() {
        this.bookDAO = new BooksDao();
        loadPopularBooks();
    }

    @FXML
    private void scrollLeft() {
        // Lấy giá trị hiện tại của thanh cuộn
        double currentHValue = scrollPane.getHvalue();
        // Di chuyển sang trái (giảm giá trị Hvalue)
        scrollPane.setHvalue(Math.max(0, currentHValue - 0.1)); // 0.1 là khoảng cách di chuyển
    }

    @FXML
    private void scrollRight() {
        // Lấy giá trị hiện tại của thanh cuộn
        double currentHValue = scrollPane.getHvalue();
        // Di chuyển sang phải (tăng giá trị Hvalue)
        scrollPane.setHvalue(Math.min(1, currentHValue + 0.1)); // 1 là giá trị tối đa
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


    private void loadPopularBooks() {
        try {
            popularBooks = bookDAO.getTopRatedBooks(10); // Lấy 10 sách phổ biến

            // Xóa các phần tử cũ (nếu có)
            containerPopular.getChildren().clear();

            // Tạo BookItem cho từng sách
            for (TopBookDTO book : popularBooks) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/bibliotech/BookItem.fxml"));
                Node bookNode = loader.load();

                // Cập nhật dữ liệu sách cho BookItem
                BookItem bookItemController = loader.getController();
                bookItemController.setBookData(book.getTitle(), book.getCoverImageUrl(), book.getDisplayPrice());

                // Gắn sự kiện nhấn vào cuốn sách
                bookNode.setOnMouseClicked(event -> {
                    handleBookClick(book.getBookId()); // Hàm xử lý chuyển trang
                });

                // Thêm vào container
                containerPopular.getChildren().add(bookNode);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading BookItem.fxml");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleBookClick(int bookId) {
        try {
            System.out.println("Book clicked with ID: " + bookId);

            // Lưu ID của sách vào SessionManager
            SessionManager.getInstance().setSelectedBookId(bookId);
            System.out.println("Saved book ID to SessionManager: " + SessionManager.getInstance().getSelectedBookId());

            // Đánh dấu là cần reload dữ liệu
            InformationBookController.setNeedsReload(true);

            // Chuyển sang trang thông tin sách
            Stage stage = (Stage) scrollPane.getScene().getWindow();

            // Xóa cache của scene information_Book
            SceneCache.clearCache("/com/example/bibliotech/information_Book.fxml");

            Scene scene = SceneCache.getScene("/com/example/bibliotech/information_Book.fxml");
            stage.setScene(scene);
            stage.show();

            System.out.println("Switched to information_Book scene");
        } catch (IOException e) {
            System.err.println("Error loading information_Book.fxml: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleMyWalletButton() {
        changeScene("/com/example/bibliotech/myWallet.fxml");
    }

    @FXML
    private void handleHomeButton() {
        changeScene("/com/example/bibliotech/home_1.fxml");
    }

    private void changeScene(String fxmlPath) {
        try {
            Stage stage = (Stage) btn_Home.getScene().getWindow();
            Scene scene = SceneCache.getScene(fxmlPath);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Error loading scene: " + fxmlPath);
            e.printStackTrace();
        }
    }

}
