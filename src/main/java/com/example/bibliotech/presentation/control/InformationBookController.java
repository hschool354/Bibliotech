package com.example.bibliotech.presentation.control;

import com.example.bibliotech.presentation.components.RatingStarsHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class InformationBookController implements Initializable {

    @FXML
    private HBox starsContainer;

    @FXML
    private Button btn_Back;

    private RatingStarsHandler ratingHandler;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ratingHandler = new RatingStarsHandler(starsContainer);
    }

    // Phương thức này có thể dùng sau để lấy rating
    public int getCurrentRating() {
        return ratingHandler.getSelectedRating();
    }

    @FXML
    public void handleBackButton() {
        changeScene("/com/example/bibliotech/home_1.fxml");
    }

    private void changeScene(String fxmlPath) {
        try {
            Stage stage = (Stage) btn_Back.getScene().getWindow();
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(fxmlPath)));

            Scene scene = new Scene(root);
            stage.setScene(scene);
            //SceneTransitionEffect.applyTransitionEffect((Pane) root);
            stage.show();
        } catch (IOException e) {
            System.err.println("Error loading scene: " + fxmlPath);
            e.printStackTrace(); // In ra lỗi để tiện theo dõi
        }
    }
}
