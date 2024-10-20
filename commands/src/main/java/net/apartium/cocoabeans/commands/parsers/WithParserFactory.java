package net.apartium.cocoabeans.commands.parsers;

import net.apartium.cocoabeans.commands.CommandNode;
import net.apartium.cocoabeans.reflect.ConstructorUtils;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @hidden
 */
@ApiStatus.Internal
public class WithParserFactory implements ParserFactory {

    @Override
    public @NotNull List<ParserResult> getArgumentParser(CommandNode commandNode, Annotation annotation, GenericDeclaration obj) {
        if (annotation instanceof WithParsers withParsers) {
            List<ParserResult> result = new ArrayList<>(withParsers.value().length);

            for (WithParser withParser : withParsers.value())
                result.addAll(getArgumentParser(commandNode, withParser, obj));

            return result;
        }

        if (!(annotation instanceof WithParser withParser))
            return List.of();


        try {
            ArgumentParser<?> argumentParser = newInstance(
                    ConstructorUtils.getDeclaredConstructors(withParser.value()),
                    withParser.priority(),
                    withParser.keyword()
            );

            if (argumentParser == null)
                return List.of();

            return List.of(new ParserResult(
                    argumentParser,
                    obj instanceof Method ? Scope.VARIANT : Scope.CLASS
            ));
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            SharedSecrets.LOGGER.log(System.Logger.Level.WARNING, "Failed to create WithParser", e);
            return List.of();
        }
    }

    private static <T extends ArgumentParser<?>> T newInstance(Collection<Constructor<T>> constructors, int priority, String keyword) throws InstantiationException, IllegalAccessException, InvocationTargetException {
        Constructor<T> matchingConstructor = null;

        boolean keywordBlank = keyword.isBlank();
        Class<?>[] paramTypes = null;
        for (Constructor<T> constructor : constructors) {
            int paramCount = constructor.getParameterCount();

            if (paramCount != 1 && paramCount != 2)
                continue;

            paramTypes = constructor.getParameterTypes();

            if (keywordBlank && paramCount == 1 && paramTypes[0].equals(int.class)) {
                matchingConstructor = constructor;
                break;
            }

            if (!keywordBlank && paramCount == 2) {
                if (paramTypes[0].equals(int.class) && paramTypes[1].equals(String.class)) {
                    matchingConstructor = constructor;
                    break;
                }

                if (paramTypes[0].equals(String.class) && paramTypes[1].equals(int.class)) {
                    matchingConstructor = constructor;
                    break;
                }
            }
        }

        if (matchingConstructor == null) {
            if (keywordBlank) {
                SharedSecrets.LOGGER.log(System.Logger.Level.WARNING,
                        "No constructor found for WithParser with priority {}",
                        priority
                );
            } else {
                SharedSecrets.LOGGER.log(System.Logger.Level.WARNING,
                        "No constructor found for WithParser with priority {} and keyword {}",
                        priority, keyword
                );
            }

            return null;
        }

        Object[] params;

        if (!keywordBlank) {
            if (paramTypes[0] == int.class)
                params = new Object[] { priority, keyword };
            else
                params = new Object[] { keyword, priority };
        } else {
            params = new Object[] { priority };
        }

        return matchingConstructor.newInstance(params);
    }

}
