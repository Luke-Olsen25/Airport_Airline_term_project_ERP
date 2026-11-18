package erp.HumanResources;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EmployeeFileRepository implements IEmployeeRepository {
    private static final String DATA_FILE = "src/data/employee.txt";
    private static final String HEADER = "employeeId|firstName|lastName|role|hireDate|annualSalary|status|assignments";

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

    @Override
    /**    (non-Javadoc)
     * Parse each record and return the employee whose ID matches (not case sensitive)
     * @see erp.HumanResources.IEmployeeRepository#findById(java.lang.String)
     */
    public Optional<Employee> findById(String employeeID) {
        if (employeeID == null || employeeID.isBlank()) {
            return Optional.empty();
        }
        String target = employeeID.trim();
        for (String record : getAllEmployeeRecords()) {
            String id = record.split("\\|", -1)[0];
            if (id.equalsIgnoreCase(target)) {
                try {
                    return Optional.of(Employee.fromText(record));
                } catch (IllegalArgumentException e) {
                    System.out.println("Skipping invalid employee record: " + record);
                    return Optional.empty();
                }
            }
        }
        return Optional.empty();
    }

    @Override
    /**    (non-Javadoc)
     * Rewrite the file with the matching employee's line replaced, keeping the header
     * and every other line as-is. Returns false if the employee isn't in the file.
     * @see erp.HumanResources.IEmployeeRepository#update(erp.HumanResources.Employee)
     */
    public boolean update(Employee employee) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(DATA_FILE));
            boolean found = false;
            for (int i = 1; i < lines.size(); i++) {
                String id = lines.get(i).split("\\|", -1)[0];
                if (id.equals(employee.getEmployeeID())) {
                    lines.set(i, employee.toText());
                    found = true;
                    break;
                }
            }
            if (!found) {
                return false;
            }
            try (FileWriter writer = new FileWriter(DATA_FILE, false)) {
                for (String line : lines) {
                    writer.write(line + "\n");
                }
            }
            return true;
        } catch (IOException e) {
            System.out.println("Storage write error: " + e.getMessage());
            return false;
        }
    }
}
