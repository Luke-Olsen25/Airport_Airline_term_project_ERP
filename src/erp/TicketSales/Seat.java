package erp.TicketSales;

/** One seat on one flight, with its fare and sale status. */
public class Seat {
    private final String seatNumber;
    private final SeatClass seatClass;
    private final double price;
    private boolean sold;

    public Seat(String seatNumber, SeatClass seatClass, double price, boolean sold) {
        this.seatNumber = seatNumber;
        this.seatClass = seatClass;
        this.price = price;
        this.sold = sold;
    }

    public String getSeatNumber() { return seatNumber; }
    public SeatClass getSeatClass() { return seatClass; }
    public double getPrice() { return price; }
    public boolean isAvailable() { return !sold; }

    /** Marks the seat as sold. Fails if someone already bought it. */
    public void markSold() {
        if (sold) {
            throw new IllegalStateException("Seat " + seatNumber + " is already sold.");
        }
        sold = true;
    }

    /** Puts a sold seat back on sale (Cancel Booking). Fails if the seat was not sold. */
    public void release() {
        if (!sold) {
            throw new IllegalStateException("Seat " + seatNumber + " is not sold.");
        }
        sold = false;
    }

    @Override
    public String toString() {
        return String.format("%-4s %-8s $%8.2f", seatNumber, seatClass, price);
    }
}
