package api.HumanResources;

import erp.HumanResources.Employee;
import erp.HumanResources.EmployeeFileRepository;
import erp.HumanResources.EmployeeRole;
import erp.HumanResources.EmployeeStatus;
import erp.HumanResources.IEmployeeRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implements IEmployeeLookup on top of HR's own repository,
 * So HR only has write acces but others can read.
 */
public class EmployeeLookup implements IEmployeeLookup {

    private final IEmployeeRepository repository;

    public EmployeeLookup() {
        this(new EmployeeFileRepository());
    }

    public EmployeeLookup(IEmployeeRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Employee> getAllEmployees() {
        List<Employee> employees = new ArrayList<>();

        for (String record : repository.getAllEmployeeRecords()) {
            try {
                employees.add(Employee.fromText(record));
            } catch (IllegalArgumentException e) {
                // Skip malformed lines (bad field count, unknown role/status, bad salary)
                System.out.println("Skipping invalid employee record: " + record);
            }
        }

        return employees;
    }

    @Override
    public List<Employee> getActiveEmployees() {
        return getAllEmployees().stream()
                .filter(e -> e.getStatus() == EmployeeStatus.ACTIVE)
                .toList();
    }

    @Override
    public List<Employee> getActiveEmployeesByRole(EmployeeRole role) {
        return getActiveEmployees().stream()
                .filter(e -> e.getRole() == role)
                .toList();
    }

    @Override
    public Optional<Employee> getEmployeeById(String employeeId) {
        if (employeeId == null || employeeId.isBlank()) {
            return Optional.empty();
        }

        String target = employeeId.trim();

        return getAllEmployees().stream()
                .filter(e -> e.getEmployeeID().equalsIgnoreCase(target))
                .findFirst();
    }
}
