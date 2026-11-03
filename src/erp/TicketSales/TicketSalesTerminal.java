package erp.TicketSales;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/**
 * Terminal screens for Ticket Sales. Only reads input and prints output;
 * every system operation goes to TicketSalesService.
 */
public class TicketSalesTerminal {
    private final TicketSalesService service;

    public TicketSalesTerminal(TicketSalesService service) {
        this.service = service;
    }

    /** Walks the ticket agent through the Purchase Ticket main success scenario. */
    public void handlePurchaseTicket(Scanner scanner) {
        System.out.println("\n--- PURCHASE TICKET ---");
        try {
            purchase(scanner);
        } catch (RuntimeException e) {
            service.cancelPurchase();
            System.out.println("\n[ERROR] " + e.getMessage());
        }
        System.out.print("\nPress Enter to return to Main Menu...");
        if (scanner.hasNextLine()) scanner.nextLine();
    }

    private void purchase(Scanner scanner) {
        // Steps 2-3: search
        List<Flight> found;
        while (true) {
            String origin = ask(scanner, "Enter Origin airport code (e.g., DSM)");
            String destination = ask(scanner, "Enter Destination airport code (e.g., ORD)");
            LocalDate date = askDate(scanner, "Enter Travel date (YYYY-MM-DD)");
            if (origin == null || destination == null || date == null) return;
            found = service.searchFlights(origin, destination, date);
            if (!found.isEmpty()) break;
            System.out.println("No flights with open seats match that search.");
            String again = ask(scanner, "Search again? (Y/N)");
            if (again == null || !again.equalsIgnoreCase("Y")) { cancel(); return; }
        }
        for (Flight f : found) System.out.println("  " + f);

        // Steps 4-5: choose a flight
        List<Seat> open = null;
        while (open == null) {
            String number = ask(scanner, "Enter Flight number (blank to cancel)");
            if (number == null || number.isEmpty()) { cancel(); return; }
            try {
                open = service.startPurchase(number);
            } catch (RuntimeException e) {
                System.out.println("  " + e.getMessage());
            }
        }
        System.out.println("Available seats:");
        for (int i = 0; i < open.size(); i++) {
            System.out.print("  " + open.get(i));
            if (i % 4 == 3 || i == open.size() - 1) System.out.println();
        }

        // Steps 6-7: choose a seat
        while (true) {
            String seat = ask(scanner, "Enter Seat number (blank to cancel)");
            if (seat == null || seat.isEmpty()) { cancel(); return; }
            try {
                double total = service.selectSeat(seat);
                TicketSale sale = service.getCurrentSale();
                System.out.printf("  Fare $%.2f + tax $%.2f = total $%.2f%n", sale.getFare(), sale.getTax(), total);
                break;
            } catch (RuntimeException e) {
                System.out.println("  " + e.getMessage());
            }
        }

        // Steps 8-9: passenger information
        while (true) {
            String name = ask(scanner, "Enter Passenger full name (blank to cancel)");
            if (name == null || name.isEmpty()) { cancel(); return; }
            String email = ask(scanner, "Enter Passenger email");
            String phone = ask(scanner, "Enter Passenger phone");
            try {
                service.enterPassengerInfo(name, email, phone);
                break;
            } catch (RuntimeException e) {
                System.out.println("  " + e.getMessage() + " Please re-enter.");
            }
        }

        // Steps 10-11: payment
        while (true) {
            double total = service.getCurrentSale().getTotal();
            System.out.printf("Total due: $%.2f%n", total);
            String m = ask(scanner, "Enter Payment method CARD or CASH (blank to cancel)");
            if (m == null || m.isEmpty()) { cancel(); return; }
            PaymentMethod method;
            try {
                method = PaymentMethod.valueOf(m.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("  Enter CARD or CASH.");
                continue;
            }
            double amount = total;
            if (method == PaymentMethod.CASH) {
                String a = ask(scanner, "Enter Cash tendered");
                try {
                    amount = Double.parseDouble(a == null ? "" : a.replace("$", "").trim());
                } catch (NumberFormatException e) {
                    System.out.println("  Enter an amount like 250.00.");
                    continue;
                }
            }
            try {
                Ticket t = service.makePayment(method, amount);
                if (method == PaymentMethod.CASH && amount > total) {
                    System.out.printf("Change due: $%.2f%n", Math.round((amount - total) * 100) / 100.0);
                }
                System.out.println("\n[SUCCESS] Payment accepted. Ticket issued:");
                System.out.println(t);
                return;
            } catch (IllegalArgumentException e) {
                System.out.println("  " + e.getMessage());
            } catch (IllegalStateException e) {
                System.out.println("  " + e.getMessage());
                cancel();
                return;
            }
        }
    }

    private void cancel() {
        service.cancelPurchase();
        System.out.println("\n[ABORTED] Purchase cancelled. Nothing was charged.");
    }

    /** Prompts and reads one line; returns null at end of input. */
    private static String ask(Scanner scanner, String prompt) {
        System.out.print(prompt + ": ");
        if (!scanner.hasNextLine()) return null;
        return scanner.nextLine().trim();
    }

    private static LocalDate askDate(Scanner scanner, String prompt) {
        while (true) {
            String s = ask(scanner, prompt);
            if (s == null) return null;
            try {
                return LocalDate.parse(s);
            } catch (DateTimeParseException e) {
                System.out.println("  Use the format YYYY-MM-DD, e.g. 2026-11-20.");
            }
        }
    }
}
