package erp.TicketSales;

/** The person who will fly on the ticket. */
public class Passenger {
    private final String name;
    private final String email;
    private final String phone;

    public Passenger(String name, String email, String phone) {
        name = clean(name);
        email = clean(email);
        phone = clean(phone);
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Passenger name is required.");
        }
        if (!email.matches("[^@\\s]+@[^@\\s]+\\.[^@\\s]+")) {
            throw new IllegalArgumentException("Email address is not valid: " + email);
        }
        if (!phone.replaceAll("[^0-9]", "").matches("[0-9]{10,15}")) {
            throw new IllegalArgumentException("Phone number must have 10 to 15 digits.");
        }
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    /** Trims input and strips quotes and backslashes so the value is safe to write into a JSON file. */
    private static String clean(String s) {
        return s == null ? "" : s.replace("\"", "").replace("\\", "").trim();
    }

    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
}
