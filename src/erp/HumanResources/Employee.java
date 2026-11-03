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
    private List<String> assignedFlights;

    public Employee(String employeeID, String firstName, String lastName, EmployeeRole role, int annualSalary) {
        this.employeeID = employeeID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.hireDate = LocalDate.now().toString();
        this.annualSalary = annualSalary;
        this.status = EmployeeStatus.ACTIVE;
        this.assignedFlights = new ArrayList<>();
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

    public List<String> getAssignedFlights() {
        return assignedFlights;
    }

    public String toJson() {
        // Format assigned flights as a JSON array of strings, e.g. ["FL100", "FL200"]
        StringBuilder flights = new StringBuilder("[");
        for (int i = 0; i < assignedFlights.size(); i++) {
            if (i > 0) {
                flights.append(", ");
            }
            flights.append("\"").append(assignedFlights.get(i)).append("\"");
        }
        flights.append("]");

        return String.format("{\"employeeId\": \"%s\", \"firstName\": \"%s\", \"lastName\": \"%s\", \"role\": \"%s\", \"hireDate\": \"%s\", \"annualSalary\": %d, \"status\": \"%s\", \"assignedFlights\": %s}",
                employeeID, firstName, lastName, role.name(), hireDate, annualSalary, status.name(), flights);
    }
}