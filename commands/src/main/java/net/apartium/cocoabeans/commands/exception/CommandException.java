package net.apartium.cocoabeans.commands.exception;

public class CommandException extends RuntimeException {

    protected final String commandName;
    protected final String[] args;
    protected final int depth;

    public CommandException() {
        this(null, null, 0);
    }

    public CommandException(String commandName, String[] args, int depth) {
        this.commandName = commandName;
        this.args = args;
        this.depth = depth;
    }

    public CommandException(String commandName, String[] args, int depth, String message) {
        super(message);
        this.commandName = commandName;
        this.args = args;
        this.depth = depth;
    }

    public CommandException(String commandName, String[] args, int depth, String message, Throwable cause) {
        super(message, cause);
        this.commandName = commandName;
        this.args = args;
        this.depth = depth;
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

}
