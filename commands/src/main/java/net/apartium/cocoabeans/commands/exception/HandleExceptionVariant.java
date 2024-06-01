package net.apartium.cocoabeans.commands.exception;

import net.apartium.cocoabeans.commands.CommandNode;

import java.lang.invoke.MethodHandle;

public record HandleExceptionVariant(
        MethodHandle method,
        Class<?>[] parameters,
        CommandNode commandNode,
        int priority
) {

}
