package erp.command;

import java.util.Scanner;

/**
 * Command pattern: a concrete command written as its own class.
 *
 * Its receiver is the CommandInvoker, which owns the "keep running" flag.
 * The other menu commands are method references whose receivers are the
 * department services (AircraftManagementService, airport_ops, and so on).
 */
public class SignOffCommand implements Command {
    private final CommandInvoker receiver;

    public SignOffCommand(CommandInvoker receiver) {
        this.receiver = receiver;
    }

    @Override
    public void execute(Scanner scanner) {
        receiver.stop();
        System.out.println("Signing off...");
    }
}
