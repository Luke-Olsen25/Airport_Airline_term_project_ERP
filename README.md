# Airport_Airline_term_project_ERP

A terminal-based Enterprise Resource Planning (ERP) system for a combined airport/airline business. Each department is a module/domain model of the system. The system is represented in a terminal based menu and data is file I/O only.

## Departments and Use Cases

| Department          | Iteration 1 (CURRENT) | Iteration 2 (Subject to Change) | Iteration 3 (Subject to Change) |
|---------------------|----------------------|-------------|-------------|
| Flight Management   | Schedule Flight      | Update Flight         | Cancel Flight         |
| Ticket Sales        | Purchase Ticket          | Cancel Booking         | Process Refund         |
| Human Resources     | Hire Employee        | Assign Employee to Flight         | Process Payroll         |
| Aircraft Management | Register Aircraft    | Schedule Maintenance         | Record Maintenance         |
| Airport Operations  | Assign Gate          | Update Gate Assignment         | Track Gate Availability         |

## Project Layout

```
Airport_Airline_term_project_ERP/
├── README.md
├── Documents/
│   ├── Iteration1/
│   │   ├── inception_document/         # Inception write-ups per department
│   │   ├── use_cases/                  # One use case per person
│   │   ├── domain_model/               # Domain model diagrams
│   │   ├── communication_diagrams/     # Communication diagrams per system operation
│   │   ├── class_diagrams/             # Partial class diagrams
│   │   └── interface_list/             # Interface lists per use case
│   ├── Iteration2/                     # Empty for now; will mirror Iteration1, no inception document
│   └── Iteration3/                     # Empty for now; will mirror Iteration1, no inception document
└── src/
    ├── erp/                            # Java source, one package per department
    │   ├── main.java                   # Entry point
    │   ├── terminal_ui.java            # Main menu; routes selections to each department
    │   ├── airport_ops.java            # Airport Operations (Assign Gate, View Gate Assignments)
    │   ├── flight_mngt.java            # Empty stub (Flight Management lives in FlightManagement/)
    │   ├── ticket_sales.java           # Empty stub (Ticket Sales lives in TicketSales/)
    │   ├── AircraftManagement/         # Aircraft Management (Register Aircraft, View Registered Aircrafts)
    │   │   ├── Aircraft.java
    │   │   ├── AircraftStatus.java
    │   │   ├── IAircraftRepository.java
    │   │   ├── AircraftFileRepository.java
    │   │   └── AircraftManagementService.java
    │   ├── FlightManagement/           # Flight Management (Schedule Flight, View Scheduled Flights)
    │   │   ├── Flight.java
    │   │   ├── Gate.java
    │   │   ├── FlightFileRepository.java
    │   │   └──  FlightManagementService.java
    │   ├── HumanResources/             # Human Resources (Hire Employee, View Current Employees)
    │   │   ├── Employee.java
    │   │   ├── EmployeeRole.java
    │   │   ├── EmployeeStatus.java
    │   │   ├── IEmployeeRepository.java
    │   │   ├── EmployeeFileRepository.java
    │   │   └── HumanResourcesService.java
    │   └── TicketSales/                # Ticket Sales (Purchase Ticket)
    │       ├── Flight.java
    │       ├── FlightCatalog.java
    │       ├── Seat.java
    │       ├── SeatClass.java
    │       ├── Passenger.java
    │       ├── Payment.java
    │       ├── PaymentMethod.java
    │       ├── Ticket.java
    │       ├── TicketSale.java
    │       ├── IFlightRepository.java
    │       ├── ITicketRepository.java
    │       ├── FlightFileRepository.java
    │       ├── TicketFileRepository.java
    │       ├── TicketSalesService.java
    │       └── TicketSalesTerminal.java
    └── data/                           # Flat-file storage (no DBMS)
        ├── aircrafts.txt               # Aircraft Management
        ├── employee.txt                # Human Resources
        ├── scheduled_flights.txt       # Flight Management
        ├── flights.txt                 # Flights offered for sale (read by Ticket Sales)
        ├── seats.txt                   # Ticket Sales
        ├── tickets.txt                 # Ticket Sales
        └── gates.txt                   # Airport Operations
```


## Iteration 1 Deliverables

| # | Deliverable                                                                  | Points | 
|---|------------------------------------------------------------------------------|--------|
| 1 | Inception paragraph(s) and use case names                                    | 5      | 
| 2 | One use case per person (name, actor, precondition, main success scenario, alternate flows) | 15 | 
| 3 | Domain model                                                                 | 10     | 
| 4 | Partial communication diagrams                                               | 30     | 
| 5 | Partial class diagrams                                                       | 3      | 
| 6 | Interface list                                                               | 2      |
| 7 | Implementation (Java listing + working partial application)                  | 35     | 

## Build and Run

Run from the repository root (data paths are relative to it):

```
javac -d out src/erp/*.java src/erp/AircraftManagement/*.java src/erp/TicketSales/*.java
java -cp out erp.terminal_ui
```

## Conventions

- Use case names start with a verb and must pass the "boss test" (it delivers measurable business value).
- About 85% of project time goes to implementation.
- Each department's data lives in its own file in `src/data/` (for example `src/data/flights.txt`).
