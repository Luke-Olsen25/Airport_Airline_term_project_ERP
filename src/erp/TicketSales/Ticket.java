package erp.TicketSales;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * The issued ticket: proof the passenger holds a seat on a flight.
 * Information Expert for its own status, so it decides whether it can be cancelled.
 */
public class Ticket {
    private final String ticketNumber;
    private final String flightId;
    private final String flightNumber;
    private final LocalDate flightDate;
    private final String origin;
    private final String destination;
    private final String seatNumber;
    private final SeatClass seatClass;
    private final String passengerName;
    private final String passengerEmail;
    private final String passengerPhone;
    private final double amountPaid;
    private final PaymentMethod paymentMethod;
    private final LocalDateTime issuedAt;
    private TicketStatus status;
    private LocalDateTime cancelledAt;

    public Ticket(String ticketNumber, String flightId, String flightNumber, LocalDate flightDate, String origin,
                  String destination, String seatNumber, SeatClass seatClass, String passengerName,
                  String passengerEmail, String passengerPhone, double amountPaid,
                  PaymentMethod paymentMethod, LocalDateTime issuedAt,
                  TicketStatus status, LocalDateTime cancelledAt) {
        this.ticketNumber = ticketNumber;
        this.flightId = flightId;
        this.flightNumber = flightNumber;
        this.flightDate = flightDate;
        this.origin = origin;
        this.destination = destination;
        this.seatNumber = seatNumber;
        this.seatClass = seatClass;
        this.passengerName = passengerName;
        this.passengerEmail = passengerEmail;
        this.passengerPhone = passengerPhone;
        this.amountPaid = amountPaid;
        this.paymentMethod = paymentMethod;
        this.issuedAt = issuedAt;
        this.status = status;
        this.cancelledAt = cancelledAt;
    }

    /** Marks the ticket cancelled. Fails if it was already cancelled. */
    public void cancel(LocalDateTime when) {
        if (isCancelled()) {
            throw new IllegalStateException("Ticket " + ticketNumber + " was already cancelled on "
                    + cancelledAt.toLocalDate() + ".");
        }
        status = TicketStatus.CANCELLED;
        cancelledAt = when;
    }

    public boolean isCancelled() { return status == TicketStatus.CANCELLED; }

    public String getTicketNumber() { return ticketNumber; }
    public String getFlightId() { return flightId; }
    public String getFlightNumber() { return flightNumber; }
    public LocalDate getFlightDate() { return flightDate; }
    public String getOrigin() { return origin; }
    public String getDestination() { return destination; }
    public String getSeatNumber() { return seatNumber; }
    public SeatClass getSeatClass() { return seatClass; }
    public String getPassengerName() { return passengerName; }
    public String getPassengerEmail() { return passengerEmail; }
    public String getPassengerPhone() { return passengerPhone; }
    public double getAmountPaid() { return amountPaid; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public LocalDateTime getIssuedAt() { return issuedAt; }
    public TicketStatus getStatus() { return status; }
    public LocalDateTime getCancelledAt() { return cancelledAt; }

    @Override
    public String toString() {
        DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return "========================================\n"
             + "  TICKET " + ticketNumber + "\n"
             + "  Passenger : " + passengerName + "\n"
             + "  Flight    : " + flightNumber + "  " + origin + " -> " + destination + "\n"
             + "  Date      : " + flightDate + "\n"
             + "  Seat      : " + seatNumber + " (" + seatClass + ")\n"
             + String.format("  Paid      : $%.2f by %s%n", amountPaid, paymentMethod)
             + "  Issued    : " + issuedAt.format(f) + "\n"
             + "  Status    : " + status + (isCancelled() ? " on " + cancelledAt.format(f) : "") + "\n"
             + "========================================";
    }
}
