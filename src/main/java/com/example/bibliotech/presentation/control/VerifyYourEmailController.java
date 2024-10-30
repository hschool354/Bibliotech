package com.example.bibliotech.presentation.control;

import com.example.bibliotech.config.DatabaseConfig;
import com.example.bibliotech.service.OTPService;
import com.example.bibliotech.utils.BaseController;
import com.example.bibliotech.utils.SceneCache;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class VerifyYourEmailController extends BaseController {
    @FXML private Button btn_Verify, btn_Back;
    @FXML private TextField txt_code1, txt_code2, txt_code3, txt_code4, txt_code5, txt_code6;
    private List<TextField> codeFields;
    private static final Logger logger = Logger.getLogger(VerifyYourEmailController.class.getName());

    private static final int OTP_LENGTH = 6;
    private static final long OTP_TIMEOUT_MINUTES = 5;

    @FXML
    public void initialize() {
        codeFields = Arrays.asList(txt_code1, txt_code2, txt_code3, txt_code4, txt_code5, txt_code6);
        setupOTPFields();
    }

    private void setupOTPFields() {
        for (int i = 0; i < codeFields.size(); i++) {
            final int index = i;
            TextField field = codeFields.get(i);

            // Number only validation
            field.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.matches("\\d*")) {
                    field.setText(newValue.replaceAll("[^\\d]", ""));
                }
                if (newValue.length() > 1) {
                    field.setText(newValue.substring(0, 1));
                }
                // Auto focus next field
                if (newValue.length() == 1 && index < codeFields.size() - 1) {
                    codeFields.get(index + 1).requestFocus();
                }
            });

            // Handle backspace to go to previous field
            field.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.BACK_SPACE && field.getText().isEmpty() && index > 0) {
                    codeFields.get(index - 1).requestFocus();
                }
            });
        }
    }

    @FXML
    public void handleVerifyButton() {
        String otp = getEnteredOTP();
        if (!validateOTP(otp)) return;

        CompletableFuture.runAsync(() -> {
            try (Connection conn = DatabaseConfig.getConnection()) {
                int userId = ForgotPasswordController.getCurrentUserId();
                if (userId <= 0) {
                    throw new IllegalStateException("Invalid user session");
                }

                if (OTPService.verifyOTP(conn, userId, otp)) {
                    Platform.runLater(() -> changeScene("/com/example/bibliotech/createpassword.fxml", btn_Verify));
                } else {
                    showAlert("Verification Failed", "Invalid or expired code. Please try again.", Alert.AlertType.ERROR);
                }
            } catch (Exception e) {
                logger.severe("Error during OTP verification: " + e.getMessage());
                showAlert("Error", "Verification failed. Please try again.", Alert.AlertType.ERROR);
            }
        });
    }

    private String getEnteredOTP() {
        return codeFields.stream()
                .map(TextField::getText)
                .collect(Collectors.joining());
    }

    private boolean validateOTP(String otp) {
        if (otp.length() != OTP_LENGTH) {
            showAlert("Invalid Code", "Please enter all " + OTP_LENGTH + " digits.", Alert.AlertType.WARNING);
            return false;
        }
        return true;
    }

    @FXML
    public void handleBackButton() {
        changeScene("/com/example/bibliotech/forgotPassword.fxml", btn_Back);
    }
}