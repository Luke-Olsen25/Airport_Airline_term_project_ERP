package api.HumanResources;

import erp.HumanResources.Employee;
import erp.HumanResources.EmployeeRole;
import java.util.List;
import java.util.Optional;

/**
 * Read-only access to Human Resources employee data for other departments
 * Other departments depend on this interface instead of reading employee.txt directly.
 */
public interface IEmployeeLookup {
    List<Employee> getAllEmployees();
    List<Employee> getActiveEmployees();
    List<Employee> getActiveEmployeesByRole(EmployeeRole role);
    Optional<Employee> getEmployeeById(String employeeId);
}
