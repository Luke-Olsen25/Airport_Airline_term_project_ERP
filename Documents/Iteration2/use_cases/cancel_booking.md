# Use Case: Cancel Booking

| Field | Value |
| --- | --- |
| Use case | Cancel Booking |
| Department | Ticket Sales |
| Primary actor | Ticket Agent (acts for the passenger) |
| Stakeholders | Passenger: wants to give up the seat and have the cancellation on record so a refund can be processed. Airline: wants the seat back on sale so it can earn revenue again. Finance (Process Refund, Iteration 3): needs a record of each cancelled ticket. |
| Preconditions | The ticket was issued by Purchase Ticket and is stored in `src/data/tickets.txt`. The flight was scheduled by Flight Management in `src/data/scheduled_flights.txt`. The Ticket Agent is at the ERP main menu. |
| Postconditions | The ticket's status is CANCELLED, with the cancellation time, in `src/data/tickets.txt`. Its seat is AVAILABLE again in `src/data/seats.txt`. No other ticket or seat is changed. |

## Main success scenario

1. Passenger asks the Ticket Agent to cancel a booking.
2. Ticket Agent selects Cancel Booking (menu option 10) and enters the ticket number.
3. System finds the ticket, checks that it is still active and that its flight has not departed, and displays the ticket details.
4. Ticket Agent confirms with the passenger and answers Y to cancel.
5. System marks the ticket CANCELLED with the current time, puts the seat back on sale, saves both files, and reports that the ticket is cancelled and the seat is open for sale again.

## Alternate flows

- **3a. No ticket with that number:** System reports that no ticket was found. Ticket Agent re-enters the number or leaves it blank to exit.
- **3b. Ticket already cancelled:** System reports the date it was cancelled. Ticket Agent re-enters the number or exits.
- **3c. Flight has already departed:** System reports that the booking can no longer be cancelled. Ticket Agent re-enters the number or exits.
- **4a. Passenger changes their mind:** Ticket Agent answers N. System discards the request; nothing is changed.
- **5a. Flight is no longer on the schedule:** System still cancels the ticket, but has no seat to release, and says so.
- **\*a. Ticket Agent leaves the ticket number blank:** System returns to the main menu; nothing is changed.

## Notes

- Money is not returned in this use case. The CANCELLED ticket is the input to Process Refund (Iteration 3).
- A ticket on a flight that Flight Management has cancelled can still be cancelled, so that passenger can be refunded.
