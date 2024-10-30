package com.example.bibliotech.utils;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;

public abstract class BaseController {
    protected void showAlert(String title, String content, Alert.AlertType type) {
        Platform.runLater(() -> {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }

    protected void changeScene(String fxmlPath, Node sourceNode) {
        try {
            Stage stage = (Stage) sourceNode.getScene().getWindow();
            Scene scene = SceneCache.getScene(fxmlPath);

            if (scene != null) {
                stage.setScene(scene);
                stage.show();
            } else {
                throw new IOException("Failed to load scene from cache");
            }
        } catch (IOException e) {
            showAlert("Error", "Failed to change scene: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }
}

