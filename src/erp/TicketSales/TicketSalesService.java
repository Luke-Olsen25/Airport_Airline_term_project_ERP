package erp.TicketSales;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Use case controller for Ticket Sales (Purchase Ticket, Cancel Booking).
 * Receives each system operation from TicketSalesTerminal and delegates to the domain objects.
 */
public class TicketSalesService {
    private final FlightCatalog catalog;
    private final ITicketRepository ticketRepository;
    private TicketSale currentSale;
    private Ticket bookingToCancel;
    private Flight flightOfBooking;

    public TicketSalesService(IFlightRepository flightRepository, ITicketRepository ticketRepository) {
        flightRepository.initializeStorage();
        ticketRepository.initializeStorage();
        this.catalog = new FlightCatalog(flightRepository);
        this.ticketRepository = ticketRepository;
    }

    // ---------------------------------------------------------------- Purchase Ticket

    /** System operation: searchFlights(origin, destination, date). */
    public List<Flight> searchFlights(String origin, String destination, LocalDate date) {
        catalog.reload();
        return catalog.search(origin, destination, date, LocalDateTime.now());
    }

    /** System operation: startPurchase(flightId). Returns the open seats. */
    public List<Seat> startPurchase(String flightId) {
        Flight flight = catalog.getFlight(flightId);
        if (flight == null) {
            throw new IllegalArgumentException("No flight with ID " + flightId + ".");
        }
        if (!flight.isOpenForSale(LocalDateTime.now())) {
            throw new IllegalStateException("Flight " + flight.getFlightNumber() + " is not open for sale.");
        }
        if (!flight.hasAvailableSeats()) {
            throw new IllegalStateException("Flight " + flight.getFlightNumber() + " is sold out.");
        }
        currentSale = new TicketSale(flight);
        return flight.getAvailableSeats();
    }

    /** System operation: selectSeat(seatNumber). Returns the total due. */
    public double selectSeat(String seatNumber) {
        requireSale();
        Seat seat = currentSale.getFlight().getSeat(seatNumber);
        currentSale.selectSeat(seat);
        return currentSale.getTotal();
    }

    /** System operation: enterPassengerInfo(name, email, phone). */
    public void enterPassengerInfo(String name, String email, String phone) {
        requireSale();
        Passenger p = new Passenger(name, email, phone);
        currentSale.setPassenger(p);
    }

    /** System operation: makePayment(method, amount). Issues and stores the ticket. */
    public Ticket makePayment(PaymentMethod method, double amountTendered) {
        requireSale();
        currentSale.makePayment(method, amountTendered);
        String number = ticketRepository.nextTicketNumber();
        Ticket ticket = currentSale.issueTicket(number);
        catalog.saveSeatChanges();
        ticketRepository.save(ticket);
        currentSale = null;
        return ticket;
    }

    /** Abandons the current purchase; nothing is saved. */
    public void cancelPurchase() { currentSale = null; }

    public TicketSale getCurrentSale() { return currentSale; }

    // ---------------------------------------------------------------- Cancel Booking

    /**
     * System operation 1: findBooking(ticketNumber). Returns the ticket to show the agent.
     * Rejects unknown tickets, tickets already cancelled, and flights that have departed.
     */
    public Ticket findBooking(String ticketNumber) {
        catalog.reload();
        Ticket ticket = ticketRepository.findByNumber(ticketNumber);
        if (ticket == null) {
            throw new IllegalArgumentException("No ticket with number " + ticketNumber + ".");
        }
        if (ticket.isCancelled()) {
            throw new IllegalStateException("Ticket " + ticket.getTicketNumber() + " was already cancelled on "
                    + ticket.getCancelledAt().toLocalDate() + ".");
        }
        LocalDateTime now = LocalDateTime.now();
        Flight flight = catalog.getFlight(ticket.getFlightId());
        boolean departed = flight != null ? flight.hasDeparted(now)
                                          : ticket.getFlightDate().isBefore(now.toLocalDate());
        if (departed) {
            throw new IllegalStateException("Flight " + ticket.getFlightNumber()
                    + " has already departed. The booking can no longer be cancelled.");
        }
        bookingToCancel = ticket;
        flightOfBooking = flight;
        return ticket;
    }

    /**
     * System operation 2: confirmCancellation(). Marks the ticket CANCELLED and puts its seat
     * back on sale. Returns true if a seat was released (false if the flight is no longer
     * on the schedule).
     */
    public boolean confirmCancellation() {
        if (bookingToCancel == null) throw new IllegalStateException("Find a booking first.");
        bookingToCancel.cancel(LocalDateTime.now());
        String seatNumber = bookingToCancel.getSeatNumber();
        boolean seatReleased = false;
        if (flightOfBooking != null) {
            Seat seat = flightOfBooking.getSeat(seatNumber);
            if (seat != null && !seat.isAvailable()) {
                seat.release();
                catalog.saveSeatChanges();
                seatReleased = true;
            }
        }
        ticketRepository.update(bookingToCancel);
        abandonCancellation();
        return seatReleased;
    }

    /** The agent declined to cancel; nothing is changed. */
    public void abandonCancellation() {
        bookingToCancel = null;
        flightOfBooking = null;
    }

    private void requireSale() {
        if (currentSale == null) throw new IllegalStateException("Start a purchase first.");
    }
}
