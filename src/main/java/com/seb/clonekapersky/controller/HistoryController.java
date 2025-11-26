package com.seb.clonekapersky.controller;

import com.seb.clonekapersky.database.ScanHistoryDAO;
import com.seb.clonekapersky.model.ScanHistory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class HistoryController {
    @FXML private TableView<ScanHistory> table;
    @FXML private TableColumn<ScanHistory, Integer> colId;
    @FXML private TableColumn<ScanHistory, String> colDate;
    @FXML private TableColumn<ScanHistory, Integer> colFiles;
    @FXML private TableColumn<ScanHistory, Integer> colThreats;
    @FXML private Label lblTotalScans;
    @FXML private Label lblTotalFiles;
    @FXML private Label lblTotalThreats;

    private ScanHistoryDAO dao = new ScanHistoryDAO();
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public void initialize() {
        // Configuration des colonnes
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        
        // Formater la date
        colDate.setCellValueFactory(cellData -> {
            if (cellData.getValue().getDateScan() != null) {
                return new javafx.beans.property.SimpleStringProperty(
                    cellData.getValue().getDateScan().format(dateFormatter)
                );
            }
            return new javafx.beans.property.SimpleStringProperty("");
        });
        
        colFiles.setCellValueFactory(new PropertyValueFactory<>("fichiersScannes"));
        colThreats.setCellValueFactory(new PropertyValueFactory<>("menacesTrouvees"));

        // Style pour les lignes avec menaces
        table.setRowFactory(tv -> {
            TableRow<ScanHistory> row = new TableRow<ScanHistory>() {
                @Override
                protected void updateItem(ScanHistory item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setStyle("");
                    } else if (item.getMenacesTrouvees() > 0) {
                        setStyle("-fx-background-color: rgba(255, 59, 48, 0.05);");
                    } else {
                        setStyle("");
                    }
                }
            };
            
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    showDetailsDialog(row.getItem());
                }
            });
            
            return row;
        });

        // Centrer les nombres
        colId.setStyle("-fx-alignment: CENTER;");
        colFiles.setStyle("-fx-alignment: CENTER;");
        colThreats.setStyle("-fx-alignment: CENTER;");

        refresh();
    }

    private void refresh() {
        try {
            List<ScanHistory> list = dao.listAll();
            ObservableList<ScanHistory> items = FXCollections.observableArrayList(list);
            table.setItems(items);
            
            // Calculer les statistiques
            int totalScans = list.size();
            int totalFiles = list.stream().mapToInt(ScanHistory::getFichiersScannes).sum();
            int totalThreats = list.stream().mapToInt(ScanHistory::getMenacesTrouvees).sum();
            
            // Mettre à jour les labels de statistiques
            if (lblTotalScans != null) {
                lblTotalScans.setText(String.valueOf(totalScans));
            }
            if (lblTotalFiles != null) {
                lblTotalFiles.setText(String.valueOf(totalFiles));
            }
            if (lblTotalThreats != null) {
                lblTotalThreats.setText(String.valueOf(totalThreats));
            }
        } catch (Exception e) {
            showError("Erreur lors du chargement", "Impossible de charger l'historique: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void clearHistory() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Effacer l'historique");
        alert.setHeaderText("Voulez-vous effacer tout l'historique ?");
        alert.setContentText("Cette action supprimera tous les enregistrements d'analyses.");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    // Implémentation à ajouter dans le DAO si nécessaire
                    showInfo("Information", "Fonctionnalité à venir");
                } catch (Exception e) {
                    showError("Erreur", "Impossible d'effacer l'historique: " + e.getMessage());
                }
            }
        });
    }

    private void showDetailsDialog(ScanHistory scan) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Détails de l'analyse");
        alert.setHeaderText("Analyse #" + scan.getId());
        
        StringBuilder details = new StringBuilder();
        details.append("Date: ").append(scan.getDateScan().format(dateFormatter)).append("\n\n");
        details.append("Fichiers analysés: ").append(scan.getFichiersScannes()).append("\n\n");
        details.append("Menaces trouvées: ").append(scan.getMenacesTrouvees()).append("\n\n");
        
        if (scan.getMenacesTrouvees() > 0) {
            details.append("⚠️ Des menaces ont été détectées et mises en quarantaine.");
        } else {
            details.append("✓ Aucune menace détectée.");
        }
        
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

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}