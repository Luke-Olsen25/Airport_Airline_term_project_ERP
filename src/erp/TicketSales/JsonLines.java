package erp.TicketSales;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads the team's JSON data files, which hold one object per line (like aircrafts.json),
 * without a full JSON parser.
 */
public final class JsonLines {
    private JsonLines() { }

    /** Returns each line of the file that holds a JSON object ("{...}"). */
    public static List<String> readObjects(String file) {
        List<String> result = new ArrayList<String>();
        try {
            if (!new File(file).exists()) return result;
            for (String line : Files.readAllLines(Paths.get(file))) {
                if (line.trim().startsWith("{")) result.add(line.trim());
            }
        } catch (IOException e) {
            System.out.println("Could not read " + file + ": " + e.getMessage());
        }
        return result;
    }

    /** Pulls one value out of a one-line JSON object, e.g. getField(line, "seatNumber") returns "3A". */
    public static String getField(String line, String key) {
        String marker = "\"" + key + "\": ";
        int start = line.indexOf(marker);
        if (start < 0) return null;
        start += marker.length();
        if (line.charAt(start) == '"') {
            return line.substring(start + 1, line.indexOf('"', start + 1));
        }
        int end = start;
        while (end < line.length() && line.charAt(end) != ',' && line.charAt(end) != '}') end++;
        return line.substring(start, end).trim();
    }
}
