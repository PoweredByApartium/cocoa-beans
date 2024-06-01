package net.apartium.cocoabeans.commands.exception;

public class InvalidUsageException extends CommandException {

    public InvalidUsageException() {

    }

    public InvalidUsageException(String commandName, String[] args, int depth) {
        super(commandName, args, depth);
    }

    public InvalidUsageException(String commandName, String[] args, int depth, String message) {
        super(commandName, args, depth, message);
    }
}
