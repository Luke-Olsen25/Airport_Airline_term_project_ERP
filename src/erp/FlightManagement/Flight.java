package erp.FlightManagement;


import java.time.LocalDateTime;

// Represents a flight and its attributes

public class Flight {
    private String flightId;
    private String flightNumber;
    private String airline;
    private String origin;
    private String destination;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private String aircraftTailNumber;
    private Gate gate;
    private String status;
    
    public Flight(
        String flightId,
        String flightNumber,
        String airline,
        String origin,
        String destination,
        LocalDateTime departureTime,
        LocalDateTime arrivalTime,
        String aircraftTailNumber,
        Gate gate
    ) {
         this(flightId, flightNumber, airline, origin, destination,
            departureTime, arrivalTime, aircraftTailNumber, gate, "Scheduled");
    }
    
    
    public Flight(String flightId, String flightNumber, String airline,
        String origin, String destination, LocalDateTime departureTime,
        LocalDateTime arrivalTime, String aircraftTailNumber, Gate gate,
        String status) {

        this.flightId = flightId;
        this.flightNumber = flightNumber;
        this.airline = airline;
        this.origin = origin;
        this.destination = destination;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.aircraftTailNumber = aircraftTailNumber;
        this.gate = gate;
        this.status = status;
    }
    
    public String getFlightId() {
        return flightId;
    }
    
    public String getFlightNumber() {
        return flightNumber;
    }
    
    public String getAirline() {
        return airline;
    }
    
    public String getOrigin() {
        return origin;
    }
    
    public String getDestination() {
        return destination;
    }
    
    public LocalDateTime getDepartureTime() {
        return departureTime;
    }
    
    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }
    
    public String getAircraftTailNumber() {
        return aircraftTailNumber;
    }
    
    public Gate getGate() {
        return gate;
    }
    
    public String getStatus() {
        return status;
    }

}