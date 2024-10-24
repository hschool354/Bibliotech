package com.example.bibliotech.presentation.control;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class VerifyYourEmailController {
    @FXML
    private Button btn_Verify, btn_Back;

    @FXML
    public void handleVerifyButton() {
        changeScene("/com/example/bibliotech/createpassword.fxml");
    }

    @FXML
    public void handleBackButton() {
        changeScene("/com/example/bibliotech/forgotPassword.fxml");
    }

    private void changeScene(String fxmlPath) {
        try {
            Stage stage = (Stage) btn_Verify.getScene().getWindow();
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(fxmlPath)));

            Scene scene = new Scene(root);
            stage.setScene(scene);

            //SceneTransitionEffect.applyTransitionEffect((Pane) root); // Uncomment if you want to use transition effects

            stage.show();
        } catch (IOException e) {
            System.err.println("Error loading scene: " + fxmlPath);
            e.printStackTrace(); // In ra lỗi để tiện theo dõi
        }
    }
}
