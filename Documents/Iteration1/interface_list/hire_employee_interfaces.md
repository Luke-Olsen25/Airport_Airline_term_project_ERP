# Interface List: Hire Employee (package `erp.HumanResources`)

## System operations (public interface of `HumanResourcesService`)

| Operation | Returns | Purpose |
| --- | --- | --- |
| handleHiring(scanner : Scanner) | void | Prompts for the new employee's name, role and salary, then hires them and displays the result |
| hireNewEmployee(employee : Employee) | boolean | Validates the salary, checks the employee ID is not already used, and saves the employee |

## Java interfaces

| Interface | Methods | Implemented by |
| --- | --- | --- |
| IEmployeeRepository | initializeStorage() : void; existsByEmployeeID(employeeID : String) : boolean; save(employee : Employee) : boolean | EmployeeFileRepository (`src/data/employee.txt`) |

## Menu entry point

`terminal_ui` calls `HumanResourcesService.handleHiring(scanner : Scanner)`.

## Data files

Plain text, one record per line, fields separated by `|`, in the column order listed below. The first line is a header naming the fields. Assigned flights are separated by `;`.

| File | Owner | Fields |
| --- | --- | --- |
| employee.txt | Human Resources | employeeId, firstName, lastName, role, hireDate, annualSalary, status (ACTIVE, ON_LEAVE or TERMINATED), assignedFlights |
