package com.example.bibliotech.presentation.control;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class CreateNewPasswordController {
    @FXML
    private Button btn_Save,btn_Back;

    @FXML
    public void handleSaveButton() throws IOException {
        Stage stage = (Stage) btn_Save.getScene().getWindow();

        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/bibliotech/login.fxml")));

        Scene scene = new Scene(root);
        stage.setScene(scene);

        //SceneTransitionEffect.applyTransitionEffect((Pane) root);

        stage.show();
    }

    @FXML
    public void handleBackButton() throws IOException {
        Stage stage = (Stage) btn_Back.getScene().getWindow();

        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/bibliotech/verifyEmail.fxml")));

        Scene scene = new Scene(root);
        stage.setScene(scene);

        //SceneTransitionEffect.applyTransitionEffect((Pane) root);

        stage.show();
    }
}
