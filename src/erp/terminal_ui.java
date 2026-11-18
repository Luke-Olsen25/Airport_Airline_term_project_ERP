package erp;

import erp.AircraftManagement.AircraftFileRepository;
import erp.AircraftManagement.AircraftManagementService;
import erp.AircraftManagement.MaintenanceFileRepository;
import erp.AircraftManagement.MaintenanceService;
import erp.FlightManagement.FlightManagementService;
import erp.HumanResources.EmployeeFileRepository;
import erp.HumanResources.HumanResourcesService;
import erp.TicketSales.FlightFileRepository;
import erp.TicketSales.TicketFileRepository;
import erp.TicketSales.TicketSalesService;
import erp.TicketSales.TicketSalesTerminal;
import erp.command.CommandInvoker;
import erp.command.SignOffCommand;
import java.util.Scanner;

public class terminal_ui {

    private static final AircraftFileRepository aircraftRepository = new AircraftFileRepository();

    private static AircraftManagementService aircraftService = new AircraftManagementService(aircraftRepository);

    private static MaintenanceService maintenanceService = new MaintenanceService(new MaintenanceFileRepository(),
            aircraftRepository);

    private static HumanResourcesService hrService = new HumanResourcesService(new EmployeeFileRepository());

    private static FlightManagementService flightService = new FlightManagementService(
            new erp.FlightManagement.FlightFileRepository(),
            aircraftRepository);

    private static TicketSalesTerminal ticketSalesTerminal = new TicketSalesTerminal(
            new TicketSalesService(new FlightFileRepository(), new TicketFileRepository()));

    /**
     * Client of the Command pattern. It builds the receivers (the department
     * services above), registers one command per menu option with the invoker,
     * and starts the invoker. Each registration binds a menu number to a
     * method on a receiver, so adding a menu option means adding one line here.
     */
    public static void run() {
        Scanner scanner = new Scanner(System.in);
        CommandInvoker invoker = new CommandInvoker();

        invoker.startSection("AIRCRAFT MANAGEMENT");
        invoker.register("1", "Register New Aircraft", aircraftService::handleRegistration);
        invoker.register("2", "View Registered Aircrafts", aircraftService::handleViewAircrafts);
        invoker.register("3", "Delete Registered Aircraft", aircraftService::handleDeleteAircraft);
        invoker.register("4", "Schedule Maintenance", maintenanceService::handleScheduleMaintenance);
        invoker.register("5", "Complete Maintenance", maintenanceService::handleCompleteMaintenance);

        invoker.startSection("FLIGHT MANAGEMENT");
        invoker.register("6", "Schedule Flight", flightService::handleScheduling);
        invoker.register("7", "View Scheduled Flights", s -> flightService.showScheduledFlights());
        invoker.register("8", "Update Flight", flightService::handleUpdate);

        invoker.startSection("TICKET SALES");
        invoker.register("9", "Purchase Ticket", ticketSalesTerminal::handlePurchaseTicket);
        invoker.register("10", "Cancel Booking", ticketSalesTerminal::handleCancelBooking);

        invoker.startSection("AIRPORT OPERATIONS");
        invoker.register("11", "Assign Gate", airport_ops::assignGate);
        invoker.register("12", "View Gate Assignments", s -> airport_ops.showAssignments());
        invoker.register("13", "Update Gate Assignment", airport_ops::updateGate);

        invoker.startSection("HUMAN RESOURCES");
        invoker.register("14", "Hire New employee", hrService::handleHiring);
        invoker.register("15", "View Current Employees", hrService::handleViewEmployees);
        invoker.register("16", "Assign Employee to Flight", hrService::handleAssignFlight);
        invoker.register("17", "Assign Employee to Gate", hrService::handleAssignGate);

        invoker.register("90", "Sign Off", new SignOffCommand(invoker));

        invoker.run(scanner, "90");
        scanner.close();
    }
}