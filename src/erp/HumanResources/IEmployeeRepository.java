package erp.HumanResources;

public interface IEmployeeRepository {
    void initializeStorage();
    boolean existsByEmployeeID(String employeeID);
    boolean save(Employee employee);
}