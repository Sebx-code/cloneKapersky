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
    private Button currentActiveButton;

    public void initialize() {
        // Définir le bouton Dashboard comme actif par défaut
        currentActiveButton = btnDashboard;
        setActiveButton(btnDashboard);
        
        // Charger la vue par défaut
        loadView("dashboard_view.fxml");
    }

    public void setPrimaryStage(Stage stage) {
        this.engine = new AntivirusEngine(stage);
        // Publier l'engine et le stage dans le contexte global
        com.seb.clonekapersky.utils.AppContext.setEngine(this.engine);
        com.seb.clonekapersky.utils.AppContext.setPrimaryStage(stage);
        
        try {
            // Démarrer la surveillance du dossier Downloads
            Path home = Path.of(System.getProperty("user.home"));
            Path downloads = home.resolve("Downloads");
            watcherService = new FileWatcherService(downloads, engine);
            watcherService.start();
            System.out.println("File watcher démarré sur: " + downloads);
        } catch (Exception e) {
            System.err.println("Impossible de démarrer le file watcher: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML 
    void showDashboard() { 
        setActiveButton(btnDashboard);
        loadView("dashboard_view.fxml"); 
    }
    
    @FXML 
    void showScan() { 
        setActiveButton(btnScan);
        loadView("scan_view.fxml"); 
    }
    
    @FXML 
    void showQuarantine() { 
        setActiveButton(btnQuarantine);
        loadView("quarantine_view.fxml"); 
    }
    
    @FXML 
    void showHistory() { 
        setActiveButton(btnHistory);
        loadView("history_view.fxml"); 
    }
    
    @FXML 
    void showSettings() { 
        setActiveButton(btnSettings);
        loadView("settings_view.fxml"); 
    }

    private void setActiveButton(Button newActiveButton) {
        // Retirer la classe active de l'ancien bouton
        if (currentActiveButton != null) {
            currentActiveButton.getStyleClass().remove("side-btn-active");
            if (!currentActiveButton.getStyleClass().contains("side-btn")) {
                currentActiveButton.getStyleClass().add("side-btn");
            }
        }
        
        // Ajouter la classe active au nouveau bouton
        if (newActiveButton != null) {
            newActiveButton.getStyleClass().remove("side-btn");
            if (!newActiveButton.getStyleClass().contains("side-btn-active")) {
                newActiveButton.getStyleClass().add("side-btn-active");
            }
            currentActiveButton = newActiveButton;
        }
    }

    private void loadView(String fxmlName) {
        try {
            String resourcePath = "/com/seb/clonekapersky/views/" + fxmlName;
            System.out.println("Chargement de la vue: " + resourcePath);
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource(resourcePath));
            Node node = loader.load();
            
            contentPane.getChildren().clear();
            contentPane.getChildren().add(node);
            
            System.out.println("Vue chargée avec succès: " + fxmlName);
        } catch (IOException e) {
            System.err.println("ERREUR: Impossible de charger la vue " + fxmlName);
            e.printStackTrace();
            
            // Afficher un message d'erreur dans le contentPane
            javafx.scene.control.Label errorLabel = new javafx.scene.control.Label(
                "❌ Erreur de chargement\n\n" +
                "Impossible de charger la vue: " + fxmlName + "\n" +
                "Erreur: " + e.getMessage()
            );
            errorLabel.setStyle(
                "-fx-font-size: 16px; " +
                "-fx-text-fill: #FF3B30; " +
                "-fx-padding: 40; " +
                "-fx-text-alignment: center;"
            );
            
            contentPane.getChildren().clear();
            contentPane.getChildren().add(errorLabel);
        } catch (Exception e) {
            System.err.println("ERREUR INATTENDUE lors du chargement de " + fxmlName);
            e.printStackTrace();
        }
    }
}