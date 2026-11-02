package erp.AircraftManagement;

import java.util.Scanner;

public class AircraftManagementService {
    private IAircraftRepository repository;

    public AircraftManagementService(IAircraftRepository repository) {
        this.repository = repository;
        this.repository.initializeStorage();
    }

    public void handleRegistration(Scanner scanner) {
        System.out.println("\n--- AIRCRAFT REGISTRATION ---");
        System.out.print("Enter Tail Number (e.g., N12345): ");
        String tailNumber = scanner.nextLine();

        System.out.print("Enter Manufacturer (e.g., Boeing): ");
        String manufacturer = scanner.nextLine();

        System.out.print("Enter Model (e.g., 737-800): ");
        String model = scanner.nextLine();

        System.out.print("Enter Serial Number (e.g., 185-0765): ");
        String serialNumber = scanner.nextLine();

        System.out.print("Enter Max Flight Range (e.g., 3100): ");
        int maxFlightRange = scanner.nextInt();

        System.out.print("Enter Seating Capacity (e.g., 135): ");
        int seatingCapacity = scanner.nextInt();

        scanner.nextLine(); // Consume newline

        try {
            Aircraft aircraft = new Aircraft(tailNumber, manufacturer, model, serialNumber, maxFlightRange, seatingCapacity);
            boolean success = registerNewAircraft(aircraft);

            if (success) {
                System.out.println("\n[SUCCESS] Aircraft " + tailNumber + " registered.");
            } else {
                System.out.println("\n[ERROR] Registration failed. Tail number or Serial Number may already exist.");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("\n[ABORTED] " + e.getMessage());
        }

        System.out.print("\nPress Enter to return to Main Menu...");
        scanner.nextLine();
    }

    public boolean registerNewAircraft(Aircraft aircraft) throws IllegalArgumentException {
        // Abort registration if negative values are input
        if (aircraft.getMaxFlightRange() < 0) {
            throw new IllegalArgumentException("Registration Aborted: Max Flight Range cannot be negative (" + aircraft.getMaxFlightRange() + ").");
        }
        if (aircraft.getSeatingCapacity() < 0) {
            throw new IllegalArgumentException("Registration Aborted: Seating Capacity cannot be negative (" + aircraft.getSeatingCapacity() + ").");
        }

        if (repository.existsByIdentifiers(aircraft.getTailNumber(), aircraft.getSerialNumber())) {
            return false;
        }
        return repository.save(aircraft);
    }
}
