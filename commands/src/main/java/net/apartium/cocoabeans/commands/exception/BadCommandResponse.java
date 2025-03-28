package net.apartium.cocoabeans.commands.exception;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.AvailableSince("0.0.22")
public class BadCommandResponse {

    protected final String commandName;
    protected final String[] args;
    protected final int depth;

    protected String message;

    public BadCommandResponse() {
        this(null, null, 0);
    }

    public BadCommandResponse(String commandName, String[] args, int depth) {
        this.commandName = commandName;
        this.args = args;
        this.depth = depth;
    }

    public BadCommandResponse(String commandName, String[] args, int depth, String message) {
        this.commandName = commandName;
        this.args = args;
        this.depth = depth;
        this.message = message;
    }


    public int getDepth() {
        return depth;
    }

    public String getCommandName() {
        return commandName;
    }

    public String[] getArgs() {
        return args;
    }

    public String getMessage() {
        return message;
    }

    public CommandException getError() {
        return new CommandException(this);
    }

    public void throwError() throws CommandException {
        throw getError();
    }
}
