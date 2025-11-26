package com.seb.clonekapersky.controller;

import com.seb.clonekapersky.database.ScanHistoryDAO;
import com.seb.clonekapersky.model.ScanHistory;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class HistoryController {
    @FXML private TableView<ScanHistory> table;
    @FXML private TableColumn<ScanHistory, Integer> colId;
    @FXML private TableColumn<ScanHistory, String> colDate;
    @FXML private TableColumn<ScanHistory, Integer> colFiles;
    @FXML private TableColumn<ScanHistory, Integer> colThreats;

    private ScanHistoryDAO dao = new ScanHistoryDAO();

    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("dateScan"));
        colFiles.setCellValueFactory(new PropertyValueFactory<>("fichiersScannes"));
        colThreats.setCellValueFactory(new PropertyValueFactory<>("menacesTrouvees"));
        refresh();
    }

    private void refresh() {
        List<ScanHistory> list = dao.listAll();
        table.setItems(FXCollections.observableArrayList(list));
    }
}
