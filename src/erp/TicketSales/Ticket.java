package erp.TicketSales;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** The issued ticket: proof the passenger holds a seat on a flight. */
public class Ticket {
    private final String ticketNumber;
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

    public Ticket(String ticketNumber, String flightNumber, LocalDate flightDate, String origin,
                  String destination, String seatNumber, SeatClass seatClass, String passengerName,
                  String passengerEmail, String passengerPhone, double amountPaid,
                  PaymentMethod paymentMethod, LocalDateTime issuedAt) {
        this.ticketNumber = ticketNumber;
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
    }

    public String getTicketNumber() { return ticketNumber; }
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
             + "========================================";
    }
}
