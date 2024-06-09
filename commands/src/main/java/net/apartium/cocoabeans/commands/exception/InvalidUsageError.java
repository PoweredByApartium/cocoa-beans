package net.apartium.cocoabeans.commands.exception;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.AvailableSince("0.0.22")
public class InvalidUsageError extends CommandError {

    public InvalidUsageError() {

    }

    public InvalidUsageError(String commandName, String[] args, int depth) {
        super(commandName, args, depth);
    }

    public InvalidUsageError(String commandName, String[] args, int depth, String message) {
        super(commandName, args, depth, message);
    }

    @Override
    public Throwable getError() {
        return new InvalidUsageException();
    }

    public class InvalidUsageException extends CommandException {

        public InvalidUsageException() {
            super(InvalidUsageError.this);
        }
    }
}
