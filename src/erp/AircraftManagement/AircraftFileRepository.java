package erp.AircraftManagement;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

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
}