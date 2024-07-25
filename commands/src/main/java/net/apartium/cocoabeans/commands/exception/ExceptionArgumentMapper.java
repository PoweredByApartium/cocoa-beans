package net.apartium.cocoabeans.commands.exception;

import net.apartium.cocoabeans.commands.Sender;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;

@ApiStatus.AvailableSince("0.0.22")
public interface ExceptionArgumentMapper {

    List<Object> map(HandleExceptionVariant handleExceptionVariant, Sender sender, String commandName, String[] args, Throwable throwable);

}
