package net.apartium.cocoabeans.commands.exception;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.AvailableSince("0.0.22")
public class UnknownCommandResponse extends BadCommandResponse {

    private final String commandName;

    public UnknownCommandResponse(String commandName) {
        this.commandName = commandName;
    }

    public String getCommandName() {
        return commandName;
    }

    @Override
    public Exception getError() {
        return new UnknownCommandException();
    }

    public class UnknownCommandException extends CommandException {

        public UnknownCommandException() {
            super(UnknownCommandResponse.this);
        }

    }
}
