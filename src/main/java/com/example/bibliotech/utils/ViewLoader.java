package com.example.bibliotech.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;

public class ViewLoader {
    private static HashMap<String, AnchorPane> viewsCache = new HashMap<>();

    public static AnchorPane loadView(String viewName) throws IOException {
        // Kiểm tra xem view đã có trong cache chưa
        if (!viewsCache.containsKey(viewName)) {
            try {
                // Tạo FXMLLoader và tải view
                FXMLLoader loader = new FXMLLoader(
                        ViewLoader.class.getResource("/com/example/bibliotech/" + viewName + ".fxml")
                );
                AnchorPane view = loader.load();
                viewsCache.put(viewName, view);
            } catch (IOException e) {
                // Xử lý lỗi khi tải view
                System.err.println("Error loading view: " + viewName);
                e.printStackTrace();
                throw e; // Ném lại ngoại lệ để xử lý ở nơi gọi
            }
        }
        return viewsCache.get(viewName);
    }

    // Phương thức để xóa cache nếu cần
    public static void clearCache() {
        viewsCache.clear();
    }
}
