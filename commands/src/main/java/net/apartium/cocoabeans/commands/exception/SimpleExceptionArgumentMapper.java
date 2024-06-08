package net.apartium.cocoabeans.commands.exception;

import net.apartium.cocoabeans.commands.Sender;

import java.util.ArrayList;
import java.util.List;

public class SimpleExceptionArgumentMapper implements ExceptionArgumentMapper {

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

            if (CommandError.class.isAssignableFrom(type) && throwable instanceof CommandException) {
                CommandError commandError = ((CommandException) throwable).getCommandError();
                if (commandError.getClass().isAssignableFrom(type)) {
                    result.add(commandError);
                    continue;
                }

            }

            return null;
        }

        return result;
    }

}
