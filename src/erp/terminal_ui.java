package erp;

import erp.AircraftManagement.AircraftFileRepository;
import erp.AircraftManagement.AircraftManagementService;
import erp.FlightManagement.FlightManagementService;
import erp.HumanResources.EmployeeFileRepository;
import erp.HumanResources.HumanResourcesService;
import erp.TicketSales.FlightFileRepository;
import erp.TicketSales.TicketFileRepository;
import erp.TicketSales.TicketSalesService;
import erp.TicketSales.TicketSalesTerminal;
import java.util.Scanner;

public class terminal_ui {

    private static AircraftManagementService aircraftService =
            new AircraftManagementService(new AircraftFileRepository());

    private static HumanResourcesService hrService =
            new HumanResourcesService(new EmployeeFileRepository());

    private static FlightManagementService flightService =
            new FlightManagementService(new erp.FlightManagement.FlightFileRepository());

    private static TicketSalesTerminal ticketSalesTerminal =
            new TicketSalesTerminal(new TicketSalesService(new FlightFileRepository(), new TicketFileRepository()));

    public static void run() {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("MAIN                   ERP Main Menu");
            System.out.println("\nSelect one of the following:\n");
            System.out.println("     1. Register New Aircraft");
            System.out.println("     2. View Registered Aircrafts");
            System.out.println("     3. Purchase Ticket");
            System.out.println("     4. Assign Gate");
            System.out.println("     5. View Gate Assignments");
            System.out.println("     6. Hire New employee");
            System.out.println("     7. Schedule Flight");
            System.out.println("     8. View Scheduled Flights");
            System.out.println("\n     90. Sign Off\n");
            System.out.print("Selection or command\n===> ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    aircraftService.handleRegistration(scanner);
                    break;
                case "2":
                    aircraftService.handleViewAircrafts(scanner);
                    break;
                case "3":
                    ticketSalesTerminal.handlePurchaseTicket(scanner);
                    break;
                case "4":
                    airport_ops.assignGate(scanner);
                    break;
                case "5":
                    airport_ops.showAssignments();
                    break;
                case "6":
                    hrService.handleHiring(scanner);
                    break;
                case "7":
                    flightService.handleScheduling(scanner);
                    break;
                case "8":
                    flightService.showScheduledFlights();
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
}