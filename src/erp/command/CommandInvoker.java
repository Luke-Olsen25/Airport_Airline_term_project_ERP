package erp.command;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Command pattern: the invoker.
 *
 * It keeps the registered commands, prints the menu, reads the user's
 * choice, and calls execute() on the matching Command. It replaces the
 * big switch statement that used to live in terminal_ui.
 *
 * Roles in this application:
 *   Command          - Command (interface), SignOffCommand (concrete class)
 *   Concrete commands - method references / lambdas registered in terminal_ui
 *   Receivers        - the department classes that do the real work
 *   Invoker          - this class
 *   Client           - terminal_ui, which creates the receivers and registers commands
 */
public class CommandInvoker {

    /** One line of the menu: a number, its label, and the command it runs. */
    private static class Entry {
        final String label;
        final Command command;

        Entry(String label, Command command) {
            this.label = label;
            this.command = command;
        }
    }

    // Insertion order is kept so the menu prints in the order commands were registered.
    private final Map<String, Entry> entries = new LinkedHashMap<>();
    // Section headings: the heading is printed just before the given menu number.
    private final Map<String, String> sectionBefore = new LinkedHashMap<>();
    private String pendingSection = null;
    private boolean running = true;

    /** Starts a new menu section. The heading prints before the next registered command. */
    public void startSection(String heading) {
        pendingSection = heading;
    }

    /** Registers a command under a menu number. */
    public void register(String key, String label, Command command) {
        if (entries.containsKey(key)) {
            throw new IllegalArgumentException("Menu number already used: " + key);
        }
        if (pendingSection != null) {
            sectionBefore.put(key, pendingSection);
            pendingSection = null;
        }
        entries.put(key, new Entry(label, command));
    }

    /** Stops the menu loop. Called by SignOffCommand. */
    public void stop() {
        running = false;
    }

    /** Main loop: show menu, read a choice, run the matching command. */
    public void run(Scanner scanner, String signOffKey) {
        while (running) {
            printMenu(signOffKey);
            String choice = scanner.nextLine();
            Entry entry = entries.get(choice);
            if (entry == null) {
                System.out.println("\nInvalid selection. Press Enter to try again.");
                scanner.nextLine();
            } else {
                entry.command.execute(scanner);
            }
        }
    }

    private void printMenu(String signOffKey) {
        System.out.println("MAIN                   ERP Main Menu");
        System.out.println("\nSelect one of the following:");
        for (Map.Entry<String, Entry> e : entries.entrySet()) {
            if (e.getKey().equals(signOffKey)) {
                continue; // printed last, below
            }
            String heading = sectionBefore.get(e.getKey());
            if (heading != null) {
                System.out.println("\n   === " + heading + " ===");
            }
            System.out.println("     " + e.getKey() + ". " + e.getValue().label);
        }
        Entry signOff = entries.get(signOffKey);
        if (signOff != null) {
            System.out.println("\n     " + signOffKey + ". " + signOff.label + "\n");
        }
        System.out.print("Selection or command\n===> ");
    }
}
