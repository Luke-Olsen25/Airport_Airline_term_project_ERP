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

public class AircraftFileRepository implements IAircraftRepository {
    private static final String DATA_FILE = "src/data/aircrafts.txt";

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
            System.out.println("Storage initialization error: " + e.getMessage());
        }
    }

    @Override
    public boolean existsByIdentifiers(String tailNumber, String serialNumber) {
        try {
            String content = new String(Files.readAllBytes(Paths.get(DATA_FILE)));
            // Checking for the exact string followed or preceded by a comma ensures we match the exact field
            return content.contains(tailNumber + ",") || content.contains("," + serialNumber + ",");
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public boolean save(Aircraft aircraft) {
        try (FileWriter writer = new FileWriter(DATA_FILE, true)) {
            writer.write(aircraft.toFileRecord() + "\n");
            return true;
        } catch (IOException e) {
            System.out.println("Storage write error: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<String> getAllAircraftRecords() {
        try {
            return Files.readAllLines(Paths.get(DATA_FILE));
        } catch (IOException e) {
            System.out.println("Storage read error: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public boolean deleteByTailNumber(String tailNumber) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(DATA_FILE));
            List<String> updatedLines = new ArrayList<>();
            boolean recordFound = false;

            for (String line : lines) {
                // Ensure we match the exact tail number by checking the appended comma
                if (line.startsWith(tailNumber + ",")) {
                    recordFound = true; // We found the target; do not add it to the updated list
                } else {
                    updatedLines.add(line);
                }
            }

            if (recordFound) {
                // Overwrite the existing file with the filtered list
                Files.write(Paths.get(DATA_FILE), updatedLines);
            }

            return recordFound;

        } catch (IOException e) {
            System.out.println("Storage delete error: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Optional<Aircraft> findByTailNumber(String tailNumber) {
        for (String line : getAllAircraftRecords()) {
            if (line.startsWith(tailNumber + ",")) {
                try {
                    return Optional.of(Aircraft.fromFileRecord(line));
                } catch (IllegalArgumentException e) {
                    System.out.println("Corrupt aircraft record skipped: " + e.getMessage());
                    return Optional.empty();
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean updateAircraft(Aircraft updatedAircraft) {
        try {
            Path path = Paths.get(DATA_FILE);
            List<String> lines = Files.readAllLines(path);
            List<String> updatedLines = new ArrayList<>();
            String prefix = updatedAircraft.getTailNumber() + ",";
            boolean recordFound = false;

            for (String line : lines) {
                if (!recordFound && line.startsWith(prefix)) {
                    updatedLines.add(updatedAircraft.toFileRecord());
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
            System.out.println("Storage update error: " + e.getMessage());
            return false;
        }
    }
}