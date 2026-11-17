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
            Flight chosen = null;
            for (Flight f : found) {
                if (f.getFlightNumber().equalsIgnoreCase(number) || f.getFlightId().equalsIgnoreCase(number)) {
                    chosen = f;
                }
            }
            if (chosen == null) {
                System.out.println("  Flight " + number + " is not in the search results.");
                continue;
            }
            try {
                open = service.startPurchase(chosen.getFlightId());
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

    /** Walks the ticket agent through the Cancel Booking main success scenario. */
    public void handleCancelBooking(Scanner scanner) {
        System.out.println("\n--- CANCEL BOOKING ---");
        try {
            cancelBooking(scanner);
        } catch (RuntimeException e) {
            service.abandonCancellation();
            System.out.println("\n[ERROR] " + e.getMessage());
        }
        System.out.print("\nPress Enter to return to Main Menu...");
        if (scanner.hasNextLine()) scanner.nextLine();
    }

    private void cancelBooking(Scanner scanner) {
        // Steps 2-3: find the booking
        Ticket ticket = null;
        while (ticket == null) {
            String number = ask(scanner, "Enter Ticket number (e.g., TK000001, blank to exit)");
            if (number == null || number.isEmpty()) {
                System.out.println("\n[ABORTED] No booking was cancelled.");
                return;
            }
            try {
                ticket = service.findBooking(number);
            } catch (RuntimeException e) {
                System.out.println("  " + e.getMessage());
            }
        }
        System.out.println(ticket);

        // Steps 4-5: confirm and cancel
        String answer = ask(scanner, "Cancel this booking? (Y/N)");
        if (answer == null || !answer.equalsIgnoreCase("Y")) {
            service.abandonCancellation();
            System.out.println("\n[ABORTED] The booking was not cancelled.");
            return;
        }
        boolean seatReleased = service.confirmCancellation();
        System.out.println("\n[SUCCESS] Ticket " + ticket.getTicketNumber() + " is cancelled.");
        if (seatReleased) {
            System.out.println("Seat " + ticket.getSeatNumber() + " on flight " + ticket.getFlightNumber()
                    + " is open for sale again.");
        } else {
            System.out.println("Flight " + ticket.getFlightNumber()
                    + " is no longer on the schedule, so no seat was released.");
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
