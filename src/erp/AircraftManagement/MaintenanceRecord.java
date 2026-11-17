package erp.AircraftManagement;

import java.util.UUID;

public class MaintenanceRecord {
    private final String recordId;
    private final String tailNumber;
    private final MaintenanceType type;
    private final String startDate;
    private final String description;
    private MaintenanceStatus status;

    public MaintenanceRecord(String tailNumber, MaintenanceType type, String startDate, String description) {
        this("MX-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
                tailNumber, type, startDate, description, MaintenanceStatus.SCHEDULED);
    }

    // Constructor for records loaded from file
    public MaintenanceRecord(String recordId, String tailNumber, MaintenanceType type, String startDate, String description, MaintenanceStatus status) {
        this.recordId = recordId;
        this.tailNumber = tailNumber;
        this.type = type;
        this.startDate = startDate;
        this.description = sanitize(description);
        this.status = status;
    }

    public static MaintenanceRecord fromFileRecord(String line) {
        String[] d = line.trim().split("\\s*,\\s*", -1);
        if (d.length != 6) {
            throw new IllegalArgumentException("Malformed maintenance record: " + line);
        }
        return new MaintenanceRecord(d[0], d[1], MaintenanceType.valueOf(d[2]), d[3], d[4], MaintenanceStatus.valueOf(d[5]));
    }

    public void markCompleted() {
        if (status != MaintenanceStatus.SCHEDULED) {
            throw new IllegalStateException("Only SCHEDULED maintenance can be completed (current: " + status + ").");
        }
        this.status = MaintenanceStatus.COMPLETED;
    }

    // Commas/newlines would corrupt the CSV line
    private static String sanitize(String text) {
        if (text == null) return "";
        return text.replace(",", ";").replace("\n", " ").replace("\r", " ").trim();
    }

    public String getRecordId() { return recordId; }
    public String getTailNumber() { return tailNumber; }
    public MaintenanceType getType() { return type; }
    public String getStartDate() { return startDate; }
    public String getDescription() { return description; }
    public MaintenanceStatus getStatus() { return status; }

    public String toFileRecord() {
        return String.join(", ",
                recordId,
                tailNumber,
                type.name(),
                startDate,
                description,
                status.name()
        );
    }
}
