package com.example.bibliotech.presentation.Animation;

import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

public class SceneTransitionEffect {

    public static void applyTransitionEffect(Pane root) {
        for (Node node : root.getChildren()) {
            node.setTranslateY(30);

            TranslateTransition tt = new TranslateTransition(Duration.millis(500), node);
            tt.setToY(0);
            tt.setDelay(Duration.millis(50 * root.getChildren().indexOf(node)));
            tt.play();
        }
    }
}