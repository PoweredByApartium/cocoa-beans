package net.apartium.cocoabeans.commands;

import org.jetbrains.annotations.ApiStatus;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Argument context is general context for all arguments
 * Used for all commands and other system like Flag system & compound parser
 * @param commandName command name
 * @param args args
 * @param sender sender
 * @param parsedArgs parsed args
 */
@ApiStatus.AvailableSince("0.0.36")
public record ArgumentContext(
    String commandName,
    String[] args,
    Sender sender,
    Map<Class<?>, List<Object>> parsedArgs
) {

    @Override
    public String toString() {
        return "{" +
                "commandName='" + commandName + '\'' +
                ", args=" + Arrays.toString(args) +
                ", sender=" + sender +
                ", parsedArgs=" + parsedArgs +
        '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArgumentContext that = (ArgumentContext) o;
        return Objects.deepEquals(args, that.args) && Objects.equals(sender, that.sender) && Objects.equals(commandName, that.commandName) && Objects.equals(parsedArgs, that.parsedArgs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(commandName, Arrays.hashCode(args), sender, parsedArgs);
    }

}
