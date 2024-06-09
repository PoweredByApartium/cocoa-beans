package net.apartium.cocoabeans.commands.spigot.exception;

import net.apartium.cocoabeans.commands.Sender;
import net.apartium.cocoabeans.commands.exception.BadCommandResponse;
import net.apartium.cocoabeans.commands.exception.CommandException;
import net.apartium.cocoabeans.commands.exception.ExceptionArgumentMapper;
import net.apartium.cocoabeans.commands.exception.HandleExceptionVariant;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;

@ApiStatus.AvailableSince("0.0.22")
public class SpigotExceptionArgumentMapper implements ExceptionArgumentMapper {

    @Override
    public List<Object> map(HandleExceptionVariant handleExceptionVariant, Sender sender, String commandName, String[] args, Throwable throwable) {
        Class<?>[] parameters = handleExceptionVariant.parameters();
        if (parameters.length == 0)
            return List.of(handleExceptionVariant.commandNode());

        List<Object> result = new ArrayList<>(parameters.length + 1);
        result.add(handleExceptionVariant.commandNode());

        for (Class<?> type : parameters) {
            if (Sender.class.isAssignableFrom(type)) {
                result.add(sender);
                continue;
            }

            if (CommandSender.class.isAssignableFrom(type)) {
                result.add(sender.getSender());
                continue;
            }

            if (type == String.class) {
                result.add(commandName);
                continue;
            }

            if (type == String[].class) {
                result.add(args);
                continue;
            }

            if (type.isAssignableFrom(throwable.getClass())) {
                result.add(throwable);
                continue;
            }

            if (BadCommandResponse.class.isAssignableFrom(type) && throwable instanceof CommandException) {
                BadCommandResponse commandError = ((CommandException) throwable).getCommandError();
                if (commandError.getClass().isAssignableFrom(type)) {
                    result.add(commandError);
                    continue;
                }

            }

            throw new IllegalArgumentException("Unsupported argument type: " + type.getName());
        }

        return result;
    }

}
