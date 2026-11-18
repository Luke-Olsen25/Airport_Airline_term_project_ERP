# Airport_Airline_term_project_ERP

A terminal-based Enterprise Resource Planning (ERP) system for a combined airport/airline business. Each department is a module/domain model of the system. The system is represented in a terminal based menu and data is file I/O only.

## Departments and Use Cases

| Department          | Iteration 1 (COMPLETE) | Iteration 2 (CURRENT) | Iteration 3 (Subject to Change) |
|---------------------|----------------------|-------------|-------------|
| Flight Management   | Schedule Flight      | Update Flight         | Cancel Flight         |
| Ticket Sales        | Purchase Ticket          | Cancel Booking         | Process Refund         |
| Human Resources     | Hire Employee        | Assign Employee to Flight or Gate         | Process Payroll         |
| Aircraft Management | Register Aircraft    | Schedule Maintenance         | Record Maintenance         |
| Airport Operations  | Assign Gate          | Update Gate Assignment         | Track Gate Availability         |

## Features (Main Menu)

The menu is grouped by department. Iteration 1 features are marked (I1) and Iteration 2 features are marked (I2).

| # | Option | Department |
|---|--------|------------|
| 1 | Register New Aircraft (I1) | Aircraft Management |
| 2 | View Registered Aircrafts (I1) | Aircraft Management |
| 3 | Delete Registered Aircraft | Aircraft Management |
| 4 | Schedule Maintenance (I2) | Aircraft Management |
| 5 | Complete Maintenance (I2) | Aircraft Management |
| 6 | Schedule Flight (I1) | Flight Management |
| 7 | View Scheduled Flights (I1) | Flight Management |
| 8 | Update Flight (I2) | Flight Management |
| 9 | Purchase Ticket (I1) | Ticket Sales |
| 10 | Cancel Booking (I2) | Ticket Sales |
| 11 | Assign Gate (I1) | Airport Operations |
| 12 | View Gate Assignments (I1) | Airport Operations |
| 13 | Update Gate Assignment (I2) | Airport Operations |
| 14 | Hire New Employee (I1) | Human Resources |
| 15 | View Current Employees (I1) | Human Resources |
| 16 | Assign Employee to Flight (I2) | Human Resources |
| 17 | Assign Employee to Gate (I2) | Human Resources |
| 90 | Sign Off | |

## Command Pattern

The menu uses the Command design pattern (package `erp.command`). Each menu option is a `Command`.
`terminal_ui` registers a command for each option with the `CommandInvoker`, which prints the menu,
reads the choice, and calls `execute()` on the matching command. The department classes (for example
`AircraftManagementService` or `airport_ops`) are the receivers that do the real work. To add a
menu option, register one more command in `terminal_ui.run()`.

## Project Layout

```
Airport_Airline_term_project_ERP/
├── README.md
├── Documents/                          # Diagrams, use cases, interface lists per iteration
└── src/
    ├── erp/                            # Java source, one package per department
    │   ├── main.java                   # Entry point
    │   ├── terminal_ui.java            # Creates the departments and registers the menu commands
    │   ├── airport_ops.java            # Airport Operations menu handlers (assign, view, update gate)
    │   ├── flight_mngt.java            # Empty stub (Flight Management lives in FlightManagement/)
    │   ├── ticket_sales.java           # Empty stub (Ticket Sales lives in TicketSales/)
    │   ├── command/                    # Command pattern
    │   │   ├── Command.java            # Command interface
    │   │   ├── CommandInvoker.java     # Invoker: menu, input, dispatch
    │   │   └── SignOffCommand.java     # Concrete command for Sign Off
    │   ├── AircraftManagement/         # Register, view, delete aircraft; schedule and complete maintenance
    │   │   ├── Aircraft, AircraftStatus
    │   │   ├── IAircraftRepository, AircraftFileRepository
    │   │   ├── AircraftManagementService
    │   │   ├── MaintenanceRecord, MaintenanceType, MaintenanceStatus
    │   │   ├── IMaintenanceRepository, MaintenanceFileRepository
    │   │   ├── MaintenanceService
    │   │   └── IFlightScheduleReader, FlightScheduleFileReader
    │   ├── AirportOperations/          # Assign gate, view gates, update gate assignment
    │   │   ├── GateAssignment
    │   │   ├── IGateAssignmentRepository, GateAssignmentFileRepository
    │   │   ├── GateAssignmentService
    │   │   └── GateOperationsTest      # Standalone test program (has its own main)
    │   ├── FlightManagement/           # Schedule, view, update flights
    │   │   ├── Flight, Gate
    │   │   ├── FlightFileRepository
    │   │   └── FlightManagementService
    │   ├── HumanResources/             # Hire, view employees; assign employees to flights and gates
    │   │   ├── Employee, EmployeeRole, EmployeeStatus
    │   │   ├── IEmployeeRepository, EmployeeFileRepository
    │   │   ├── HumanResourcesService
    │   │   ├── IFlightScheduleLookup, FlightScheduleLookup
    │   │   └── IGateLookup, GateLookup
    │   └── TicketSales/                # Purchase ticket, cancel booking
    │       ├── Flight, FlightCatalog, Seat, SeatClass
    │       ├── Passenger, Payment, PaymentMethod
    │       ├── Ticket, TicketSale, TicketStatus
    │       ├── IFlightRepository, FlightFileRepository
    │       ├── ITicketRepository, TicketFileRepository
    │       └── TicketSalesService, TicketSalesTerminal
    └── data/                           # Flat-file storage (no DBMS)
        ├── aircrafts.txt               # Aircraft Management
        ├── maintenance_logs.txt        # Aircraft Management (maintenance records)
        ├── scheduled_flights.txt       # Flight Management
        ├── flights.txt                 # Ticket Sales (flights available to sell)
        ├── seats.txt                   # Ticket Sales (seats per flight)
        ├── tickets.txt                 # Ticket Sales
        ├── gates.txt                   # Airport Operations (current assignments)
        ├── gate_history.txt            # Airport Operations (log of gate changes)
        └── employee.txt                # Human Resources
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

Run from the repository root (data paths are relative to it). Requires Java 8 or newer.

Windows (PowerShell):

```
javac -d out (Get-ChildItem src -Recurse -Filter *.java).FullName
java -cp out erp.main
```

Mac, Linux, or Git Bash:

```
javac -d out $(find src -name "*.java")
java -cp out erp.main
```

If you pulled new code, delete the old `out` folder and compile again so the menu matches the source.

## Conventions

- Use case names start with a verb and must pass the "boss test" (it delivers measurable business value).
- About 85% of project time goes to implementation.
- Each department's data lives in its own file in `src/data/` (for example `src/data/flights.txt`).
