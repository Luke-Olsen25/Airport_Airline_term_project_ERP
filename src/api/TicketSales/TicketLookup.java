package api.TicketSales;

import erp.TicketSales.Flight;
import erp.TicketSales.FlightFileRepository;
import erp.TicketSales.IFlightRepository;
import erp.TicketSales.ITicketRepository;
import erp.TicketSales.Seat;
import erp.TicketSales.Ticket;
import erp.TicketSales.TicketFileRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implements ITicketLookup on top of Ticket Sales' own repositories,
 * so only Ticket Sales writes tickets and seats, but other departments can read them.
 */
public class TicketLookup implements ITicketLookup {

    private final ITicketRepository tickets;
    private final IFlightRepository flights;

    public TicketLookup() {
        this(new TicketFileRepository(), new FlightFileRepository());
    }

    public TicketLookup(ITicketRepository tickets, IFlightRepository flights) {
        this.tickets = tickets;
        this.flights = flights;
    }

    /** A ticket by number (active or cancelled), e.g. for check-in or boarding. */
    @Override
    public Optional<Ticket> getTicketByNumber(String ticketNumber) {
        if (ticketNumber == null || ticketNumber.trim().isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(tickets.findByNumber(ticketNumber));
    }

    /** Passengers still booked on a flight (cancelled tickets are left out). */
    @Override
    public List<Ticket> getActiveTicketsForFlight(String flightId) {
        List<Ticket> result = new ArrayList<>();
        if (flightId == null) return result;
        for (Ticket t : tickets.loadAll()) {
            if (!t.isCancelled() && t.getFlightId().equalsIgnoreCase(flightId.trim())) {
                result.add(t);
            }
        }
        return result;
    }

    /** Every cancelled ticket, e.g. as input for Process Refund. */
    @Override
    public List<Ticket> getCancelledTickets() {
        List<Ticket> result = new ArrayList<>();
        for (Ticket t : tickets.loadAll()) {
            if (t.isCancelled()) result.add(t);
        }
        return result;
    }

    /** Seats sold on a flight; 0 if Ticket Sales does not know the flight. */
    @Override
    public int getSeatsSold(String flightId) {
        Flight flight = findFlight(flightId);
        if (flight == null) return 0;
        int sold = 0;
        for (Seat s : flight.getAllSeats()) {
            if (!s.isAvailable()) sold++;
        }
        return sold;
    }

    /** Seats still for sale on a flight; 0 if Ticket Sales does not know the flight. */
    @Override
    public int getSeatsAvailable(String flightId) {
        Flight flight = findFlight(flightId);
        return flight == null ? 0 : flight.getAvailableSeats().size();
    }

    private Flight findFlight(String flightId) {
        if (flightId == null) return null;
        for (Flight f : flights.loadFlights()) {
            if (f.getFlightId().equalsIgnoreCase(flightId.trim())) return f;
        }
        return null;
    }
}
