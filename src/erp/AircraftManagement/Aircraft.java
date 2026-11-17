package erp.AircraftManagement;

public class Aircraft {
    private String tailNumber;
    private String manufacturer;
    private String model;
    private String serialNumber;
    private int maxFlightRange;
    private int seatingCapacity;
    private AircraftStatus status;

    public Aircraft(String tailNumber, String manufacturer, String model, String serialNumber, int maxFlightRange, int seatingCapacity) {
        this.tailNumber = tailNumber;
        this.manufacturer = manufacturer;
        this.model = model;
        this.serialNumber = serialNumber;
        this.maxFlightRange = maxFlightRange;
        this.seatingCapacity = seatingCapacity;
        this.status = AircraftStatus.GROUNDED_PENDING_INSPECTION;
    }

    public String getTailNumber() {
        return tailNumber;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public int getMaxFlightRange() {
        return maxFlightRange;
    }

    public int getSeatingCapacity() {
        return seatingCapacity;
    }

    public AircraftStatus getStatus() { return status;}

    public void setStatus(AircraftStatus status) {this.status = status;}

    public static Aircraft fromFileRecord(String line) {
        String[] d = line.split(",", -1);
        if (d.length != 7) {
            throw new IllegalArgumentException("Malformed aircraft record: " + line);
        }
        Aircraft aircraft = new Aircraft(d[0], d[1], d[2], d[3],
                Integer.parseInt(d[4].trim()), Integer.parseInt(d[5].trim()));
        aircraft.setStatus(AircraftStatus.valueOf(d[6].trim()));
        return aircraft;
    }

    public String toFileRecord() {
        return String.join(",",
                tailNumber,
                manufacturer,
                model,
                serialNumber,
                String.valueOf(maxFlightRange),
                String.valueOf(seatingCapacity),
                status.name()
        );
    }
}
