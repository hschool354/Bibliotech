package com.example.bibliotech.presentation.Animation;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.util.Duration;

public class TypewriterEffect {
    private final String text;
    private final Label label;
    private final Timeline timeline;
    private int index = 0;
    private final int speed;
    private final boolean loop;

    public TypewriterEffect(String text, Label label, int speed, boolean loop) {
        this.text = text;
        this.label = label;
        this.speed = speed;
        this.loop = loop;

        timeline = new Timeline();
        timeline.getKeyFrames().add(
                new KeyFrame(Duration.millis(speed), event -> {
                    if (index <= text.length()) {
                        label.setText(text.substring(0, index));
                        index++;
                    } else {
                        if (loop) {
                            resetAnimation();
                        } else {
                            timeline.stop();
                        }
                    }
                })
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
    }

    private void resetAnimation() {
        index = 0;
        label.setText("");
        // Adjust the delay here to 2 seconds before restarting the animation
        Timeline pauseTimeline = new Timeline(new KeyFrame(Duration.seconds(2), e -> {}));
        pauseTimeline.setOnFinished(e -> timeline.play());
        pauseTimeline.play();
    }

    public void play() {
        timeline.play();
    }

    public void stop() {
        timeline.stop();
    }
}

