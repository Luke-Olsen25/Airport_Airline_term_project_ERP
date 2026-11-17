package erp;

import erp.AirportOperations.GateAssignment;
import erp.AirportOperations.GateAssignmentFileRepository;
import erp.AirportOperations.GateAssignmentService;
import erp.AirportOperations.GateAssignmentService.GateResult;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

/**
 * Airport Operations console controller. Reads employee input and prints
 * results; all gate rules are decided by GateAssignmentService.
 */
public class airport_ops {

    private static GateAssignmentService service =
            new GateAssignmentService(new GateAssignmentFileRepository());

    /** For tests: use a service backed by other files. */
    public static void setService(GateAssignmentService replacement) {
        service = replacement;
    }

    public static void showAssignments() {
        System.out.println("\n--- Current Gate Assignments ---");
        List<GateAssignment> assignments = service.getAssignments();

        if (assignments.isEmpty()) {
            System.out.println("No gate assignments found.");
            return;
        }
        for (GateAssignment a : assignments) {
            System.out.println("Flight: " + a.getFlightId() + " | Gate: " + a.getGateId());
        }
    }

    public static void assignGate(Scanner scanner) {
        System.out.println("\n--- Assign Gate ---");
        showAssignments();

        System.out.print("\nEnter Flight ID: ");
        String flightId = scanner.nextLine().trim().toUpperCase();
        if (flightId.isEmpty()) {
            System.out.println("Flight ID cannot be empty.");
            return;
        }

        System.out.println("\nAirport Gates:");
        for (String gate : service.getValidGates()) {
            System.out.println("Gate " + gate);
        }
        System.out.print("\nEnter Gate: ");
        String gate = scanner.nextLine().trim().toUpperCase();

        GateResult result = service.assignGate(flightId, gate);
        if (result == GateResult.OK) {
            System.out.println("\nSuccess! Flight " + flightId + " assigned to Gate " + gate + ".");
        } else if (result == GateResult.GATE_OCCUPIED) {
            System.out.println("Gate is already assigned.");
        } else {
            System.out.println(messageFor(result, gate));
        }
    }

    public static void updateGate(Scanner scanner) {
        System.out.println("\n--- Update Gate Assignment ---");
        showAssignments();

        System.out.print("\nEnter Flight ID: ");
        String flightId = scanner.nextLine().trim().toUpperCase();
        if (flightId.isEmpty()) {
            System.out.println("Flight ID cannot be empty.");
            return;
        }

        Optional<GateAssignment> current = service.findByFlight(flightId);
        if (!current.isPresent()) {
            System.out.println(messageFor(GateResult.FLIGHT_NOT_ASSIGNED, null));
            return;
        }
        String oldGate = current.get().getGateId();
        System.out.println("Flight " + flightId + " is currently at Gate " + oldGate + ".");

        System.out.println("\nGate availability:");
        for (Map.Entry<String, String> e : service.getGateStatus().entrySet()) {
            System.out.println("Gate " + e.getKey() + " - "
                    + (e.getValue() == null ? "FREE" : "OCCUPIED by " + e.getValue()));
        }

        System.out.print("\nEnter New Gate: ");
        String newGate = scanner.nextLine().trim().toUpperCase();

        GateResult check = service.validateUpdate(flightId, newGate);
        if (check != GateResult.OK) {
            System.out.println(messageFor(check, newGate));
            return;
        }

        System.out.println("\nChange Flight " + flightId + ": Gate " + oldGate + " -> Gate " + newGate);
        System.out.print("Confirm (Y/N)? ");
        if (!scanner.nextLine().trim().equalsIgnoreCase("Y")) {
            System.out.println("Update canceled. No changes made.");
            return;
        }

        GateResult result = service.updateGate(flightId, newGate);
        if (result == GateResult.OK || result == GateResult.OK_HISTORY_NOT_SAVED) {
            System.out.println("\nSuccess! Flight " + flightId + " moved from Gate " + oldGate
                    + " to Gate " + newGate + ".");
            if (result == GateResult.OK_HISTORY_NOT_SAVED) {
                System.out.println("Warning: the change was saved but could not be written to the gate history log.");
            }
        } else {
            System.out.println(messageFor(result, newGate));
        }
    }

    private static String messageFor(GateResult result, String gate) {
        switch (result) {
            case EMPTY_FLIGHT:
                return "Flight ID cannot be empty.";
            case INVALID_GATE:
                return "Invalid gate.";
            case FLIGHT_NOT_ASSIGNED:
                return "Flight has no gate assignment. Use Assign Gate first.";
            case FLIGHT_ALREADY_ASSIGNED:
                return "Flight already has a gate assigned.";
            case SAME_GATE:
                return "New gate is the same as the current gate.";
            case GATE_OCCUPIED:
                return "Gate " + gate + " is already assigned to another flight.";
            default:
                return "Error saving gate assignment. No changes were made.";
        }
    }
}
