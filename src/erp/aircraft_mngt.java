package erp;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class aircraft_mngt {
    private static final String DATA_FILE = "src/data/aircrafts.json";

    public static class Aircraft {
        String tailNumber;
        String manufacturer;
        String model;
        String serialNumber;
        int maxFlightRange;
        int seatingCapacity;


        public Aircraft(String tailNumber, String manufacturer, String model, String serialNumber, int maxFlightRange, int seatingCapacity) {
            this.tailNumber = tailNumber;
            this.manufacturer = manufacturer;
            this.model = model;
            this.maxFlightRange = maxFlightRange;
            this.serialNumber = serialNumber;
            this.seatingCapacity = seatingCapacity;
        }

        // Convert aircraft inputs to JSON format
        public String toJson() {
            return String.format("{\"tailNumber\": \"%s\", \"manufacturer\": \"%s\", \"model\": \"%s\", \"serialNumber\": %s, \"maxFlightRange\": \"%s\", \"seatingCapacity\": %d}",
                        tailNumber, manufacturer, model, serialNumber, maxFlightRange, seatingCapacity);
        }
    }

    // Ensures the data folder and file exist
    public static void initializeStorage() {
        try {
            File directory = new File("src/data");
            if (!directory.exists()){
                directory.mkdirs();
            }
            File file = new File(DATA_FILE);
            if (!file.exists()){
                file.createNewFile();
            }
        } catch (IOException e) {
            System.out.println("Error initializing storage: " + e.getMessage());
        }
    }

    public static boolean registerAircraft(String tailNumber, String manufacturer, String model, String serialNumber, int maxFlightRange, int seatingCapacity) {
        try {
            // Check for duplicate Tail Number or Serial Number
            String content = new String(Files.readAllBytes(Paths.get(DATA_FILE)));
            if (content.contains("\"tailNumber\": \"" + tailNumber + "\"") || content.contains("\"serialNumber\": \"" + serialNumber + "\"")) {
                return false; // Aircraft already exists
            }

            // Save new aircraft
            Aircraft newAircraft = new Aircraft(tailNumber, manufacturer, model, serialNumber, maxFlightRange, seatingCapacity);
            FileWriter writer = new FileWriter(DATA_FILE, true);
            writer.write(newAircraft.toJson() + "\n");
            writer.close();
            return true;

        } catch (IOException e) {
            System.out.println("Storage error: " + e.getMessage());
            return false;
        }
    }
}
