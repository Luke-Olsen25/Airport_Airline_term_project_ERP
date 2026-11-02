package erp.TicketSales;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** A scheduled flight and its seat map (Information Expert for its seats). */
public class Flight {
    private final String flightNumber;
    private final String origin;
    private final String destination;
    private final LocalDate date;
    private final LocalTime departureTime;
    private final LocalTime arrivalTime;
    private final Map<String, Seat> seats = new LinkedHashMap<String, Seat>();

    public Flight(String flightNumber, String origin, String destination,
                  LocalDate date, LocalTime departureTime, LocalTime arrivalTime) {
        this.flightNumber = flightNumber;
        this.origin = origin;
        this.destination = destination;
        this.date = date;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
    }

    public void addSeat(Seat seat) { seats.put(seat.getSeatNumber().toUpperCase(), seat); }

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
        return origin.equalsIgnoreCase(from) && destination.equalsIgnoreCase(to) && date.equals(onDate);
    }

    /** Lowest fare among seats still for sale, or 0 if sold out. */
    public double getLowestFare() {
        double low = 0;
        for (Seat s : getAvailableSeats()) {
            if (low == 0 || s.getPrice() < low) low = s.getPrice();
        }
        return low;
    }

    public String getFlightNumber() { return flightNumber; }
    public String getOrigin() { return origin; }
    public String getDestination() { return destination; }
    public LocalDate getDate() { return date; }
    public LocalTime getDepartureTime() { return departureTime; }
    public LocalTime getArrivalTime() { return arrivalTime; }

    @Override
    public String toString() {
        return String.format("%-6s %s -> %s  %s  %s-%s  %2d seats left  from $%.2f",
                flightNumber, origin, destination, date, departureTime, arrivalTime,
                getAvailableSeats().size(), getLowestFare());
    }
}
