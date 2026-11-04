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
