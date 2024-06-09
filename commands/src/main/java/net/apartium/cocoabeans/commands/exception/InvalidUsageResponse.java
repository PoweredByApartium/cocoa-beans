package net.apartium.cocoabeans.commands.exception;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.AvailableSince("0.0.22")
public class InvalidUsageResponse extends BadCommandResponse {

    public InvalidUsageResponse() {

    }

    public InvalidUsageResponse(String commandName, String[] args, int depth) {
        super(commandName, args, depth);
    }

    public InvalidUsageResponse(String commandName, String[] args, int depth, String message) {
        super(commandName, args, depth, message);
    }

    @Override
    public Exception getError() {
        return new InvalidUsageException();
    }

    public class InvalidUsageException extends CommandException {

        public InvalidUsageException() {
            super(InvalidUsageResponse.this);
        }
    }
}
