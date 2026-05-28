package net.apartium.cocoabeans.commands.exception;

import net.apartium.cocoabeans.commands.*;
import net.apartium.cocoabeans.commands.requirements.ArgumentRequirement;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

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
                Map.of(CommandException.class, List.of(
                        context -> context.parsedArgs().entrySet()
                                .stream()
                                .filter(entry -> CommandException.class.isAssignableFrom(entry.getKey()))
                                .map(Map.Entry::getValue)
                                .filter(list -> !list.isEmpty())
                                .findFirst()
                                .map(List::getFirst)
                                .orElse(null)
                ))
        );

        assertEquals(1, argumentIndices.size());
        ArgumentIndex<?> argumentIndex = argumentIndices.get(0);

        CommandException commandException = new CommandException(null);

        assertEquals(commandException, argumentIndex.get(new ArgumentContext("test", new String[0], null, Map.of(CommandException.class, List.of(commandException)))));
        assertNull(argumentIndex.get(new ArgumentContext("test", new String[0], null, Map.of())));
    }

    @Test
    void providingCustomIndex() {
        ArgumentMapper mapper = new SimpleArgumentMapper();

        Map<Class<?>, List<ArgumentIndex<?>>> providedArgumentIndexesByType = Map.of(String.class, List.of(context -> "this is cool", context -> "second value"));

        List<ArgumentIndex<?>> argumentIndices = mapper.mapIndices(
                new RegisteredVariant.Parameter[]{
                        new RegisteredVariant.Parameter(String.class, String.class, new ArgumentRequirement[0], null)
                },
                List.of(),
                List.of(),
                providedArgumentIndexesByType
        );

        assertEquals(1, argumentIndices.size());
        ArgumentIndex<?> argumentIndex = argumentIndices.get(0);

        Object obj = argumentIndex.get(new ArgumentContext("test", new String[0], null, Map.of()));
        assertNotNull(obj);

        assertInstanceOf(String.class, obj);
        assertEquals("this is cool", obj);

        argumentIndices = mapper.mapIndices(
                new RegisteredVariant.Parameter[]{
                        new RegisteredVariant.Parameter(String.class, String.class, new ArgumentRequirement[0], null),
                        new RegisteredVariant.Parameter(String.class, String.class, new ArgumentRequirement[0], null)
                },
                List.of(),
                List.of(),
                providedArgumentIndexesByType
        );

        assertEquals(2,  argumentIndices.size());

        argumentIndex = argumentIndices.get(0);
        assertEquals("this is cool", argumentIndex.get(new ArgumentContext("test", new String[0], null, Map.of())));

        argumentIndex = argumentIndices.get(1);
        assertEquals("second value",  argumentIndex.get(new ArgumentContext("test", new String[0], null, Map.of())));
    }

}
