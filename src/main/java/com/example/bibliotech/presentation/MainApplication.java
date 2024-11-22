package com.example.bibliotech.presentation;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class MainApplication extends Application {
    private static final String LOGIN_FXML = "/com/example/bibliotech/login.fxml";
    private static final String HELLO_VIEW_FXML = "/com/example/bibliotech/hello-view.fxml";

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Tải FXML cho giao diện đầu tiên
        FXMLLoader loader = new FXMLLoader(getClass().getResource(HELLO_VIEW_FXML));
        Parent root = loader.load();

        // Đặt Scene cho Stage với background trong suốt
        Scene scene = new Scene(root, 600, 400);
        scene.setFill(Color.TRANSPARENT);

        // Loại bỏ các nút đóng, phóng to, thu nhỏ và làm cho cửa sổ trong suốt
        primaryStage.initStyle(StageStyle.TRANSPARENT);

        // Bo tròn góc và thêm background
        setupWindowStyle(root);

        // Lấy ImageView từ root bằng cách sử dụng lookup
        ImageView imageView = (ImageView) root.lookup("#img_Logo1");
        if (imageView != null) {
            clipImage(imageView, 20); // Thiết lập bán kính bo góc cho hình ảnh
        } else {
            System.out.println("ImageView not found!");
        }

        primaryStage.setScene(scene);
        primaryStage.show();

        // Sử dụng PauseTransition để chờ 1 giây rồi chuyển sang giao diện thứ 2
        PauseTransition pause = new PauseTransition(Duration.seconds(10));
        pause.setOnFinished(event -> fadeToLoginScreen(primaryStage));
        pause.play();
    }

    // Thiết lập kiểu cho cửa sổ
    private void setupWindowStyle(Parent root) {
        root.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 20;" +
                        "-fx-border-radius: 20;" +
                        "-fx-border-color: #cccccc;" +
                        "-fx-border-width: 1;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 0);"
        );
    }

    // Hàm bo góc cho hình ảnh
    private void clipImage(ImageView imageView, double radius) {
        // Tạo một hình chữ nhật với các góc bo tròn
        Rectangle clip = new Rectangle(imageView.getFitWidth(), imageView.getFitHeight());
        clip.setArcWidth(radius);
        clip.setArcHeight(radius);
        imageView.setClip(clip);
    }

    private void fadeToLoginScreen(Stage oldStage) {
        try {
            // Tải giao diện đăng nhập
            FXMLLoader loader = new FXMLLoader(getClass().getResource(LOGIN_FXML));
            Parent loginRoot = loader.load();

            Scene loginScene = new Scene(loginRoot, 900, 700);
            Stage newStage = new Stage();
            newStage.setScene(loginScene);
            newStage.initStyle(StageStyle.DECORATED);

            // Thêm hiệu ứng chuyển tiếp
            FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.5), oldStage.getScene().getRoot());
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.setOnFinished(event -> {
                oldStage.close(); // Đóng cửa sổ cũ sau khi hiệu ứng hoàn tất
                newStage.show();  // Hiển thị cửa sổ mới
            });
            fadeOut.play();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
