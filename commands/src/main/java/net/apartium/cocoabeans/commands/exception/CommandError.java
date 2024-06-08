package net.apartium.cocoabeans.commands.exception;

public class CommandError {

    protected final String commandName;
    protected final String[] args;
    protected final int depth;

    protected String message;

    public CommandError() {
        this(null, null, 0);
    }

    public CommandError(String commandName, String[] args, int depth) {
        this.commandName = commandName;
        this.args = args;
        this.depth = depth;
    }

    public CommandError(String commandName, String[] args, int depth, String message) {
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

    public Throwable getError() {
        return new CommandException(this);
    }

    public void throwError() throws Throwable {
        throw getError();
    }
}
