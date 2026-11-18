# Interface List: Cancel Booking (package `erp.TicketSales`)

## System operations (public interface of `TicketSalesService`)

| Operation | Returns | Purpose |
| --- | --- | --- |
| findBooking(ticketNumber : String) | Ticket | Finds an active ticket whose flight has not departed and holds it for cancellation |
| confirmCancellation() | boolean | Cancels the held ticket and releases its seat; true if a seat was released |
| abandonCancellation() | void | Agent declined; clears the held ticket without changing anything |

## Changed or new Java interfaces

| Interface | Methods | Implemented by |
| --- | --- | --- |
| ITicketRepository | initializeStorage() : void; nextTicketNumber() : String; save(t : Ticket) : boolean; **findByNumber(n : String) : Ticket**; **update(t : Ticket) : boolean**; loadAll() : List&lt;Ticket&gt; | TicketFileRepository (`src/data/tickets.txt`) |
| IFlightRepository | initializeStorage() : void; loadFlights() : List&lt;Flight&gt;; saveSeats(flights : List&lt;Flight&gt;) : void | FlightFileRepository (reads Flight Management's `src/data/scheduled_flights.txt`; reads and writes `src/data/seats.txt`) |

Bold = new in Iteration 2.

## Ticket Sales API for other departments (package `api.TicketSales`)

Read-only, following the team's shared-API convention (same style as `api.HumanResources.IEmployeeLookup`). Only Ticket Sales writes tickets and seats; other departments read them through this interface.

| Interface | Methods | Implemented by |
| --- | --- | --- |
| ITicketLookup | getTicketByNumber(ticketNumber : String) : Optional&lt;Ticket&gt;; getActiveTicketsForFlight(flightId : String) : List&lt;Ticket&gt;; getCancelledTickets() : List&lt;Ticket&gt;; getSeatsSold(flightId : String) : int; getSeatsAvailable(flightId : String) : int | TicketLookup (wraps `ITicketRepository` and `IFlightRepository`) |

## Domain methods added in Iteration 2

| Class | Method | Purpose |
| --- | --- | --- |
| Ticket | cancel(when : LocalDateTime) : void | Marks the ticket CANCELLED; fails if already cancelled |
| Ticket | isCancelled() : boolean | Status check |
| Flight | hasDeparted(now : LocalDateTime) : boolean | True once the departure time is reached |
| Flight | isOpenForSale(now : LocalDateTime) : boolean | Scheduled and not departed |
| Flight | createDefaultSeatMap() : void | Creates 24 seats at the default fares |
| Seat | release() : void | Puts a sold seat back on sale |
| SeatClass | getDefaultFare() : double | Standard fare for the cabin |
| FlightCatalog | reload() : void | Re-reads flights and seats from the files |

## Menu entry point

`terminal_ui` option 14 calls `TicketSalesTerminal.handleCancelBooking(scanner : Scanner)`.

## Data files

Plain text, one record per line, fields separated by `|`.

| File | Owner | Fields |
| --- | --- | --- |
| scheduled_flights.txt | Flight Management (read only) | flightId, flightNumber, airline, origin, destination, departure, arrival, aircraftId, gateId, gateAirport, terminal, status |
| seats.txt | Ticket Sales | flightId, seatNumber, seatClass, price, status (AVAILABLE or SOLD) |
| tickets.txt | Ticket Sales | ticketNumber, flightId, flightNumber, flightDate, origin, destination, seatNumber, seatClass, passengerName, passengerEmail, passengerPhone, amountPaid, paymentMethod, issuedAt, status (ACTIVE or CANCELLED), cancelledAt |
