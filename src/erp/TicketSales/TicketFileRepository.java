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

/** Stores issued tickets in src/data/tickets.json, one JSON object per line. */
public class TicketFileRepository implements ITicketRepository {
    private static final String DATA_FILE = "src/data/tickets.json";

    @Override
    public void initializeStorage() {
        try {
            File directory = new File("src/data");
            if (!directory.exists()) {
                directory.mkdirs();
            }
            File file = new File(DATA_FILE);
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

    /** Inserts the ticket before the closing bracket, the same way AircraftFileRepository does. */
    @Override
    public boolean save(Ticket ticket) {
        try {
            String content = new String(Files.readAllBytes(Paths.get(DATA_FILE))).trim();
            if (content.isEmpty() || content.equals("[]")) {
                content = "[\n  " + ticket.toJson() + "\n]";
            } else {
                int lastBracketIndex = content.lastIndexOf("]");
                String prefix = content.substring(0, lastBracketIndex).trim();
                prefix += prefix.endsWith("[") ? "\n  " : ",\n  ";
                content = prefix + ticket.toJson() + "\n]";
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

    @Override
    public List<Ticket> loadAll() {
        List<Ticket> result = new ArrayList<Ticket>();
        for (String line : JsonLines.readObjects(DATA_FILE)) {
            try {
                result.add(new Ticket(field(line, "ticketNumber"), field(line, "flightNumber"),
                        LocalDate.parse(field(line, "flightDate")), field(line, "origin"), field(line, "destination"),
                        field(line, "seatNumber"), SeatClass.valueOf(field(line, "seatClass")),
                        field(line, "passengerName"), field(line, "passengerEmail"), field(line, "passengerPhone"),
                        Double.parseDouble(field(line, "amountPaid")), PaymentMethod.valueOf(field(line, "paymentMethod")),
                        LocalDateTime.parse(field(line, "issuedAt"))));
            } catch (RuntimeException e) {
                System.out.println("Skipping unreadable ticket record: " + line);
            }
        }
        return result;
    }

    private static String field(String line, String key) {
        return JsonLines.getField(line, key);
    }
}
