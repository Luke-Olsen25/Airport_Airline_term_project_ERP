package erp.FlightManagement;


import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class FlightManagementService {

    private final FlightFileRepository repository;

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public FlightManagementService(FlightFileRepository repository) {
        this.repository = repository;
    }

    public void handleScheduling(Scanner scanner) {
        System.out.println("\n========== SCHEDULE FLIGHT ==========");

        try {
            System.out.print("Flight ID: ");
            String flightId = scanner.nextLine().trim();

            System.out.print("Flight Number: ");
            String flightNumber = scanner.nextLine().trim();

            System.out.print("Airline: ");
            String airline = scanner.nextLine().trim();

            System.out.print("Origin: ");
            String origin = scanner.nextLine().trim();

            System.out.print("Destination: ");
            String destination = scanner.nextLine().trim();

            System.out.print("Departure (yyyy-MM-dd HH:mm): ");
            LocalDateTime departureTime = LocalDateTime.parse(
                    scanner.nextLine().trim(), DATE_FORMAT);

            System.out.print("Arrival (yyyy-MM-dd HH:mm): ");
            LocalDateTime arrivalTime = LocalDateTime.parse(
                    scanner.nextLine().trim(), DATE_FORMAT);

            System.out.print("Aircraft ID: ");
            String aircraftId = scanner.nextLine().trim();

            System.out.print("Gate ID: ");
            String gateId = scanner.nextLine().trim();

            System.out.print("Gate Airport: ");
            String gateAirport = scanner.nextLine().trim();

            System.out.print("Terminal: ");
            String terminal = scanner.nextLine().trim();

            Gate gate = new Gate(gateId, gateAirport, terminal);

            Flight flight = new Flight(
                    flightId,
                    flightNumber,
                    airline,
                    origin,
                    destination,
                    departureTime,
                    arrivalTime,
                    aircraftId,
                    gate
            );

            System.out.print("\nSchedule this flight? (Y/N): ");

            if (!scanner.nextLine().trim().equalsIgnoreCase("Y")) {
                System.out.println("Scheduling cancelled.");
                return;
            }

            scheduleFlight(flight);

            System.out.println(
                    "\nSUCCESS: Flight " + flightNumber + " was scheduled.");

        } catch (DateTimeParseException e) {
            System.out.println("\nERROR: Invalid date or time format.");

        } catch (IllegalArgumentException e) {
            System.out.println("\nERROR: " + e.getMessage());

        } catch (IOException e) {
            System.out.println("\nERROR: Unable to save or retrieve flight information.");
        }
    }

    public void scheduleFlight(Flight flight) throws IOException {
        validateFlight(flight);

        List<Flight> existingFlights = repository.findAll();

        for (Flight existing : existingFlights) {

            if (existing.getFlightId().equalsIgnoreCase(flight.getFlightId())) {
                throw new IllegalArgumentException(
                        "A flight with this ID already exists.");
            }

            if (overlaps(flight, existing)) {

                if (flight.getAircraftId().equalsIgnoreCase(
                        existing.getAircraftId())) {
                    throw new IllegalArgumentException(
                            "Aircraft is already assigned to another flight at this time.");
                }

                if (flight.getGate().getGateId().equalsIgnoreCase(
                        existing.getGate().getGateId())
                        && flight.getGate().getAirport().equalsIgnoreCase(
                        existing.getGate().getAirport())) {
                    throw new IllegalArgumentException(
                            "Gate is already assigned to another flight at this time.");
                }
            }
        }

        repository.save(flight);
    }

    private void validateFlight(Flight flight) {
        if (flight == null) {
            throw new IllegalArgumentException("Flight cannot be null.");
        }

        if (isBlank(flight.getFlightId())
                || isBlank(flight.getFlightNumber())
                || isBlank(flight.getAirline())
                || isBlank(flight.getOrigin())
                || isBlank(flight.getDestination())
                || isBlank(flight.getAircraftId())
                || flight.getGate() == null
                || isBlank(flight.getGate().getGateId())
                || isBlank(flight.getGate().getAirport())
                || isBlank(flight.getGate().getTerminal())) {
            throw new IllegalArgumentException(
                    "All flight fields are required.");
        }

        if (flight.getOrigin().equalsIgnoreCase(flight.getDestination())) {
            throw new IllegalArgumentException(
                    "Origin and destination cannot be the same.");
        }

        if (flight.getDepartureTime() == null
                || flight.getArrivalTime() == null
                || !flight.getArrivalTime().isAfter(flight.getDepartureTime())) {
            throw new IllegalArgumentException(
                    "Arrival time must be after departure time.");
        }
    }

    private boolean overlaps(Flight a, Flight b) {
        return a.getDepartureTime().isBefore(b.getArrivalTime())
                && b.getDepartureTime().isBefore(a.getArrivalTime());
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    public void showScheduledFlights() {
        System.out.println("\n========== SCHEDULED FLIGHTS ==========");

        try {
            List<Flight> flights = repository.findAll();

            if (flights.isEmpty()) {
                System.out.println("No scheduled flights found.");
                return;
            }

            for (Flight flight : flights) {
                System.out.println("--------------------------------");
                System.out.println("Flight ID: " + flight.getFlightId());
                System.out.println("Flight Number: " + flight.getFlightNumber());
                System.out.println("Airline: " + flight.getAirline());

                System.out.println("Route: " + flight.getOrigin()
                        + " -> " + flight.getDestination());

                System.out.println("Departure: " + flight.getDepartureTime());
                System.out.println("Arrival: " + flight.getArrivalTime());
                System.out.println("Aircraft: " + flight.getAircraftId());

                System.out.println("Gate: " + flight.getGate().getGateId());
                System.out.println("Gate Airport: " + flight.getGate().getAirport());
                System.out.println("Terminal: " + flight.getGate().getTerminal());

                System.out.println("Status: " + flight.getStatus());
            }

        } catch (IOException e) {
            System.out.println("ERROR: Unable to read flight information.");
        }
    }
}
