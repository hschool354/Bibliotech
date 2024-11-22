package com.example.bibliotech.presentation.control;

import com.example.bibliotech.presentation.Animation.SceneTransitionEffect;
import com.example.bibliotech.presentation.Animation.TypewriterEffect;
import com.example.bibliotech.utils.SceneCache;
import com.example.bibliotech.service.UserService;
import com.example.bibliotech.model.Users;
import com.example.bibliotech.exception.LoginException;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    @FXML private Button btn_SignIn, btn_Login, btn_forgotPassword;
    @FXML private Label sloganLabel;
    @FXML private TextField txt_Username;
    @FXML private PasswordField txt_Password;

    private final UserService userService;

    public LoginController() {
        this.userService = new UserService();
    }

    @FXML
    public void handleSignInButton() {
        changeScene("/com/example/bibliotech/sign_up.fxml");
    }

    @FXML
    public void handleLoginButton() {
        String username = txt_Username.getText();
        String password = txt_Password.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Please enter both username and password.");
            return;
        }

        try {
            Users user = userService.login(username, password);
            if (user != null) {
                String nextScene;

                // Check registration status first
                if ("PENDING".equals(user.getRegistrationStatus())) {
                    nextScene = "/com/example/bibliotech/profile_signUp.fxml";
                    System.out.println("Routing to Profile Setup - First Time Login");
                }
                // If registration is completed, route based on admin status
                else if (user.isAdmin()) {
                    nextScene = "/com/example/bibliotech/AdminDashboard.fxml";
                    System.out.println("Routing to Admin Dashboard");
                } else {
                    nextScene = "/com/example/bibliotech/home_1.fxml";
                    System.out.println("Routing to User Home");
                }

                // Log the routing decision
                System.out.println("User registration status: " + user.getRegistrationStatus());
                System.out.println("User is admin: " + user.isAdmin());
                System.out.println("Next scene: " + nextScene);

                changeScene(nextScene);
            }
        } catch (LoginException e) {
            showAlert("Login Failed", e.getMessage());
        }
    }

    @FXML
    public void handleForgotButton() {
        changeScene("/com/example/bibliotech/forgotPassword.fxml");
    }


    private void showAlert(String title, String content) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void changeScene(String fxmlPath) {
        try {
            Stage stage = (Stage) btn_SignIn.getScene().getWindow();
            Scene scene = SceneCache.getScene(fxmlPath);

            stage.setScene(scene);
            stage.setResizable(false);

            // Center the stage
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            stage.setX((screenBounds.getWidth() - stage.getWidth()) / 2 + screenBounds.getMinX());
            stage.setY((screenBounds.getHeight() - stage.getHeight()) / 2 + screenBounds.getMinY());

            // Apply transition effect
            SceneTransitionEffect.applyTransitionEffect((Pane) scene.getRoot());

            stage.show();
        } catch (IOException e) {
            System.err.println("Error loading scene: " + fxmlPath);
            e.printStackTrace();
            showAlert("Error", "Failed to load the next screen. Please try again.");
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        TypewriterEffect effect = new TypewriterEffect(
                "READ A BOOK, LIVE A\nTHOUSAND LIVES",
                sloganLabel,
                120,
                true
        );
        effect.play();
    }
}