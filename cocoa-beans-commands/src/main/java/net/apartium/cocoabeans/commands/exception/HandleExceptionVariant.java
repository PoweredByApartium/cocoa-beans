package net.apartium.cocoabeans.commands.exception;

import net.apartium.cocoabeans.commands.ArgumentIndex;
import net.apartium.cocoabeans.commands.GenericNode;
import org.jetbrains.annotations.ApiStatus;

import java.lang.invoke.MethodHandle;
import java.util.List;

@ApiStatus.AvailableSince("0.0.22")
public record HandleExceptionVariant(
        Class<? extends Throwable> exceptionType,
        MethodHandle method,
        Class<?>[] parameters,
        GenericNode node,
        List<ArgumentIndex<?>> argumentIndexList,
        int priority
) {

}
