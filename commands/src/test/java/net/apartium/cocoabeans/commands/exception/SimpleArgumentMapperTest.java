package net.apartium.cocoabeans.commands.exception;

import net.apartium.cocoabeans.commands.*;
import net.apartium.cocoabeans.commands.requirements.ArgumentRequirement;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class SimpleArgumentMapperTest {

    @Test
    void missingCommandExceptionAndWith() {
        ArgumentMapper mapper = new SimpleArgumentMapper();

        List<ArgumentIndex<?>> argumentIndices = mapper.mapIndices(
                new RegisteredVariant.Parameter[]{
                        new RegisteredVariant.Parameter(CommandException.class, CommandException.class, new ArgumentRequirement[0], null)
                },
                List.of(),
                List.of(),
                List.of(CommandException.class)
        );

        assertEquals(1, argumentIndices.size());
        ArgumentIndex<?> argumentIndex = argumentIndices.get(0);

        CommandException commandException = new CommandException(null);

        assertEquals(commandException, argumentIndex.get(new ArgumentContext("test", new String[0], null, Map.of(CommandException.class, List.of(commandException)))));
        assertNull(argumentIndex.get(new ArgumentContext("test", new String[0], null, Map.of())));
    }

}
