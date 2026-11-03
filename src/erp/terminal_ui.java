package erp;

import erp.AircraftManagement.AircraftFileRepository;
import erp.AircraftManagement.AircraftManagementService;
import erp.TicketSales.FlightFileRepository;
import erp.TicketSales.TicketFileRepository;
import erp.TicketSales.TicketSalesService;
import erp.TicketSales.TicketSalesTerminal;
import java.util.Scanner;

public class terminal_ui {

    private static AircraftManagementService aircraftService =
            new AircraftManagementService(new AircraftFileRepository());

    private static TicketSalesTerminal ticketSalesTerminal =
            new TicketSalesTerminal(new TicketSalesService(new FlightFileRepository(), new TicketFileRepository()));

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("MAIN                   ERP Main Menu");
            System.out.println("\nSelect one of the following:\n");
            System.out.println("     1. Register new aircraft");
            System.out.println("     2. Purchase ticket");
            System.out.println("\n     90. Sign off\n");
            System.out.print("Selection or command\n===> ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    aircraftService.handleRegistration(scanner);
                    break;
                case "2":
                    ticketSalesTerminal.handlePurchaseTicket(scanner);
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
