package com.seb.clonekapersky;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Main application launcher. Loads the Kaspersky-like main view.
 */
public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/seb/clonekapersky/views/main_view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 600);
        scene.getStylesheets().add(getClass().getResource("/assets/kaspersky.css").toExternalForm());
        stage.setTitle("Mini Antivirus");
        stage.setScene(scene);
        stage.show();

        // pass Stage to controller so it can int services
        Object controller = fxmlLoader.getController();
        if (controller instanceof com.seb.clonekapersky.controller.MainController) {
            ((com.seb.clonekapersky.controller.MainController) controller).setPrimaryStage(stage);
        }
    }

    public static void main(String[] args) { launch(); }
}
