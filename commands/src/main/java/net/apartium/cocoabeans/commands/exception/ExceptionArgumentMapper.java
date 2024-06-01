package net.apartium.cocoabeans.commands.exception;

import net.apartium.cocoabeans.commands.Sender;

import java.util.List;

public interface ExceptionArgumentMapper {

    List<Object> map(HandleExceptionVariant handleExceptionVariant, Sender sender, String commandName, String[] args, Throwable throwable);

}
