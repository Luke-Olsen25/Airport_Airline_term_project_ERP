package erp.TicketSales;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A scheduled flight as Ticket Sales sees it: the schedule data from Flight Management
 * plus the seats Ticket Sales sells on it. Information Expert for its seats, its
 * departure time and whether it is open for sale.
 */
public class Flight {
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm");
    private static final String SCHEDULED = "Scheduled";

    private final String flightId;
    private final String flightNumber;
    private final String airline;
    private final String origin;
    private final String destination;
    private final LocalDateTime departureTime;
    private final LocalDateTime arrivalTime;
    private final String status;
    private final Map<String, Seat> seats = new LinkedHashMap<String, Seat>();

    public Flight(String flightId, String flightNumber, String airline, String origin, String destination,
                  LocalDateTime departureTime, LocalDateTime arrivalTime, String status) {
        this.flightId = flightId;
        this.flightNumber = flightNumber;
        this.airline = airline;
        this.origin = origin;
        this.destination = destination;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.status = status;
    }

    public void addSeat(Seat seat) { seats.put(seat.getSeatNumber().toUpperCase(), seat); }

    /**
     * Creator: a Flight contains its Seats, so it creates the standard 24-seat map
     * (row 1 First, row 2 Business, rows 3-6 Economy) at the default fares.
     */
    public void createDefaultSeatMap() {
        for (int row = 1; row <= 6; row++) {
            SeatClass seatClass = row == 1 ? SeatClass.FIRST : row == 2 ? SeatClass.BUSINESS : SeatClass.ECONOMY;
            for (char col = 'A'; col <= 'D'; col++) {
                addSeat(new Seat("" + row + col, seatClass, seatClass.getDefaultFare(), false));
            }
        }
    }

    public boolean hasSeatMap() { return !seats.isEmpty(); }

    public Seat getSeat(String seatNumber) {
        return seatNumber == null ? null : seats.get(seatNumber.trim().toUpperCase());
    }

    public Collection<Seat> getAllSeats() { return seats.values(); }

    public List<Seat> getAvailableSeats() {
        List<Seat> result = new ArrayList<Seat>();
        for (Seat s : seats.values()) {
            if (s.isAvailable()) result.add(s);
        }
        return result;
    }

    public boolean hasAvailableSeats() { return !getAvailableSeats().isEmpty(); }

    /** True if this flight runs between the given airports on the given date. */
    public boolean matches(String from, String to, LocalDate onDate) {
        return origin.equalsIgnoreCase(from.trim()) && destination.equalsIgnoreCase(to.trim())
                && getDate().equals(onDate);
    }

    /** True once the departure time has been reached. */
    public boolean hasDeparted(LocalDateTime now) { return !departureTime.isAfter(now); }

    /** Tickets can be sold only on scheduled flights that have not departed. */
    public boolean isOpenForSale(LocalDateTime now) {
        return SCHEDULED.equalsIgnoreCase(status) && !hasDeparted(now);
    }

    /** Lowest fare among seats still for sale, or 0 if sold out. */
    public double getLowestFare() {
        double low = 0;
        for (Seat s : getAvailableSeats()) {
            if (low == 0 || s.getPrice() < low) low = s.getPrice();
        }
        return low;
    }

    public String getFlightId() { return flightId; }
    public String getFlightNumber() { return flightNumber.toUpperCase(); }
    public String getAirline() { return airline; }
    public String getOrigin() { return origin.toUpperCase(); }
    public String getDestination() { return destination.toUpperCase(); }
    public LocalDate getDate() { return departureTime.toLocalDate(); }
    public LocalDateTime getDepartureTime() { return departureTime; }
    public LocalDateTime getArrivalTime() { return arrivalTime; }
    public String getStatus() { return status; }

    @Override
    public String toString() {
        return String.format("%-6s %s -> %s  %s  %s-%s  %2d seats left  from $%.2f",
                getFlightNumber(), getOrigin(), getDestination(), getDate(),
                departureTime.format(TIME), arrivalTime.format(TIME),
                getAvailableSeats().size(), getLowestFare());
    }
}
