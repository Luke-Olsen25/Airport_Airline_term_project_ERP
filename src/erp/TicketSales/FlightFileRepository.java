package erp.TicketSales;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Reads flights from src/data/flights.txt (owned by Flight Management) and
 * the seats for sale from src/data/seats.txt (owned by Ticket Sales).
 * Only seats.txt is ever written by Ticket Sales. One record per line, fields separated by '|':
 *
 * flights.txt: flightNumber|origin|destination|date|departureTime|arrivalTime|tailNumber
 * seats.txt:   flightNumber|seatNumber|seatClass|price|AVAILABLE or SOLD
 */
public class FlightFileRepository implements IFlightRepository {
    private static final String FLIGHTS_FILE = "src/data/flights.txt";
    private static final String SEATS_FILE = "src/data/seats.txt";

    @Override
    public void initializeStorage() {
        try {
            File directory = new File("src/data");
            if (!directory.exists()) {
                directory.mkdirs();
            }
            File file = new File(SEATS_FILE);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            System.out.println("Storage initialization error: " + e.getMessage());
        }
    }

    @Override
    public List<Flight> loadFlights() {
        Map<String, Flight> flights = new LinkedHashMap<String, Flight>();
        for (String[] p : readRecords(FLIGHTS_FILE, 6)) {
            try {
                Flight f = new Flight(p[0], p[1], p[2], LocalDate.parse(p[3]),
                        LocalTime.parse(p[4]), LocalTime.parse(p[5]));
                flights.put(f.getFlightNumber(), f);
            } catch (RuntimeException e) {
                System.out.println("Skipping unreadable flight record: " + String.join("|", p));
            }
        }
        for (String[] p : readRecords(SEATS_FILE, 5)) {
            Flight f = flights.get(p[0]);
            if (f == null) continue;
            try {
                f.addSeat(new Seat(p[1], SeatClass.valueOf(p[2]), Double.parseDouble(p[3]), "SOLD".equals(p[4])));
            } catch (RuntimeException e) {
                System.out.println("Skipping unreadable seat record: " + String.join("|", p));
            }
        }
        return new ArrayList<Flight>(flights.values());
    }

    @Override
    public void saveSeats(List<Flight> flights) {
        try (FileWriter writer = new FileWriter(SEATS_FILE, false)) {
            for (Flight f : flights) {
                for (Seat s : f.getAllSeats()) {
                    writer.write(f.getFlightNumber() + "|" + s.getSeatNumber() + "|" + s.getSeatClass() + "|"
                            + String.format("%.2f", s.getPrice()) + "|" + (s.isAvailable() ? "AVAILABLE" : "SOLD")
                            + System.lineSeparator());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Storage write error: " + e.getMessage(), e);
        }
    }

    /** Splits each non-blank line of a file on '|'; skips lines with too few fields. */
    static List<String[]> readRecords(String file, int minFields) {
        List<String[]> result = new ArrayList<String[]>();
        try {
            if (!new File(file).exists()) return result;
            for (String line : Files.readAllLines(Paths.get(file))) {
                if (line.trim().isEmpty()) continue;
                String[] p = line.trim().split("\\|");
                if (p.length >= minFields) result.add(p);
            }
        } catch (IOException e) {
            System.out.println("Could not read " + file + ": " + e.getMessage());
        }
        return result;
    }
}
