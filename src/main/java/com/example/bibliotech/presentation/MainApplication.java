package com.example.bibliotech.presentation;

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
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Tải FXML cho giao diện đầu tiên
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/bibliotech/hello-view.fxml"));
        Parent root = loader.load();

        // Đặt Scene cho Stage với background trong suốt
        Scene scene = new Scene(root, 600, 400);
        scene.setFill(Color.TRANSPARENT);

        // Loại bỏ các nút đóng, phóng to, thu nhỏ và làm cho cửa sổ trong suốt
        primaryStage.initStyle(StageStyle.TRANSPARENT);

        // Bo tròn góc và thêm background
        root.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 20;" +
                        "-fx-border-radius: 20;" +
                        "-fx-border-color: #cccccc;" +
                        "-fx-border-width: 1;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 0);"
        );

        // Lấy ImageView từ root bằng cách sử dụng lookup
        ImageView imageView = (ImageView) root.lookup("#img_Logo1");
        if (imageView != null) {
            clipImage(imageView, 20); // Thiết lập bán kính bo góc cho hình ảnh
        } else {
            System.out.println("ImageView not found!");
        }

        primaryStage.setScene(scene);
        primaryStage.show();

        // Sử dụng PauseTransition để chờ 5 giây rồi chuyển sang giao diện thứ 2
        PauseTransition pause = new PauseTransition(Duration.seconds(1));
        pause.setOnFinished(event -> loadLoginScreen(primaryStage)); // Sau 5 giây, tải giao diện login
        pause.play();
    }

    // Hàm bo góc cho hình ảnh
    private void clipImage(ImageView imageView, double radius) {
        // Tạo một hình chữ nhật với các góc bo tròn
        Rectangle clip = new Rectangle(imageView.getFitWidth(), imageView.getFitHeight());
        clip.setArcWidth(radius);
        clip.setArcHeight(radius);
        imageView.setClip(clip);
    }

    private void loadLoginScreen(Stage oldStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/bibliotech/login.fxml"));
            Parent loginRoot = loader.load();

            Stage newStage = new Stage();
            Scene loginScene = new Scene(loginRoot, 900, 700);

            newStage.setScene(loginScene);

            newStage.initStyle(StageStyle.DECORATED);

            newStage.show();
            oldStage.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}