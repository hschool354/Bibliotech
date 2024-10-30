package com.example.bibliotech.presentation.control;

import com.example.bibliotech.DTO.ProfileSettingDTO;
import com.example.bibliotech.exception.DatabaseException;
import com.example.bibliotech.exception.UserServiceException;
import com.example.bibliotech.model.Users;
import com.example.bibliotech.service.UserService;
import com.example.bibliotech.utils.SessionManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;
import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;

public class AccountSettingProfile {
    @FXML private TextField txt_profilePictureURL, txt_userID, txt_userName,
            txt_firstName, txt_lastName, txt_phone, txt_address;
    @FXML private TextArea txtArea_bio;
    @FXML private Button btn_saveChanges, btn_ChooseImage;
    @FXML private DatePicker datePicker_dob;
    @FXML private ComboBox<String> comboBox_nationality;
    @FXML private ImageView image_ProfilePicture;

    private Users currentUser;
    private UserService userService;
    private File selectedImageFile;

    public AccountSettingProfile() {
        userService = new UserService();
    }

    @FXML
    public void initialize() {
        loadCountries();
        loadUserFromSession();
        setupEventHandlers();
    }

    private void setupEventHandlers() {
        btn_ChooseImage.setOnAction(event -> handleChooseImage());
        btn_saveChanges.setOnAction(event -> handleSaveChanges());
    }

    private void loadCountries() {
        List<String> countries = Arrays.asList(
                "Vietnam", "United States", "United Kingdom", "Japan", "China",
                "South Korea", "Singapore", "Malaysia", "Thailand", "Indonesia",
                "Australia", "Canada", "France", "Germany", "Italy", "Other"
        );
        comboBox_nationality.setItems(FXCollections.observableArrayList(countries));
        comboBox_nationality.setValue("Other"); // Set giá trị mặc định
    }

    private void loadUserFromSession() {
        try {
            Users sessionUser = SessionManager.getInstance().getCurrentUser();
            if (sessionUser == null) {
                showAlert(Alert.AlertType.ERROR, "Error", "No user session found!");
                return;
            }

            // Load full user data from database using session user's ID
            try {
                currentUser = userService.loadSettingProfile(sessionUser.getUserId());
                if (currentUser == null) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to load user data from database!");
                    return;
                }
            } catch (DatabaseException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Database error: " + e.getMessage());
                return;
            }

            populateFormFields();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load user data: " + e.getMessage());
        }
    }

    private void populateFormFields() {
        if (currentUser != null) {
            txt_userID.setText(String.valueOf(currentUser.getUserId()));
            txt_userName.setText(currentUser.getUsername());

            // Split full name into first and last name
            String fullName = currentUser.getFullName();
            if (fullName != null && fullName.contains(" ")) {
                String[] names = fullName.split(" ", 2);
                txt_firstName.setText(names[0]);
                txt_lastName.setText(names[1]);
            } else {
                txt_firstName.setText(fullName);
                txt_lastName.setText("");
            }

            txt_phone.setText(currentUser.getPhone());
            txt_address.setText(currentUser.getAddress());
            txtArea_bio.setText(currentUser.getBio());
            txt_profilePictureURL.setText(currentUser.getProfilePictureUrl());

            // Set date picker
            // Thay đổi đoạn code này trong phương thức populateFormFields()
            if (currentUser.getDob() != null) {
                LocalDate dob = ((java.sql.Date) currentUser.getDob()).toLocalDate();
                datePicker_dob.setValue(dob);
            }

            if (currentUser.getNationality() != null && !currentUser.getNationality().isEmpty()) {
                // Kiểm tra xem giá trị có trong danh sách không
                if (comboBox_nationality.getItems().contains(currentUser.getNationality())) {
                    comboBox_nationality.setValue(currentUser.getNationality());
                } else {
                    comboBox_nationality.setValue("Other");
                }
            }
            comboBox_nationality.setValue(currentUser.getNationality());

            // Load profile picture
            loadProfilePicture();
        }
    }

    private void loadProfilePicture() {
        String profilePicUrl = currentUser.getProfilePictureUrl();
        if (profilePicUrl != null && !profilePicUrl.isEmpty()) {
            try {
                File imageFile = new File("src/main/resources/Profile_Picture/" + profilePicUrl);
                if (imageFile.exists()) {
                    Image image = new Image(imageFile.toURI().toString());
                    image_ProfilePicture.setImage(image);
                }
            } catch (Exception e) {
                System.err.println("Failed to load profile picture: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleChooseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Profile Picture");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        File file = fileChooser.showOpenDialog(btn_ChooseImage.getScene().getWindow());
        if (file != null) {
            selectedImageFile = file;
            txt_profilePictureURL.setText(file.getName());

            // Preview the selected image
            Image image = new Image(file.toURI().toString());
            image_ProfilePicture.setImage(image);
        }
    }

    @FXML
    private void handleSaveChanges() {
        try {
            updateUserFromForm();
            userService.updateProfileSetting(currentUser, selectedImageFile);

            showAlert(Alert.AlertType.INFORMATION, "Success", "Profile updated successfully!");

            // Refresh the session user
            SessionManager.getInstance().setCurrentUser(currentUser);

        } catch (UserServiceException | IllegalArgumentException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to save changes: " + e.getMessage());
        }
    }

    private void updateUserFromForm() {
        if (txt_firstName.getText().trim().isEmpty() || txt_lastName.getText().trim().isEmpty()) {
            throw new IllegalArgumentException("First name and last name are required");
        }

        currentUser.setFullName(txt_firstName.getText().trim() + " " + txt_lastName.getText().trim());
        currentUser.setPhone(txt_phone.getText());

        // Update date handling
        if (datePicker_dob.getValue() != null) {
            java.sql.Date sqlDate = java.sql.Date.valueOf(datePicker_dob.getValue());
            currentUser.setDob(sqlDate);
        }

        currentUser.setAddress(txt_address.getText());
        currentUser.setNationality(comboBox_nationality.getValue());
        currentUser.setBio(txtArea_bio.getText());
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}