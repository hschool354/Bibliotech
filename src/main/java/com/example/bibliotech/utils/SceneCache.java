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
    private static final Map<String, FXMLLoader> loaderCache = new HashMap<>();

    public static Scene getScene(String fxmlPath) throws IOException {
        // Check if Scene exists in cache
        if (!sceneCache.containsKey(fxmlPath)) {
            // Create new FXMLLoader
            FXMLLoader loader = new FXMLLoader(SceneCache.class.getResource(fxmlPath));
            Parent root = loader.load();
            Scene scene = new Scene(root);

            // Store both the Scene and the loader
            sceneCache.put(fxmlPath, scene);
            loaderCache.put(fxmlPath, loader);
        }
        return sceneCache.get(fxmlPath);
    }

    public static <T> T getController(String fxmlPath) {
        FXMLLoader loader = loaderCache.get(fxmlPath);
        if (loader != null) {
            return loader.getController();
        }
        return null;
    }

    public static void clearCache() {
        sceneCache.clear();
        loaderCache.clear();
    }
}