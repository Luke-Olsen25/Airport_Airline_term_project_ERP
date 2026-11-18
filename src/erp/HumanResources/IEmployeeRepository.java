package erp.HumanResources;

import java.util.List;
import java.util.Optional;

public interface IEmployeeRepository {
    void initializeStorage();
    boolean existsByEmployeeID(String employeeID);
    boolean save(Employee employee);
    List<String> getAllEmployeeRecords();
    Optional<Employee> findById(String employeeID);
    boolean update(Employee employee);
}