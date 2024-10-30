package com.example.bibliotech.presentation.control;

import com.example.bibliotech.config.DatabaseConfig;
import com.example.bibliotech.utils.BaseController;
import com.example.bibliotech.utils.PasswordUtil;
import com.example.bibliotech.utils.SceneCache;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class CreateNewPasswordController extends BaseController {
    @FXML private Button btn_Save, btn_Back;
    @FXML private TextField txt_Password, txt_confirmPassword;
    private static final Logger logger = Logger.getLogger(CreateNewPasswordController.class.getName());

    private static final String PASSWORD_PATTERN = "^.{6,}$";
    private static final Pattern pattern = Pattern.compile(PASSWORD_PATTERN);

    @FXML
    public void handleSaveButton() {
        if (!validatePasswords()) return;

        CompletableFuture.runAsync(() -> {
            try (Connection conn = DatabaseConfig.getConnection()) {
                int userId = ForgotPasswordController.getCurrentUserId();
                if (userId <= 0) {
                    throw new IllegalStateException("Invalid user session");
                }

                String hashedPassword = PasswordUtil.hashPassword(txt_Password.getText());
                updateUserPassword(conn, userId, hashedPassword);

                Platform.runLater(() -> {
                    showAlert("Success", "Password updated successfully!", Alert.AlertType.INFORMATION);
                    changeScene("/com/example/bibliotech/login.fxml", btn_Save);
                });
            } catch (Exception e) {
                logger.severe("Error updating password: " + e.getMessage());
                showAlert("Error", "Failed to update password. Please try again.", Alert.AlertType.ERROR);
            }
        });
    }

    private boolean validatePasswords() {
        String password = txt_Password.getText();
        String confirmPassword = txt_confirmPassword.getText();

        if (password.isEmpty() || confirmPassword.isEmpty()) {
            showAlert("Validation Error", "Please fill in both password fields.", Alert.AlertType.WARNING);
            return false;
        }

        if (!pattern.matcher(password).matches()) {
            showAlert("Invalid Password",
                    "Password must be at least 8 characters long and contain both letters and numbers.",
                    Alert.AlertType.WARNING);
            return false;
        }

        if (!password.equals(confirmPassword)) {
            showAlert("Password Mismatch", "Passwords do not match.", Alert.AlertType.WARNING);
            return false;
        }

        return true;
    }

    private void updateUserPassword(Connection conn, int userId, String hashedPassword) throws SQLException {
        String sql = "UPDATE Users SET password = ?, updated_at = CURRENT_TIMESTAMP WHERE user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, hashedPassword);
            stmt.setInt(2, userId);
            int updated = stmt.executeUpdate();
            if (updated != 1) {
                throw new SQLException("Failed to update password - no rows affected");
            }
        }
    }

    @FXML
    public void handleBackButton() {
        changeScene("/com/example/bibliotech/verifyEmail.fxml", btn_Back);
    }
}
