package com.seb.clonekapersky.utils;

import javafx.animation.FadeTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Simple toast notification utility for JavaFX.
 */
public class Toast {
    public static void show(Stage owner, String message) {
        Popup popup = new Popup();
        Label lbl = new Label(message);
        lbl.setStyle("-fx-background-color: rgba(17,17,17,0.95); -fx-text-fill: white; -fx-padding: 10px; -fx-border-radius: 8px; -fx-background-radius: 8px;");
        StackPane content = new StackPane(lbl);
        content.setPadding(new Insets(6));
        popup.getContent().add(content);
        popup.setAutoFix(true);
        popup.setAutoHide(true);

        // place bottom-right of owner
        double x = owner.getX() + owner.getWidth() - 320;
        double y = owner.getY() + owner.getHeight() - 100;
        popup.show(owner, x, y);

        // animate: slide up + fade
        TranslateTransition slideIn = new TranslateTransition(Duration.millis(350), content);
        slideIn.setFromY(40);
        slideIn.setToY(0);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(350), content);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        SequentialTransition show = new SequentialTransition();
        show.getChildren().addAll(slideIn, fadeIn);
        show.play();

        // hide after 3 seconds with reverse animation
        new Thread(() -> {
            try { Thread.sleep(3000); } catch (InterruptedException ignored) {}
            javafx.application.Platform.runLater(() -> {
                TranslateTransition slideOut = new TranslateTransition(Duration.millis(280), content);
                slideOut.setFromY(0);
                slideOut.setToY(40);
                FadeTransition fadeOut = new FadeTransition(Duration.millis(280), content);
                fadeOut.setFromValue(1);
                fadeOut.setToValue(0);
                SequentialTransition hide = new SequentialTransition(slideOut, fadeOut);
                hide.setOnFinished(ev -> popup.hide());
                hide.play();
            });
        }).start();
    }
}
