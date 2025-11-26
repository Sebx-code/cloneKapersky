package com.seb.clonekapersky.database;

import com.seb.clonekapersky.model.QuarantineEntry;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class QuarantineDAO {
    private static final String INSERT = "INSERT INTO quarantine (emplacement_original, date_isolation, hash_detecte, stored_path) VALUES (?, ?, ?, ?)";
    private static final String SELECT_ALL = "SELECT file_id, emplacement_original, date_isolation, hash_detecte, stored_path FROM quarantine ORDER BY date_isolation DESC";
    private static final String DELETE = "DELETE FROM quarantine WHERE file_id = ?";

    public int insertQuarantine(String originalPath, LocalDateTime dateIsolation, String hashDetecte, String storedPath) {
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, originalPath);
            ps.setTimestamp(2, Timestamp.valueOf(dateIsolation));
            ps.setString(3, hashDetecte);
            ps.setString(4, storedPath);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
            return -1;
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting quarantine entry", e);
        }
    }

    public List<QuarantineEntry> listAll() {
        List<QuarantineEntry> list = new ArrayList<>();
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new QuarantineEntry(rs.getInt(1), rs.getString(2), rs.getTimestamp(3).toLocalDateTime(), rs.getString(4), rs.getString(5)));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error listing quarantine entries", e);
        }
        return list;
    }

    public void delete(int fileId) {
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE)) {
            ps.setInt(1, fileId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting quarantine entry", e);
        }
    }
}
