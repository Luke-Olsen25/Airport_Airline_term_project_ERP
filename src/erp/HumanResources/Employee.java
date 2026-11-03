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

    // One line of the employee text file, fields separated by '|' and assigned flights by ';'
    public String toText() {
        return String.format("%s|%s|%s|%s|%s|%d|%s|%s",
                employeeID, firstName, lastName, role.name(), hireDate, annualSalary, status.name(),
                String.join(";", assignedFlights));
    }
}