package erp.TicketSales;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Adapter between Ticket Sales and the files it depends on (Protected Variations):
 * - Flights come from Flight Management's src/data/scheduled_flights.txt, read through
 *   Flight Management's own repository, so their file format stays their concern.
 * - Seats come from src/data/seats.txt (owned by Ticket Sales), one per line:
 *   flightId|seatNumber|seatClass|price|AVAILABLE or SOLD
 * A flight with no seats yet gets the default seat map; it is saved with the first sale.
 */
public class FlightFileRepository implements IFlightRepository {
    private static final String SEATS_FILE = "src/data/seats.txt";

    private final erp.FlightManagement.FlightFileRepository schedule;

    public FlightFileRepository() {
        this(new erp.FlightManagement.FlightFileRepository());
    }

    public FlightFileRepository(erp.FlightManagement.FlightFileRepository schedule) {
        this.schedule = schedule;
    }

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
        for (erp.FlightManagement.Flight f : readSchedule()) {
            Flight flight = new Flight(f.getFlightId(), f.getFlightNumber(), f.getAirline(), f.getOrigin(),
                    f.getDestination(), f.getDepartureTime(), f.getArrivalTime(), f.getStatus());
            flights.put(flight.getFlightId().toUpperCase(), flight);
        }
        for (String[] p : readRecords(SEATS_FILE, 5)) {
            Flight f = flights.get(p[0].toUpperCase());
            if (f == null) continue;
            try {
                f.addSeat(new Seat(p[1], SeatClass.valueOf(p[2]), Double.parseDouble(p[3]), "SOLD".equals(p[4])));
            } catch (RuntimeException e) {
                System.out.println("Skipping unreadable seat record: " + String.join("|", p));
            }
        }
        for (Flight f : flights.values()) {
            if (!f.hasSeatMap()) f.createDefaultSeatMap();
        }
        return new ArrayList<Flight>(flights.values());
    }

    @Override
    public void saveSeats(List<Flight> flights) {
        List<String> lines = new ArrayList<String>();
        // Keep seats of flights that are no longer on the schedule, so no sales history is lost.
        for (String[] p : readRecords(SEATS_FILE, 5)) {
            boolean known = false;
            for (Flight f : flights) {
                if (f.getFlightId().equalsIgnoreCase(p[0])) known = true;
            }
            if (!known) lines.add(String.join("|", p));
        }
        for (Flight f : flights) {
            for (Seat s : f.getAllSeats()) {
                lines.add(f.getFlightId() + "|" + s.getSeatNumber() + "|" + s.getSeatClass() + "|"
                        + String.format("%.2f", s.getPrice()) + "|" + (s.isAvailable() ? "AVAILABLE" : "SOLD"));
            }
        }
        try (FileWriter writer = new FileWriter(SEATS_FILE, false)) {
            for (String line : lines) {
                writer.write(line + System.lineSeparator());
            }
        } catch (IOException e) {
            throw new RuntimeException("Storage write error: " + e.getMessage(), e);
        }
    }

    private List<erp.FlightManagement.Flight> readSchedule() {
        try {
            return schedule.findAll();
        } catch (IOException | RuntimeException e) {
            System.out.println("Could not read scheduled flights: " + e.getMessage());
            return new ArrayList<erp.FlightManagement.Flight>();
        }
    }

    /** Splits each non-blank line of a file on '|'; skips lines with too few fields. */
    static List<String[]> readRecords(String file, int minFields) {
        List<String[]> result = new ArrayList<String[]>();
        try {
            if (!new File(file).exists()) return result;
            for (String line : Files.readAllLines(Paths.get(file))) {
                if (line.trim().isEmpty()) continue;
                String[] p = line.trim().split("\\|", -1);
                if (p.length >= minFields) result.add(p);
            }
        } catch (IOException e) {
            System.out.println("Could not read " + file + ": " + e.getMessage());
        }
        return result;
    }
}
