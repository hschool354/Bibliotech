package com.example.bibliotech.presentation.control;

import com.example.bibliotech.presentation.Animation.TypewriterEffect;
import com.example.bibliotech.utils.SceneCache;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class Home_1_Controller implements Initializable {
    @FXML
    private Button btn_readNow;

    @FXML
    private ImageView gifImageView;

    @FXML
    private Label sloganLabel;

    @FXML
    public void handleReadNowButton() {
        changeScene("/com/example/bibliotech/information_Book.fxml");
    }

    private void changeScene(String fxmlPath) {
        try {
            Stage stage = (Stage) btn_readNow.getScene().getWindow(); // Lấy stage hiện tại
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


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadGifImage();
        playTypewriterEffect();
    }

    private void loadGifImage() {
        // In đường dẫn tài nguyên để kiểm tra
        URL gifURL = getClass().getResource("/icons/gif_book_1.gif");
        System.out.println("GIF URL: " + gifURL);

        if (gifURL != null) {
            Image gifImage = new Image(gifURL.toExternalForm());
            gifImageView.setImage(gifImage);
        } else {
            System.out.println("GIF resource not found!");
        }
    }

    private void playTypewriterEffect() {
        TypewriterEffect effect = new TypewriterEffect("A library at your fingertips\n knowledge without limits", sloganLabel, 120, true);
        effect.play();
    }
}
