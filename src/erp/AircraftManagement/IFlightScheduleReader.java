package erp.AircraftManagement;

import java.util.List;

public interface IFlightScheduleReader {
    /** Returns IDs of flights with status "Scheduled" or "In-Flight" that use this aircraft. */
    List<String> findActiveFlightIds(String tailNumber);
}
