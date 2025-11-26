package com.seb.clonekapersky.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Controller for Dashboard view. Shows quick stats and buttons to start scans.
 */
public class DashboardController {
    @FXML private Label lblThreatsBlocked;
    @FXML private Label lblSystemStatus;
    @FXML private Label lblFilesScanned;
    @FXML private Button btnQuickScan;
    @FXML private Button btnFullScan;
    @FXML private ListView<String> activityList;
    
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    public void initialize() {
        lblThreatsBlocked.setText("0");
        lblFilesScanned.setText("0");
        lblSystemStatus.setText("Protégé");
        
        if (activityList != null) {
            activityList.setItems(FXCollections.observableArrayList());
            addActivity("✓ Protection temps réel activée");
            addActivity("ℹ️ Base de données à jour");
        }
    }
    
    private void addActivity(String message) {
        if (activityList == null) return;
        String timestamp = LocalDateTime.now().format(TIME_FORMATTER);
        activityList.getItems().add(0, timestamp + " - " + message);
        
        // Limite à 10 entrées
        if (activityList.getItems().size() > 10) {
            activityList.getItems().remove(10, activityList.getItems().size());
        }
    }

    @FXML void quickScan() {
        var engine = com.seb.clonekapersky.utils.AppContext.getEngine();
        if (engine == null) {
            lblSystemStatus.setText("❌ Moteur non disponible");
            lblSystemStatus.setStyle("-fx-text-fill: #d1121e;");
            return;
        }
        
        lblSystemStatus.setText("⏳ Scan en cours...");
        lblSystemStatus.setStyle("-fx-text-fill: #ff9800;");
        btnQuickScan.setDisable(true);
        btnFullScan.setDisable(true);
        addActivity("🔍 Démarrage de l'analyse rapide");
        
        // Scan UNIQUEMENT Downloads (non récursif)
        var home = java.nio.file.Path.of(System.getProperty("user.home"));
        var downloads = home.resolve("Downloads").toFile();
        
        if (!downloads.exists()) {
            lblSystemStatus.setText("❌ Dossier Downloads introuvable");
            lblSystemStatus.setStyle("-fx-text-fill: #d1121e;");
            btnQuickScan.setDisable(false);
            btnFullScan.setDisable(false);
            addActivity("❌ Erreur : Dossier Downloads introuvable");
            return;
        }
        
        var svc = engine.createDirectoryScanService(downloads, new com.seb.clonekapersky.service.AntivirusEngine.ScanCallback() {
            int threats = 0;
            int scanned = 0;
            
            @Override 
            public void onFileScanned(java.io.File file, boolean threatFound, String hash) {
                scanned++;
                if (threatFound) {
                    threats++;
                    addActivity("⚠️ Menace détectée : " + file.getName());
                }
            }
            
            @Override 
            public void onProgress(int scannedNow, int total) {
                lblSystemStatus.setText("⏳ Analyse : " + scannedNow + "/" + total);
                lblFilesScanned.setText(String.valueOf(scannedNow));
            }
            
            @Override 
            public void onFinished(int scannedTotal, int threatsFound) {
                lblThreatsBlocked.setText(String.valueOf(threatsFound));
                lblFilesScanned.setText(String.valueOf(scannedTotal));
                
                if (threatsFound > 0) {
                    lblSystemStatus.setText("⚠️ Attention requise");
                    lblSystemStatus.setStyle("-fx-text-fill: #d1121e;");
                    addActivity("⚠️ Analyse terminée : " + threatsFound + " menace(s) trouvée(s)");
                } else {
                    lblSystemStatus.setText("✓ Protégé");
                    lblSystemStatus.setStyle("-fx-text-fill: #00a88e;");
                    addActivity("✓ Analyse terminée : Aucune menace détectée");
                }
                
                btnQuickScan.setDisable(false);
                btnFullScan.setDisable(false);
            }
        });
        svc.start();
    }

    @FXML void fullScan() {
        // Afficher un avertissement avant le scan complet
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.WARNING);
        alert.setTitle("Analyse Complète");
        alert.setHeaderText("⚠️ Opération Intensive");
        alert.setContentText("L'analyse complète peut prendre beaucoup de temps et de ressources.\n\n" +
                             "Recommandations :\n" +
                             "• Fermez les autres applications\n" +
                             "• Branchez l'alimentation secteur\n" +
                             "• Ne mettez pas en veille\n\n" +
                             "Continuer ?");
        
        var result = alert.showAndWait();
        if (result.isEmpty() || result.get() != javafx.scene.control.ButtonType.OK) {
            return;
        }
        
        var engine = com.seb.clonekapersky.utils.AppContext.getEngine();
        if (engine == null) {
            lblSystemStatus.setText("❌ Moteur non disponible");
            lblSystemStatus.setStyle("-fx-text-fill: #d1121e;");
            return;
        }
        
        lblSystemStatus.setText("⏳ Scan complet en cours...");
        lblSystemStatus.setStyle("-fx-text-fill: #ff9800;");
        btnQuickScan.setDisable(true);
        btnFullScan.setDisable(true);
        addActivity("🔍 Démarrage de l'analyse complète");
        
        // Scan limité au dossier Documents
        var home = java.nio.file.Path.of(System.getProperty("user.home"));
        var documents = home.resolve("Documents").toFile();
        
        if (!documents.exists()) {
            lblSystemStatus.setText("❌ Dossier Documents introuvable");
            lblSystemStatus.setStyle("-fx-text-fill: #d1121e;");
            btnQuickScan.setDisable(false);
            btnFullScan.setDisable(false);
            addActivity("❌ Erreur : Dossier Documents introuvable");
            return;
        }
        
        var svc = engine.createDirectoryScanService(documents, new com.seb.clonekapersky.service.AntivirusEngine.ScanCallback() {
            int lastReported = 0;
            
            @Override 
            public void onFileScanned(java.io.File file, boolean threatFound, String hash) {
                if (threatFound) {
                    addActivity("⚠️ Menace : " + file.getName());
                }
            }
            
            @Override 
            public void onProgress(int scanned, int total) {
                lblFilesScanned.setText(String.valueOf(scanned));
                
                // Rapport tous les 50 fichiers
                if (scanned - lastReported >= 50) {
                    lblSystemStatus.setText("⏳ Analyse : " + scanned + " fichiers");
                    lastReported = scanned;
                }
            }
            
            @Override 
            public void onFinished(int scanned, int threatsFound) {
                lblFilesScanned.setText(String.valueOf(scanned));
                lblThreatsBlocked.setText(String.valueOf(threatsFound));
                
                if (threatsFound > 0) {
                    lblSystemStatus.setText("⚠️ Attention requise");
                    lblSystemStatus.setStyle("-fx-text-fill: #d1121e;");
                    addActivity("⚠️ Analyse complète : " + threatsFound + " menace(s)");
                } else {
                    lblSystemStatus.setText("✓ Protégé");
                    lblSystemStatus.setStyle("-fx-text-fill: #00a88e;");
                    addActivity("✓ Analyse complète terminée : Système sain");
                }
                
                btnQuickScan.setDisable(false);
                btnFullScan.setDisable(false);
            }
        });
        svc.start();
    }
}