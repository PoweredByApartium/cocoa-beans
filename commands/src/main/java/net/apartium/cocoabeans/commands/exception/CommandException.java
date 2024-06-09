package net.apartium.cocoabeans.commands.exception;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.AvailableSince("0.0.22")
public class CommandException extends RuntimeException {

    private final CommandError commandError;

    public CommandException(CommandError commandError) {
        this.commandError = commandError;
    }

    public CommandError getCommandError() {
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
