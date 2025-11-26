package com.seb.clonekapersky.controller;

import com.seb.clonekapersky.database.QuarantineDAO;
import com.seb.clonekapersky.model.QuarantineEntry;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class QuarantineController {
    @FXML private TableView<QuarantineEntry> table;
    @FXML private TableColumn<QuarantineEntry, Integer> colId;
    @FXML private TableColumn<QuarantineEntry, String> colFile;
    @FXML private TableColumn<QuarantineEntry, String> colDate;
    @FXML private TableColumn<QuarantineEntry, String> colHash;
    @FXML private Button btnRestore;
    @FXML private Button btnDelete;
    @FXML private Label lblCount;

    private QuarantineDAO dao = new QuarantineDAO();
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public void initialize() {
        // Configuration des colonnes
        colId.setCellValueFactory(new PropertyValueFactory<>("fileId"));
        colFile.setCellValueFactory(new PropertyValueFactory<>("emplacementOriginal"));
        
        // Formater la date
        colDate.setCellValueFactory(cellData -> {
            if (cellData.getValue().getDateIsolation() != null) {
                return new javafx.beans.property.SimpleStringProperty(
                    cellData.getValue().getDateIsolation().format(dateFormatter)
                );
            }
            return new javafx.beans.property.SimpleStringProperty("");
        });
        
        // Formater le hash (afficher seulement les 16 premiers caractères)
        colHash.setCellValueFactory(cellData -> {
            String hash = cellData.getValue().getHashDetecte();
            if (hash != null && hash.length() > 16) {
                return new javafx.beans.property.SimpleStringProperty(hash.substring(0, 16) + "...");
            }
            return new javafx.beans.property.SimpleStringProperty(hash != null ? hash : "");
        });

        // Style pour les lignes avec menaces
        table.setRowFactory(tv -> {
            TableRow<QuarantineEntry> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    showDetailsDialog(row.getItem());
                }
            });
            return row;
        });

        // Désactiver les boutons si aucune sélection
        btnRestore.setDisable(true);
        btnDelete.setDisable(true);
        
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean hasSelection = newSelection != null;
            btnRestore.setDisable(!hasSelection);
            btnDelete.setDisable(!hasSelection);
        });

        refresh();
    }

    private void refresh() {
        try {
            List<QuarantineEntry> list = dao.listAll();
            ObservableList<QuarantineEntry> items = FXCollections.observableArrayList(list);
            table.setItems(items);
            
            // Mettre à jour le compteur
            if (lblCount != null) {
                lblCount.setText(String.valueOf(list.size()));
            }
        } catch (Exception e) {
            showError("Erreur lors du chargement", "Impossible de charger les fichiers en quarantaine: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void restoreSelected() {
        QuarantineEntry sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) {
            showWarning("Aucune sélection", "Veuillez sélectionner un fichier à restaurer.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Restaurer le fichier");
        alert.setHeaderText("Restaurer ce fichier ?");
        alert.setContentText("Le fichier sera restauré à son emplacement d'origine : \n" + sel.getEmplacementOriginal());
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    String stored = sel.getStoredPath();
                    if (stored == null || stored.isEmpty()) {
                        showError("Erreur", "Chemin du fichier quarantiné introuvable.");
                        return;
                    }

                    java.nio.file.Path restoredPath = java.nio.file.Path.of(stored);
                    if (!java.nio.file.Files.exists(restoredPath)) {
                        showError("Erreur", "Le fichier quarantiné n'existe plus sur le disque.");
                        return;
                    }

                    java.nio.file.Path origPath = java.nio.file.Path.of(sel.getEmplacementOriginal());
                    if (origPath.getParent() != null && !java.nio.file.Files.exists(origPath.getParent())) {
                        java.nio.file.Files.createDirectories(origPath.getParent());
                    }

                    java.nio.file.Files.move(restoredPath, origPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                    dao.delete(sel.getFileId());
                    
                    refresh();
                    showSuccess("Restauré", "Le fichier a été restauré avec succès.");
                    com.seb.clonekapersky.utils.LoggerUtil.log("INFO", "Restored quarantined file to " + origPath);
                    
                } catch (Exception e) {
                    showError("Erreur de restauration", "Impossible de restaurer le fichier: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    @FXML
    public void deleteSelected() {
        QuarantineEntry sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) {
            showWarning("Aucune sélection", "Veuillez sélectionner un fichier à supprimer.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Supprimer définitivement");
        alert.setHeaderText("⚠️ Action irréversible");
        alert.setContentText("Voulez-vous supprimer définitivement ce fichier ?\nCette action ne peut pas être annulée.");
        
        ButtonType btnConfirm = new ButtonType("Supprimer", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancel = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(btnConfirm, btnCancel);

        alert.showAndWait().ifPresent(response -> {
            if (response == btnConfirm) {
                try {
                    String stored = sel.getStoredPath();
                    if (stored != null && !stored.isEmpty()) {
                        java.nio.file.Path quarantinedPath = java.nio.file.Path.of(stored);
                        if (java.nio.file.Files.exists(quarantinedPath)) {
                            java.nio.file.Files.delete(quarantinedPath);
                        }
                    }
                    
                    dao.delete(sel.getFileId());
                    refresh();
                    showSuccess("Supprimé", "Le fichier a été supprimé définitivement.");
                    com.seb.clonekapersky.utils.LoggerUtil.log("INFO", "Deleted quarantined file entry id=" + sel.getFileId());
                    
                } catch (Exception e) {
                    showError("Erreur de suppression", "Impossible de supprimer le fichier: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    private void showDetailsDialog(QuarantineEntry entry) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Détails du fichier");
        alert.setHeaderText("Informations complètes");
        
        StringBuilder details = new StringBuilder();
        details.append("ID: ").append(entry.getFileId()).append("\n\n");
        details.append("Fichier: ").append(entry.getEmplacementOriginal()).append("\n\n");
        details.append("Date d'isolation: ").append(entry.getDateIsolation().format(dateFormatter)).append("\n\n");
        details.append("Hash SHA-256: ").append(entry.getHashDetecte()).append("\n\n");
        details.append("Emplacement en quarantaine: ").append(entry.getStoredPath());
        
        alert.setContentText(details.toString());
        alert.showAndWait();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}