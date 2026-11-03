package erp.TicketSales;

import java.time.LocalDateTime;

/**
 * One in-progress purchase: the flight, seat, passenger and payment.
 * Creator of Payment and Ticket (it holds all the data they need).
 */
public class TicketSale {
    /** U.S. federal excise tax on domestic airfare. */
    public static final double TAX_RATE = 0.075;

    private final Flight flight;
    private final LocalDateTime startedAt;
    private Seat seat;
    private Passenger passenger;
    private Payment payment;
    private boolean complete;

    public TicketSale(Flight flight) {
        if (flight == null) throw new IllegalArgumentException("Flight is required.");
        this.flight = flight;
        this.startedAt = LocalDateTime.now();
    }

    public void selectSeat(Seat chosen) {
        if (chosen == null) {
            throw new IllegalArgumentException("That seat does not exist on flight " + flight.getFlightNumber() + ".");
        }
        if (!chosen.isAvailable()) {
            throw new IllegalStateException("Seat " + chosen.getSeatNumber() + " is already sold.");
        }
        this.seat = chosen;
    }

    public void setPassenger(Passenger p) { this.passenger = p; }

    public double getFare() { return seat == null ? 0 : seat.getPrice(); }

    public double getTax() { return Math.round(getFare() * TAX_RATE * 100) / 100.0; }

    public double getTotal() { return Math.round((getFare() + getTax()) * 100) / 100.0; }

    /** Creates the Payment; fails if the sale is incomplete or the amount is wrong. */
    public Payment makePayment(PaymentMethod method, double amountTendered) {
        if (seat == null) throw new IllegalStateException("Select a seat before paying.");
        if (passenger == null) throw new IllegalStateException("Enter passenger information before paying.");
        if (!seat.isAvailable()) {
            throw new IllegalStateException("Seat " + seat.getSeatNumber() + " was sold to someone else. Choose another seat.");
        }
        this.payment = new Payment(method, amountTendered, getTotal());
        return payment;
    }

    /** Marks the seat sold and creates the Ticket. Call only after a successful payment. */
    public Ticket issueTicket(String ticketNumber) {
        if (payment == null) throw new IllegalStateException("Payment has not been made.");
        seat.markSold();
        complete = true;
        return new Ticket(ticketNumber, flight.getFlightNumber(), flight.getDate(),
                flight.getOrigin(), flight.getDestination(), seat.getSeatNumber(), seat.getSeatClass(),
                passenger.getName(), passenger.getEmail(), passenger.getPhone(),
                getTotal(), payment.getMethod(), LocalDateTime.now());
    }

    public Flight getFlight() { return flight; }
    public Seat getSeat() { return seat; }
    public Passenger getPassenger() { return passenger; }
    public Payment getPayment() { return payment; }
    public boolean isComplete() { return complete; }
    public LocalDateTime getStartedAt() { return startedAt; }
}
