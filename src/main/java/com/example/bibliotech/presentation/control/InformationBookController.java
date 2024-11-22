package com.example.bibliotech.presentation.control;

import com.example.bibliotech.DTO.BookCategoryDTO;
import com.example.bibliotech.DTO.BriefBookDTO;
import com.example.bibliotech.DTO.DetailedBookDTO;
import com.example.bibliotech.dao.BooksDao;
import com.example.bibliotech.exception.DatabaseException;
import com.example.bibliotech.model.Books;
import com.example.bibliotech.presentation.components.RatingStarsHandler;
import com.example.bibliotech.service.BookCategoryService;
import com.example.bibliotech.service.UserService;
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
import java.math.BigDecimal;
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

    private static boolean needsReload = true;

    public InformationBookController() {
        this.booksDao = new BooksDao();
        this.bookCategoryService = new BookCategoryService();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Initializing InformationBookController...");
        ratingHandler = new RatingStarsHandler(starsContainer);
        ratingHandler.setReadOnly(true);

        Platform.runLater(() -> {
            Scene scene = btn_Back.getScene();
            if (scene != null) {
                Stage stage = (Stage) scene.getWindow();
                // Add listener for when scene becomes visible
                stage.sceneProperty().addListener((observable, oldScene, newScene) -> {
                    if (newScene != null) {
                        newScene.windowProperty().addListener((observableWindow, oldWindow, newWindow) -> {
                            if (newWindow != null) {
                                stage.showingProperty().addListener((observableShowing, oldShowing, newShowing) -> {
                                    if (newShowing) {
                                        System.out.println("Scene is now visible - reloading data");
                                        clearBookData();
                                        loadSelectedBook();
                                    }
                                });
                            }
                        });
                    }
                });
            }

            // Initial load
            clearBookData();
            loadSelectedBook();
        });
    }


    public static void setNeedsReload(boolean value) {
        needsReload = value;
    }

    // Phương thức này có thể dùng sau để lấy rating
    public int getCurrentRating() {
        return ratingHandler.getSelectedRating();
    }

    @FXML
    public void handleBackButton() {
        try {
            String previousScene = SceneCache.getPreviousScene();
            if (previousScene == null) {
                previousScene = "/com/example/bibliotech/home_1.fxml";
            }

            // Clear current book data before switching scenes
            clearBookData();

            Stage stage = (Stage) btn_Back.getScene().getWindow();
            Scene scene = SceneCache.getScene(previousScene);
            stage.setScene(scene);
            stage.show();

            // Refresh data when returning to home screen
            if (previousScene.equals("/com/example/bibliotech/home_1.fxml")) {
                Home_1_Controller homeController = SceneCache.getController("/com/example/bibliotech/home_1.fxml");
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

    private void changeScene(String fxmlPath) {
        try {
            Stage stage = (Stage) btn_Back.getScene().getWindow(); // Lấy stage hiện tại
            Scene scene = SceneCache.getScene(fxmlPath); // Lấy Scene từ cache
            stage.setScene(scene);

            //SceneTransitionEffect.applyTransitionEffect((Pane) root);

            stage.show();
        } catch (IOException e) {
            System.err.println("Error loading scene: " + fxmlPath);
            e.printStackTrace();
        }
    }

    private void loadSelectedBook() {
        Integer selectedBookId = SessionManager.getInstance().getSelectedBookId();
        System.out.println("Loading book with ID: " + selectedBookId);

        if (selectedBookId == null) {
            showError("No book selected");
            return;
        }

        try {
            Optional<DetailedBookDTO> bookOpt = booksDao.findDetailedBookById(selectedBookId);
            if (bookOpt.isPresent()) {
                System.out.println("Found book: " + bookOpt.get().getTitle());
                currentBook = bookOpt.get();
                displayBookDetails(currentBook);

                // Kiểm tra xem sách đã được mua chưa
                int currentUserId = SessionManager.getInstance().getCurrentUserId();
                List<Books> purchasedBooks = booksDao.getBooksPurchasedByUser(currentUserId);
                boolean isBookPurchased = purchasedBooks.stream()
                        .anyMatch(book -> book.getBookId() == selectedBookId);

                // Cập nhật nút "Mua"
                if (isBookPurchased) {
                    Platform.runLater(() -> {
                        btn_Buy.setText("Purchase");
                        btn_Buy.setDisable(true); // Vô hiệu hóa nút nếu đã mua
                    });
                }
            } else {
                System.out.println("No book found with ID: " + selectedBookId);
                showError("Book not found with ID: " + selectedBookId);
            }
        } catch (SQLException | DatabaseException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
            showError("Failed to load book: " + e.getMessage());
        }
    }


    private void clearBookData() {
        System.out.println("Clearing old book data");
        Platform.runLater(() -> {
            if (lbl_Title != null) lbl_Title.setText("");
            if (lbl_author != null) lbl_author.setText("");
            if (lbl_readingAge != null) lbl_readingAge.setText("");
            if (lbl_language != null) lbl_language.setText("");
            if (lbl_pages != null) lbl_pages.setText("");
            if (lbl_PublicationDate != null) lbl_PublicationDate.setText("");
            if (txtArea_description != null) txtArea_description.setText("");
            if (ratingHandler != null) ratingHandler.setInitialRating(0);
            if (categoriesContainer != null) categoriesContainer.getChildren().clear();
            loadPlaceholderImage();
        });
    }

    private void displayBookDetails(DetailedBookDTO book) {
        System.out.println("Displaying book details for: " + book.getTitle());
        currentBook = book;

        Platform.runLater(() -> {
            try {
                lbl_Title.setText(book.getTitle());
                lbl_author.setText("Author: " + book.getAuthor());
                lbl_readingAge.setText(book.getReadingDifficulty());
                lbl_language.setText(book.getLanguage());
                lbl_pages.setText(String.format("%d pages", book.getPageCount()));
                lbl_PublicationDate.setText(String.valueOf(book.getPublicationYear()));
                txtArea_description.setText(book.getDescription());

                loadBookCover(book.getCoverImageUrl());

                double averageRating = book.getAverageRating();
                if (averageRating > 0) {
                    ratingHandler.setInitialRating((int) Math.round(averageRating));
                } else {
                    ratingHandler.setInitialRating(0);
                }

                loadBookCategories(book.getBookId());

                System.out.println("Book details displayed successfully");
            } catch (Exception e) {
                System.err.println("Error displaying book details: " + e.getMessage());
                e.printStackTrace();
            }
        });
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

    @FXML
    public void handleBuyButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/bibliotech/BuyBook.fxml"));
            Parent root = loader.load();

            // Lấy controller của BuyBookController
            BuyBookController buyBookController = loader.getController();

            // Truyền dữ liệu sách và balance
            if (currentBook != null) {
                int currentUserId = SessionManager.getInstance().getCurrentUserId();
                BigDecimal balance = new UserService().getUserBalance(currentUserId);
                // Assuming the userId is available, such as from the session manager:
                buyBookController.setBookData(currentBook, balance, currentUserId);

            }

            // Hiển thị màn hình mới
            Stage stage = (Stage) btn_Buy.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.err.println("Error switching to BuyBook scene: " + e.getMessage());
            e.printStackTrace();
        }
    }




}
