package net.apartium.cocoabeans.commands.exception;

public class UnknownCommandException extends CommandException {

    private final String commandName;

    public UnknownCommandException(String commandName) {
        this.commandName = commandName;
    }

    public String getCommandName() {
        return commandName;
    }

}
