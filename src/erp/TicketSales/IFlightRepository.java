package erp.TicketSales;

import java.util.List;

/** Storage for flights and the seats Ticket Sales sells on them. */
public interface IFlightRepository {
    void initializeStorage();
    List<Flight> loadFlights();
    void saveSeats(List<Flight> flights);
}
