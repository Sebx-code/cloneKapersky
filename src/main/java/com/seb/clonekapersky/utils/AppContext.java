package com.seb.clonekapersky.utils;

import com.seb.clonekapersky.service.AntivirusEngine;
import javafx.stage.Stage;

/**
 * Simple application context to share main objects across controllers.
 */
public class AppContext {
    private static AntivirusEngine engine;
    private static Stage primaryStage;

    public static void setEngine(AntivirusEngine e) { engine = e; }
    public static AntivirusEngine getEngine() { return engine; }

    public static void setPrimaryStage(Stage s) { primaryStage = s; }
    public static Stage getPrimaryStage() { return primaryStage; }
}
