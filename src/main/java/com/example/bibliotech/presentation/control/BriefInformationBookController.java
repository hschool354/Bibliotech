package com.example.bibliotech.presentation.control;

import com.example.bibliotech.DTO.BriefBookDTO;
import com.example.bibliotech.utils.SceneCache;
import com.example.bibliotech.utils.SessionManager;
import com.example.bibliotech.dao.BooksDao;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea; // Thay đổi từ TextField sang TextArea
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.fxml.Initializable;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.stage.Stage;


public class BriefInformationBookController implements Initializable {
    private static final Logger logger = Logger.getLogger(BriefInformationBookController.class.getName());

    @FXML private ImageView image;
    @FXML private Label lbl_Tittle, lbl_Author, lbl_Pages, lbl_Average, lbl_Rating;
    @FXML private Button btn_ReadNow;
    @FXML private TextArea txtArena_description;

    private BriefBookDTO currentBook;
    private final BooksDao booksDao;

    public BriefInformationBookController() {
        this.booksDao = new BooksDao();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Platform.runLater(() -> {
            try {
                setupReadNowButton();
                Integer selectedBookId = SessionManager.getInstance().getSelectedBookId();
                if (selectedBookId != null) {
                    loadSelectedBook();
                }
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error during initialization", e);
                showError("Initialization failed: " + e.getMessage());
            }
        });
    }

    public void loadSelectedBook() {
        Integer selectedBookId = SessionManager.getInstance().getSelectedBookId();
        if (selectedBookId == null) {
            showError("Không tìm thấy thông tin sách được chọn");
            return;
        }

        logger.info("Loading book data for ID: " + selectedBookId);
        clearCurrentData();

        new Thread(() -> {
            try {
                Optional<BriefBookDTO> bookOpt = booksDao.findBriefBookById(selectedBookId);

                Platform.runLater(() -> {
                    if (bookOpt.isPresent()) {
                        logger.info("Displaying book data...");
                        displayBook(bookOpt.get());
                    } else {
                        showError("Không tìm thấy thông tin sách với ID: " + selectedBookId);
                    }
                });
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Error loading book data", e);
                Platform.runLater(() -> {
                    showError("Không thể tải thông tin sách: " + e.getMessage());
                });
            }
        }).start();
    }

    private void displayBook(BriefBookDTO book) {
        try {
            currentBook = book;

            lbl_Tittle.setText(book.getTitle());
            lbl_Author.setText(book.getAuthor());
            lbl_Pages.setText(String.format("%d trang", book.getPageCount()));
            lbl_Average.setText(String.format("%.1f/5.0", book.getAverageRating()));
            lbl_Rating.setText(String.format("%d", book.getRatingCount()));
            txtArena_description.setText(book.getDescription());

            loadBookCover(book.getCoverImageUrl());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error displaying book data", e);
            showError("Error displaying book data: " + e.getMessage());
        }
    }

    private void loadBookCover(String coverImageUrl) {
        if (coverImageUrl != null && !coverImageUrl.isEmpty()) {
            try {
                String resourcePath = "/book-covers/" + coverImageUrl;
                URL imageUrl = getClass().getResource(resourcePath);

                if (imageUrl != null) {
                    Image bookCover = new Image(imageUrl.toExternalForm());
                    image.setImage(bookCover);
                } else {
                    logger.warning("Book cover not found: " + coverImageUrl);
                    loadPlaceholderImage();
                }
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error loading book cover: " + coverImageUrl, e);
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
                image.setImage(placeholder);
            } else {
                logger.warning("Placeholder image not found");
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error loading placeholder image", e);
        }
    }

    private void setupReadNowButton() {
        btn_ReadNow.setOnAction(event -> {
            if (currentBook != null) {
                openBookReader(currentBook.getBookId());
            } else {
                logger.warning("Attempted to open reader with no book selected");
            }
        });
    }

    private void openBookReader(int bookId) {
        try {
            // Lưu book ID vào session
            SessionManager.getInstance().setSelectedBookId(bookId);

            // Lấy stage hiện tại
            Stage stage = (Stage) btn_ReadNow.getScene().getWindow();

            // Đảm bảo đường dẫn FXML chính xác và tồn tại
            String fxmlPath = "/com/example/bibliotech/information_Book.fxml";
            URL resourceUrl = getClass().getResource(fxmlPath);
            if (resourceUrl == null) {
                throw new IOException("FXML file not found: " + fxmlPath);
            }

            // Thử load scene từ cache
            Scene scene = null;
            try {
                scene = SceneCache.getScene(fxmlPath);
            } catch (IOException e) {
                logger.log(Level.WARNING, "Could not load scene from cache, attempting fresh load", e);

                // Nếu không load được từ cache, load mới
                FXMLLoader loader = new FXMLLoader(resourceUrl);
                Parent root = loader.load();
                scene = new Scene(root);

                // Cập nhật cache với scene mới
                SceneCache.clearCache(fxmlPath);
                try {
                    scene = SceneCache.getScene(fxmlPath);
                } catch (IOException ex) {
                    logger.log(Level.SEVERE, "Failed to update scene cache", ex);
                    // Tiếp tục sử dụng scene đã load mới
                }
            }

            if (scene != null) {
                // Set scene và hiển thị
                stage.setScene(scene);
                stage.show();
            } else {
                throw new IOException("Scene could not be loaded");
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error opening book reader", e);
            showError("Cannot open book reader: " + e.getMessage());
        }
    }

    private void showError(String message) {
        logger.warning(message);
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    public void refreshData() {
        logger.info("Refreshing book data");
        loadSelectedBook();
    }

    private void clearCurrentData() {
        logger.info("Clearing current book data");
        currentBook = null;
        Platform.runLater(() -> {
            try {
                lbl_Tittle.setText("");
                lbl_Author.setText("");
                lbl_Pages.setText("");
                lbl_Average.setText("");
                lbl_Rating.setText("");
                txtArena_description.setText("");
                loadPlaceholderImage();
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error clearing data", e);
            }
        });
    }
}