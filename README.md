Mini Antivirus Prototype (JavaFX)

Quick start:

1. Create the MySQL database and tables:

```sql
-- run the schema file in src/main/resources/db/schema.sql
SOURCE src/main/resources/db/schema.sql;
```

2. Update DB credentials in `DBConnection.java` (URL, USER, PASS).

3. Build & run with Maven (ensure Java 17+ and JavaFX available):

```bash
mvn clean javafx:run
```

Notes:
- UI files are in `src/main/resources/com/seb/clonekapersky/views`.
- CSS theme: `src/main/resources/assets/kaspersky.css`.
- Quarantined files are moved to `quarantine_store/` in project root.
- Logs are written to `logs/antivirus.log`.

Quarantine: use the Quarantine view to restore or delete quarantined files.

Quick test suggestions:

- Ensure `mini_antivirus` DB exists and `threats_signatures` contains at least one hash you can reproduce.
- Create a small test file and compute its SHA-256 with the `HashUtils` utility (or use `sha256sum`), insert that hash into `threats_signatures`, then place the file into `~/Downloads` to see auto-scan/quarantine behavior.

YARA integration:

- This project supports YARA rules using the `yara` CLI. The KMSPico rule `KMSPico_AllVariants.yar` is included under `src/main/resources/assets/yara/` and will be executed automatically during scans if the `yara` binary is available on PATH.
- To install YARA on macOS:

```bash
brew install yara
```

- If `yara` is not installed the scanner will skip YARA checks but will continue using hash/datasource detection.
