package erp.AircraftManagement;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MaintenanceFileRepository implements IMaintenanceRepository {
    private static final String DATA_FILE = "src/data/maintenance_logs.txt";

    @Override
    public void initializeStorage() {
        try {
            File directory = new File("src/data");
            if (!directory.exists()) {
                directory.mkdirs();
            }
            File file = new File(DATA_FILE);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            System.out.println("Maintenance storage initialization error: " + e.getMessage());
        }
    }

    @Override
    public boolean save(MaintenanceRecord record) {
        try (FileWriter writer = new FileWriter(DATA_FILE, true)) {
            writer.write(record.toFileRecord() + "\n");
            return true;
        } catch (IOException e) {
            System.out.println("Maintenance storage write error: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean update(MaintenanceRecord record) {
        try {
            Path path = Paths.get(DATA_FILE);
            List<String> lines = Files.readAllLines(path);
            List<String> updatedLines = new ArrayList<>();
            String prefix = record.getRecordId() + ",";
            boolean recordFound = false;

            for (String line : lines) {
                if (!recordFound && line.startsWith(prefix)) {
                    updatedLines.add(record.toFileRecord());
                    recordFound = true;
                } else {
                    updatedLines.add(line);
                }
            }

            if (recordFound) {
                Files.write(path, updatedLines);
            }
            return recordFound;

        } catch (IOException e) {
            System.out.println("Maintenance storage update error: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Optional<MaintenanceRecord> findScheduledByTailNumber(String tailNumber) {
        for (MaintenanceRecord record : findAll()) {
            if (record.getTailNumber().equals(tailNumber)
                    && record.getStatus() == MaintenanceStatus.SCHEDULED) {
                return Optional.of(record);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<MaintenanceRecord> findAll() {
        List<MaintenanceRecord> records = new ArrayList<>();
        try {
            for (String line : Files.readAllLines(Paths.get(DATA_FILE))) {
                if (line.isBlank()) continue;
                try {
                    records.add(MaintenanceRecord.fromFileRecord(line));
                } catch (IllegalArgumentException e) {
                    System.out.println("Corrupt maintenance record skipped: " + line);
                }
            }
        } catch (IOException e) {
            System.out.println("Maintenance storage read error: " + e.getMessage());
        }
        return records;
    }
}
