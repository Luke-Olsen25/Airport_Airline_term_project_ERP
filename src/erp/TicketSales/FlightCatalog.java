package erp.TicketSales;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** All flights Ticket Sales knows about (Pure Fabrication over the flight data store). */
public class FlightCatalog {
    private final IFlightRepository repository;
    private List<Flight> flights;

    public FlightCatalog(IFlightRepository repository) {
        this.repository = repository;
        this.flights = repository.loadFlights();
    }

    /** Re-reads the files, so flights Flight Management scheduled since start-up are included. */
    public void reload() { flights = repository.loadFlights(); }

    /** Flights between two airports on a date that are open for sale and have seats left. */
    public List<Flight> search(String origin, String destination, LocalDate date, LocalDateTime now) {
        List<Flight> result = new ArrayList<Flight>();
        for (Flight f : flights) {
            if (f.matches(origin, destination, date) && f.isOpenForSale(now) && f.hasAvailableSeats()) {
                result.add(f);
            }
        }
        return result;
    }

    /** Finds a flight by its Flight Management flight ID, or null. */
    public Flight getFlight(String flightId) {
        if (flightId == null) return null;
        for (Flight f : flights) {
            if (f.getFlightId().equalsIgnoreCase(flightId.trim())) return f;
        }
        return null;
    }

    public List<Flight> getAllFlights() { return flights; }

    /** Persists seat status after a sale or a cancellation. */
    public void saveSeatChanges() { repository.saveSeats(flights); }
}
