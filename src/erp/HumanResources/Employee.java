package erp.HumanResources;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Employee {
    private String employeeID;
    private String firstName;
    private String lastName;
    private EmployeeRole role;
    private String hireDate;
    private int annualSalary;
    private EmployeeStatus status;
    private List<String> assignments;

    public Employee(String employeeID, String firstName, String lastName, EmployeeRole role, int annualSalary) {
        this.employeeID = employeeID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.hireDate = LocalDate.now().toString();
        this.annualSalary = annualSalary;
        this.status = EmployeeStatus.ACTIVE;
        this.assignments = new ArrayList<>();
    }

    /**
     * Rebuilds an existing Employee from a stored record (used by fromText)
     * @param employeeID
     * @param firstName
     * @param lastName
     * @param role
     * @param hireDate
     * @param annualSalary
     * @param status
     * @param assignments
     */
    public Employee(String employeeID, String firstName, String lastName, EmployeeRole role, String hireDate,
                    int annualSalary, EmployeeStatus status, List<String> assignments) {
        this.employeeID = employeeID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.hireDate = hireDate;
        this.annualSalary = annualSalary;
        this.status = status;
        this.assignments = new ArrayList<>(assignments);
    }

    // Reverse of toText(): parses one line of the employee text file
    public static Employee fromText(String line) {
        String[] fields = line.split("\\|", -1);
        if (fields.length != 8) {
            throw new IllegalArgumentException("Expected 8 fields but found " + fields.length + ": " + line);
        }

        List<String> assignments = new ArrayList<>();
        for (String id : fields[7].split(";")) {
            if (!id.isBlank()) {
                assignments.add(id.trim());
            }
        }

        return new Employee(
                fields[0],
                fields[1],
                fields[2],
                EmployeeRole.valueOf(fields[3]),
                fields[4],
                Integer.parseInt(fields[5]),
                EmployeeStatus.valueOf(fields[6]),
                assignments);
    }

    public String getEmployeeID() {
        return employeeID;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public EmployeeRole getRole() {
        return role;
    }

    public String getHireDate() {
        return hireDate;
    }

    public int getAnnualSalary() {
        return annualSalary;
    }

    public EmployeeStatus getStatus() {
        return status;
    }

    public List<String> getAssignments() {
        return assignments;
    }

    // One line of the employee text file, fields separated by '|' and assignments (flight or gate IDs) by ';'
    public String toText() {
        return String.format("%s|%s|%s|%s|%s|%d|%s|%s",
                employeeID, firstName, lastName, role.name(), hireDate, annualSalary, status.name(),
                String.join(";", assignments));
    }
}