package net.apartium.cocoabeans.commands.exception;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.AvailableSince("0.0.22")
public class UnknownCommandError extends CommandError {

    private final String commandName;

    public UnknownCommandError(String commandName) {
        this.commandName = commandName;
    }

    public String getCommandName() {
        return commandName;
    }

    @Override
    public Throwable getError() {
        return new UnknownCommandException();
    }

    public class UnknownCommandException extends CommandException {

        public UnknownCommandException() {
            super(UnknownCommandError.this);
        }

    }
}
