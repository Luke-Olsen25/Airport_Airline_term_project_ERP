
package erp;

import java.io.*;
import java.util.Scanner;

public class airport_ops {

    private static final String FILE_NAME = "src/data/gates.txt";

    private static final String[] GATES = {
        "A1", "A2", "A3", "B1", "B2", "B3", "C1", "C2"
    };

    public static void showAssignments() {
        System.out.println("\n--- Current Gate Assignments ---");

        File file = new File(FILE_NAME);

        if (!file.exists()) {
            System.out.println("No gate assignments found.");
            return;
        }

        try (Scanner reader = new Scanner(file)) {
            boolean found = false;

            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                String[] data = line.split(",");

                if (data.length == 2) {
                    System.out.println("Flight: " + data[0]
                            + " | Gate: " + data[1]);
                    found = true;
                }
            }

            if (!found) {
                System.out.println("No gate assignments found.");
            }

        } catch (IOException e) {
            System.out.println("Error reading assignments.");
        }
    }

    public static void assignGate(Scanner scanner) {
        System.out.println("\n--- Assign Gate ---");

        showAssignments();

        System.out.print("\nEnter Flight ID: ");
        String flightId = scanner.nextLine().trim().toUpperCase();

        if (flightId.isEmpty()) {
            System.out.println("Flight ID cannot be empty.");
            return;
        }

        System.out.println("\nAirport Gates:");

        for (String availableGate : GATES) {
            System.out.println("Gate " + availableGate);
        }

        System.out.print("\nEnter Gate: ");
        String gate = scanner.nextLine().trim().toUpperCase();

        boolean validGate = false;

        for (String availableGate : GATES) {
            if (availableGate.equals(gate)) {
                validGate = true;
                break;
            }
        }

        if (!validGate) {
            System.out.println("Invalid gate.");
            return;
        }

        File file = new File(FILE_NAME);

        try {
            if (file.exists()) {
                try (Scanner reader = new Scanner(file)) {
                    while (reader.hasNextLine()) {
                        String[] data = reader.nextLine().split(",");

                        if (data.length != 2) {
                            continue;
                        }

                        if (data[0].equalsIgnoreCase(flightId)) {
                            System.out.println(
                                "Flight already has a gate assigned.");
                            return;
                        }

                        if (data[1].equalsIgnoreCase(gate)) {
                            System.out.println(
                                "Gate is already assigned.");
                            return;
                        }
                    }
                }
            }

            File folder = file.getParentFile();

            if (folder != null) {
                folder.mkdirs();
            }

            try (RandomAccessFile writer =
                    new RandomAccessFile(file, "rw")) {

                long length = writer.length();

                if (length > 0) {
                    writer.seek(length - 1);
                    int lastChar = writer.read();

                    writer.seek(length);

                    if (lastChar != '\n' && lastChar != '\r') {
                        writer.writeBytes(System.lineSeparator());
                    }
                }

                writer.writeBytes(flightId + "," + gate
                        + System.lineSeparator());
            }

            System.out.println("\nSuccess! Flight " + flightId
                    + " assigned to Gate " + gate + ".");

        } catch (IOException e) {
            System.out.println("Error saving gate assignment.");
        }
    }
}
