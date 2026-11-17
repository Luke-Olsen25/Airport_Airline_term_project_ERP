package erp.AirportOperations;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/** File-backed gate assignments stored as "flightId,gateId" lines. */
public class GateAssignmentFileRepository implements IGateAssignmentRepository {
    private static final String DEFAULT_FILE = "src/data/gates.txt";
    private static final String DEFAULT_HISTORY_FILE = "src/data/gate_history.txt";
    private final Path path;
    private final Path historyPath;

    public GateAssignmentFileRepository() {
        this(DEFAULT_FILE, DEFAULT_HISTORY_FILE);
    }

    public GateAssignmentFileRepository(String filePath, String historyFilePath) {
        this.path = Paths.get(filePath);
        this.historyPath = Paths.get(historyFilePath);
    }

    @Override
    public boolean recordChange(String flightId, String oldGateId, String newGateId) {
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String line = time + "," + flightId + "," + oldGateId + "," + newGateId + System.lineSeparator();
        try {
            if (historyPath.getParent() != null) {
                Files.createDirectories(historyPath.getParent());
            }
            Files.write(historyPath, line.getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public List<GateAssignment> findAll() {
        List<GateAssignment> result = new ArrayList<>();
        if (!Files.exists(path)) {
            return result;
        }
        try {
            for (String line : Files.readAllLines(path)) {
                GateAssignment a = GateAssignment.fromText(line);
                if (a != null) {
                    result.add(a);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading assignments.");
        }
        return result;
    }

    @Override
    public boolean append(GateAssignment assignment) {
        try {
            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }
            try (RandomAccessFile writer = new RandomAccessFile(path.toFile(), "rw")) {
                long length = writer.length();
                if (length > 0) {
                    // Iteration 1 fix: never glue a record onto an unterminated last line.
                    writer.seek(length - 1);
                    int lastChar = writer.read();
                    writer.seek(length);
                    if (lastChar != '\n' && lastChar != '\r') {
                        writer.writeBytes(System.lineSeparator());
                    }
                }
                writer.writeBytes(assignment.toText() + System.lineSeparator());
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public boolean updateGate(String flightId, String newGateId) {
        try {
            List<String> lines = Files.readAllLines(path);
            boolean changed = false;
            for (int i = 0; i < lines.size(); i++) {
                GateAssignment a = GateAssignment.fromText(lines.get(i));
                if (a != null && a.getFlightId().equalsIgnoreCase(flightId)) {
                    lines.set(i, new GateAssignment(a.getFlightId(), newGateId).toText());
                    changed = true;
                    break;
                }
            }
            if (!changed) {
                return false;
            }
            // Write a complete temp file first, then swap it in, so a failure
            // can never leave gates.txt half-written.
            Path temp = path.resolveSibling(path.getFileName() + ".tmp");
            StringBuilder sb = new StringBuilder();
            for (String line : lines) {
                sb.append(line).append(System.lineSeparator());
            }
            Files.write(temp, sb.toString().getBytes(StandardCharsets.UTF_8));
            try {
                Files.move(temp, path, StandardCopyOption.ATOMIC_MOVE,
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temp, path, StandardCopyOption.REPLACE_EXISTING);
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
