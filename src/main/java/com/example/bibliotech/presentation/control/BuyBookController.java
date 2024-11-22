package com.example.bibliotech.presentation.control;

import com.example.bibliotech.DTO.DetailedBookDTO;
import com.example.bibliotech.dao.TransactionDao;
import com.example.bibliotech.exception.DatabaseException;
import com.example.bibliotech.model.TransactionStatus;
import com.example.bibliotech.model.Transactions;
import com.example.bibliotech.utils.SceneCache;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BuyBookController {

    private static final Logger logger = Logger.getLogger(BuyBookController.class.getName());

    @FXML
    private Label lb_BookPrice, lb_Title, lb_author, lb_Balance, lb_BookPrice1;

    @FXML
    private ImageView image_Book;

    private BigDecimal userBalance;
    private BigDecimal bookPrice;
    private int userId; // Assuming userId is passed to the controller

    @FXML
    private Button btn_Buy,btn_Back;

    private TransactionDao transactionDao = new TransactionDao();

    public void setBookData(DetailedBookDTO book, BigDecimal balance, int userId) {
        this.userBalance = balance;
        this.userId = userId;

        Platform.runLater(() -> {
            lb_Title.setText(book.getTitle());
            lb_author.setText("Author: " + book.getAuthor());

            // Set the book price (discounted or original)
            if (book.getDiscountedPrice() != null && book.getDiscountedPrice().compareTo(book.getOriginalPrice()) < 0) {
                bookPrice = BigDecimal.valueOf(book.getDiscountedPrice()); // Set the bookPrice here
                lb_BookPrice.setText(String.format("$%.2f", book.getDiscountedPrice()));
                lb_BookPrice1.setText(String.format("$%.2f", book.getDiscountedPrice()));
                btn_Buy.setText(String.format("Buy for $%.2f", book.getDiscountedPrice()));
            } else {
                bookPrice = BigDecimal.valueOf(book.getOriginalPrice()); // Set the bookPrice here
                lb_BookPrice.setText(String.format("$%.2f", book.getOriginalPrice()));
                lb_BookPrice1.setText(String.format("$%.2f", book.getOriginalPrice()));
                btn_Buy.setText(String.format("Buy for $%.2f", book.getOriginalPrice()));
            }

            lb_Balance.setText(String.format("$%.2f", userBalance));

            // Load book image
            loadBookCoverImage(book);
        });
    }


    private void loadBookCoverImage(DetailedBookDTO book) {
        if (book.getCoverImageUrl() != null) {
            String resourcePath = "/book-covers/" + book.getCoverImageUrl();
            URL imageUrl = getClass().getResource(resourcePath);
            if (imageUrl != null) {
                Image bookCover = new Image(imageUrl.toExternalForm());
                image_Book.setImage(bookCover);
            } else {
                loadPlaceholderImage();
            }
        } else {
            loadPlaceholderImage();
        }
    }

    private void loadPlaceholderImage() {
        try {
            URL placeholderUrl = getClass().getResource("/book-covers/placeholder.png");
            if (placeholderUrl != null) {
                Image placeholder = new Image(placeholderUrl.toExternalForm());
                image_Book.setImage(placeholder);
            }
        } catch (Exception e) {
            System.err.println("Error loading placeholder image: " + e.getMessage());
        }
    }

    @FXML
    private void handleBuyButton() throws DatabaseException, IOException {
        // Check if the user has enough balance to purchase the book
        if (userBalance.compareTo(bookPrice) >= 0) {
            // Deduct the book price from the user's balance
            userBalance = userBalance.subtract(bookPrice);

            // Update the balance in the UI
            lb_Balance.setText(String.format("$%.2f", userBalance));

            // Add transaction record to the database
            Transactions transaction = new Transactions(userId, null, "MUA", bookPrice, TransactionStatus.COMPLETED);
            transactionDao.addTransaction(transaction);

            // Disable the buy button after purchase
            btn_Buy.setDisable(true);
            btn_Buy.setText("Purchased");

            // Optionally, show a success message

            Stage stage = (Stage) btn_Buy.getScene().getWindow();
            Scene scene = SceneCache.getScene("/com/example/bibliotech/SuccessDeposit.fxml");
            stage.setScene(scene);
            stage.show();
        } else {
            // Show an error message if the balance is insufficient
            showAlert(AlertType.ERROR, "Insufficient Funds", "You do not have enough balance to purchase this book.");
        }
    }

    private void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void handleBackButton() {
        try {
            String previousScene = SceneCache.getPreviousScene();
            if (previousScene == null) {
                previousScene = "/com/example/bibliotech/information_Book.fxml";
            }

//            // Clear current book data before switching scenes
//            clearBookData();

            Stage stage = (Stage) btn_Back.getScene().getWindow();
            Scene scene = SceneCache.getScene(previousScene);
            stage.setScene(scene);
            stage.show();

            // Refresh data when returning to home screen
            if (previousScene.equals("/com/example/bibliotech/information_Book.fxml")) {
                Home_1_Controller homeController = SceneCache.getController("/com/example/bibliotech/information_Book.fxml");
                if (homeController != null) {
                    homeController.refreshPopularBooks();
                    homeController.refreshSaleBooks();
                }
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error navigating back", e);
            showError("Navigation failed: " + e.getMessage());
        }
    }

    private void showError(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

}
