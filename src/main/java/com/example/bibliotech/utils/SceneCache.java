package com.example.bibliotech.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SceneCache {
    private static final Map<String, Scene> sceneCache = new HashMap<>();

    public static Scene getScene(String fxmlPath) throws IOException {
        // Kiểm tra xem Scene đã được cache hay chưa
        if (!sceneCache.containsKey(fxmlPath)) {
            Parent root = FXMLLoader.load(Objects.requireNonNull(SceneCache.class.getResource(fxmlPath)));
            Scene scene = new Scene(root);
            sceneCache.put(fxmlPath, scene); // Lưu Scene vào cache
        }
        return sceneCache.get(fxmlPath); // Trả về Scene từ cache
    }
}

