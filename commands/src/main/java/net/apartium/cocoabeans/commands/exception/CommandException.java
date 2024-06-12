package net.apartium.cocoabeans.commands.exception;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.AvailableSince("0.0.22")
public class CommandException extends RuntimeException {

    private final BadCommandResponse commandError;

    public CommandException(BadCommandResponse commandError) {
        this.commandError = commandError;
    }

    public BadCommandResponse getCommandError() {
        return commandError;
    }

    public String getCommandName() {
        return commandError.getCommandName();
    }

    public String[] getArgs() {
        return commandError.getArgs();
    }

    public String getMessage() {
        return commandError.getMessage();
    }

    public int getDepth() {
        return commandError.getDepth();
    }
}
