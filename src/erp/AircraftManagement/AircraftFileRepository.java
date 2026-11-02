package erp.AircraftManagement;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class AircraftFileRepository implements IAircraftRepository {
    private static final String DATA_FILE = "src/data/aircrafts.json";

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
                // Initialize empty JSON array structure
                try (FileWriter writer = new FileWriter(file)) {
                    writer.write("[]");
                }
            } else if (file.length() == 0) {
                try (FileWriter writer = new FileWriter(file)) {
                    writer.write("[]");
                }
            }
        } catch (IOException e) {
            System.out.println("Storage initialization error: " + e.getMessage());
        }
    }

    @Override
    public boolean existsByIdentifiers(String tailNumber, String serialNumber) {
        try {
            String content = new String(Files.readAllBytes(Paths.get(DATA_FILE)));
            return content.contains("\"tailNumber\": \"" + tailNumber + "\"") ||
                    content.contains("\"serialNumber\": \"" + serialNumber + "\"");
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public boolean save(Aircraft aircraft) {
        try {
            String content = new String(Files.readAllBytes(Paths.get(DATA_FILE))).trim();

            if (content.isEmpty() || content.equals("[]")) {
                content = "[\n  " + aircraft.toJson() + "\n]";
            } else {
                // Insert new aircraft before closing JSON bracket
                int lastBracketIndex = content.lastIndexOf("]");
                if (lastBracketIndex != -1) {
                    String prefix = content.substring(0, lastBracketIndex).trim();
                    if (!prefix.endsWith("[")) {
                        prefix += ",\n  ";
                    } else {
                        prefix += "\n  ";
                    }
                    content = prefix + aircraft.toJson() + "\n]";
                }
            }

            try (FileWriter writer = new FileWriter(DATA_FILE, false)) {
                writer.write(content);
            }
            return true;
        } catch (IOException e) {
            System.out.println("Storage write error: " + e.getMessage());
            return false;
        }
    }
}