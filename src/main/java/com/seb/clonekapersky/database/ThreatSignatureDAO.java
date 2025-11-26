package com.seb.clonekapersky.database;

import com.seb.clonekapersky.model.ThreatSignature;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ThreatSignatureDAO {

    private static final String SELECT_BY_HASH = "SELECT hash, nom_malware, type, severite FROM threats_signatures WHERE hash = ?";
    private static final String INSERT_SIGNATURE = "INSERT INTO threats_signatures (hash, nom_malware, type, severite) VALUES (?, ?, ?, ?)";
    private static final String SELECT_ALL = "SELECT hash, nom_malware, type, severite FROM threats_signatures";

    public boolean isThreatHash(String hash) {
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_HASH)) {
            ps.setString(1, hash);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error checking threat hash", e);
        }
    }

    public void insertSignature(ThreatSignature sig) {
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_SIGNATURE)) {
            ps.setString(1, sig.getHash());
            ps.setString(2, sig.getNomMalware());
            ps.setString(3, sig.getType());
            ps.setString(4, sig.getSeverite());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting signature", e);
        }
    }

    public List<ThreatSignature> listAll() {
        List<ThreatSignature> list = new ArrayList<>();
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new ThreatSignature(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4)));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error listing signatures", e);
        }
        return list;
    }
}
