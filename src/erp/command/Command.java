package erp.command;

import java.util.Scanner;

/**
 * Command pattern: the Command interface.
 *
 * Each menu option is one Command. The invoker (CommandInvoker) only knows
 * this interface. It never knows which department does the work.
 * Because there is a single abstract method, a Command can also be written
 * as a lambda or method reference when registering it.
 */
@FunctionalInterface
public interface Command {
    /** Runs the request. The Scanner is shared so commands can prompt the user. */
    void execute(Scanner scanner);
}
