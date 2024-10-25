package com.example.bibliotech.presentation.control;

import com.example.bibliotech.presentation.Animation.SceneTransitionEffect;
import com.example.bibliotech.presentation.Animation.TypewriterEffect;
import com.example.bibliotech.utils.SceneCache;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML
    private Button btn_SignIn, btn_Login, btn_forgotPassword;

    @FXML
    private Label sloganLabel;

    @FXML
    private TextField txt_Username;

    @FXML
    public void handleSignInButton() {
        changeScene("/com/example/bibliotech/sign_up.fxml");
    }

    @FXML
    public void handleLoginButton() {
        String username = txt_Username.getText();
        String fxmlPath = username.isEmpty()
                ? "/com/example/bibliotech/home_1.fxml"
                : "/com/example/bibliotech/profile_signUp.fxml";

        changeScene(fxmlPath);
    }

    @FXML
    public void handleForgotButton() {
        changeScene("/com/example/bibliotech/forgotPassword.fxml");
    }

    private void changeScene(String fxmlPath) {
        try {
            // Lấy stage hiện tại
            Stage stage = (Stage) btn_SignIn.getScene().getWindow();

            // Lấy Scene từ SceneCache
            Scene scene = SceneCache.getScene(fxmlPath);

            // Thiết lập Scene mới
            stage.setScene(scene);
            stage.setResizable(false);

            // Căn giữa stage
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            stage.setX((screenBounds.getWidth() - stage.getWidth()) / 2 + screenBounds.getMinX());
            stage.setY((screenBounds.getHeight() - stage.getHeight()) / 2 + screenBounds.getMinY());

            // Áp dụng hiệu ứng chuyển cảnh
            SceneTransitionEffect.applyTransitionEffect((Pane) scene.getRoot());

            stage.show();
        } catch (IOException e) {
            System.err.println("Error loading scene: " + fxmlPath);
            e.printStackTrace(); // In ra lỗi để tiện theo dõi
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        TypewriterEffect effect = new TypewriterEffect("READ A BOOK, LIVE A\nTHOUSAND LIVES", sloganLabel, 120, true);
        effect.play();
    }
}
