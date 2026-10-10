package net.apartium.cocoabeans.commands.parsers.option;

import net.apartium.cocoabeans.commands.GenericNode;
import net.apartium.cocoabeans.commands.option.FlagArgument;
import net.apartium.cocoabeans.commands.option.NamedArgument;
import net.apartium.cocoabeans.commands.option.OptionArgument;
import net.apartium.cocoabeans.commands.parsers.ArgumentParser;
import net.apartium.cocoabeans.commands.parsers.ParserFactory;
import net.apartium.cocoabeans.commands.parsers.Scope;
import net.apartium.cocoabeans.reflect.ConstructorUtils;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @hidden
 */
@ApiStatus.Internal
public class WithNamedParserFactory implements ParserFactory {

    @Override
    public @NotNull Collection<ParserResult> getArgumentParser(GenericNode node, Annotation annotation, GenericDeclaration obj) {
        if (annotation instanceof WithFlagArguments withFlagArguments) {
            return Arrays.stream(withFlagArguments.value())
                    .map(a -> getArgumentParser(node, a, obj))
                    .flatMap(Collection::stream)
                    .toList();
        }

        if (annotation instanceof WithOptionArguments withOptionArguments) {
            return Arrays.stream(withOptionArguments.value())
                    .map(a -> getArgumentParser(node, a, obj))
                    .flatMap(Collection::stream)
                    .toList();
        }

        if (annotation instanceof WithFlagArgument withFlagArgument) {
            return List.of(new ParserResult(
                    newInstanceParser(
                        withFlagArgument.keyword(),
                        newInstanceFlag(withFlagArgument),
                        withFlagArgument.priority()
                    ),
                    obj instanceof Method ? Scope.VARIANT : Scope.CLASS
            ));
        }

        if (annotation instanceof WithOptionArgument withOptionArgument) {
            return List.of(new ParserResult(
                    newInstanceParser(
                            withOptionArgument.keyword(),
                            newInstanceOption(withOptionArgument),
                            withOptionArgument.priority()
                    ),
                    obj instanceof Method ? Scope.VARIANT : Scope.CLASS
            ));
        }

        return List.of();
    }

    private <T extends ArgumentParser<?>> T newInstanceArgumentParser(Class<T> clazz) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        Set<Constructor<T>> constructors = ConstructorUtils.getDeclaredConstructors(clazz);

        for (Constructor<T> constructor : constructors) {
            T argumentParser = newInstanceArgumentParser(constructor);
            if (argumentParser != null)
                return argumentParser;
        }

        throw new IllegalArgumentException("No suitable constructor found");
    }

    private <T extends ArgumentParser<?>> T newInstanceArgumentParser(Constructor<T> constructor) throws InvocationTargetException, IllegalAccessException, InstantiationException {
        if (constructor.getParameterCount() == 0)
            return constructor.newInstance();

        if (constructor.getParameterCount() == 1) {
            if (constructor.getParameterTypes()[0].equals(int.class))
                return constructor.newInstance(0);

            if (constructor.getParameterTypes()[0].equals(String.class))
                return constructor.newInstance("");
        }

        if (constructor.getParameterCount() == 2) {
            if (constructor.getParameterTypes()[0].equals(int.class) && constructor.getParameterTypes()[1].equals(String.class))
                return constructor.newInstance(0, "");

            if (constructor.getParameterTypes()[0].equals(String.class) && constructor.getParameterTypes()[1].equals(int.class))
                return constructor.newInstance("", 0);
        }

        return null;
    }

    private FlagArgument newInstanceFlag(WithFlagArgument withFlagArgument) {
        return NamedArgument.flag(withFlagArgument.form(), withFlagArgument.aliases());
    }

    private OptionArgument<?> newInstanceOption(WithOptionArgument withOptionArgument) {
        try {
            return NamedArgument.option()
                    .form(withOptionArgument.form())
                    .forms(withOptionArgument.aliases())
                    .usage(withOptionArgument.usage())
                    .parser((ArgumentParser<Object>) newInstanceArgumentParser(withOptionArgument.parser()))
                    .build();
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private NamedArgumentParser newInstanceParser(String keyword, NamedArgument namedArgument, int priority) {
        return new NamedArgumentParser(
                keyword,
                priority,
                List.of(namedArgument)
        );
    }

}
