package erp.TicketSales;

import java.time.LocalDate;
import java.util.List;

/**
 * Use case controller for Purchase Ticket. Receives each system operation
 * from TicketSalesTerminal and delegates to the domain objects.
 */
public class TicketSalesService {
    private final FlightCatalog catalog;
    private final ITicketRepository ticketRepository;
    private TicketSale currentSale;

    public TicketSalesService(IFlightRepository flightRepository, ITicketRepository ticketRepository) {
        flightRepository.initializeStorage();
        ticketRepository.initializeStorage();
        this.catalog = new FlightCatalog(flightRepository);
        this.ticketRepository = ticketRepository;
    }

    /** System operation 1: searchFlights(origin, destination, date). */
    public List<Flight> searchFlights(String origin, String destination, LocalDate date) {
        return catalog.search(origin, destination, date);
    }

    /** System operation 2: startPurchase(flightNumber). Returns the open seats. */
    public List<Seat> startPurchase(String flightNumber) {
        Flight flight = catalog.getFlight(flightNumber);
        if (flight == null) {
            throw new IllegalArgumentException("No flight with number " + flightNumber + ".");
        }
        if (!flight.hasAvailableSeats()) {
            throw new IllegalStateException("Flight " + flight.getFlightNumber() + " is sold out.");
        }
        currentSale = new TicketSale(flight);
        return flight.getAvailableSeats();
    }

    /** System operation 3: selectSeat(seatNumber). Returns the total due. */
    public double selectSeat(String seatNumber) {
        requireSale();
        Seat seat = currentSale.getFlight().getSeat(seatNumber);
        currentSale.selectSeat(seat);
        return currentSale.getTotal();
    }

    /** System operation 4: enterPassengerInfo(name, email, phone). */
    public void enterPassengerInfo(String name, String email, String phone) {
        requireSale();
        Passenger p = new Passenger(name, email, phone);
        currentSale.setPassenger(p);
    }

    /** System operation 5: makePayment(method, amount). Issues and stores the ticket. */
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

    private void requireSale() {
        if (currentSale == null) throw new IllegalStateException("Start a purchase first.");
    }
}
