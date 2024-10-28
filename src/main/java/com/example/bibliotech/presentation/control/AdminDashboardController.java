package com.example.bibliotech.presentation.control;

import com.example.bibliotech.utils.SceneCache;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import java.io.IOException;

public class AdminDashboardController {
    @FXML
    private Button btn_Book,btn_User;

    @FXML
    public void handleBookManagerButton() {
        changeScene("/com/example/bibliotech/AdminBookManager.fxml");
    }

    @FXML
    public void handlAccountManagerButton() {
        changeScene("/com/example/bibliotech/AdminAccountManager.fxml");
    }


    private void changeScene(String fxmlPath) {
        try {
            Stage stage = (Stage) btn_Book.getScene().getWindow(); // Lấy stage hiện tại
            Scene scene = SceneCache.getScene(fxmlPath); // Lấy Scene từ cache
            stage.setScene(scene); // Thiết lập Scene mới

            // Có thể thêm hiệu ứng chuyển cảnh nếu cần
            // SceneTransitionEffect.applyTransitionEffect((Pane) scene.getRoot());

            stage.show(); // Hiển thị stage với Scene mới
        } catch (IOException e) {
            System.err.println("Error loading scene: " + fxmlPath);
            e.printStackTrace(); // In ra lỗi để tiện theo dõi
        }
    }
}
