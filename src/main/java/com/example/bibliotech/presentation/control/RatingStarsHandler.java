package com.example.bibliotech.presentation.control;


import javafx.scene.layout.HBox;
import javafx.scene.shape.SVGPath;
import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.List;

public class RatingStarsHandler {
    private final HBox container;
    private final List<SVGPath> stars = new ArrayList<>();
    private int selectedRating = 0;
    private static final int MAX_STARS = 5;

    public RatingStarsHandler(HBox container) {
        this.container = container;
        createStars();
    }

    private void createStars() {
        String starPath = "M 12,2.5 L 14.4,9.5 L 21.5,9.5 L 15.8,13.8 L 18.2,20.5 L 12,16.3 L 5.8,20.5 L 8.2,13.8 L 2.5,9.5 L 9.6,9.5 Z";

        for (int i = 0; i < MAX_STARS; i++) {
            SVGPath star = new SVGPath();
            star.setContent(starPath);
            star.setFill(Color.LIGHTGRAY);
            star.setScaleX(0.8);
            star.setScaleY(0.8);

            final int starIndex = i;

            star.setOnMouseEntered(e -> handleMouseEnter(starIndex));
            star.setOnMouseExited(e -> handleMouseExit());
            star.setOnMouseClicked(e -> handleMouseClick(starIndex));

            stars.add(star);
            container.getChildren().add(star);
        }
    }

    private void handleMouseEnter(int starIndex) {
        for (int j = 0; j <= starIndex; j++) {
            stars.get(j).setFill(Color.GOLD);
        }
    }

    private void handleMouseExit() {
        for (int j = 0; j < MAX_STARS; j++) {
            if (j < selectedRating) {
                stars.get(j).setFill(Color.GOLD);
            } else {
                stars.get(j).setFill(Color.LIGHTGRAY);
            }
        }
    }

    private void handleMouseClick(int starIndex) {
        selectedRating = starIndex + 1;
        System.out.println("Rating selected: " + selectedRating);

        for (int j = 0; j < MAX_STARS; j++) {
            stars.get(j).setFill(j < selectedRating ? Color.GOLD : Color.LIGHTGRAY);
        }
    }

    public int getSelectedRating() {
        return selectedRating;
    }
}