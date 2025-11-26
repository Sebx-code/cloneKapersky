package com.seb.clonekapersky.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton DB connection manager.
 * Configuration pour MAMP.
 */
public class DBConnection {
    // ✅ Configuration MAMP
    private static final String JDBC_URL = "jdbc:mysql://localhost:8889/mini_antivirus?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = "root"; // ← Mot de passe MAMP par défaut

    private static DBConnection instance;

    private DBConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL driver not found", e);
        }
    }

    public static synchronized DBConnection getInstance() {
        if (instance == null) {
            instance = new DBConnection();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        try {
            Connection conn = DriverManager.getConnection(JDBC_URL, USER, PASS);
            System.out.println("✓ Connexion MySQL MAMP réussie");
            return conn;
        } catch (SQLException e) {
            System.err.println("❌ Erreur de connexion MySQL MAMP: " + e.getMessage());
            System.err.println("   URL: " + JDBC_URL);
            System.err.println("   User: " + USER);
            System.err.println("   Vérifiez que MAMP est démarré !");
            throw e;
        }
    }
    
    /**
     * Teste la connexion à la base de données
     */
    public static boolean testConnection() {
        try {
            Connection conn = getInstance().getConnection();
            boolean valid = conn.isValid(2);
            conn.close();
            return valid;
        } catch (SQLException e) {
            System.err.println("Test de connexion échoué: " + e.getMessage());
            return false;
        }
    }
}