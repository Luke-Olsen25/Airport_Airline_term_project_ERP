package erp.FlightManagement;


public class Gate {
    private String gateId;
    private String airport;
    private String terminal;

    public Gate(String gateId, String airport, String terminal) {
        this.gateId = gateId;
        this.airport = airport;
        this.terminal = terminal;
    }

    public String getGateId() {
        return gateId;
    }

    public String getAirport() {
        return airport;
    }

    public String getTerminal() {
        return terminal;
    }
}