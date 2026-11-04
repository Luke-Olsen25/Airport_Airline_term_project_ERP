package erp.HumanResources;

import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class HumanResourcesService {
    private static final Random RANDOM = new Random();
    private IEmployeeRepository repository;

    public HumanResourcesService(IEmployeeRepository repository) {
        this.repository = repository;
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
        String format = "%-8s | %-12s | %-12s | %-22s | %-10s | %-10s | %-10s%n";
        int shown = 0;

        System.out.printf(format, "ID", "First Name", "Last Name", "Role", "Hire Date", "Salary", "Status");
        System.out.println("-------------------------------------------------------------------------------------------------");

        for (String record : records) {
            // Limit -1 keeps the trailing empty assignedFlights field
            String[] data = record.split("\\|", -1);
            if (data.length < 7 || data[6].equals(EmployeeStatus.TERMINATED.name())) {
                continue; // Skip malformed lines and terminated employees
            }
            System.out.printf(format, data[0], data[1], data[2], data[3], data[4], "$" + data[5], data[6]);
            shown++;
        }

        if (shown == 0) {
            System.out.println("No current employees found.");
        }

        System.out.print("\nPress Enter to return to Main Menu...");
        scanner.nextLine();
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