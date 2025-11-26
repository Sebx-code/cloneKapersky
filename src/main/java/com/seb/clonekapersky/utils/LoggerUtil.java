package com.seb.clonekapersky.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Lightweight logger writing to `logs/antivirus.log`.
 */
public class LoggerUtil {
    private static final Path LOG_PATH = Path.of("logs/antivirus.log");
    private static final DateTimeFormatter TF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static void ensureLogFile() {
        try {
            if (!Files.exists(LOG_PATH.getParent())) Files.createDirectories(LOG_PATH.getParent());
            if (!Files.exists(LOG_PATH)) Files.createFile(LOG_PATH);
        } catch (IOException e) {
            System.err.println("Could not create log file: " + e.getMessage());
        }
    }

    public static void log(String level, String message) {
        ensureLogFile();
        String line = String.format("[%s] %s - %s%n", LocalDateTime.now().format(TF), level, message);
        try {
            Files.writeString(LOG_PATH, line, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("Failed to write log: " + e.getMessage());
        }
    }
}
