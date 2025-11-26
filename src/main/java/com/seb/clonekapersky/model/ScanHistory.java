package com.seb.clonekapersky.model;

import java.time.LocalDateTime;

public class ScanHistory {
    private int id;
    private LocalDateTime dateScan;
    private int fichiersScannes;
    private int menacesTrouvees;

    public ScanHistory(int id, LocalDateTime dateScan, int fichiersScannes, int menacesTrouvees) {
        this.id = id;
        this.dateScan = dateScan;
        this.fichiersScannes = fichiersScannes;
        this.menacesTrouvees = menacesTrouvees;
    }

    public int getId() { return id; }
    public LocalDateTime getDateScan() { return dateScan; }
    public int getFichiersScannes() { return fichiersScannes; }
    public int getMenacesTrouvees() { return menacesTrouvees; }
}
