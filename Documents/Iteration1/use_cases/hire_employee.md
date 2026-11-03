# Use Case: Hire Employee

| Field | Value |
| --- | --- |
| Use case | Hire Employee |
| Department | Human Resources |
| Primary actor | HR Specialist |
| Stakeholders | New employee: wants to be on record with the correct role and salary. Airline: wants every employee recorded once, with a unique ID, so they can be assigned and paid. Flight Management, Aircraft Management and Airport Operations: need active employees in the right roles to staff flights, maintenance and gates. Finance: needs each employee's salary for payroll. |
| Preconditions | HR Worker is at the ERP main menu. The new employee has accepted an offer for one of the airline's roles (Pilot, Co-Pilot, Flight Attendant, Baggage Handler, Maintenance Technician, Gate Agent). |
| Postconditions | An Employee is created with a unique employee ID, today's hire date, status ACTIVE and no assigned flights, and is saved to `src/data/employee.txt`. |

## Main success scenario

1. HR worker selects "Hire new employee" from the main menu.
2. HR worker enters the employee's first name.
3. HR worker enters the employee's last name.
4. System displays the available roles.
5. HR Worker enters the employee's role.
6. HR worker enters the employee's annual salary.
7. System validates the role and salary.
8. System generates a unique employee ID and records today's date as the hire date.
9. System saves the employee with status ACTIVE and no assigned flights.
10. System displays a confirmation with the employee's name and new employee ID.

## Alternate flows

- **7a. Role is not one of the available roles:** System reports the role is unknown and cancels the hire. Nothing is saved. HR Worker starts over from step 1.
- **7b. Annual salary is negative:** System reports that the salary cannot be negative and cancels the hire. Nothing is saved. HR Specialist starts over from step 1.
- **8a. Generated employee ID is already in use:** System generates a new ID until it finds one not used by any existing employee.
- **9a. Employee ID is found to already exist when saving:** System reports that the hire failed. Nothing is saved. HR Worker starts over from step 1.
- **9b. Employee file cannot be written:** System reports a storage error. Nothing is saved.
