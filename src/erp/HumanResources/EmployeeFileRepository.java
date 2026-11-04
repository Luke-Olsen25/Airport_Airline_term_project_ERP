package erp.HumanResources;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class EmployeeFileRepository implements IEmployeeRepository {
    private static final String DATA_FILE = "src/data/employee.txt";
    private static final String HEADER = "employeeId|firstName|lastName|role|hireDate|annualSalary|status|assignedFlights";

    @Override
    /**    (non-Javadoc)
     * Initialize file with a header line naming each field
     * @see erp.HumanResources.IEmployeeRepository#initializeStorage()
     */
    public void initializeStorage() {
        try {
            File directory = new File("src/data");
            if (!directory.exists()) {
                directory.mkdirs();
            }
            File file = new File(DATA_FILE);
            if (!file.exists()) {
                file.createNewFile();
                try (FileWriter writer = new FileWriter(file)) {
                    writer.write(HEADER + "\n");
                }
            } else if (file.length() == 0) {
                try (FileWriter writer = new FileWriter(file)) {
                    writer.write(HEADER + "\n");
                }
            }
        } catch (IOException e) {
            System.out.println("Storage initialization error: " + e.getMessage());
        }
    }

    @Override
    /**    (non-Javadoc)
     * Skip the header line and compare the first field of each record
     * @see erp.HumanResources.IEmployeeRepository#existsByEmployeeID(java.lang.String)
     */
    public boolean existsByEmployeeID(String employeeID) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(DATA_FILE));
            for (int i = 1; i < lines.size(); i++) {
                String[] fields = lines.get(i).split("\\|");
                if (fields[0].equals(employeeID)) {
                    return true;
                }
            }
            return false;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    /**    (non-Javadoc)
     * Append the new employee as its own line at the end of the file
     * @see erp.HumanResources.IEmployeeRepository#save(erp.HumanResources.Employee)
     */
    public boolean save(Employee employee) {
        try (FileWriter writer = new FileWriter(DATA_FILE, true)) {
            writer.write(employee.toText() + "\n");
            return true;
        } catch (IOException e) {
            System.out.println("Storage write error: " + e.getMessage());
            return false;
        }
    }

    @Override
    /**    (non-Javadoc)
     * Return every employee record line, skipping the header and blank lines
     * @see erp.HumanResources.IEmployeeRepository#getAllEmployeeRecords()
     */
    public List<String> getAllEmployeeRecords() {
        List<String> records = new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(Paths.get(DATA_FILE));
            for (int i = 1; i < lines.size(); i++) {
                if (!lines.get(i).isBlank()) {
                    records.add(lines.get(i));
                }
            }
        } catch (IOException e) {
            System.out.println("Storage read error: " + e.getMessage());
        }
        return records;
    }
}
