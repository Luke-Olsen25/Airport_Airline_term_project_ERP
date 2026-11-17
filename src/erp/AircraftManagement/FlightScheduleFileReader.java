package erp.AircraftManagement;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FlightScheduleFileReader implements IFlightScheduleReader {
    private static final String FLIGHTS_FILE = "src/data/scheduled_flights.txt";
    private static final int FLIGHT_ID_INDEX = 0;
    private static final int TAIL_NUMBER_INDEX = 7;
    private static final int STATUS_INDEX = 11;

    @Override
    public List<String> findActiveFlightIds(String tailNumber) {
        List<String> activeFlights = new ArrayList<>();
        Path path = Paths.get(FLIGHTS_FILE);
        if (!Files.exists(path)) {
            return activeFlights;
        }

        try {
            for (String line : Files.readAllLines(path)) {
                if (line.isBlank()) continue;
                String[] data = line.split("\\|", -1);
                if (data.length <= STATUS_INDEX) continue;

                String flightTail = data[TAIL_NUMBER_INDEX].trim();
                String status = data[STATUS_INDEX].trim();

                if (flightTail.equalsIgnoreCase(tailNumber.trim())
                        && (status.equalsIgnoreCase("Scheduled") || status.equalsIgnoreCase("In-Flight"))) {
                    activeFlights.add(data[FLIGHT_ID_INDEX].trim());
                }
            }
        } catch (IOException e) {
            System.out.println("Flight schedule read error: " + e.getMessage());
        }
        return activeFlights;
    }
}
