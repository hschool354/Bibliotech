package com.example.bibliotech.presentation.control;

import com.example.bibliotech.model.Books;
import com.example.bibliotech.service.BookService;
import com.example.bibliotech.utils.DataManager;
import com.example.bibliotech.utils.SceneCache;
import com.example.bibliotech.exception.BookServiceException;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Scene;

import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class AdminDetailBookController implements Initializable {
    @FXML private TextField txt_ID, txt_IBSN, txt_Title, txt_Author, txt_PageCount, txt_EstimatedReadingTime,
            txt_originalPrice, txt_DiscountedPrice, txt_StockQuantity, txt_CreatedAt, txt_UpdatedAt,txt_CoverImageURL;

    @FXML private TextArea txtArena_description;

    @FXML private DatePicker dateTimePicker_pub;

    @FXML private ComboBox<String> comboBox_Language, comboBox_ReadingDifficulty, comboBox_ContrenRating, ComboBox_Deal;

    @FXML private Button btn_ChooseImage, btn_Save, btn_cancel, btn_Reset;

    @FXML private ImageView imageView_cover;

    private File selectedImageFile;
    private Books currentBook;
    private Books originalBook; // For reset functionality
    private BookService bookService;

    private static final String[] LANGUAGES = {
            "Vietnamese", "English", "French", "German", "Spanish",
            "Chinese", "Japanese", "Korean", "Other"
    };

    private static final String[] READING_DIFFICULTIES = {"EASY", "MEDIUM", "HARD"};
    private static final String[] CONTENT_RATINGS = {"EVERYONE", "TEEN", "MATURE"};

    public AdminDetailBookController() {
        this.bookService = new BookService();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupComboBoxes();
        setupButtons();

        Books selectedBook = DataManager.getInstance().getSelectedBook();
        if (selectedBook != null) {
            try {
                // Lấy thông tin chi tiết của sách từ database
                Books detailedBook = bookService.getBookById(selectedBook.getBookId());
                loadBook(detailedBook);
            } catch (BookServiceException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to load book details: " + e.getMessage());
                handleCancel(); // Return to book manager
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "No book selected");
            handleCancel(); // Return to book manager
        }
    }

    public void loadBook(Books book) {
        this.currentBook = book;
        this.originalBook = copyBook(book); // Create a copy for reset functionality
        populateFields();
        loadBookCover();
    }

    private void setupComboBoxes() {
        comboBox_Language.getItems().addAll(LANGUAGES);
        comboBox_ReadingDifficulty.getItems().addAll(READING_DIFFICULTIES);
        comboBox_ContrenRating.getItems().addAll(CONTENT_RATINGS);
        // Add deals if needed
        ComboBox_Deal.getItems().addAll("No Deal", "Deal 1", "Deal 2"); // Customize based on your needs
    }

    private void setupButtons() {
        btn_ChooseImage.setOnAction(e -> handleChooseImage());
        btn_Save.setOnAction(e -> handleSave());
        btn_cancel.setOnAction(e -> handleCancel());
        btn_Reset.setOnAction(e -> handleReset());
    }

    private void populateFields() {
        txt_ID.setText(String.valueOf(currentBook.getBookId()));
        txt_IBSN.setText(currentBook.getIsbn());
        txt_Title.setText(currentBook.getTitle());
        txt_Author.setText(currentBook.getAuthor());
        txt_PageCount.setText(String.valueOf(currentBook.getPageCount()));
        txt_EstimatedReadingTime.setText(String.valueOf(currentBook.getEstimatedReadingTime()));
        txt_originalPrice.setText(String.valueOf(currentBook.getOriginalPrice()));
        txt_DiscountedPrice.setText(currentBook.getDiscountedPrice() != null ?
                String.valueOf(currentBook.getDiscountedPrice()) : "");
        txt_StockQuantity.setText(String.valueOf(currentBook.getStockQuantity()));
        txtArena_description.setText(currentBook.getDescription());

        if (currentBook.getPublicationYear() > 0) {
            dateTimePicker_pub.setValue(LocalDate.of(currentBook.getPublicationYear(), 1, 1));
        }

        comboBox_Language.setValue(currentBook.getLanguage());
        comboBox_ReadingDifficulty.setValue(currentBook.getReadingDifficulty());
        comboBox_ContrenRating.setValue(currentBook.getContentRating());
        ComboBox_Deal.setValue(currentBook.getDealId() != null ? "Deal " + currentBook.getDealId() : "No Deal");

        if (currentBook.getCreatedAt() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            txt_CreatedAt.setText(currentBook.getCreatedAt().toLocalDateTime().format(formatter));
        }

        if (currentBook.getUpdatedAt() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            txt_UpdatedAt.setText(currentBook.getUpdatedAt().toLocalDateTime().format(formatter));
        }

    }

    private void loadBookCover() {
        if (currentBook.getCoverImageUrl() != null && !currentBook.getCoverImageUrl().isEmpty()) {
            try {
                String resourcePath = "/book-covers/" + currentBook.getCoverImageUrl();
                URL imageUrl = getClass().getResource(resourcePath);
                if (imageUrl != null) {
                    Image image = new Image(imageUrl.toExternalForm());
                    imageView_cover.setImage(image);
                    imageView_cover.setFitHeight(200);
                    imageView_cover.setFitWidth(150);
                    imageView_cover.setPreserveRatio(true);
                }
            } catch (Exception e) {
                e.printStackTrace();
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
                imageView_cover.setImage(placeholder);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleChooseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Book Cover Image");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.jpeg", "*.png")
        );

        File file = fileChooser.showOpenDialog(btn_ChooseImage.getScene().getWindow());
        if (file != null) {
            selectedImageFile = file;
            Image image = new Image(file.toURI().toString());
            imageView_cover.setImage(image);
        }
    }

    @FXML
    private void handleSave() {
        try {
            updateBookFromFields();
            bookService.updateBook(currentBook, selectedImageFile);
            showAlert(Alert.AlertType.INFORMATION, "Success", "Book updated successfully!");

            // Clear the selected book before navigating back
            DataManager.getInstance().clearSelectedBook();
            navigateToBookManager();
        } catch (BookServiceException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to update book: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        DataManager.getInstance().clearSelectedBook();
        navigateToBookManager();
    }

    @FXML
    private void handleReset() {
        currentBook = copyBook(originalBook);
        populateFields();
        loadBookCover();
        selectedImageFile = null;
    }

    private void updateBookFromFields() {
        currentBook.setIsbn(txt_IBSN.getText());
        currentBook.setTitle(txt_Title.getText());
        currentBook.setAuthor(txt_Author.getText());
        currentBook.setPageCount(Integer.parseInt(txt_PageCount.getText()));
        currentBook.setEstimatedReadingTime(Integer.parseInt(txt_EstimatedReadingTime.getText()));
        currentBook.setOriginalPrice(Double.parseDouble(txt_originalPrice.getText()));

        String discountedPrice = txt_DiscountedPrice.getText();
        currentBook.setDiscountedPrice(discountedPrice.isEmpty() ? null : Double.parseDouble(discountedPrice));

        currentBook.setStockQuantity(Integer.parseInt(txt_StockQuantity.getText()));
        currentBook.setDescription(txtArena_description.getText());

        if (dateTimePicker_pub.getValue() != null) {
            currentBook.setPublicationYear(dateTimePicker_pub.getValue().getYear());
        }

        currentBook.setLanguage(comboBox_Language.getValue());
        currentBook.setReadingDifficulty(comboBox_ReadingDifficulty.getValue());
        currentBook.setContentRating(comboBox_ContrenRating.getValue());

        String dealValue = ComboBox_Deal.getValue();
        currentBook.setDealId(dealValue.equals("No Deal") ? null :
                Integer.parseInt(dealValue.replace("Deal ", "")));
    }

    private Books copyBook(Books book) {
        // Create a deep copy of the book
        Books copy = new Books(
                book.getBookId(),
                book.getTitle(),
                book.getAuthor(),
                book.getIsbn(),
                book.getOriginalPrice(),
                book.getDiscountedPrice(),
                book.getPublicationYear(),
                book.getLanguage(),
                book.getPageCount(),
                book.getAverageRating(),
                book.getRatingCount(),
                book.getDescription(),
                book.getCoverImageUrl(),
                book.getStockQuantity(),
                book.getDealId(),
                book.getReadingDifficulty(),
                book.getEstimatedReadingTime(),
                book.getContentRating()
        );
        return copy;
    }

    private void navigateToBookManager() {
        try {
            Stage stage = (Stage) btn_cancel.getScene().getWindow();
            Scene scene = SceneCache.getScene("/com/example/bibliotech/AdminBookManager.fxml");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to navigate back to Book Manager");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}