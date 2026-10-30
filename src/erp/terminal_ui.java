package erp;

import java.util.Scanner;

public class terminal_ui {

    public static void main(String[] args) {
        aircraft_mngt.initializeStorage();
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("MAIN                   ERP Main Menu");
            System.out.println("\nSelect one of the following:\n");
            System.out.println("     1. Register new aircraft");
            System.out.println("     2. (Placeholder)");
            System.out.println("\n     90. Sign off\n");
            System.out.print("Selection or command\n===> ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    handleRegistration(scanner);
                    break;
                case "2":
                    System.out.println("\n(Placeholder). Press Enter to return.");
                    scanner.nextLine();
                    break;
                case "90":
                    running = false;
                    System.out.println("Signing off...");
                    break;
                default:
                    System.out.println("\nInvalid selection. Press Enter to try again.");
                    scanner.nextLine();
            }
        }
        scanner.close();
    }

    private static void handleRegistration(Scanner scanner) {
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

        // Consume the newline character
        scanner.nextLine();


        boolean success = aircraft_mngt.registerAircraft(tailNumber, manufacturer, model, serialNumber, maxFlightRange, seatingCapacity);

        if (success) {
            System.out.println("\n[SUCCESS] Aircraft " + tailNumber + " registered.");
        } else {
            System.out.println("\n[ERROR] Registration failed. Tail number " + tailNumber + " may already exist.");
        }

        System.out.print("\nPress Enter to return to Main Menu...");
        scanner.nextLine();
    }
}