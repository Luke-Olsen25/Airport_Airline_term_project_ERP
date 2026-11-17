package erp.AirportOperations;

import erp.AirportOperations.GateAssignmentService.GateResult;
import erp.airport_ops;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

/**
 * Self-contained tests (no JUnit). Uses temporary files only; never touches src/data.
 * Run: java -cp out erp.AirportOperations.GateOperationsTest
 */
public class GateOperationsTest {
    private static int passed = 0;
    private static int failed = 0;

    private static Path dir;
    private static Path gates;
    private static Path history;

    private static void check(String name, boolean ok) {
        if (ok) {
            passed++;
            System.out.println("PASS  " + name);
        } else {
            failed++;
            System.out.println("FAIL  " + name);
        }
    }

    private static GateAssignmentService freshService(String gatesContent) throws Exception {
        Files.deleteIfExists(gates);
        Files.deleteIfExists(history);
        if (gatesContent != null) {
            Files.write(gates, gatesContent.getBytes(StandardCharsets.UTF_8));
        }
        return new GateAssignmentService(
                new GateAssignmentFileRepository(gates.toString(), history.toString()));
    }

    private static String read(Path p) throws Exception {
        return new String(Files.readAllBytes(p), StandardCharsets.UTF_8).replace("\r\n", "\n");
    }

    private static String runUpdate(GateAssignmentService s, String input) {
        PrintStream original = System.out;
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        System.setOut(new PrintStream(buf));
        try {
            airport_ops.setService(s);
            airport_ops.updateGate(new Scanner(input));
        } finally {
            System.setOut(original);
        }
        return buf.toString();
    }

    private static final String BASE = "AA101,A1\nDL205,A2\nUA310,A3\nEE222,C1\n";

    public static void main(String[] args) throws Exception {
        dir = Files.createTempDirectory("gatetest");
        gates = dir.resolve("gates.txt");
        history = dir.resolve("gate_history.txt");

        // successful update changes only one record
        GateAssignmentService s = freshService(BASE);
        check("update succeeds", s.updateGate("AA101", "C2") == GateResult.OK);
        check("only target record changed",
                read(gates).equals("AA101,C2\nDL205,A2\nUA310,A3\nEE222,C1\n"));
        check("persists on re-read", new GateAssignmentFileRepository(gates.toString(), history.toString())
                .findAll().get(0).getGateId().equals("C2"));
        List<String> h = Files.readAllLines(history);
        check("history recorded", h.size() == 1 && h.get(0).endsWith(",AA101,A1,C2"));
        check("no temp file left", !Files.exists(dir.resolve("gates.txt.tmp")));

        // rejections leave data unchanged
        s = freshService(BASE);
        check("unknown flight", s.updateGate("ZZ999", "C2") == GateResult.FLIGHT_NOT_ASSIGNED);
        check("invalid gate", s.updateGate("AA101", "Z9") == GateResult.INVALID_GATE);
        check("same gate", s.updateGate("AA101", "A1") == GateResult.SAME_GATE);
        check("occupied gate", s.updateGate("AA101", "A2") == GateResult.GATE_OCCUPIED);
        check("empty flight", s.updateGate("", "C2") == GateResult.EMPTY_FLIGHT);
        check("file unchanged after rejections", read(gates).equals(BASE));
        check("no history after rejections", !Files.exists(history));

        // malformed lines preserved; no trailing newline handled
        s = freshService("AA101,A1\ngarbage line\n\nDL205,A2");
        check("malformed ignored in listing", s.getAssignments().size() == 2);
        check("update with malformed lines", s.updateGate("DL205", "B1") == GateResult.OK);
        check("malformed lines preserved",
                read(gates).equals("AA101,A1\ngarbage line\n\nDL205,B1\n"));

        // history failure does not undo the update
        s = freshService(BASE);
        Files.createDirectory(history); // a directory where the log file should be
        check("history failure reported", s.updateGate("AA101", "C2") == GateResult.OK_HISTORY_NOT_SAVED);
        check("history failure keeps update", read(gates).startsWith("AA101,C2\n"));
        Files.delete(history);

        // Iteration 1 assign behavior
        s = freshService("AA101,A1");
        check("assign ok", s.assignGate("AA999", "C1") == GateResult.OK);
        check("assign no glued records", read(gates).equals("AA101,A1\nAA999,C1\n"));
        check("assign duplicate flight", s.assignGate("AA999", "C2") == GateResult.FLIGHT_ALREADY_ASSIGNED);
        check("assign occupied gate", s.assignGate("AA888", "A1") == GateResult.GATE_OCCUPIED);
        check("assign invalid gate", s.assignGate("AA888", "Z9") == GateResult.INVALID_GATE);

        // missing file
        s = freshService(null);
        check("missing file lists empty", s.getAssignments().isEmpty());
        check("missing file update rejected", s.updateGate("AA101", "C2") == GateResult.FLIGHT_NOT_ASSIGNED);
        check("missing file assign creates file",
                s.assignGate("AA1", "A1") == GateResult.OK && Files.exists(gates));

        // availability shown during reassignment
        s = freshService(BASE);
        check("availability", "AA101".equals(s.getGateStatus().get("A1")) && s.getGateStatus().get("C2") == null);

        // console flow: cancel and confirm
        s = freshService(BASE);
        String out = runUpdate(s, "aa101\nc2\nn\n");
        check("cancel prints message", out.contains("canceled"));
        check("cancel leaves file unchanged", read(gates).equals(BASE));
        out = runUpdate(s, "aa101\nc2\ny\n");
        check("confirm succeeds", out.contains("Success"));
        check("confirm updates file", read(gates).startsWith("AA101,C2\n"));

        System.out.println("\n" + passed + " passed, " + failed + " failed");
        if (failed > 0) {
            System.exit(1);
        }
    }
}
