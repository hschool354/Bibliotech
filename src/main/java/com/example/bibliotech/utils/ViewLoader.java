package com.example.bibliotech.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.Node;
import javafx.fxml.Initializable;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Optional;
import java.util.WeakHashMap;
import java.util.logging.Logger;
import java.util.logging.Level;

public class ViewLoader {
    private static final Logger LOGGER = Logger.getLogger(ViewLoader.class.getName());
    private static final String VIEW_PATH = "/com/example/bibliotech/";

    // Using WeakHashMap to allow garbage collection of unused views
    private static final WeakHashMap<String, CacheEntry> viewsCache = new WeakHashMap<>();

    private static class CacheEntry {
        final AnchorPane view;
        final Object controller;
        final long timestamp;

        CacheEntry(AnchorPane view, Object controller) {
            this.view = view;
            this.controller = controller;
            this.timestamp = System.currentTimeMillis();
        }

        boolean isExpired() {
            // Cache entries expire after 5 minutes of non-use
            return System.currentTimeMillis() - timestamp > 300000;
        }
    }

    public static synchronized ViewLoadResult loadView(String viewName, boolean forceReload) throws IOException {
        try {
            if (!forceReload) {
                CacheEntry entry = viewsCache.get(viewName);
                if (entry != null && !entry.isExpired()) {
                    LOGGER.info("Returning cached view: " + viewName);
                    return new ViewLoadResult(entry.view, entry.controller);
                }
            }

            LOGGER.info("Loading fresh view: " + viewName);
            FXMLLoader loader = new FXMLLoader(
                    ViewLoader.class.getResource(VIEW_PATH + viewName + ".fxml")
            );

            AnchorPane view = loader.load();
            Object controller = loader.getController();

            // Store in cache
            viewsCache.put(viewName, new CacheEntry(view, controller));

            return new ViewLoadResult(view, controller);

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to load view: " + viewName, e);
            throw new ViewLoadException("Failed to load view: " + viewName, e);
        }
    }

    public static synchronized void clearCache() {
        LOGGER.info("Clearing view cache");
        viewsCache.clear();
    }

    public static synchronized void removeFromCache(String viewName) {
        LOGGER.info("Removing view from cache: " + viewName);
        viewsCache.remove(viewName);
    }

    public static class ViewLoadResult {
        private final AnchorPane view;
        private final Object controller;

        public ViewLoadResult(AnchorPane view, Object controller) {
            this.view = view;
            this.controller = controller;
        }

        public AnchorPane getView() { return view; }
        public Object getController() { return controller; }

        public void setAnchors(Double top, Double right, Double bottom, Double left) {
            if (top != null) AnchorPane.setTopAnchor(view, top);
            if (right != null) AnchorPane.setRightAnchor(view, right);
            if (bottom != null) AnchorPane.setBottomAnchor(view, bottom);
            if (left != null) AnchorPane.setLeftAnchor(view, left);
        }
    }

    public static class ViewLoadException extends RuntimeException {
        public ViewLoadException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}