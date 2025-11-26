package com.seb.clonekapersky.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

/**
 * Controller for Dashboard view. Shows quick stats and buttons to start scans.
 */
public class DashboardController {
    @FXML private Label lblThreatsBlocked;
    @FXML private Label lblSystemStatus;
    @FXML private Button btnQuickScan;
    @FXML private Button btnFullScan;

    public void initialize() {
        lblThreatsBlocked.setText("0");
        lblSystemStatus.setText("Protégé");
    }

    @FXML void quickScan() {
        lblSystemStatus.setText("Scan rapide en cours...");
        // Quick scan: scan Downloads folder
        var engine = com.seb.clonekapersky.utils.AppContext.getEngine();
        if (engine == null) return;
        var home = java.nio.file.Path.of(System.getProperty("user.home"));
        var downloads = home.resolve("Downloads").toFile();
        var svc = engine.createDirectoryScanService(downloads, new com.seb.clonekapersky.service.AntivirusEngine.ScanCallback() {
            int threats = 0;
            int scanned = 0;
            @Override public void onFileScanned(java.io.File file, boolean threatFound, String hash) {
                scanned++;
                if (threatFound) threats++;
            }
            @Override public void onProgress(int scannedNow, int total) { }
            @Override public void onFinished(int scannedTotal, int threatsFound) {
                lblThreatsBlocked.setText(String.valueOf(threatsFound));
                lblSystemStatus.setText("Scan rapide terminé");
            }
        });
        svc.start();
    }

    @FXML void fullScan() {
        lblSystemStatus.setText("Scan complet en cours...");
        var engine = com.seb.clonekapersky.utils.AppContext.getEngine();
        if (engine == null) return;
        // For demo, full scan will target user home recursively
        var home = java.nio.file.Path.of(System.getProperty("user.home")).toFile();
        var svc = engine.createDirectoryScanService(home, new com.seb.clonekapersky.service.AntivirusEngine.ScanCallback() {
            @Override public void onFileScanned(java.io.File file, boolean threatFound, String hash) { }
            @Override public void onProgress(int scanned, int total) { }
            @Override public void onFinished(int scanned, int threatsFound) {
                lblSystemStatus.setText("Scan complet terminé");
                lblThreatsBlocked.setText(String.valueOf(threatsFound));
            }
        });
        svc.start();
    }
}
