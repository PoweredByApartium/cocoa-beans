package net.apartium.cocoabeans.commands.parsers;

import net.apartium.cocoabeans.commands.CommandNode;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class WithParserFactory implements ParserFactory {

    @Override
    public @Nullable ParserResult getArgumentParser(CommandNode commandNode, Annotation annotation, GenericDeclaration obj) {
        if (!(annotation instanceof WithParser withParser))
            return null;

        try {
            Constructor<? extends ArgumentParser<?>>[] ctors = (Constructor<? extends ArgumentParser<?>>[]) withParser.value().getDeclaredConstructors();
            return new ParserResult(newInstance((Constructor<ArgumentParser<?>>[]) ctors, withParser.priority()), obj instanceof Method ? Scope.VARIANT : Scope.CLASS);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            return null;
        }
    }

    private static <T> T newInstance(Constructor<T>[] ctors, int priority) throws InstantiationException, IllegalAccessException, InvocationTargetException {
        Constructor<?> constructor = null;

        if (ctors.length > 1) {
            for (Constructor<T> ctor : ctors) {
                if (ctor.getParameterCount() == 1 && ctor.getParameterTypes()[0].equals(int.class))
                    constructor = ctor;
            }
        }

        if (constructor == null)
            constructor = ctors[0];

        Object[] params;
        if (constructor.getParameterCount() == 1) {
            params = new Object[] {priority};
        } else {
            if (priority > 0)
                SharedSecrets.LOGGER.log(System.Logger.Level.WARNING, "Registered parser {} with priority, but it doesn't support it", constructor.getDeclaringClass().getName());

            params = new Object[0];
        }
        return (T) constructor.newInstance(params);
    }
}
