package erp.HumanResources;

import java.util.List;

/**
 * What Human Resources needs to know about scheduled flights in order to assign crew.
 * HR depends on this interface instead of on Flight Management's classes or files.
 */
public interface IFlightScheduleLookup {
    List<String> getScheduledFlightIds();
    boolean flightExists(String flightId);
}
