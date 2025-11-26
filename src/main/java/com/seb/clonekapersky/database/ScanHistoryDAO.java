package com.seb.clonekapersky.database;

import com.seb.clonekapersky.model.ScanHistory;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ScanHistoryDAO {
    private static final String INSERT = "INSERT INTO scan_history (date_scan, fichiers_scannes, menaces_trouvees) VALUES (?, ?, ?)";
    private static final String SELECT_ALL = "SELECT id, date_scan, fichiers_scannes, menaces_trouvees FROM scan_history ORDER BY date_scan DESC";

    public void insert(LocalDateTime dateScan, int fichiersScannes, int menacesTrouvees) {
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT)) {
            ps.setTimestamp(1, Timestamp.valueOf(dateScan));
            ps.setInt(2, fichiersScannes);
            ps.setInt(3, menacesTrouvees);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting scan history", e);
        }
    }

    public List<ScanHistory> listAll() {
        List<ScanHistory> list = new ArrayList<>();
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new ScanHistory(rs.getInt(1), rs.getTimestamp(2).toLocalDateTime(), rs.getInt(3), rs.getInt(4)));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error listing scan history", e);
        }
        return list;
    }
}
