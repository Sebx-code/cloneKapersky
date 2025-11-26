package com.seb.clonekapersky.controller;

import com.seb.clonekapersky.service.AntivirusEngine;
import com.seb.clonekapersky.service.FileWatcherService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Path;

public class MainController {
    @FXML private Button btnDashboard;
    @FXML private Button btnScan;
    @FXML private Button btnQuarantine;
    @FXML private Button btnHistory;
    @FXML private Button btnSettings;
    @FXML private StackPane contentPane;

    private AntivirusEngine engine;
    private FileWatcherService watcherService;

    public void initialize() {
        // Buttons wired from FXML; default view
        loadView("dashboard_view.fxml");
    }

    public void setPrimaryStage(Stage stage) {
        this.engine = new AntivirusEngine(stage);
        // publish engine and stage to global context for child controllers
        com.seb.clonekapersky.utils.AppContext.setEngine(this.engine);
        com.seb.clonekapersky.utils.AppContext.setPrimaryStage(stage);
        try {
            // Start watching user's Downloads as an example (silently ignore errors)
            Path home = Path.of(System.getProperty("user.home"));
            Path downloads = home.resolve("Downloads");
            watcherService = new FileWatcherService(downloads, engine);
            watcherService.start();
        } catch (Exception e) {
            // no-op
        }
    }

    @FXML void showDashboard() { loadView("dashboard_view.fxml"); }
    @FXML void showScan() { loadView("scan_view.fxml"); }
    @FXML void showQuarantine() { loadView("quarantine_view.fxml"); }
    @FXML void showHistory() { loadView("history_view.fxml"); }
    @FXML void showSettings() { loadView("settings_view.fxml"); }

    private void loadView(String fxmlName) {
        try {
            Node n = FXMLLoader.load(getClass().getResource("/com/seb/clonekapersky/views/" + fxmlName));
            contentPane.getChildren().setAll(n);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
