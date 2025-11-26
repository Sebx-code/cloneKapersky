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
    }

    @FXML void selectFolder() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Sélectionner un fichier à scanner");
        File f = chooser.showOpenDialog(null);
        if (f != null) {
            lblStatus.setText("Scan en cours: " + f.getName());
            // create a small anonymous callback
            engine = new AntivirusEngine(null);
            engine.scanFileAsync(f, new AntivirusEngine.ScanCallback() {
                @Override
                public void onFileScanned(File file, boolean threatFound, String hash) {
                    Platform.runLater(() -> liveList.getItems().add(file.getName() + (threatFound ? " - MENACE" : " - OK") + " (" + hash.substring(0,8) + "...)"));
                }

                @Override
                public void onProgress(int scanned, int total) {
                    Platform.runLater(() -> progressBar.setProgress(total>0 ? (double)scanned/total : ProgressBar.INDETERMINATE_PROGRESS));
                }

                @Override
                public void onFinished(int scanned, int threatsFound) {
                    Platform.runLater(() -> lblStatus.setText("Terminé. Menaces: " + threatsFound));
                }
            });
        }
    }
}
