package com.seb.clonekapersky.service;

import com.seb.clonekapersky.database.QuarantineDAO;
import com.seb.clonekapersky.database.ScanHistoryDAO;
import com.seb.clonekapersky.database.ThreatSignatureDAO;
import com.seb.clonekapersky.utils.HashUtils;
import com.seb.clonekapersky.utils.LoggerUtil;
import com.seb.clonekapersky.utils.Toast;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Core scanning engine. Uses background Tasks/Services and DAOs.
 */
public class AntivirusEngine {

    private final ThreatSignatureDAO signatureDAO = new ThreatSignatureDAO();
    private final QuarantineDAO quarantineDAO = new QuarantineDAO();
    private final ScanHistoryDAO historyDAO = new ScanHistoryDAO();
    private final Stage primaryStage;

    public interface ScanCallback {
        void onFileScanned(File file, boolean threatFound, String hash);
        void onProgress(int scanned, int total);
        void onFinished(int scanned, int threatsFound);
    }

    public AntivirusEngine(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public Service<Void> createDirectoryScanService(File dir, ScanCallback callback) {
        return new Service<>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<>() {
                    @Override
                    protected Void call() {
                        AtomicInteger scanned = new AtomicInteger();
                        AtomicInteger threats = new AtomicInteger();
                        File[] files = dir.listFiles();
                        int total = files == null ? 0 : files.length;
                        if (files != null) {
                            for (File f : files) {
                                if (isCancelled()) break;
                                if (f.isDirectory()) {
                                    scanDirectoryRecursive(f, scanned, threats, callback);
                                } else {
                                    boolean found = scanSingleFile(f, callback);
                                    if (found) threats.incrementAndGet();
                                    scanned.incrementAndGet();
                                    int s = scanned.get();
                                    if (callback != null) Platform.runLater(() -> callback.onProgress(s, total));
                                }
                            }
                        }
                        historyDAO.insert(LocalDateTime.now(), scanned.get(), threats.get());
                        if (callback != null) Platform.runLater(() -> callback.onFinished(scanned.get(), threats.get()));
                        LoggerUtil.log("INFO", "Scan finished. scanned=" + scanned.get() + " threats=" + threats.get());
                        return null;
                    }
                };
            }
        };
    }

    private void scanDirectoryRecursive(File dir, AtomicInteger scanned, AtomicInteger threats, ScanCallback callback) {
        File[] files = dir.listFiles();
        if (files == null) return;
        for (File f : files) {
            if (f.isDirectory()) scanDirectoryRecursive(f, scanned, threats, callback);
            else {
                boolean found = scanSingleFile(f, callback);
                if (found) threats.incrementAndGet();
                scanned.incrementAndGet();
                int s = scanned.get();
                if (callback != null) Platform.runLater(() -> callback.onProgress(s, -1));
            }
        }
    }

    private boolean scanSingleFile(File file, ScanCallback callback) {
        try {
            String sha = HashUtils.generateSHA256(file);
            AtomicBoolean threat = new AtomicBoolean(signatureDAO.isThreatHash(sha));

            // YARA aggressive detection
            try {
                java.util.Optional<String> yaraMatch = YaraScanner.scan(file);
                if (yaraMatch.isPresent()) {
                    threat.set(true);
                    LoggerUtil.log("WARN", "YARA detected " + yaraMatch.orElse("<rule>") + " on " + file.getAbsolutePath());
                }
            } catch (Throwable t) {
                LoggerUtil.log("ERROR", "YARA check failed: " + t.getMessage());
            }

            if (callback != null) Platform.runLater(() -> callback.onFileScanned(file, threat.get(), sha));
            if (threat.get()) {
                int id = moveToQuarantine(file, sha);
                LoggerUtil.log("WARN", "Threat found: " + file.getAbsolutePath() + " -> quarantine id=" + id);
                Platform.runLater(() -> Toast.show(primaryStage, "Menace détectée: " + file.getName()));
            }
            return threat.get();
        } catch (IOException e) {
            LoggerUtil.log("ERROR", "Failed to hash file: " + file.getAbsolutePath() + " - " + e.getMessage());
            return false;
        }
    }

    private int moveToQuarantine(File file, String hash) {
        try {
            Path quarantineDir = Path.of("quarantine_store");
            if (!Files.exists(quarantineDir)) Files.createDirectories(quarantineDir);
            String name = System.currentTimeMillis() + "__" + file.getName();
            Path target = quarantineDir.resolve(name);
            Files.move(file.toPath(), target);
            // store the actual path where the file was saved in quarantine
            String storedPath = target.toAbsolutePath().toString();
            return quarantineDAO.insertQuarantine(file.getAbsolutePath(), LocalDateTime.now(), hash, storedPath);
        } catch (IOException e) {
            LoggerUtil.log("ERROR", "Failed to move to quarantine: " + e.getMessage());
            return -1;
        }
    }

    /**
     * Scan a single file asynchronously and update history.
     */
    public void scanFileAsync(File file, ScanCallback callback) {
        Service<Void> svc = new Service<>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<>() {
                    @Override
                    protected Void call() {
                        boolean threat = scanSingleFile(file, callback);
                        historyDAO.insert(LocalDateTime.now(), 1, threat ? 1 : 0);
                        if (callback != null) Platform.runLater(() -> callback.onFinished(1, threat ? 1 : 0));
                        return null;
                    }
                };
            }
        };
        svc.start();
    }
}
