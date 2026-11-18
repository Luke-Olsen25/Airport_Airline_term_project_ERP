package erp.FlightManagement;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;
import erp.AircraftManagement.Aircraft;
import erp.AircraftManagement.AircraftStatus;
import erp.AircraftManagement.IAircraftRepository;

public class FlightManagementService {

    private final FlightFileRepository repository;
    
    private final IAircraftRepository aircraftRepository;
    
    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            
    public FlightManagementService(FlightFileRepository repository, IAircraftRepository aircraftRepository) {
        this.repository = repository;
        this.aircraftRepository = aircraftRepository;
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

            System.out.print("Aircraft Tail Number: ");
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
                    gate);

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

    public void handleUpdate(Scanner scanner) {
        try {
            // 1. Ask for the Flight ID.
            System.out.print("Enter Flight ID to update: ");
            String flightId = scanner.nextLine().trim();

            // 2. Find the existing flight.
            List<Flight> flights = repository.findAll();

            Flight existing = null;

            for (Flight flight : flights) {
                if (flight.getFlightId().equalsIgnoreCase(flightId)) {
                    existing = flight;
                    break;
                }
            }

            if (existing == null) {
                System.out.println("Flight not found.");
                return;
            }

            // 3. Display current flight information.
            System.out.println("\nCurrent Flight Information:");
            System.out.println("Flight Number: " + existing.getFlightNumber());
            System.out.println("Airline: " + existing.getAirline());
            System.out.println("Origin: " + existing.getOrigin());
            System.out.println("Destination: " + existing.getDestination());
            System.out.println("Departure: " + existing.getDepartureTime());
            System.out.println("Arrival: " + existing.getArrivalTime());
            System.out.println("Aircraft Tail Number: " + existing.getAircraftTailNumber());
            System.out.println("Gate: " + existing.getGate().getGateId());
            System.out.println("Status: " + existing.getStatus());

            // 4. Collect updated information.
            System.out.println("\nEnter new information (leave blank to keep current value).");

            String flightNumber = readOrKeep(
                    scanner, "Flight Number", existing.getFlightNumber());

            String airline = readOrKeep(
                    scanner, "Airline", existing.getAirline());

            String origin = readOrKeep(
                    scanner, "Origin", existing.getOrigin());

            String destination = readOrKeep(
                    scanner, "Destination", existing.getDestination());

            String departureInput = readOrKeep(
                    scanner, "Departure (yyyy-MM-dd HH:mm)",
                    existing.getDepartureTime().format(
                            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));

            String arrivalInput = readOrKeep(
                    scanner, "Arrival (yyyy-MM-dd HH:mm)",
                    existing.getArrivalTime().format(
                            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));

            String aircraftId = readOrKeep(
                    scanner, "Aircraft Tail Number", existing.getAircraftTailNumber());

            String gateId = readOrKeep(
                    scanner, "Gate ID", existing.getGate().getGateId());

            String gateAirport = readOrKeep(
                    scanner, "Gate Airport", existing.getGate().getAirport());

            String terminal = readOrKeep(
                    scanner, "Terminal", existing.getGate().getTerminal());

            String status = readOrKeep(
                    scanner, "Status", existing.getStatus());

            // 5. Create the updated Flight object.
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            LocalDateTime departureTime = LocalDateTime.parse(departureInput, formatter);

            LocalDateTime arrivalTime = LocalDateTime.parse(arrivalInput, formatter);

            Gate gate = new Gate(gateId, gateAirport, terminal);

            Flight updatedFlight = new Flight(
                    existing.getFlightId(),
                    flightNumber,
                    airline,
                    origin,
                    destination,
                    departureTime,
                    arrivalTime,
                    aircraftId,
                    gate,
                    status);

            // 6. Confirm and update.
            System.out.print("\nConfirm flight update? (Y/N): ");
            String confirmation = scanner.nextLine().trim();

            if (!confirmation.equalsIgnoreCase("Y")) {
                System.out.println("Flight update cancelled.");
                return;
            }

            updateFlight(updatedFlight);

            System.out.println("Flight updated successfully!");

        } catch (DateTimeParseException e) {
            System.out.println("Invalid date/time format. Use yyyy-MM-dd HH:mm.");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println("Error updating flight: " + e.getMessage());
        }
    }

    public void scheduleFlight(Flight flight) throws IOException {
        validateFlight(flight);
        validateAircraftAvailability(flight.getAircraftTailNumber());
        
        List<Flight> existingFlights = repository.findAll();

        for (Flight existing : existingFlights) {

            if (existing.getFlightId().equalsIgnoreCase(flight.getFlightId())) {
                throw new IllegalArgumentException(
                        "A flight with this ID already exists.");
            }

            if (overlaps(flight, existing)) {

                if (flight.getAircraftTailNumber().equalsIgnoreCase(
                        existing.getAircraftTailNumber())) {
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

    public void updateFlight(Flight updatedFlight) throws IOException {
        validateFlight(updatedFlight);
        validateAircraftAvailability(updatedFlight.getAircraftTailNumber());
        List<Flight> existingFlights = repository.findAll();

        for (Flight existing : existingFlights) {

            // Skip the flight being updated.
            if (existing.getFlightId().equalsIgnoreCase(
                    updatedFlight.getFlightId())) {
                continue;
            }

            // Check for aircraft scheduling conflicts.
            if (updatedFlight.getAircraftTailNumber().equalsIgnoreCase(
                    existing.getAircraftTailNumber()) && overlaps(updatedFlight, existing)) {

                throw new IllegalArgumentException("Aircraft is already assigned to another flight at this time.");
            }

            // Check for gate scheduling conflicts.
            if (updatedFlight.getGate().getGateId().equalsIgnoreCase(
                    existing.getGate().getGateId())
                    && updatedFlight.getGate().getAirport().equalsIgnoreCase(
                            existing.getGate().getAirport())
                    && overlaps(updatedFlight, existing)) {

                throw new IllegalArgumentException(
                        "Gate is already assigned to another flight at this time.");
            }

        }

        repository.update(updatedFlight);

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
                || isBlank(flight.getAircraftTailNumber())
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
    
    private void validateAircraftAvailability(String tailNumber) {
        Aircraft aircraft = aircraftRepository.findByTailNumber(tailNumber)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                            "Aircraft " + tailNumber + " is not registered."));
        if (aircraft.getStatus() != AircraftStatus.ACTIVE) {
            throw new IllegalArgumentException(
                    "Aircraft " + tailNumber + " is unavailable. Status: "
                            + aircraft.getStatus());
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
                System.out.println("Aircraft: " + flight.getAircraftTailNumber());

                System.out.println("Gate: " + flight.getGate().getGateId());
                System.out.println("Gate Airport: " + flight.getGate().getAirport());
                System.out.println("Terminal: " + flight.getGate().getTerminal());

                System.out.println("Status: " + flight.getStatus());
            }

        } catch (IOException e) {
            System.out.println("ERROR: Unable to read flight information.");
        }
    }

    private String readOrKeep(
            Scanner scanner, String field, String currentValue) {

        System.out.print(field + " [" + currentValue + "]: ");

        String input = scanner.nextLine().trim();

        return input.isEmpty() ? currentValue : input;
    }
}
