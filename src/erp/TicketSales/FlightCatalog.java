package erp.TicketSales;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** All flights offered for sale (Pure Fabrication over the flight data store). */
public class FlightCatalog {
    private final IFlightRepository repository;
    private final List<Flight> flights;

    public FlightCatalog(IFlightRepository repository) {
        this.repository = repository;
        this.flights = repository.loadFlights();
    }

    /** Flights between two airports on a date that still have seats for sale. */
    public List<Flight> search(String origin, String destination, LocalDate date) {
        List<Flight> result = new ArrayList<Flight>();
        for (Flight f : flights) {
            if (f.matches(origin, destination, date) && f.hasAvailableSeats()) result.add(f);
        }
        return result;
    }

    public Flight getFlight(String flightNumber) {
        if (flightNumber == null) return null;
        for (Flight f : flights) {
            if (f.getFlightNumber().equalsIgnoreCase(flightNumber.trim())) return f;
        }
        return null;
    }

    public List<Flight> getAllFlights() { return flights; }

    /** Persists seat status after a sale. */
    public void saveSeatChanges() { repository.saveSeats(flights); }
}
