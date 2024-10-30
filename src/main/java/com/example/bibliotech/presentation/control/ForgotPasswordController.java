package com.example.bibliotech.presentation.control;

import com.example.bibliotech.config.DatabaseConfig;
import com.example.bibliotech.service.EmailService;
import com.example.bibliotech.service.OTPService;
import com.example.bibliotech.utils.BaseController;
import com.example.bibliotech.utils.SceneCache;
import com.example.bibliotech.utils.SceneTransitionEffect;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import javax.mail.MessagingException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public class ForgotPasswordController extends BaseController {
    @FXML private Button btn_Send, btn_Back;
    @FXML private TextField txt_Email;
    private static final Logger logger = Logger.getLogger(ForgotPasswordController.class.getName());

    private static int currentUserId;
    public static int getCurrentUserId() { return currentUserId; }

    private boolean userExists(Connection conn, String email) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    private int getUserId(Connection conn, String email) throws SQLException {
        String sql = "SELECT user_id FROM users WHERE email = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("user_id");
                }
                throw new SQLException("User not found with email: " + email);
            }
        }
    }

    @FXML
    public void handleSendButton() {
        if (!validateInput()) return;

        String email = txt_Email.getText().trim();

        // Disable nút gửi để tránh gửi nhiều lần
        btn_Send.setDisable(true);

        CompletableFuture.runAsync(() -> {
            try (Connection conn = DatabaseConfig.getConnection()) {
                if (!userExists(conn, email)) {
                    Platform.runLater(() -> {
                        showAlert("Error", "Email not found in our system.", Alert.AlertType.ERROR);
                        btn_Send.setDisable(false);
                    });
                    return;
                }

                String otp = OTPService.generateOTP();
                currentUserId = getUserId(conn, email);

                OTPService.saveOTP(conn, currentUserId, otp);
                EmailService.sendOTP(email, otp);

                Platform.runLater(() -> {
                    btn_Send.setDisable(false);
                    changeScene("/com/example/bibliotech/verifyEmail.fxml", btn_Send);
                });

            } catch (SQLException e) {
                logger.severe("Database error while processing forgot password: " + e.getMessage());
                Platform.runLater(() -> {
                    showAlert("Error", "Database error occurred. Please try again.", Alert.AlertType.ERROR);
                    btn_Send.setDisable(false);
                });
            } catch (MessagingException e) {
                logger.severe("Failed to send email: " + e.getMessage());
                Platform.runLater(() -> {
                    showAlert("Error", "Failed to send email. Please try again.", Alert.AlertType.ERROR);
                    btn_Send.setDisable(false);
                });
            } catch (Exception e) {
                logger.severe("Unexpected error: " + e.getMessage());
                Platform.runLater(() -> {
                    showAlert("Error", "An unexpected error occurred.", Alert.AlertType.ERROR);
                    btn_Send.setDisable(false);
                });
            }
        });
    }

    private boolean validateInput() {
        String email = txt_Email.getText().trim();
        if (email.isEmpty()) {
            showAlert("Validation Error", "Please enter your email address.", Alert.AlertType.WARNING);
            return false;
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showAlert("Validation Error", "Please enter a valid email address.", Alert.AlertType.WARNING);
            return false;
        }
        return true;
    }

    @FXML
    public void handleBackButton() {
        changeScene("/com/example/bibliotech/login.fxml", btn_Back);
    }
}