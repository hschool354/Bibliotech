package com.example.bibliotech.presentation.control;

import com.example.bibliotech.exception.DatabaseException;
import com.example.bibliotech.model.Users;
import com.example.bibliotech.service.UserService;
import com.example.bibliotech.utils.SceneCache;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class AdminAddAccountController {
    @FXML private TextField txt_UserName, txt_Email, txt_FirstName, txt_LastName, txt_Phone, txt_Address;
    @FXML private PasswordField passwordField_Password, passwordField_ConfirmPassword;
    @FXML private DatePicker datePicker_Dob;
    @FXML private RadioButton radioButton_Male, radioButton_Female, radioButton_Other;
    @FXML private ComboBox<String> comboBox_Nationality;
    @FXML private Button btn_Save, btn_Clear, btn_Cancel, btn_Home, btn_Book;

    private final UserService userService;

    public AdminAddAccountController() {
        this.userService = new UserService();
    }

    @FXML
    public void initialize() {
        loadCountries();
    }

    @FXML
    public void handleAddBookButton() {
        changeScene("/com/example/bibliotech/AdminBookManager.fxml");
    }

    @FXML
    public void handleHomeButton() {
        changeScene("/com/example/bibliotech/AdminDashboard.fxml");
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

    private Users createAdminUser() {
        Users admin = new Users();
        admin.setUsername(txt_UserName.getText().trim());
        admin.setEmail(txt_Email.getText().trim());
        admin.setFullName(txt_FirstName.getText().trim() + " " + txt_LastName.getText().trim());
        admin.setPassword(passwordField_Password.getText().trim());
        admin.setDob(Date.valueOf(datePicker_Dob.getValue()));
        admin.setGender(getSelectedGender());
        admin.setNationality(comboBox_Nationality.getSelectionModel().getSelectedItem());
        admin.setRegistrationStatus("COMPLETED");
        return admin;
    }

    @FXML
    private void handleSaveButton() {
        if (validateInputs()) {
            try {
                Users admin = createAdminUser();
                userService.addAdmin(admin);
                showSuccessMessage("Admin user created successfully!");
                clearFields();
            } catch (DatabaseException e) {
                showErrorMessage("Error creating admin user: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleClearButton() {
        clearFields();
    }

    @FXML
    private void handleCancelButton() {
        changeScene("/com/example/bibliotech/AdminAccountManager.fxml");
    }

    private void clearFields() {
        txt_UserName.clear();
        txt_Email.clear();
        txt_FirstName.clear();
        txt_LastName.clear();
        passwordField_Password.clear();
        passwordField_ConfirmPassword.clear();
        datePicker_Dob.setValue(null);
        radioButton_Male.setSelected(false);
        radioButton_Female.setSelected(false);
        radioButton_Other.setSelected(false);
        comboBox_Nationality.getSelectionModel().clearSelection();
    }

    private boolean validateInputs() {
        String username = txt_UserName.getText().trim();
        String email = txt_Email.getText().trim();
        String firstName = txt_FirstName.getText().trim();
        String lastName = txt_LastName.getText().trim();
        String password = passwordField_Password.getText().trim();
        String confirmPassword = passwordField_ConfirmPassword.getText().trim();
        LocalDate dob = datePicker_Dob.getValue();
        String gender = getSelectedGender();
        String nationality = comboBox_Nationality.getSelectionModel().getSelectedItem();

        if (username.isEmpty() || email.isEmpty() || firstName.isEmpty() || lastName.isEmpty() ||
                password.isEmpty() || confirmPassword.isEmpty() || dob == null || gender == null ||
                nationality == null) {
            showErrorMessage("All fields must be filled!");
            return false;
        }

        if (!password.equals(confirmPassword)) {
            showErrorMessage("Passwords do not match!");
            return false;
        }

        return true;
    }

    private String getSelectedGender() {
        if (radioButton_Male.isSelected()) return "Male";
        if (radioButton_Female.isSelected()) return "Female";
        return "Other";
    }

    private void loadCountries() {
        List<String> countries = Arrays.asList("Country1", "Country2", "Country3");
        comboBox_Nationality.setItems(FXCollections.observableArrayList(countries));
    }

    private void showSuccessMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
        alert.showAndWait();
    }

    private void showErrorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.showAndWait();
    }
}
