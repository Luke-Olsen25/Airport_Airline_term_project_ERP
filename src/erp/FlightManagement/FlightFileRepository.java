package erp.FlightManagement;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FlightFileRepository {

    private final Path filePath = Path.of("src", "data", "scheduled_flights.txt");

    public void save(Flight flight) throws IOException {
        Files.createDirectories(filePath.getParent());

        String record = String.join("|",
                flight.getFlightId(),
                flight.getFlightNumber(),
                flight.getAirline(),
                flight.getOrigin(),
                flight.getDestination(),
                flight.getDepartureTime().toString(),
                flight.getArrivalTime().toString(),
                flight.getAircraftId(),
                flight.getGate().getGateId(),
                flight.getGate().getAirport(),
                flight.getGate().getTerminal(),
                flight.getStatus()
        );

        Files.writeString(
                filePath,
                record + System.lineSeparator(),
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
        );
    }

    public List<Flight> findAll() throws IOException {
        List<Flight> flights = new ArrayList<>();

        if (!Files.exists(filePath)) {
            return flights;
        }

        List<String> records = Files.readAllLines(filePath);

        for (String record : records) {
            String[] fields = record.split("\\|", -1);

            if (fields.length != 12) {
                continue;
            }

            Gate gate = new Gate(
                    fields[8],
                    fields[9],
                    fields[10]
            );

            Flight flight = new Flight(
                    fields[0],
                    fields[1],
                    fields[2],
                    fields[3],
                    fields[4],
                    LocalDateTime.parse(fields[5]),
                    LocalDateTime.parse(fields[6]),
                    fields[7],
                    gate,
                    fields[11]
            );

            flights.add(flight);
        }

        return flights;
    }
}
