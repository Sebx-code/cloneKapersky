package com.seb.clonekapersky.service;

import com.seb.clonekapersky.utils.LoggerUtil;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

/**
 * Utility to run YARA CLI against a file using embedded rule(s).
 * Requires `yara` binary to be installed and available on PATH.
 */
public class YaraScanner {

    private static Path ensureRuleFile() throws IOException {
        String resource = "/assets/yara/KMSPico_AllVariants.yar";
        InputStream is = YaraScanner.class.getResourceAsStream(resource);
        if (is == null) throw new FileNotFoundException("YARA rule resource not found: " + resource);
        Path tmp = Files.createTempFile("miniav_rules_", ".yar");
        tmp.toFile().deleteOnExit();
        try (OutputStream os = Files.newOutputStream(tmp)) {
            is.transferTo(os);
        }
        return tmp;
    }

    /**
     * Scans a file with the embedded rule. Returns the matched rule name if any.
     */
    public static Optional<String> scan(File file) {
        try {
            Path ruleFile = ensureRuleFile();
            ProcessBuilder pb = new ProcessBuilder("yara", ruleFile.toAbsolutePath().toString(), file.getAbsolutePath());
            pb.redirectErrorStream(true);
            Process p = pb.start();
            StringBuilder out = new StringBuilder();
            try (BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                String line;
                while ((line = r.readLine()) != null) out.append(line).append("\n");
            }
            int exit = p.waitFor();
            if (exit == 0) {
                String stdout = out.toString().trim();
                if (!stdout.isEmpty()) {
                    String firstLine = stdout.split("\r?\n")[0].trim();
                    LoggerUtil.log("INFO", "YARA matched: " + firstLine + " for file " + file.getAbsolutePath());
                    return Optional.of(firstLine);
                } else {
                    return Optional.of("YARA_MATCH");
                }
            } else if (exit == 1) {
                // no match
                return Optional.empty();
            } else {
                LoggerUtil.log("ERROR", "YARA scan error for " + file.getAbsolutePath() + ": exit=" + exit + " output=" + out.toString());
                return Optional.empty();
            }
        } catch (IOException | InterruptedException e) {
            LoggerUtil.log("ERROR", "YARA scan failed: " + e.getMessage());
            return Optional.empty();
        }
    }
}
