package erp.HumanResources;

import java.util.ArrayList;
import java.util.List;

/**
 * Reads gates through
 * Airport Operations' own repository
 */
public class GateLookup implements IGateLookup {
    private final erp.AirportOperations.IGateAssignmentRepository gates;

    public GateLookup() {
        this(new erp.AirportOperations.GateAssignmentFileRepository());
    }

    public GateLookup(erp.AirportOperations.IGateAssignmentRepository gates) {
        this.gates = gates;
    }

    /** Each gate that appears in gates.txt, listed once, in file order. */
    @Override
    public List<String> getGateIds() {
        List<String> ids = new ArrayList<>();
        for (erp.AirportOperations.GateAssignment assignment : gates.findAll()) {
            String gateId = assignment.getGateId();
            boolean seen = false;
            for (String id : ids) {
                if (id.equalsIgnoreCase(gateId)) {
                    seen = true;
                    break;
                }
            }
            if (!seen) {
                ids.add(gateId);
            }
        }
        return ids;
    }
}
