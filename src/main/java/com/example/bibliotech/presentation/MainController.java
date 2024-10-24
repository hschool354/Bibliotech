package com.example.bibliotech.presentation;

import com.example.bibliotech.config.DatabaseConfig;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MainController {

    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        DatabaseConfig connectionNow = new DatabaseConfig();
        connectionNow.getConnection();

        welcomeText.setText("Welcome to JavaFX Application!");
    }
}