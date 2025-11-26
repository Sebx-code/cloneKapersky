package com.seb.clonekapersky.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ToggleButton;

public class SettingsController {
    @FXML private ToggleButton toggleTheme;

    public void initialize() {
        toggleTheme.setSelected(false);
    }

    @FXML void toggleThemeAction() {
        // This would toggle CSS between light/dark in a real app
        boolean dark = toggleTheme.isSelected();
        toggleTheme.setText(dark ? "Dark" : "Light");
    }
}
