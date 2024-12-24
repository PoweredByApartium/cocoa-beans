package net.apartium.cocoabeans.commands.spigot.exception;

import net.apartium.cocoabeans.commands.CommandContext;
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
    public List<Object> map(HandleExceptionVariant handleExceptionVariant, CommandContext context, Sender sender, String commandName, String[] args, Throwable throwable) {
        Class<?>[] parameters = handleExceptionVariant.parameters();
        if (parameters.length == 0)
            return List.of(handleExceptionVariant.node());

        List<Object> result = new ArrayList<>(parameters.length + 1);
        result.add(handleExceptionVariant.node());

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

            if (type == CommandContext.class) {
                result.add(context);
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
                BadCommandResponse commandError = ((CommandException) throwable).getBadCommandResponse();
                if (type.isAssignableFrom(commandError.getClass())) {
                    result.add(commandError);
                    continue;
                }

            }

            throw new IllegalArgumentException("Unsupported argument type: " + type.getName());
        }

        return result;
    }

}
