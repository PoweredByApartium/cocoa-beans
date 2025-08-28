package net.apartium.cocoabeans.commands.exception;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.AvailableSince("0.0.22")
public class CommandException extends RuntimeException {

    private final BadCommandResponse badCommandResponse;

    public CommandException(BadCommandResponse commandError) {
        this.badCommandResponse = commandError;
    }

    public BadCommandResponse getBadCommandResponse() {
        return badCommandResponse;
    }

    public String getCommandName() {
        return badCommandResponse.getCommandName();
    }

    public String[] getArgs() {
        return badCommandResponse.getArgs();
    }

    public String getMessage() {
        return badCommandResponse.getMessage();
    }

    public int getDepth() {
        return badCommandResponse.getDepth();
    }
}
