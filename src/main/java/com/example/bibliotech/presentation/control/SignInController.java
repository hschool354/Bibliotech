package com.example.bibliotech.presentation.control;

import com.example.bibliotech.presentation.Animation.SceneTransitionEffect;
import com.example.bibliotech.presentation.Animation.TypewriterEffect;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class SignInController implements Initializable {
    @FXML
    private Button btn_Login;

    @FXML
    private Label sloganLabel;

    @FXML
    public void handleLoginButton() throws IOException {
        System.out.println("Login In button clicked!");

        Stage stage = (Stage) btn_Login.getScene().getWindow();

        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/bibliotech/login.fxml")));

        Scene scene = new Scene(root);

        stage.setScene(scene);

        SceneTransitionEffect.applyTransitionEffect((Pane) root);

        stage.show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        TypewriterEffect effect = new TypewriterEffect("READ A BOOK, LIVE A\n THOUSAND LIVES",sloganLabel, 120,true);
        effect.play();
    }
}