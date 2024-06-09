package net.apartium.cocoabeans.commands.exception;

import net.apartium.cocoabeans.commands.CommandNode;
import org.jetbrains.annotations.ApiStatus;

import java.lang.invoke.MethodHandle;

@ApiStatus.AvailableSince("0.0.22")
public record HandleExceptionVariant(
        MethodHandle method,
        Class<?>[] parameters,
        CommandNode commandNode,
        int priority
) {

}
