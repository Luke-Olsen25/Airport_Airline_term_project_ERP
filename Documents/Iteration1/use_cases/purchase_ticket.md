# Use Case: Purchase Ticket

| Field | Value |
| --- | --- |
| Use case | Purchase Ticket |
| Department | Ticket Sales |
| Primary actor | Ticket Agent (sells on behalf of the passenger) |
| Stakeholders | Passenger: wants a confirmed seat and a ticket. Airline: wants payment collected and the seat recorded as sold. Airport Operations: needs the ticket record for check-in and boarding. |
| Preconditions | Flights exist in `src/data/flights.txt` and their seats for sale exist in `src/data/seats.txt`. |
| Postconditions | A Ticket is created and saved to `src/data/tickets.txt`; the seat is marked SOLD in `src/data/seats.txt`; payment is recorded on the ticket. |

## Main success scenario

1. Passenger arrives at the ticket counter and asks to buy a ticket.
2. Ticket Agent enters the origin, destination and travel date.
3. System displays the matching flights that still have open seats, with times and the lowest fare.
4. Ticket Agent enters the chosen flight number.
5. System starts a new ticket sale and displays the available seats with class and price.
6. Ticket Agent enters the chosen seat number.
7. System reserves the seat for this sale and displays the fare, tax (7.5%) and total.
8. Ticket Agent enters the passenger's name, email and phone number.
9. System validates and records the passenger information.
10. Ticket Agent enters the payment method (card or cash) and amount.
11. System validates the payment, marks the seat sold, generates a ticket number, saves the ticket and seat status, and displays the ticket (and change due for cash).

## Alternate flows

- **3a. No flights match:** System reports that no flights with open seats were found. Ticket Agent enters a new search or ends the use case.
- **4a. Invalid flight number:** System reports that no such flight exists. Ticket Agent re-enters the flight number.
- **4b. Flight is sold out:** System reports the flight is sold out. Ticket Agent chooses another flight.
- **6a. Seat does not exist:** System reports the seat is not on this flight. Ticket Agent re-enters the seat.
- **6b. Seat already sold:** System reports the seat is taken. Ticket Agent chooses another seat.
- **9a. Invalid passenger information** (blank name, bad email, phone not 10 to 15 digits): System explains the problem. Ticket Agent re-enters the information.
- **11a. Cash tendered is less than the total:** System rejects the payment and shows the amount due. Ticket Agent collects the full amount or changes the payment method.
- **11b. Card amount does not equal the total:** System rejects the payment. Ticket Agent re-enters the payment.
- **11c. Seat was sold to someone else before payment:** System cancels the sale without charging. Ticket Agent restarts at step 4.
- **\*a. At any step, the passenger cancels:** Ticket Agent leaves the input blank; System discards the sale. Nothing is charged or saved.
