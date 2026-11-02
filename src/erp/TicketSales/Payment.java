package erp.TicketSales;

/** Payment collected for a ticket sale. */
public class Payment {
    private final PaymentMethod method;
    private final double amountTendered;
    private final double amountDue;

    public Payment(PaymentMethod method, double amountTendered, double amountDue) {
        if (method == null) {
            throw new IllegalArgumentException("Payment method is required.");
        }
        if (amountTendered + 0.005 < amountDue) {
            throw new IllegalArgumentException(String.format(
                    "Insufficient payment: $%.2f tendered, $%.2f due.", amountTendered, amountDue));
        }
        if (method == PaymentMethod.CARD && Math.abs(amountTendered - amountDue) > 0.005) {
            throw new IllegalArgumentException(String.format(
                    "Card payments must be for the exact amount due ($%.2f).", amountDue));
        }
        this.method = method;
        this.amountTendered = amountTendered;
        this.amountDue = amountDue;
    }

    public PaymentMethod getMethod() { return method; }
    public double getAmountTendered() { return amountTendered; }
    public double getAmountDue() { return amountDue; }
    public double getChange() { return Math.round((amountTendered - amountDue) * 100) / 100.0; }
}
