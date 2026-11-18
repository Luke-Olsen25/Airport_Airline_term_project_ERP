package api.TicketSales;

import erp.TicketSales.Ticket;
import java.util.List;
import java.util.Optional;

/**
 * Read-only access to Ticket Sales data for other departments.
 * Other departments depend on this interface instead of reading tickets.txt or seats.txt directly.
 */
public interface ITicketLookup {
    Optional<Ticket> getTicketByNumber(String ticketNumber);
    List<Ticket> getActiveTicketsForFlight(String flightId);
    List<Ticket> getCancelledTickets();
    int getSeatsSold(String flightId);
    int getSeatsAvailable(String flightId);
}
