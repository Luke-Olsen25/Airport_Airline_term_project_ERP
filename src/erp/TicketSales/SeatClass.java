package erp.TicketSales;

/** Cabin class of a seat, with the standard fare used when a flight's seat map is first created. */
public enum SeatClass {
    FIRST(649.00), BUSINESS(389.00), ECONOMY(149.00);

    private final double defaultFare;

    SeatClass(double defaultFare) {
        this.defaultFare = defaultFare;
    }

    public double getDefaultFare() { return defaultFare; }
}
