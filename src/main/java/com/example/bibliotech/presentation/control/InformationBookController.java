package com.example.bibliotech.presentation.control;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class InformationBookController implements Initializable  {

    @FXML
    private HBox starsContainer;
    private RatingStarsHandler ratingHandler;

    @FXML
    private Button btn_Back;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ratingHandler = new RatingStarsHandler(starsContainer);
    }

    // Phương thức này có thể dùng sau để lấy rating
    public int getCurrentRating() {
        return ratingHandler.getSelectedRating();
    }

    @FXML
    public void handleBackButton() throws IOException {
        Stage stage = (Stage) btn_Back.getScene().getWindow();

        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/bibliotech/home_1.fxml")));

        Scene scene = new Scene(root);
        stage.setScene(scene);

        //SceneTransitionEffect.applyTransitionEffect((Pane) root);

        stage.show();
    }
}
