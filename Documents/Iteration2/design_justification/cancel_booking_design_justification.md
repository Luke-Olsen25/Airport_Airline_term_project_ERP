# Design Justification: Cancel Booking

Each responsibility in Cancel Booking is given to the class that holds the information it needs (Information Expert). The supporting choices below keep the Ticket Sales package decoupled from the user interface, from the file format, and from the other departments' code.

## Information Expert

| Responsibility | Assigned to | Why this class |
| --- | --- | --- |
| Decide whether a ticket can be cancelled, and cancel it | `Ticket.cancel(when)`, `Ticket.isCancelled()` | The ticket holds its own status and cancellation time. Only it can tell whether it was already cancelled (alternate flow 3b). |
| Decide whether the flight has departed | `Flight.hasDeparted(now)` | The flight holds the departure date and time from Flight Management (alternate flow 3c). |
| Find the seat that belongs to the ticket | `Flight.getSeat(seatNumber)` | A flight contains its seats, keyed by seat number. |
| Put the seat back on sale | `Seat.release()` | The seat holds its own sold/available state and refuses to release a seat that was not sold. |
| Find a flight by ID | `FlightCatalog.getFlight(flightId)` | The catalog is the only object that holds all flights. |
| Know each cabin's standard fare | `SeatClass.getDefaultFare()` | The fare belongs to the cabin class, not to the code that builds seat maps. |

## Creator

- **`Flight` creates its `Seat` objects** in `createDefaultSeatMap()`, because a flight contains (aggregates) its seats. A newly scheduled flight gets 24 seats the first time Ticket Sales sees it.
- **`TicketFileRepository` creates `Ticket` objects** when it reads `tickets.txt`, because it holds the initializing data (one record per ticket).
- **`TicketSale` creates the `Ticket`** at the end of Purchase Ticket (Iteration 1); Cancel Booking changes that same object.

## Controller

- **`TicketSalesService` is the use case controller** for all Ticket Sales system operations: `findBooking(ticketNumber)` and `confirmCancellation()` for Cancel Booking, plus the Purchase Ticket operations.
- **`TicketSalesTerminal` only reads input and prints output.** It never touches tickets, seats or files.
- **Each department has its own controller,** rather than one controller for the whole ERP. The shared `terminal_ui` menu just routes to each department, so no single controller becomes bloated.
- **Cancel Booking is two system operations,** find and then confirm. This matches the main success scenario, where the agent must see the ticket before agreeing to cancel. The controller keeps the found ticket and flight (`bookingToCancel`, `flightOfBooking`) between the two calls. `abandonCancellation()` clears them when the agent declines (alternate flow 4a).

## Low Coupling and Protected Variations

- **Storage is behind interfaces.** `ITicketRepository` and `IFlightRepository` hide the file format. The controller and domain classes never open a file, so changing the format would change only the two `*FileRepository` classes. `ITicketRepository` gained `findByNumber` and `update` for this use case.
- **Shared data goes through read-only APIs (team convention).** Ticket Sales publishes `api.TicketSales.ITicketLookup`, implemented by `TicketLookup` on top of Ticket Sales' own repositories. Other departments can look up a ticket, list the passengers booked on a flight, count seats sold or left, and get cancelled tickets (for refunds). They never read `tickets.txt` or `seats.txt` directly and can't change them. This is Protected Variations and Indirection: the file layout can change without breaking another department, and only Ticket Sales writes its own data.
- **Ticket Sales reads Flight Management's flights through one adapter.** `FlightFileRepository` (Ticket Sales) calls Flight Management's own `FlightFileRepository.findAll()` rather than parsing `scheduled_flights.txt` itself. If Flight Management changes its file format, only their repository changes, and Ticket Sales keeps working. This is the only Ticket Sales class that depends on another department. Once Flight Management publishes its read-only API (`api.FlightManagement`), only this adapter has to change.
- **The file format stays backward compatible.** `tickets.txt` gained `flightId`, `status` and `cancelledAt` fields, and Iteration 1 records (13 fields) are still read as ACTIVE tickets. Existing data did not have to be converted.

## High Cohesion

- **Ticket Sales keeps its own `Flight` class** rather than reusing `erp.FlightManagement.Flight`. Flight Management's class describes the schedule: aircraft, gate, times. Ticket Sales' class adds the seats and the sales rules (`isOpenForSale`, `getLowestFare`). Putting seats into Flight Management's class would mix two departments' responsibilities and couple their code to ours.
- **`FlightCatalog` (Pure Fabrication)** keeps searching and reloading flights out of the controller.

## Other major design choices

- **Mark CANCELLED instead of deleting the record.** Cancelled tickets stay in `tickets.txt` with the time of cancellation. Process Refund (Iteration 3) needs that record, ticket numbers are never reused, and sales history is kept.
- **Key flights by `flightId`, not flight number.** A flight number such as AA201 repeats every day, but `flightId` is unique. Seats and new tickets are stored by `flightId`.
- **Reload flights at the start of each use case.** `FlightCatalog.reload()` re-reads the schedule, so a flight scheduled in the same session (menu option 8) can be sold or cancelled right away.
- **Allow cancelling a ticket on a cancelled flight.** The passenger must be able to get a refund. If a flight is no longer on the schedule at all, the ticket is still cancelled and the ticket's own flight date is used for the "already departed" check (alternate flow 5a).
