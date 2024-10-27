package com.example.bibliotech.presentation.control;

import com.example.bibliotech.utils.SceneCache;
import com.example.bibliotech.service.UserService;
import com.example.bibliotech.utils.SessionManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.*;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.scene.control.Alert.AlertType;


import javafx.scene.input.KeyEvent;
import java.net.URL;
import java.sql.Date;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class ProfileSignUpController implements Initializable {

    @FXML private TextField txt_firstName, txt_lastName, txt_phone, txt_address;
    @FXML private TextArea txtArena_bio;
    @FXML private DatePicker datePicker_dob;
    @FXML private ComboBox<String> comboBox_nationality;
    @FXML private CheckBox checkBox_Male, checkBox_Female, checkBox_Other;
    @FXML private Button btn_Save, btn_skipForNow;
    @FXML private Label lbl_firstName_error, lbl_lastName_error,lbl_phone_error, lbl_nationality_error;

    private UserService userService;

    public ProfileSignUpController() {
        this.userService = new UserService();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadCountries();
        setupGenderCheckBoxes();
        setupValidationLabels();
        setupRequiredFieldIndicators();
        setupTextFieldRestrictions();
    }

    private void setupTextFieldRestrictions() {
        txt_firstName.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            if (!event.getCharacter().matches("[a-zA-Z]")) {
                event.consume();
            }
        });

        txt_lastName.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            if (!event.getCharacter().matches("[a-zA-Z]")) {
                event.consume();
            }
        });

        txt_phone.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            if (!event.getCharacter().matches("[0-9]")) {
                event.consume();
            }
        });
    }

    private void setupValidationLabels() {
        // Create and style error labels
        lbl_firstName_error.setVisible(false);
        lbl_firstName_error.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        lbl_lastName_error.setVisible(false);
        lbl_lastName_error.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        lbl_phone_error.setVisible(false);
        lbl_phone_error.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        lbl_nationality_error.setVisible(false);
        lbl_nationality_error.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
    }



    private void loadCountries() {
        List<String> countries = Arrays.asList(
                "Vietnam", "United States", "United Kingdom", "Japan", "China",
                "South Korea", "Singapore", "Malaysia", "Thailand", "Indonesia",
                "Australia", "Canada", "France", "Germany", "Italy", "Other"
        );
        comboBox_nationality.setItems(FXCollections.observableArrayList(countries));
    }

    private void setupGenderCheckBoxes() {
        checkBox_Male.setOnAction(e -> {
            if (checkBox_Male.isSelected()) {
                checkBox_Female.setSelected(false);
                checkBox_Other.setSelected(false);
            }
        });

        checkBox_Female.setOnAction(e -> {
            if (checkBox_Female.isSelected()) {
                checkBox_Male.setSelected(false);
                checkBox_Other.setSelected(false);
            }
        });

        checkBox_Other.setOnAction(e -> {
            if (checkBox_Other.isSelected()) {
                checkBox_Male.setSelected(false);
                checkBox_Female.setSelected(false);
            }
        });
    }
    private void setupRequiredFieldIndicators() {
        // Add red asterisk to required fields
        txt_firstName.setPromptText("First Name *");
        txt_lastName.setPromptText("Last Name *");
        txt_phone.setPromptText("Phone Number *");
        comboBox_nationality.setPromptText("Select Nationality *");
    }

    @FXML
    private void handleSave() {
        try {
            // Clear previous error messages
            clearValidationErrors();

            // Validate required fields
            if (!validateRequiredFields()) {
                return;
            }

            SessionManager sessionManager = SessionManager.getInstance();
            if (!sessionManager.isLoggedIn()) {
                showAlert(AlertType.ERROR, "Error", "No user logged in. Please login again.");
                return;
            }

            String firstName = txt_firstName.getText().trim();
            String lastName = txt_lastName.getText().trim();
            String fullName = firstName + " " + lastName;

            String phone = txt_phone.getText().trim();
            String address = txt_address.getText().trim();
            String nationality = comboBox_nationality.getValue();
            String bio = txtArena_bio.getText().trim();
            String gender = getSelectedGender();

            if (!isValidPhoneNumber(phone)) {
                lbl_phone_error.setText("Invalid phone number format (10-15 digits)");
                lbl_phone_error.setVisible(true);
                txt_phone.requestFocus();
                return;
            }

            Date dob = null;
            if (datePicker_dob.getValue() != null) {
                dob = Date.valueOf(datePicker_dob.getValue());
            }

            // Update user profile with the concatenated full name
            userService.updateUserProfile(
                    sessionManager.getCurrentUserId(),
                    fullName,
                    phone,
                    dob,
                    gender,
                    address.isEmpty() ? null : address,
                    nationality,
                    bio.isEmpty() ? null : bio
            );

            showSuccessAndNavigate();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Error", "Failed to save profile: " + e.getMessage());
        }
    }



    private boolean validateRequiredFields() {
        boolean isValid = true;

        // Validate First Name
        if (txt_firstName.getText().trim().isEmpty()) {
            lbl_firstName_error.setText("First name is required");
            lbl_firstName_error.setVisible(true);
            isValid = false;
        }

        // Validate Phone
        if (txt_phone.getText().trim().isEmpty()) {
            lbl_phone_error.setText("Phone number is required");
            lbl_phone_error.setVisible(true);
            isValid = false;
        }

        // Validate Nationality
        if (comboBox_nationality.getValue() == null) {
            lbl_nationality_error.setText("Please select your nationality");
            lbl_nationality_error.setVisible(true);
            isValid = false;
        }

        return isValid;
    }

    private boolean isValidPhoneNumber(String phone) {
        return phone.matches("^[0-9]{10,15}$");
    }

    private void clearValidationErrors() {
        lbl_firstName_error.setVisible(false);
        lbl_phone_error.setVisible(false);
        lbl_nationality_error.setVisible(false);
    }

    @FXML
    private void handleSkipForNow() {
        // Show confirmation dialog before skipping
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Skip Profile Setup");
        alert.setHeaderText("Are you sure you want to skip?");
        alert.setContentText("You can always update your profile later from the settings menu.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    SessionManager sessionManager = SessionManager.getInstance();
                    if (sessionManager.isLoggedIn()) {
                        userService.skipProfileUpdate(sessionManager.getCurrentUserId());
                        changeScene("/com/example/bibliotech/home_1.fxml");
                    }
                } catch (Exception e) {
                    showAlert(AlertType.ERROR, "Error", "Failed to skip: " + e.getMessage());
                }
            }
        });
    }

    private void showSuccessAndNavigate() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText("Profile updated successfully!");
        alert.showAndWait();

        changeScene("/com/example/bibliotech/home_1.fxml");
    }

    private void showAlert(AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void changeScene(String fxmlPath) {
        try {
            Stage stage = (Stage) btn_Save.getScene().getWindow();
            Scene scene = SceneCache.getScene(fxmlPath);

            stage.setScene(scene);
            stage.setResizable(false);

            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            stage.setX((screenBounds.getWidth() - stage.getWidth()) / 2 + screenBounds.getMinX());
            stage.setY((screenBounds.getHeight() - stage.getHeight()) / 2 + screenBounds.getMinY());

            stage.show();
        } catch (Exception e) {
            System.err.println("Error loading scene: " + fxmlPath);
            e.printStackTrace();
        }
    }

    private String getSelectedGender() {
        if (checkBox_Male.isSelected()) return "Male";
        if (checkBox_Female.isSelected()) return "Female";
        if (checkBox_Other.isSelected()) return "Other";
        return "Not specified";
    }

}
