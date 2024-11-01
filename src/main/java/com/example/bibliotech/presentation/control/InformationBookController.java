package com.example.bibliotech.presentation.control;

import com.example.bibliotech.DTO.BookCategoryDTO;
import com.example.bibliotech.DTO.BriefBookDTO;
import com.example.bibliotech.DTO.DetailedBookDTO;
import com.example.bibliotech.dao.BooksDao;
import com.example.bibliotech.presentation.components.RatingStarsHandler;
import com.example.bibliotech.service.BookCategoryService;
import com.example.bibliotech.utils.SceneCache;
import com.example.bibliotech.utils.SessionManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;


import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InformationBookController implements Initializable {
    private static final Logger logger = Logger.getLogger(InformationBookController.class.getName());

    @FXML
    private HBox starsContainer;

    @FXML
    private Button btn_Back;

    private RatingStarsHandler ratingHandler;

    @FXML private Label lbl_Title;
    @FXML private Label lbl_author;
    @FXML private Label lbl_readingAge;
    @FXML private Label lbl_language;
    @FXML private Label lbl_pages;
    @FXML private Label lbl_PublicationDate;

    @FXML private TextArea txtArea_description;

    @FXML private ImageView image_Book;

    @FXML private Button btn_addToFavorite;
    @FXML private Button btn_ReadForFree;
    @FXML private Button btn_Buy;

    private final BooksDao booksDao;
    private DetailedBookDTO currentBook;

    @FXML private Pane categoriesContainer;
    private final BookCategoryService bookCategoryService;

    public InformationBookController() {
        this.booksDao = new BooksDao();
        this.bookCategoryService = new BookCategoryService();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ratingHandler = new RatingStarsHandler(starsContainer);
        ratingHandler.setReadOnly(true);

        // Load book data after initialization
        Platform.runLater(() -> {
            try {
                clearBookData();
                loadSelectedBook();
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error loading book data", e);
                showError("Failed to load book details: " + e.getMessage());
            }
        });
    }

    // Phương thức này có thể dùng sau để lấy rating
    public int getCurrentRating() {
        return ratingHandler.getSelectedRating();
    }

    @FXML
    public void handleBackButton() {
        try {
            Stage stage = (Stage) btn_Back.getScene().getWindow();

            // Clear cache for information_Book.fxml before going back
            SceneCache.clearCache("/com/example/bibliotech/information_Book.fxml");

            // Get fresh instance of home_1.fxml
            Scene scene = SceneCache.getScene("/com/example/bibliotech/home_1.fxml");

            // Get controller and refresh data
            Home_1_Controller homeController = SceneCache.getController("/com/example/bibliotech/home_1.fxml");
            if (homeController != null) {
                homeController.refreshPopularBooks();
                homeController.refreshSaleBooks();
            }

            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error navigating back", e);
            showError("Navigation failed: " + e.getMessage());
        }
    }

    private void changeScene(String fxmlPath) {
        try {
            Stage stage = (Stage) btn_Back.getScene().getWindow(); // Lấy stage hiện tại
            Scene scene = SceneCache.getScene(fxmlPath); // Lấy Scene từ cache
            stage.setScene(scene);

            //SceneTransitionEffect.applyTransitionEffect((Pane) root);

            stage.show();
        } catch (IOException e) {
            System.err.println("Error loading scene: " + fxmlPath);
            e.printStackTrace(); // In ra lỗi để tiện theo dõi
        }
    }

    private void loadSelectedBook() {
        Integer selectedBookId = SessionManager.getInstance().getSelectedBookId();
        if (selectedBookId == null) {
            showError("No book selected");
            return;
        }

        new Thread(() -> {
            try {
                Optional<DetailedBookDTO> bookOpt = booksDao.findDetailedBookById(selectedBookId);

                Platform.runLater(() -> {
                    if (bookOpt.isPresent()) {
                        displayBookDetails(bookOpt.get());
                    } else {
                        showError("Book not found with ID: " + selectedBookId);
                    }
                });
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Error loading book data", e);
                Platform.runLater(() -> showError("Failed to load book: " + e.getMessage()));
            }
        }).start();
    }

    private void clearBookData() {
        lbl_Title.setText("");
        lbl_author.setText("");
        lbl_readingAge.setText("");
        lbl_language.setText("");
        lbl_pages.setText("");
        lbl_PublicationDate.setText("");
        txtArea_description.setText("");
        loadPlaceholderImage();
        if (ratingHandler != null) {
            ratingHandler.setInitialRating(0);
        }
    }

    private void displayBookDetails(DetailedBookDTO book) {
        currentBook = book;

        // Update UI elements with book details
        lbl_Title.setText(book.getTitle());
        lbl_author.setText("Author: " +book.getAuthor());
        lbl_readingAge.setText(book.getReadingDifficulty());
        lbl_language.setText(book.getLanguage());
        lbl_pages.setText(String.format("%d pages", book.getPageCount()));
        lbl_PublicationDate.setText(String.valueOf(book.getPublicationYear()));
        txtArea_description.setText(book.getDescription());

        // Load book cover
        loadBookCover(book.getCoverImageUrl());

        double averageRating = book.getAverageRating();
        if (averageRating > 0) {
            ratingHandler.setInitialRating((int) Math.round(averageRating));
        } else {
            ratingHandler.setInitialRating(0);
        }

        loadBookCategories(book.getBookId());
    }

    private void loadBookCover(String coverImageUrl) {
        if (coverImageUrl != null && !coverImageUrl.isEmpty()) {
            try {
                String resourcePath = "/book-covers/" + coverImageUrl;
                URL imageUrl = getClass().getResource(resourcePath);

                if (imageUrl != null) {
                    Image bookCover = new Image(imageUrl.toExternalForm());
                    image_Book.setImage(bookCover);
                } else {
                    loadPlaceholderImage();
                }
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error loading book cover", e);
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
            logger.log(Level.SEVERE, "Error loading placeholder image", e);
        }
    }

    private void loadBookCategories(int bookId) {
        try {
            List<BookCategoryDTO> categories = bookCategoryService.getBookCategories(bookId);
            Platform.runLater(() -> {
                categoriesContainer.getChildren().clear();
                for (BookCategoryDTO category : categories) {
                    Label categoryLabel = new Label(category.getCategoryName());
                    categoryLabel.getStyleClass().add("category-label");
                    // Add some basic styling
                    categoryLabel.setStyle(
                            "-fx-background-color: #f0f0f0;" +
                                    "-fx-padding: 5 10 5 10;" +
                                    "-fx-background-radius: 15;" +
                                    "-fx-margin: 5;"
                    );
                    categoriesContainer.getChildren().add(categoryLabel);
                }
            });
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error loading book categories", e);
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
