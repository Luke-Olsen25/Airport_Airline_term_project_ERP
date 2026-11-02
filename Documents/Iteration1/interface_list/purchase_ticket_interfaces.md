# Interface List: Purchase Ticket (package `erp.TicketSales`)

## System operations (public interface of `TicketSalesService`)

| Operation | Returns | Purpose |
| --- | --- | --- |
| searchFlights(origin : String, destination : String, date : LocalDate) | List&lt;Flight&gt; | Flights with open seats on that route and date |
| startPurchase(flightNumber : String) | List&lt;Seat&gt; | Starts a TicketSale and lists open seats |
| selectSeat(seatNumber : String) | double | Reserves the seat for this sale; returns the total due |
| enterPassengerInfo(name : String, email : String, phone : String) | void | Validates and records the passenger |
| makePayment(method : PaymentMethod, amountTendered : double) | Ticket | Takes payment, issues and saves the ticket |
| cancelPurchase() | void | Discards the current sale |

## Java interfaces

| Interface | Methods | Implemented by |
| --- | --- | --- |
| IFlightRepository | initializeStorage() : void; loadFlights() : List&lt;Flight&gt;; saveSeats(flights : List&lt;Flight&gt;) : void | FlightFileRepository (`src/data/flights.json` read-only, `src/data/seats.json`) |
| ITicketRepository | initializeStorage() : void; nextTicketNumber() : String; save(t : Ticket) : boolean; loadAll() : List&lt;Ticket&gt; | TicketFileRepository (`src/data/tickets.json`) |

## Menu entry point

`terminal_ui` option 2 calls `TicketSalesTerminal.handlePurchaseTicket(scanner : Scanner)`.

## Data files

JSON array, one object per line (same layout as `aircrafts.json`). Read with `JsonLines.getField`, no full parser.

| File | Owner | Fields |
| --- | --- | --- |
| flights.json | Flight Management (sample data until Schedule Flight is done) | flightNumber, origin, destination, date, departureTime, arrivalTime, tailNumber |
| seats.json | Ticket Sales | flightNumber, seatNumber, seatClass, price, status (AVAILABLE or SOLD) |
| tickets.json | Ticket Sales | ticketNumber, flightNumber, flightDate, origin, destination, seatNumber, seatClass, passengerName, passengerEmail, passengerPhone, amountPaid, paymentMethod, issuedAt |
