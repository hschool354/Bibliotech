package com.example.bibliotech.presentation.control;

import com.example.bibliotech.presentation.Animation.TypewriterEffect;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.ResourceBundle;

public class GifHomeController implements Initializable {
    @FXML
    private ImageView gifImageView;

    @FXML
    private Label sloganLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadGifImage();
        playTypewriterEffect();
    }

    private void loadGifImage() {
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
        TypewriterEffect effect = new TypewriterEffect(
                "A library at your fingertips\n knowledge without limits",
                sloganLabel,
                120,
                true
        );
        effect.play();
    }
}
