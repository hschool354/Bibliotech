package com.example.bibliotech.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Stack;

public class SceneCache {
    private static final Stack<String> sceneHistory = new Stack<>();
    private static final Map<String, Scene> sceneCache = new HashMap<>();
    private static final Map<String, FXMLLoader> loaderCache = new HashMap<>();

    public static Scene getScene(String fxmlPath) throws IOException {
        // Always reload information_Book.fxml
        if (fxmlPath.equals("/com/example/bibliotech/information_Book.fxml")) {
            FXMLLoader loader = new FXMLLoader(SceneCache.class.getResource(fxmlPath));
            Parent root = loader.load();
            Scene scene = new Scene(root);

            sceneCache.put(fxmlPath, scene);
            loaderCache.put(fxmlPath, loader);

            // Push the scene path to history stack
            if (sceneHistory.isEmpty() || !fxmlPath.equals(sceneHistory.peek())) {
                sceneHistory.push(fxmlPath);
            }

            return scene;
        }

        // For other scenes, use existing cache logic
        if (!sceneCache.containsKey(fxmlPath)) {
            FXMLLoader loader = new FXMLLoader(SceneCache.class.getResource(fxmlPath));
            Parent root = loader.load();
            Scene scene = new Scene(root);

            sceneCache.put(fxmlPath, scene);
            loaderCache.put(fxmlPath, loader);
        }

        // Push the scene path to history stack
        if (sceneHistory.isEmpty() || !fxmlPath.equals(sceneHistory.peek())) {
            sceneHistory.push(fxmlPath);
        }

        return sceneCache.get(fxmlPath);
    }

    public static String getPreviousScene() {
        if (sceneHistory.size() > 1) {
            // Pop the current scene
            sceneHistory.pop();
            // Return the previous scene
            return sceneHistory.peek();
        }
        return null;
    }

    public static <T> T getController(String fxmlPath) {
        FXMLLoader loader = loaderCache.get(fxmlPath);
        if (loader != null) {
            return loader.getController();
        }
        return null;
    }

    public static void clearCache(String fxmlPath) {
        sceneCache.remove(fxmlPath);
        loaderCache.remove(fxmlPath);
    }

    public static void clearCache() {
        sceneCache.clear();
        loaderCache.clear();
        sceneHistory.clear();
    }
}