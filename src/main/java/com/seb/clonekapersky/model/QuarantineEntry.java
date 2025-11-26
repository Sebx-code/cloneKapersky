package com.seb.clonekapersky.model;

import java.time.LocalDateTime;

public class QuarantineEntry {
    private int fileId;
    private String emplacementOriginal;
    private LocalDateTime dateIsolation;
    private String hashDetecte;
    private String storedPath;

    public QuarantineEntry(int fileId, String emplacementOriginal, LocalDateTime dateIsolation, String hashDetecte, String storedPath) {
        this.fileId = fileId;
        this.emplacementOriginal = emplacementOriginal;
        this.dateIsolation = dateIsolation;
        this.hashDetecte = hashDetecte;
        this.storedPath = storedPath;
    }

    public int getFileId() { return fileId; }
    public String getEmplacementOriginal() { return emplacementOriginal; }
    public LocalDateTime getDateIsolation() { return dateIsolation; }
    public String getHashDetecte() { return hashDetecte; }
    public String getStoredPath() { return storedPath; }
}
