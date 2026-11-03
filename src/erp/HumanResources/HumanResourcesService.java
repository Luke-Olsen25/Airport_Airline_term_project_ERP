package erp.HumanResources;

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