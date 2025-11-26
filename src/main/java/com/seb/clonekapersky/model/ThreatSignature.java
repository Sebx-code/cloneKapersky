package com.seb.clonekapersky.model;

/**
 * Model for threat signature.
 */
public class ThreatSignature {
    private String hash;
    private String nomMalware;
    private String type;
    private String severite;

    public ThreatSignature(String hash, String nomMalware, String type, String severite) {
        this.hash = hash;
        this.nomMalware = nomMalware;
        this.type = type;
        this.severite = severite;
    }

    public String getHash() { return hash; }
    public String getNomMalware() { return nomMalware; }
    public String getType() { return type; }
    public String getSeverite() { return severite; }
}
