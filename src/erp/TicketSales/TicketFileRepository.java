package erp.TicketSales;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Stores issued tickets in src/data/tickets.txt, one per line, fields separated by '|':
 * ticketNumber|flightId|flightNumber|flightDate|origin|destination|seatNumber|seatClass|
 * passengerName|passengerEmail|passengerPhone|amountPaid|paymentMethod|issuedAt|status|cancelledAt
 * Iteration 1 lines (13 fields, no flightId or status) are still read, as ACTIVE tickets.
 */
public class TicketFileRepository implements ITicketRepository {
    private static final String DATA_FILE = "src/data/tickets.txt";

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
    public String nextTicketNumber() {
        int max = 0;
        for (Ticket t : loadAll()) {
            try {
                max = Math.max(max, Integer.parseInt(t.getTicketNumber().substring(2)));
            } catch (RuntimeException ignored) {
                // skip malformed numbers
            }
        }
        return String.format("TK%06d", max + 1);
    }

    /** Appends the ticket as one new line at the end of the file. */
    @Override
    public boolean save(Ticket t) {
        try (FileWriter writer = new FileWriter(DATA_FILE, true)) {
            writer.write(toRecord(t) + System.lineSeparator());
            return true;
        } catch (IOException e) {
            System.out.println("Storage write error: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Ticket findByNumber(String ticketNumber) {
        if (ticketNumber == null) return null;
        for (Ticket t : loadAll()) {
            if (t.getTicketNumber().equalsIgnoreCase(ticketNumber.trim())) return t;
        }
        return null;
    }

    /** Rewrites the file with the changed ticket in place of its old line. */
    @Override
    public boolean update(Ticket changed) {
        List<Ticket> all = loadAll();
        try (FileWriter writer = new FileWriter(DATA_FILE, false)) {
            for (Ticket t : all) {
                Ticket out = t.getTicketNumber().equalsIgnoreCase(changed.getTicketNumber()) ? changed : t;
                writer.write(toRecord(out) + System.lineSeparator());
            }
            return true;
        } catch (IOException e) {
            System.out.println("Storage write error: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Ticket> loadAll() {
        List<Ticket> result = new ArrayList<Ticket>();
        for (String[] p : FlightFileRepository.readRecords(DATA_FILE, 13)) {
            try {
                result.add(p.length >= 16 ? fromRecord(p) : fromIteration1Record(p));
            } catch (RuntimeException e) {
                System.out.println("Skipping unreadable ticket record: " + String.join("|", p));
            }
        }
        return result;
    }

    private static String toRecord(Ticket t) {
        return String.join("|", t.getTicketNumber(), t.getFlightId(), t.getFlightNumber(),
                t.getFlightDate().toString(), t.getOrigin(), t.getDestination(), t.getSeatNumber(),
                t.getSeatClass().name(), t.getPassengerName(), t.getPassengerEmail(), t.getPassengerPhone(),
                String.format("%.2f", t.getAmountPaid()), t.getPaymentMethod().name(),
                t.getIssuedAt().withNano(0).toString(), t.getStatus().name(),
                t.getCancelledAt() == null ? "" : t.getCancelledAt().withNano(0).toString());
    }

    private static Ticket fromRecord(String[] p) {
        return new Ticket(p[0], p[1], p[2], LocalDate.parse(p[3]), p[4], p[5], p[6],
                SeatClass.valueOf(p[7]), p[8], p[9], p[10], Double.parseDouble(p[11]),
                PaymentMethod.valueOf(p[12]), LocalDateTime.parse(p[13]), TicketStatus.valueOf(p[14]),
                p[15].isEmpty() ? null : LocalDateTime.parse(p[15]));
    }

    /** Iteration 1 format had no flightId (the flight number was the key) and no status. */
    private static Ticket fromIteration1Record(String[] p) {
        return new Ticket(p[0], p[1], p[1], LocalDate.parse(p[2]), p[3], p[4], p[5],
                SeatClass.valueOf(p[6]), p[7], p[8], p[9], Double.parseDouble(p[10]),
                PaymentMethod.valueOf(p[11]), LocalDateTime.parse(p[12]), TicketStatus.ACTIVE, null);
    }
}
