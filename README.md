# Airport_Airline_term_project_ERP

A terminal-based Enterprise Resource Planning (ERP) system for a combined airport/airline business. Each department is a module/domain model of the system. The system is represented in a terminal based menu and data is file I/O only.

## Departments and Use Cases

| Department          | Iteration 1 (CURRENT) | Iteration 2 (Subject to Change) | Iteration 3 (Subject to Change) |
|---------------------|----------------------|-------------|-------------|
| Flight Management   | Schedule Flight      | Update Flight         | Cancel Flight         |
| Ticket Sales        | Book Ticket          | Cancel Booking         | Process Refund         |
| Human Resources     | Hire Employee        | Assign Employee to Flight         | Process Payroll         |
| Aircraft Management | Register Aircraft    | Schedule Maintenance         | Record Maintenance         |
| Airport Operations  | Assign Gate          | Update Gate Assignment         | Track Gate Availability         |

## Project Layout

```
Airport_Airline_term_project_ERP/
├── README.md
├── Documents/
│   ├── Iteration1/
│   ├── Iteration2/                     # Same layout as Iteration1, no inception document
│   └── Iteration3/                     # Same layout as Iteration1, no inception document
├── src/                                # Java source (one package per department, planned)
│   ├── erp/
│   │   ├── Main.java                   # Main menu / entry point
│   │   ├── terminal_ui/                # Shared terminal screen helpers
│   │   ├── flight/                     # Flight Management
│   │   ├── tickets/                    # Ticket Sales
│   │   ├── hr/                         # Human Resources
│   │   ├── aircraft/                   # Aircraft Management
│   │   └── operations/                 # Airport Operations
│   └── data/                           # Flat-file storage (no DBMS)
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

## Conventions

- Use case names start with a verb and must pass the "boss test" (it delivers measurable business value).
- About 85% of project time goes to implementation.
- Each department's data lives in its own file in `src/data/` (for example `src/data/flights.txt`).
