package com.seb.clonekapersky.controller;

import com.seb.clonekapersky.service.AntivirusEngine;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;

import java.io.File;

public class ScanController {
    @FXML private ProgressBar progressBar;
    @FXML private ListView<String> liveList;
    @FXML private Button btnSelectFolder;
    @FXML private Label lblStatus;

    private AntivirusEngine engine;

    public void initialize() {
        progressBar.setProgress(0);
        lblStatus.setText("Prêt");
        
        // ✅ Récupérer l'engine du contexte dès l'initialisation
        engine = com.seb.clonekapersky.utils.AppContext.getEngine();
        
        // Vérifier que l'engine est disponible
        if (engine == null) {
            lblStatus.setText("❌ Moteur antivirus non initialisé");
            btnSelectFolder.setDisable(true);
        }
    }

    @FXML void selectFolder() {
        // ✅ Vérification de l'engine
        if (engine == null) {
            engine = com.seb.clonekapersky.utils.AppContext.getEngine();
            if (engine == null) {
                Platform.runLater(() -> {
                    lblStatus.setText("❌ Erreur : Moteur non disponible");
                    liveList.getItems().add("ERREUR : Le moteur antivirus n'est pas initialisé");
                });
                return;
            }
        }
        
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Sélectionner un fichier à scanner");
        File f = chooser.showOpenDialog(null);
        
        if (f != null) {
            // Réinitialiser l'interface
            liveList.getItems().clear();
            progressBar.setProgress(0);
            lblStatus.setText("Scan en cours: " + f.getName());
            
            // Créer le callback de scan
            engine.scanFileAsync(f, new AntivirusEngine.ScanCallback() {
                @Override
                public void onFileScanned(File file, boolean threatFound, String hash) {
                    Platform.runLater(() -> {
                        String status = threatFound ? " - ⚠️ MENACE" : " - ✓ OK";
                        String hashPreview = hash != null ? " (" + hash.substring(0, Math.min(8, hash.length())) + "...)" : "";
                        liveList.getItems().add(file.getName() + status + hashPreview);
                    });
                }

                @Override
                public void onProgress(int scanned, int total) {
                    Platform.runLater(() -> {
                        if (total > 0) {
                            progressBar.setProgress((double) scanned / total);
                        } else {
                            progressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
                        }
                    });
                }

                @Override
                public void onFinished(int scanned, int threatsFound) {
                    Platform.runLater(() -> {
                        progressBar.setProgress(1.0);
                        String status = threatsFound > 0 
                            ? "⚠️ Terminé. Menaces trouvées: " + threatsFound
                            : "✓ Terminé. Aucune menace détectée.";
                        lblStatus.setText(status);
                        liveList.getItems().add("──────────────────────────");
                        liveList.getItems().add("Scan terminé : " + scanned + " fichier(s) analysé(s)");
                    });
                }
            });
        }
    }
}