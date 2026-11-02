package erp.TicketSales;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Reads flights from src/data/flights.json (owned by Flight Management) and
 * the seats for sale from src/data/seats.json (owned by Ticket Sales).
 * Only seats.json is ever written by Ticket Sales.
 * Files hold a JSON array with one object per line, like aircrafts.json.
 */
public class FlightFileRepository implements IFlightRepository {
    private static final String FLIGHTS_FILE = "src/data/flights.json";
    private static final String SEATS_FILE = "src/data/seats.json";

    @Override
    public void initializeStorage() {
        try {
            File directory = new File("src/data");
            if (!directory.exists()) {
                directory.mkdirs();
            }
            File file = new File(SEATS_FILE);
            if (!file.exists() || file.length() == 0) {
                try (FileWriter writer = new FileWriter(file)) {
                    writer.write("[]");
                }
            }
        } catch (IOException e) {
            System.out.println("Storage initialization error: " + e.getMessage());
        }
    }

    @Override
    public List<Flight> loadFlights() {
        Map<String, Flight> flights = new LinkedHashMap<String, Flight>();
        for (String line : JsonLines.readObjects(FLIGHTS_FILE)) {
            try {
                Flight f = new Flight(JsonLines.getField(line, "flightNumber"), JsonLines.getField(line, "origin"),
                        JsonLines.getField(line, "destination"), LocalDate.parse(JsonLines.getField(line, "date")),
                        LocalTime.parse(JsonLines.getField(line, "departureTime")), LocalTime.parse(JsonLines.getField(line, "arrivalTime")));
                flights.put(f.getFlightNumber(), f);
            } catch (RuntimeException e) {
                System.out.println("Skipping unreadable flight record: " + line);
            }
        }
        for (String line : JsonLines.readObjects(SEATS_FILE)) {
            Flight f = flights.get(JsonLines.getField(line, "flightNumber"));
            if (f == null) continue;
            try {
                f.addSeat(new Seat(JsonLines.getField(line, "seatNumber"), SeatClass.valueOf(JsonLines.getField(line, "seatClass")),
                        Double.parseDouble(JsonLines.getField(line, "price")), "SOLD".equals(JsonLines.getField(line, "status"))));
            } catch (RuntimeException e) {
                System.out.println("Skipping unreadable seat record: " + line);
            }
        }
        return new ArrayList<Flight>(flights.values());
    }

    @Override
    public void saveSeats(List<Flight> flights) {
        StringBuilder content = new StringBuilder("[");
        boolean first = true;
        for (Flight f : flights) {
            for (Seat s : f.getAllSeats()) {
                content.append(first ? "\n  " : ",\n  ");
                content.append(String.format("{\"flightNumber\": \"%s\", \"seatNumber\": \"%s\", \"seatClass\": \"%s\", \"price\": %.2f, \"status\": \"%s\"}",
                        f.getFlightNumber(), s.getSeatNumber(), s.getSeatClass().name(), s.getPrice(),
                        s.isAvailable() ? "AVAILABLE" : "SOLD"));
                first = false;
            }
        }
        content.append(first ? "]" : "\n]");
        try (FileWriter writer = new FileWriter(SEATS_FILE, false)) {
            writer.write(content.toString());
        } catch (IOException e) {
            throw new RuntimeException("Storage write error: " + e.getMessage(), e);
        }
    }
}
