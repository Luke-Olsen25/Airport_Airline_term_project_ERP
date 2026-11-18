package erp.HumanResources;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

public class HumanResourcesService {
    private static final Random RANDOM = new Random();
    // Roles that are eligable for lfights
    private static final Set<EmployeeRole> FLIGHT_ROLES = EnumSet.of(
            EmployeeRole.PILOT, EmployeeRole.CO_PILOT,
            EmployeeRole.FLIGHT_ATTENDANT, EmployeeRole.MAINTENANCE_TECHNICIAN);
    // Roles that are eligable for a gate
    private static final Set<EmployeeRole> GATE_ROLES = EnumSet.of(EmployeeRole.GATE_AGENT);

    private IEmployeeRepository repository;
    private IFlightScheduleLookup flightSchedule;
    private IGateLookup gateLookup;

    public HumanResourcesService(IEmployeeRepository repository) {
        this(repository, new FlightScheduleLookup(), new GateLookup());
    }

    public HumanResourcesService(IEmployeeRepository repository, IFlightScheduleLookup flightSchedule,
                                 IGateLookup gateLookup) {
        this.repository = repository;
        this.flightSchedule = flightSchedule;
        this.gateLookup = gateLookup;
        this.repository.initializeStorage();
    }

    public void handleHiring(Scanner scanner) {
        System.out.println("\n--- HIRE EMPLOYEE ---");
        System.out.print("Enter First Name (e.g., Mark): ");
        String firstName = scanner.nextLine();

        System.out.print("Enter Last Name (e.g., Chavez): ");
        String lastName = scanner.nextLine();

        System.out.println("Roles: PILOT, CO_PILOT, FLIGHT_ATTENDANT, BAGGAGE_HANDLER, MAINTENANCE_TECHNICIAN, GATE_AGENT");
        System.out.print("Enter Role (e.g., PILOT): ");
        String roleInput = scanner.nextLine();

        System.out.print("Enter Annual Salary (e.g., 52000): ");
        int annualSalary = scanner.nextInt();

        scanner.nextLine(); // Consume newline

        try {
            EmployeeRole role;
            try {
                role = EmployeeRole.valueOf(roleInput.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Hiring Aborted: Unknown role (" + roleInput + ").");
            }

            Employee employee = new Employee(generateUniqueEmployeeID(), firstName, lastName, role, annualSalary);
            boolean success = hireNewEmployee(employee);

            if (success) {
                System.out.println("\n[SUCCESS] Employee " + firstName + " " + lastName + " hired with ID " + employee.getEmployeeID() + ".");
            } else {
                System.out.println("\n[ERROR] Hiring failed. Employee ID may already exist.");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("\n[ABORTED] " + e.getMessage());
        }

        System.out.print("\nPress Enter to return to Main Menu...");
        scanner.nextLine();
    }

    public void handleViewEmployees(Scanner scanner) {
        System.out.println("\n--- CURRENT EMPLOYEES ---");
        List<String> records = repository.getAllEmployeeRecords();
        String format = "%-8s | %-12s | %-12s | %-22s | %-10s | %-10s | %-10s | %s%n";
        int shown = 0;

        System.out.printf(format, "ID", "First Name", "Last Name", "Role", "Hire Date", "Salary", "Status", "Assignments");
        System.out.println("---------------------------------------------------------------------------------------------------------------------");

        for (String record : records) {
            // Limit -1 keeps the trailing empty assignments field
            String[] data = record.split("\\|", -1);
            if (data.length < 7 || data[6].equals(EmployeeStatus.TERMINATED.name())) {
                continue; // Skip malformed lines and terminated employees
            }
            String assigned = data.length > 7 ? data[7] : "";
            System.out.printf(format, data[0], data[1], data[2], data[3], data[4], "$" + data[5], data[6], assigned);
            shown++;
        }

        if (shown == 0) {
            System.out.println("No current employees found.");
        }

        System.out.print("\nPress Enter to return to Main Menu...");
        scanner.nextLine();
    }

    public void handleAssignFlight(Scanner scanner) {
        handleAssignment(scanner, "FLIGHT", FLIGHT_ROLES,
                "No active pilots, co-pilots, flight attendants, or maintenance technicians found.",
                flightSchedule.getScheduledFlightIds(), "Scheduled Flights");
    }

    public void handleAssignGate(Scanner scanner) {
        handleAssignment(scanner, "GATE", GATE_ROLES,
                "No active gate agents found.",
                gateLookup.getGateIds(), "Gates");
    }

    /**
     * Assigns a scheduled flight to an active employee whose role works flights.
     * @return true if the assignment was saved, false if the write failed
     * @throws IllegalArgumentException if any business rule is broken
     */
    public boolean assignEmployeeToFlight(String employeeId, String flightId) throws IllegalArgumentException {
        return assign(employeeId, flightId, FLIGHT_ROLES, "flight",
                flightSchedule.getScheduledFlightIds(), "scheduled flight");
    }

    /**
     * Assigns a gate to an active gate agent.
     * @return true if the assignment was saved, false if the write failed
     * @throws IllegalArgumentException if any business rule is broken
     */
    public boolean assignEmployeeToGate(String employeeId, String gateId) throws IllegalArgumentException {
        return assign(employeeId, gateId, GATE_ROLES, "gate", gateLookup.getGateIds(), "gate");
    }

    // Shared prompts for assigning a flight or a gate. 'kind' is FLIGHT or GATE.
    private void handleAssignment(Scanner scanner, String kind, Set<EmployeeRole> roles, String noEmployeesMessage,
                                  List<String> validIds, String validIdsLabel) {
        String noun = kind.toLowerCase();
        String title = kind.charAt(0) + noun.substring(1);
        System.out.println("\n--- ASSIGN EMPLOYEE TO " + kind + " ---");

        List<Employee> eligible = getEligibleEmployees(roles);
        if (eligible.isEmpty()) {
            System.out.println(noEmployeesMessage);
            System.out.print("\nPress Enter to return to Main Menu...");
            scanner.nextLine();
            return;
        }

        String format = "%-8s | %-25s | %-22s | %s%n";
        System.out.printf(format, "ID", "Name", "Role", "Assigned " + title + "s");
        System.out.println("--------------------------------------------------------------------------------");
        for (Employee e : eligible) {
            System.out.printf(format, e.getEmployeeID(), e.getFirstName() + " " + e.getLastName(),
                    e.getRole(), String.join(", ", e.getAssignments()));
        }

        System.out.print("\nEnter Employee ID (e.g., " + eligible.get(0).getEmployeeID() + "): ");
        String employeeId = scanner.nextLine();

        if (validIds.isEmpty()) {
            System.out.println("\n[ABORTED] No " + validIdsLabel.toLowerCase() + " found.");
            System.out.print("\nPress Enter to return to Main Menu...");
            scanner.nextLine();
            return;
        }
        System.out.println(validIdsLabel + ": " + String.join(", ", validIds));
        System.out.print("Enter " + title + " ID (e.g., " + validIds.get(0) + "): ");
        String targetId = scanner.nextLine();

        try {
            boolean saved = kind.equals("FLIGHT")
                    ? assignEmployeeToFlight(employeeId, targetId)
                    : assignEmployeeToGate(employeeId, targetId);
            if (saved) {
                System.out.println("\n[SUCCESS] Employee " + employeeId.trim().toUpperCase()
                        + " assigned to " + noun + " " + targetId.trim() + ".");
            } else {
                System.out.println("\n[ERROR] Assignment could not be saved.");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("\n[ABORTED] " + e.getMessage());
        }

        System.out.print("\nPress Enter to return to Main Menu...");
        scanner.nextLine();
    }

    // Shared rules: employee exists, is active, has an eligible role, the target exists, and isn't already assigned
    private boolean assign(String employeeId, String targetId, Set<EmployeeRole> roles, String noun,
                           List<String> validIds, String validIdsNoun) throws IllegalArgumentException {
        Employee employee = repository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("No employee found with ID (" + employeeId + ")."));

        if (employee.getStatus() != EmployeeStatus.ACTIVE) {
            throw new IllegalArgumentException("Employee " + employee.getEmployeeID()
                    + " is not active (" + employee.getStatus() + ").");
        }

        if (!roles.contains(employee.getRole())) {
            throw new IllegalArgumentException(employee.getRole() + " employees cannot be assigned to " + noun + "s.");
        }

        String matchedId = findMatchingId(targetId, validIds)
                .orElseThrow(() -> new IllegalArgumentException("No " + validIdsNoun + " found with ID (" + targetId + ")."));

        for (String assigned : employee.getAssignments()) {
            if (assigned.equalsIgnoreCase(matchedId)) {
                throw new IllegalArgumentException("Employee " + employee.getEmployeeID()
                        + " is already assigned to " + noun + " " + matchedId + ".");
            }
        }

        employee.getAssignments().add(matchedId);
        return repository.update(employee);
    }

    // Active employees whose role is in the given set
    private List<Employee> getEligibleEmployees(Set<EmployeeRole> roles) {
        List<Employee> eligible = new ArrayList<>();
        for (String record : repository.getAllEmployeeRecords()) {
            try {
                Employee e = Employee.fromText(record);
                if (e.getStatus() == EmployeeStatus.ACTIVE && roles.contains(e.getRole())) {
                    eligible.add(e);
                }
            } catch (IllegalArgumentException ex) {
                // Skip malformed lines
            }
        }
        return eligible;
    }

    // Matches the entered ID against the valid IDs (not case sensitive) and returns it as the owning department stores it
    private Optional<String> findMatchingId(String input, List<String> validIds) {
        if (input == null || input.isBlank()) {
            return Optional.empty();
        }
        for (String id : validIds) {
            if (id.equalsIgnoreCase(input.trim())) {
                return Optional.of(id);
            }
        }
        return Optional.empty();
    }

    public boolean hireNewEmployee(Employee employee) throws IllegalArgumentException {
        // Abort hiring if a negative salary is input
        if (employee.getAnnualSalary() < 0) {
            throw new IllegalArgumentException("Hiring Aborted: Annual Salary cannot be negative (" + employee.getAnnualSalary() + ").");
        }

        if (repository.existsByEmployeeID(employee.getEmployeeID())) {
            return false;
        }
        return repository.save(employee);
    }

    // Random ID in the form E######, regenerated until it isn't already in use
    private String generateUniqueEmployeeID() {
        String id;
        do {
            id = String.format("E%06d", RANDOM.nextInt(1_000_000));
        } while (repository.existsByEmployeeID(id));
        return id;
    }
}