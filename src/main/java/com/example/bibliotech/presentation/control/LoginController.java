package com.example.bibliotech.presentation.control;

import com.example.bibliotech.presentation.Animation.SceneTransitionEffect;
import com.example.bibliotech.presentation.Animation.TypewriterEffect;
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
    private Button btn_SignIn,btn_Login,btn_forgotPassword;

    @FXML
    private Label sloganLabel;

    @FXML
    private TextField txt_Username;

    @FXML
    public void handleSignInButton() throws IOException {
        System.out.println("Sign In button clicked!");
        // Lấy Stage hiện tại của form Login
        Stage stage = (Stage) btn_SignIn.getScene().getWindow();

        // Load FXML của form SignIn
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/bibliotech/sign_up.fxml")));

        // Tạo Scene mới cho SignIn
        Scene scene = new Scene(root);
        stage.setScene(scene);

        SceneTransitionEffect.applyTransitionEffect((Pane) root);

        // Hiển thị form SignIn
        stage.show();
    }

    @FXML
    public void handleLoginButton() throws IOException {

        System.out.println("Login button clicked!");

        String username = txt_Username.getText();

        Stage stage = (Stage) btn_Login.getScene().getWindow();
        Parent root;

        if(!username.isEmpty()){
            root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/bibliotech/profile_signUp.fxml")));
        }else {
            root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/bibliotech/home_1.fxml")));
        }

        Scene scene = new Scene(root);
        stage.setScene(scene);

        stage.setResizable(false);

        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX((screenBounds.getWidth() - stage.getWidth()) / 2 + screenBounds.getMinX());
        stage.setY((screenBounds.getHeight() - stage.getHeight()) / 2 + screenBounds.getMinY());

        stage.show();
    }

    @FXML
    public void handleForgotButton() throws IOException {
        System.out.println("Forgot Password button clicked!");

        Stage stage = (Stage) btn_forgotPassword.getScene().getWindow();

        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/bibliotech/forgotPassword.fxml")));

        Scene scene = new Scene(root);
        stage.setScene(scene);

        //SceneTransitionEffect.applyTransitionEffect((Pane) root);

        stage.show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        TypewriterEffect effect = new TypewriterEffect("READ A BOOK, LIVE A\n THOUSAND LIVES",sloganLabel, 120,true);
        effect.play();

    }

}

