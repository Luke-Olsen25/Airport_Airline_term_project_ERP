package erp.AirportOperations;

import java.util.List;

public interface IGateAssignmentRepository {
    /** All well-formed assignments; empty list if the file is missing. */
    List<GateAssignment> findAll();

    /** Adds a new assignment line at the end of gates.txt. */
    boolean append(GateAssignment assignment);

    /**
     * Changes the gate of one flight's record in place. Every other line
     * (including malformed ones) is left untouched. Returns false on failure,
     * in which case the file is unchanged.
     */
    boolean updateGate(String flightId, String newGateId);

    /** Appends "timestamp,flightId,oldGate,newGate" to the gate history log. */
    boolean recordChange(String flightId, String oldGateId, String newGateId);
}
