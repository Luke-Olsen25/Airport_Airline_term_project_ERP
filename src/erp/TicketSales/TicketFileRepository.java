package erp.TicketSales;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Stores issued tickets in src/data/tickets.txt, one per line, fields separated by '|':
 * ticketNumber|flightNumber|flightDate|origin|destination|seatNumber|seatClass|
 * passengerName|passengerEmail|passengerPhone|amountPaid|paymentMethod|issuedAt
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
        String line = String.join("|", t.getTicketNumber(), t.getFlightNumber(), t.getFlightDate().toString(),
                t.getOrigin(), t.getDestination(), t.getSeatNumber(), t.getSeatClass().name(),
                t.getPassengerName(), t.getPassengerEmail(), t.getPassengerPhone(),
                String.format("%.2f", t.getAmountPaid()), t.getPaymentMethod().name(),
                t.getIssuedAt().withNano(0).toString());
        try (FileWriter writer = new FileWriter(DATA_FILE, true)) {
            writer.write(line + System.lineSeparator());
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
                result.add(new Ticket(p[0], p[1], LocalDate.parse(p[2]), p[3], p[4], p[5],
                        SeatClass.valueOf(p[6]), p[7], p[8], p[9], Double.parseDouble(p[10]),
                        PaymentMethod.valueOf(p[11]), LocalDateTime.parse(p[12])));
            } catch (RuntimeException e) {
                System.out.println("Skipping unreadable ticket record: " + String.join("|", p));
            }
        }
        return result;
    }
}
