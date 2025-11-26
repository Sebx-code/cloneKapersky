package com.seb.clonekapersky.controller;

import com.seb.clonekapersky.database.QuarantineDAO;
import com.seb.clonekapersky.model.QuarantineEntry;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class QuarantineController {
    @FXML private TableView<QuarantineEntry> table;
    @FXML private TableColumn<QuarantineEntry, Integer> colId;
    @FXML private TableColumn<QuarantineEntry, String> colFile;
    @FXML private TableColumn<QuarantineEntry, String> colDate;
    @FXML private TableColumn<QuarantineEntry, String> colHash;

    private QuarantineDAO dao = new QuarantineDAO();

    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("fileId"));
        colFile.setCellValueFactory(new PropertyValueFactory<>("emplacementOriginal"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("dateIsolation"));
        colHash.setCellValueFactory(new PropertyValueFactory<>("hashDetecte"));
        refresh();
    }

    private void refresh() {
        List<QuarantineEntry> list = dao.listAll();
        table.setItems(FXCollections.observableArrayList(list));
    }

    public void restoreSelected() {
        QuarantineEntry sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
        alert.setTitle("Restaurer le fichier");
        alert.setHeaderText(null);
        alert.setContentText("Restaurer le fichier sélectionné à son emplacement d'origine ?");
        var res = alert.showAndWait();
        if (res.isEmpty() || res.get() != javafx.scene.control.ButtonType.OK) return;
        try {
            String stored = sel.getStoredPath();
            if (stored == null) {
                javafx.application.Platform.runLater(() -> com.seb.clonekapersky.utils.Toast.show(com.seb.clonekapersky.utils.AppContext.getPrimaryStage(), "Chemin stocké introuvable."));
                return;
            }
            java.nio.file.Path restored = java.nio.file.Path.of(stored);
            if (java.nio.file.Files.exists(restored)) {
                java.nio.file.Path orig = java.nio.file.Path.of(sel.getEmplacementOriginal());
                if (orig.getParent() != null && !java.nio.file.Files.exists(orig.getParent())) java.nio.file.Files.createDirectories(orig.getParent());
                java.nio.file.Files.move(restored, orig);
                dao.delete(sel.getFileId());
                refresh();
                com.seb.clonekapersky.utils.LoggerUtil.log("INFO", "Restored quarantined file to " + orig);
                javafx.application.Platform.runLater(() -> com.seb.clonekapersky.utils.Toast.show(com.seb.clonekapersky.utils.AppContext.getPrimaryStage(), "Fichier restauré."));
            } else {
                javafx.application.Platform.runLater(() -> com.seb.clonekapersky.utils.Toast.show(com.seb.clonekapersky.utils.AppContext.getPrimaryStage(), "Fichier quarantiné introuvable."));
            }
        } catch (Exception e) {
            e.printStackTrace();
            javafx.application.Platform.runLater(() -> com.seb.clonekapersky.utils.Toast.show(com.seb.clonekapersky.utils.AppContext.getPrimaryStage(), "Erreur lors de la restauration."));
        }
    }

    public void deleteSelected() {
        QuarantineEntry sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
        alert.setTitle("Supprimer définitivement");
        alert.setHeaderText(null);
        alert.setContentText("Supprimer définitivement le fichier quarantiné ? Cette action est irréversible.");
        var res = alert.showAndWait();
        if (res.isEmpty() || res.get() != javafx.scene.control.ButtonType.OK) return;
        try {
            String stored = sel.getStoredPath();
            if (stored != null) {
                java.nio.file.Path quarantined = java.nio.file.Path.of(stored);
                if (java.nio.file.Files.exists(quarantined)) java.nio.file.Files.delete(quarantined);
            }
            dao.delete(sel.getFileId());
            refresh();
            com.seb.clonekapersky.utils.LoggerUtil.log("INFO", "Deleted quarantined file entry id=" + sel.getFileId());
            javafx.application.Platform.runLater(() -> com.seb.clonekapersky.utils.Toast.show(com.seb.clonekapersky.utils.AppContext.getPrimaryStage(), "Entrée supprimée."));
        } catch (Exception e) {
            e.printStackTrace();
            javafx.application.Platform.runLater(() -> com.seb.clonekapersky.utils.Toast.show(com.seb.clonekapersky.utils.AppContext.getPrimaryStage(), "Erreur lors de la suppression."));
        }
    }

    /**
     * Find a quarantined file by matching suffix with original filename.
     */
    private java.nio.file.Path findQuarantinedFile(java.nio.file.Path qdir, String originalPath) throws java.io.IOException {
        String base = java.nio.file.Path.of(originalPath).getFileName().toString();
        try (java.util.stream.Stream<java.nio.file.Path> s = java.nio.file.Files.list(qdir)) {
            return s.filter(p -> p.getFileName().toString().endsWith("__" + base)).findFirst().orElse(null);
        }
    }
}
