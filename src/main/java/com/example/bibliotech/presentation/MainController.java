package com.example.bibliotech.presentation;

import com.example.bibliotech.dao.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MainController {

    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        DatabaseConnection connectionNow = new DatabaseConnection();
        connectionNow.getConnection();

        welcomeText.setText("Welcome to JavaFX Application!");
    }
}