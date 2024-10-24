package com.example.bibliotech.presentation.control;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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


public class Home_1_Controller {
    @FXML
    private Button btn_readNow;

    @FXML
    private ImageView gifImageView;

    @FXML
    private Label sloganLabel;



    @FXML
    public void handleReadNowButton() throws IOException {
        Stage stage = (Stage) btn_readNow.getScene().getWindow();

        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/bibliotech/information_Book.fxml")));

        Scene scene = new Scene(root);
        stage.setScene(scene);

        //SceneTransitionEffect.applyTransitionEffect((Pane) root);

        stage.show();
    }

   @FXML
   public void initialize() {
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


}
