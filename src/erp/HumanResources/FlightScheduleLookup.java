package erp.HumanResources;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads scheduled flights through
 * Flight Management's own repository 
 */
public class FlightScheduleLookup implements IFlightScheduleLookup {
    private final erp.FlightManagement.FlightFileRepository schedule;

    public FlightScheduleLookup() {
        this(new erp.FlightManagement.FlightFileRepository());
    }

    public FlightScheduleLookup(erp.FlightManagement.FlightFileRepository schedule) {
        this.schedule = schedule;
    }

    @Override
    public List<String> getScheduledFlightIds() {
        List<String> ids = new ArrayList<>();
        for (erp.FlightManagement.Flight flight : readSchedule()) {
            ids.add(flight.getFlightId());
        }
        return ids;
    }

    @Override
    public boolean flightExists(String flightId) {
        if (flightId == null || flightId.isBlank()) {
            return false;
        }
        String target = flightId.trim();
        for (String id : getScheduledFlightIds()) {
            if (id.equalsIgnoreCase(target)) {
                return true;
            }
        }
        return false;
    }

    private List<erp.FlightManagement.Flight> readSchedule() {
        try {
            return schedule.findAll();
        } catch (IOException | RuntimeException e) {
            System.out.println("Could not read scheduled flights: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}
