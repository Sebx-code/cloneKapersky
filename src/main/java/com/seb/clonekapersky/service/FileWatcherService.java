package com.seb.clonekapersky.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.nio.file.StandardWatchEventKinds.*;

/**
 * Watches a directory and triggers scans on file creation/modification.
 */
public class FileWatcherService {
    private final Path folder;
    private final AntivirusEngine engine;
    private WatchService watcher;
    private ExecutorService executor;
    private volatile boolean running = false;

    public FileWatcherService(Path folder, AntivirusEngine engine) {
        this.folder = folder;
        this.engine = engine;
    }

    public void start() throws IOException {
        if (running) return;
        watcher = FileSystems.getDefault().newWatchService();
        folder.register(watcher, ENTRY_CREATE, ENTRY_MODIFY);
        executor = Executors.newSingleThreadExecutor();
        running = true;
        executor.submit(this::process);
    }

    private void process() {
        while (running) {
            try {
                WatchKey key = watcher.take();
                for (WatchEvent<?> ev : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = ev.kind();
                    Path rel = (Path) ev.context();
                    Path abs = folder.resolve(rel);
                    if (kind == ENTRY_CREATE || kind == ENTRY_MODIFY) {
                        // Trigger async scan
                        engine.scanFileAsync(abs.toFile(), null);
                    }
                }
                key.reset();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public void stop() throws IOException {
        running = false;
        if (watcher != null) watcher.close();
        if (executor != null) executor.shutdownNow();
    }
}
