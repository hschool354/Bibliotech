package com.example.bibliotech.presentation.control;

import com.example.bibliotech.model.Books;
import com.example.bibliotech.presentation.Animation.SceneTransitionEffect;
import com.example.bibliotech.service.BookService;
import com.example.bibliotech.exception.BookServiceException;
import com.example.bibliotech.utils.SceneCache;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;

public class AdminAddBookController {
    @FXML private TextField txt_IBSN, txt_title, txt_author, txt_page_Count,
            txt_Estimated_Reading_Time, txt_Cover_Image_URL,
            txt_Original_Price, txt_Discounted_Price, txt_Stock_Quantity;
    @FXML private TextArea textArena_description;
    @FXML private ComboBox<String> comboBox_language, comboBox_Reading_Difficylty,
            comboBox_Content_Rating;
    @FXML private DatePicker datePicker_PublicationYear;
    @FXML private Button btn_Save, btn_Clear, btn_Cancel, btn_Home, btn_ChooseImage;

    private File selectedImageFile;
    private final BookService bookService;

    // Constants
    private static final String[] LANGUAGES = {
            "Vietnamese", "English", "French", "German", "Spanish",
            "Chinese", "Japanese", "Korean", "Other"
    };

    private static final String[] READING_DIFFICULTIES = {"EASY", "MEDIUM", "HARD"};
    private static final String[] CONTENT_RATINGS = {"EVERYONE", "TEEN", "MATURE"};

    public AdminAddBookController() {
        this.bookService = new BookService();
    }

    @FXML
    public void initialize() {
        initializeComboBoxes();
        setupTextFieldValidation();
        setupEventHandlers();
    }

    private void initializeComboBoxes() {
        comboBox_language.setItems(FXCollections.observableArrayList(LANGUAGES));
        comboBox_Reading_Difficylty.setItems(FXCollections.observableArrayList(READING_DIFFICULTIES));
        comboBox_Content_Rating.setItems(FXCollections.observableArrayList(CONTENT_RATINGS));
    }

    private void setupTextFieldValidation() {
        // ISBN validation (13 digits)
        txt_IBSN.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d{0,13}")) {
                txt_IBSN.setText(oldVal);
            }
        });

        // Page count validation (positive numbers only)
        txt_page_Count.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                txt_page_Count.setText(oldVal);
            }
        });

        // Reading time validation (positive numbers only)
        txt_Estimated_Reading_Time.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                txt_Estimated_Reading_Time.setText(oldVal);
            }
        });

        // Price validation (decimal numbers with 2 decimal places)
        txt_Original_Price.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*\\.?\\d{0,2}")) {
                txt_Original_Price.setText(oldVal);
            }
        });

        txt_Discounted_Price.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.isEmpty() && !newVal.matches("\\d*\\.?\\d{0,2}")) {
                txt_Discounted_Price.setText(oldVal);
            }
        });

        // Stock quantity validation (positive integers only)
        txt_Stock_Quantity.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                txt_Stock_Quantity.setText(oldVal);
            }
        });

        // Publication year validation
        datePicker_PublicationYear.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.getYear() < 1600 || date.isAfter(LocalDate.now()));
            }
        });
    }

    private boolean validateInputs() {
        if (txt_title.getText().trim().isEmpty() ||
                txt_author.getText().trim().isEmpty() ||
                txt_IBSN.getText().trim().isEmpty() ||
                txt_Original_Price.getText().isEmpty() ||
                txt_Stock_Quantity.getText().isEmpty() ||
                txt_page_Count.getText().isEmpty() ||
                txt_Estimated_Reading_Time.getText().isEmpty() ||
                comboBox_language.getValue() == null ||
                comboBox_Reading_Difficylty.getValue() == null ||
                comboBox_Content_Rating.getValue() == null ||
                datePicker_PublicationYear.getValue() == null) {
            return false;
        }

        // Validate ISBN length
        if (txt_IBSN.getText().length() != 13) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "ISBN must be 13 digits");
            return false;
        }

        // Validate prices
        double originalPrice = Double.parseDouble(txt_Original_Price.getText());
        if (originalPrice < 0) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Original price cannot be negative");
            return false;
        }

        if (!txt_Discounted_Price.getText().isEmpty()) {
            double discountedPrice = Double.parseDouble(txt_Discounted_Price.getText());
            if (discountedPrice < 0 || discountedPrice > originalPrice) {
                showAlert(Alert.AlertType.ERROR, "Validation Error",
                        "Discounted price must be between 0 and original price");
                return false;
            }
        }

        // Validate page count
        if (Integer.parseInt(txt_page_Count.getText()) <= 0) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Page count must be greater than 0");
            return false;
        }

        return true;
    }

    private void setupEventHandlers() {
        btn_ChooseImage.setOnAction(e -> handleImageSelection());
        btn_Save.setOnAction(e -> handleSaveBook());
        btn_Clear.setOnAction(e -> clearForm());
        btn_Cancel.setOnAction(e -> handleCancel());
    }

    private void handleImageSelection() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Book Cover Image");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        File file = fileChooser.showOpenDialog(btn_ChooseImage.getScene().getWindow());
        if (file != null) {
            selectedImageFile = file;
            txt_Cover_Image_URL.setText(file.getName());
        }
    }

    private void handleSaveBook() {
        if (!validateInputs()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please fill in all required fields.");
            return;
        }

        try {
            Books book = createBookFromInputs();
            boolean success = bookService.addBook(book, null, selectedImageFile);

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Book has been successfully added!");
                clearForm();
            }
        } catch (BookServiceException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to add book: " + e.getMessage());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Please enter valid numbers for numeric fields.");
        }
    }

    private Books createBookFromInputs() {
        Double discountedPrice = txt_Discounted_Price.getText().isEmpty() ?
                null : Double.parseDouble(txt_Discounted_Price.getText());

        LocalDate publicationDate = datePicker_PublicationYear.getValue();
        int publicationYear = publicationDate.getYear();

        return new Books(
                0, // book_id will be generated
                txt_title.getText().trim(),
                txt_author.getText().trim(),
                txt_IBSN.getText().trim(),
                Double.parseDouble(txt_Original_Price.getText()),
                discountedPrice,
                publicationYear,
                comboBox_language.getValue(),
                Integer.parseInt(txt_page_Count.getText()),
                0.0, // average rating starts at 0
                0,   // rating count starts at 0
                textArena_description.getText().trim(),
                "", // cover image URL will be set by service
                Integer.parseInt(txt_Stock_Quantity.getText()),
                null, // deal ID is null by default
                comboBox_Reading_Difficylty.getValue(),
                Integer.parseInt(txt_Estimated_Reading_Time.getText()),
                comboBox_Content_Rating.getValue()
        );
    }

    private void handleCancel() {
        changeScene("/com/example/bibliotech/AdminBookManager.fxml");
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void clearForm() {
        txt_title.clear();
        txt_author.clear();
        txt_IBSN.clear();
        txt_Original_Price.clear();
        txt_Discounted_Price.clear();
        txt_Stock_Quantity.clear();
        txt_page_Count.clear();
        txt_Estimated_Reading_Time.clear();
        txt_Cover_Image_URL.clear();
        textArena_description.clear();
        datePicker_PublicationYear.setValue(null);
        comboBox_language.setValue(null);
        comboBox_Reading_Difficylty.setValue(null);
        comboBox_Content_Rating.setValue(null);
        selectedImageFile = null;
    }

    private void changeScene(String fxmlPath) {
        try {
            Stage stage = (Stage) btn_Cancel.getScene().getWindow();
            Scene scene = SceneCache.getScene(fxmlPath);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Error loading scene: " + fxmlPath);
            e.printStackTrace();
        }
    }
}