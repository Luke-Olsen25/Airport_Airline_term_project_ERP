package erp.HumanResources;

import java.util.List;

public interface IEmployeeRepository {
    void initializeStorage();
    boolean existsByEmployeeID(String employeeID);
    boolean save(Employee employee);
    List<String> getAllEmployeeRecords();
}