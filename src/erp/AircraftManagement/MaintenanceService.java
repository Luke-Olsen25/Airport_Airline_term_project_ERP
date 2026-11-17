package erp.AircraftManagement;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MaintenanceService {
    private final IMaintenanceRepository maintenanceRepository;
    private static final String FLIGHTS_FILE = "src/data/scheduled_flights.txt";
    private static final int FLIGHT_ID_INDEX = 0;
    private static final int TAIL_NUMBER_INDEX = 7;
    private static final int FLIGHT_STATUS_INDEX = 11;
    private static final String CONFLICT_PREFIX = "FLIGHT CONFLICT: ";

    private final IAircraftRepository aircraftRepository;

    public MaintenanceService(IMaintenanceRepository maintenanceRepository, IAircraftRepository aircraftRepository) {
        this.maintenanceRepository = maintenanceRepository;
        this.aircraftRepository = aircraftRepository;
        this.maintenanceRepository.initializeStorage();
    }

    // ===================== BUSINESS LOGIC =====================

    public MaintenanceRecord scheduleMaintenance(String tailNumber, MaintenanceType type, String startDate, String description)
            throws IllegalStateException {

        Aircraft aircraft = aircraftRepository.findByTailNumber(tailNumber)
                .orElseThrow(() -> new IllegalArgumentException("Aircraft " + tailNumber + " is not registered."));

        if (aircraft.getStatus() == AircraftStatus.RETIRED) {
            throw new IllegalStateException("Aircraft " + tailNumber + " is RETIRED and cannot be maintained.");
        }
        if (maintenanceRepository.findScheduledByTailNumber(tailNumber).isPresent()) {
            throw new IllegalStateException("Aircraft " + tailNumber + " already has open maintenance scheduled.");
        }

        List<String> conflicts = findActiveFlightIds(tailNumber);
        if (!conflicts.isEmpty()) {
            throw new IllegalStateException(CONFLICT_PREFIX + "Aircraft " + tailNumber
                    + " is assigned to active flight(s): " + String.join(", ", conflicts)
                    + ". Maintenance cannot be scheduled.");
        }

        AircraftStatus previousStatus = aircraft.getStatus();
        aircraft.setStatus(AircraftStatus.IN_MAINTENANCE);
        if (!aircraftRepository.updateAircraft(aircraft)) {
            throw new IllegalStateException("Failed to update aircraft status in storage.");
        }

        MaintenanceRecord record = new MaintenanceRecord(tailNumber, type, startDate, description);
        if (!maintenanceRepository.save(record)) {
            // Roll back so the two files stay consistent
            aircraft.setStatus(previousStatus);
            aircraftRepository.updateAircraft(aircraft);
            throw new IllegalStateException("Failed to write maintenance log. Aircraft status rolled back.");
        }
        return record;
    }

    public MaintenanceRecord completeMaintenance(String tailNumber) {
        Aircraft aircraft = aircraftRepository.findByTailNumber(tailNumber)
                .orElseThrow(() -> new IllegalArgumentException("Aircraft " + tailNumber + " is not registered."));

        MaintenanceRecord record = maintenanceRepository.findScheduledByTailNumber(tailNumber)
                .orElseThrow(() -> new IllegalStateException("No SCHEDULED maintenance found for " + tailNumber + "."));

        AircraftStatus previousStatus = aircraft.getStatus();
        aircraft.setStatus(AircraftStatus.ACTIVE);
        if (!aircraftRepository.updateAircraft(aircraft)) {
            throw new IllegalStateException("Failed to update aircraft status in storage.");
        }

        record.markCompleted();
        if (!maintenanceRepository.update(record)) {
            aircraft.setStatus(previousStatus);
            aircraftRepository.updateAircraft(aircraft);
            throw new IllegalStateException("Failed to update maintenance log. Aircraft status rolled back.");
        }
        return record;
    }

    /**
     * Reads scheduled_flights.txt (pipe-delimited) and returns IDs of flights
     * that use this aircraft and have status "Scheduled" or "In-Flight".
     */
    private List<String> findActiveFlightIds(String tailNumber) {
        List<String> activeFlights = new ArrayList<>();
        Path path = Paths.get(FLIGHTS_FILE);
        if (!Files.exists(path)) {
            return activeFlights; // No schedule file means no flights
        }

        try {
            for (String line : Files.readAllLines(path)) {
                if (line.isBlank()) continue;
                String[] data = line.split("\\|", -1);
                if (data.length <= FLIGHT_STATUS_INDEX) continue;

                String flightTail = data[TAIL_NUMBER_INDEX].trim();
                String status = data[FLIGHT_STATUS_INDEX].trim();

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

    // ===================== TERMINAL HANDLERS =====================

    public void handleScheduleMaintenance(Scanner scanner) {
        System.out.println("\n--- SCHEDULE AIRCRAFT MAINTENANCE ---");
        System.out.print("Enter Tail Number (e.g., N12345): ");
        String tailNumber = scanner.nextLine().trim();

        MaintenanceType type = promptMaintenanceType(scanner);
        if (type == null) {
            System.out.println("\n[ABORTED] Invalid maintenance type selection.");
            pause(scanner);
            return;
        }

        System.out.print("Enter Start Date (YYYY-MM-DD, blank for today): ");
        String dateInput = scanner.nextLine().trim();
        String startDate;
        try {
            startDate = dateInput.isEmpty() ? LocalDate.now().toString() : LocalDate.parse(dateInput).toString();
        } catch (DateTimeParseException e) {
            System.out.println("\n[ABORTED] Invalid date format: " + dateInput);
            pause(scanner);
            return;
        }

        System.out.print("Enter Description: ");
        String description = scanner.nextLine();

        try {
            MaintenanceRecord record = scheduleMaintenance(tailNumber, type, startDate, description);
            System.out.println("\n[SUCCESS] Maintenance " + record.getRecordId() + " scheduled for " + tailNumber
                    + ". Aircraft status set to IN_MAINTENANCE.");
        } catch (IllegalStateException e) {
            if (e.getMessage() != null && e.getMessage().startsWith(CONFLICT_PREFIX)) {
                System.out.println("\n[CONFLICT] " + e.getMessage().substring(CONFLICT_PREFIX.length()));
            } else {
                System.out.println("\n[ERROR] " + e.getMessage());
            }
        } catch (IllegalArgumentException e) {
            System.out.println("\n[ERROR] " + e.getMessage());
        }
        pause(scanner);
    }

    public void handleCompleteMaintenance(Scanner scanner) {
        System.out.println("\n--- COMPLETE AIRCRAFT MAINTENANCE ---");
        System.out.print("Enter Tail Number (e.g., N12345): ");
        String tailNumber = scanner.nextLine().trim();

        try {
            MaintenanceRecord record = completeMaintenance(tailNumber);
            System.out.println("\n[SUCCESS] Maintenance " + record.getRecordId() + " completed. Aircraft "
                    + tailNumber + " is now ACTIVE.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("\n[ERROR] " + e.getMessage());
        }
        pause(scanner);
    }

    private MaintenanceType promptMaintenanceType(Scanner scanner) {
        MaintenanceType[] types = MaintenanceType.values();
        System.out.println("Select Maintenance Type:");
        for (int i = 0; i < types.length; i++) {
            System.out.println("     " + (i + 1) + ". " + types[i].name());
        }
        System.out.print("===> ");
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            return (choice >= 1 && choice <= types.length) ? types[choice - 1] : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void pause(Scanner scanner) {
        System.out.print("\nPress Enter to return to Main Menu...");
        scanner.nextLine();
    }
}
