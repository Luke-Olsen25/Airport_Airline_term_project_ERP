package erp.AirportOperations;

/**
 * Immutable value object: one flight assigned to one gate.
 * Persisted as a single "flightId,gateId" line in gates.txt.
 */
public class GateAssignment {
    private final String flightId;
    private final String gateId;

    public GateAssignment(String flightId, String gateId) {
        this.flightId = flightId;
        this.gateId = gateId;
    }

    public String getFlightId() {
        return flightId;
    }

    public String getGateId() {
        return gateId;
    }

    public String toText() {
        return flightId + "," + gateId;
    }

    /** Parses a "flightId,gateId" line; returns null for malformed lines. */
    public static GateAssignment fromText(String line) {
        if (line == null) return null;
        String[] data = line.split(",");
        if (data.length != 2) return null;
        String flight = data[0].trim();
        String gate = data[1].trim();
        if (flight.isEmpty() || gate.isEmpty()) return null;
        return new GateAssignment(flight, gate);
    }
}
