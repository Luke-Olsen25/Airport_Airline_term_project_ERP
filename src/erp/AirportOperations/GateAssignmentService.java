package erp.AirportOperations;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Owns the gate rules. It knows the valid gates and (through the repository)
 * the current assignments, so it is the Information Expert for deciding whether
 * an assignment or reassignment is allowed.
 */
public class GateAssignmentService {

    private static final List<String> VALID_GATES = Collections.unmodifiableList(
            Arrays.asList("A1", "A2", "A3", "B1", "B2", "B3", "C1", "C2"));

    /** Outcome of an assign or update request. */
    public enum GateResult {
        OK,
        OK_HISTORY_NOT_SAVED,
        EMPTY_FLIGHT,
        INVALID_GATE,
        FLIGHT_NOT_ASSIGNED,
        FLIGHT_ALREADY_ASSIGNED,
        SAME_GATE,
        GATE_OCCUPIED,
        SAVE_FAILED
    }

    private final IGateAssignmentRepository repository;

    public GateAssignmentService(IGateAssignmentRepository repository) {
        this.repository = repository;
    }

    public List<String> getValidGates() {
        return VALID_GATES;
    }

    public boolean isValidGate(String gateId) {
        return VALID_GATES.contains(gateId);
    }

    public List<GateAssignment> getAssignments() {
        return repository.findAll();
    }

    public Optional<GateAssignment> findByFlight(String flightId) {
        for (GateAssignment a : repository.findAll()) {
            if (a.getFlightId().equalsIgnoreCase(flightId)) {
                return Optional.of(a);
            }
        }
        return Optional.empty();
    }

    /** Every valid gate mapped to the flight occupying it, or null if free. */
    public Map<String, String> getGateStatus() {
        Map<String, String> status = new LinkedHashMap<>();
        for (String gate : VALID_GATES) {
            status.put(gate, null);
        }
        for (GateAssignment a : repository.findAll()) {
            String gate = a.getGateId().toUpperCase();
            if (status.containsKey(gate)) {
                status.put(gate, a.getFlightId());
            }
        }
        return status;
    }

    /** Iteration 1: give an unassigned flight a free gate. */
    public GateResult assignGate(String flightId, String gateId) {
        if (flightId == null || flightId.isEmpty()) {
            return GateResult.EMPTY_FLIGHT;
        }
        if (!isValidGate(gateId)) {
            return GateResult.INVALID_GATE;
        }
        for (GateAssignment a : repository.findAll()) {
            if (a.getFlightId().equalsIgnoreCase(flightId)) {
                return GateResult.FLIGHT_ALREADY_ASSIGNED;
            }
            if (a.getGateId().equalsIgnoreCase(gateId)) {
                return GateResult.GATE_OCCUPIED;
            }
        }
        return repository.append(new GateAssignment(flightId, gateId))
                ? GateResult.OK : GateResult.SAVE_FAILED;
    }

    /** Iteration 2: checks a reassignment without changing anything. Returns OK if allowed. */
    public GateResult validateUpdate(String flightId, String newGate) {
        if (flightId == null || flightId.isEmpty()) {
            return GateResult.EMPTY_FLIGHT;
        }
        Optional<GateAssignment> current = findByFlight(flightId);
        if (!current.isPresent()) {
            return GateResult.FLIGHT_NOT_ASSIGNED;
        }
        if (!isValidGate(newGate)) {
            return GateResult.INVALID_GATE;
        }
        if (current.get().getGateId().equalsIgnoreCase(newGate)) {
            return GateResult.SAME_GATE;
        }
        for (GateAssignment a : repository.findAll()) {
            if (a.getGateId().equalsIgnoreCase(newGate)) {
                return GateResult.GATE_OCCUPIED;
            }
        }
        return GateResult.OK;
    }

    /**
     * Iteration 2: reassigns a flight. Validates again, rewrites gates.txt, then
     * logs the change. A failed history write does not undo the reassignment
     * (result OK_HISTORY_NOT_SAVED).
     */
    public GateResult updateGate(String flightId, String newGate) {
        GateResult check = validateUpdate(flightId, newGate);
        if (check != GateResult.OK) {
            return check;
        }
        String oldGate = findByFlight(flightId).get().getGateId();
        if (!repository.updateGate(flightId, newGate)) {
            return GateResult.SAVE_FAILED;
        }
        return repository.recordChange(flightId, oldGate, newGate)
                ? GateResult.OK : GateResult.OK_HISTORY_NOT_SAVED;
    }
}
