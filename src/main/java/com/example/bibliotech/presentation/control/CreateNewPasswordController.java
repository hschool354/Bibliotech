package com.example.bibliotech.presentation.control;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class CreateNewPasswordController {
    @FXML
    private Button btn_Save, btn_Back;

    @FXML
    public void handleSaveButton() {
        changeScene("/com/example/bibliotech/login.fxml");
    }

    @FXML
    public void handleBackButton() {
        changeScene("/com/example/bibliotech/verifyEmail.fxml");
    }

    private void changeScene(String fxmlPath) {
        try {
            // Lấy stage hiện tại
            Stage stage = (Stage) btn_Save.getScene().getWindow(); // Hoặc btn_Back, tùy thuộc vào nút nào được nhấn

            // Tải layout mới từ FXML
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(fxmlPath)));

            // Tạo và thiết lập Scene mới
            Scene scene = new Scene(root);
            stage.setScene(scene);

            // Có thể áp dụng hiệu ứng chuyển cảnh nếu cần
            // SceneTransitionEffect.applyTransitionEffect((Pane) root);

            stage.show();
        } catch (IOException e) {
            e.printStackTrace(); // In ra lỗi để tiện theo dõi
        }
    }
}
