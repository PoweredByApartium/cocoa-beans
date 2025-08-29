package net.apartium.cocoabeans.commands.parsers;

import net.apartium.cocoabeans.CollectionHelpers;
import net.apartium.cocoabeans.commands.GenericNode;
import net.apartium.cocoabeans.reflect.ClassUtils;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * A factory associated with specific annotations to create argument parsers based on annotations present in the command class
 * @see WithParserFactory
 * @see SourceParserFactory
 * @see CommandParserFactory
 */
@ApiStatus.AvailableSince("0.0.30")
public interface ParserFactory {

    /**
     * Get argument parsers from a class
     * @param node command node
     * @param clazz target class
     * @param parserFactories parser factories for caching
     * @return argument parsers
     */
    @ApiStatus.AvailableSince("0.0.37")
    static Map<String, ArgumentParser<?>> findClassParsers(GenericNode node, Class<?> clazz, Map<Class<? extends ParserFactory>, ParserFactory> parserFactories) {
        Map<String, ArgumentParser<?>> result = new HashMap<>();

        for (Class<?> c : ClassUtils.getSuperClassAndInterfaces(clazz)) {
            CollectionHelpers.mergeInto(
                    result,
                    getArgumentParsers(node, c.getAnnotations(), c, true, parserFactories)
            );

            for (Method method : c.getMethods()) {
                try {
                    result.putAll(ParserFactory.getArgumentParsers(node, method.getAnnotations(), clazz.getMethod(method.getName(), method.getParameterTypes()), true, parserFactories));
                } catch (NoSuchMethodException ignored) {
                    // ignored
                }
            }
        }

        return result;
    }

    /**
     * Get argument parsers from an array of annotations
     * @param node command node
     * @param annotations annotations
     * @param obj the class / method
     * @param limitToClassParsers limit to class parsers only
     * @param parserFactories parser factories for caching
     * @return argument parsers
     */
    @ApiStatus.AvailableSince("0.0.37")
    static Map<String, ArgumentParser<?>> getArgumentParsers(GenericNode node, Annotation[] annotations, GenericDeclaration obj, boolean limitToClassParsers, Map<Class<? extends ParserFactory>, ParserFactory> parserFactories) {
        Map<String, ArgumentParser<?>> result = new HashMap<>();

        for (Annotation annotation : annotations)
            result.putAll(getArgumentParsers(node, annotation, obj, limitToClassParsers, parserFactories));

        return result;
    }

    /**
     * Get argument parsers from an annotation
     * @param node command node
     * @param annotation annotation
     * @param obj the class / method
     * @param limitToClassParsers limit to class parsers only
     * @param parserFactories parser factories for caching
     * @return argument parsers
     */
    @ApiStatus.AvailableSince("0.0.37")
    static Map<String, ArgumentParser<?>> getArgumentParsers(GenericNode node, Annotation annotation, GenericDeclaration obj, boolean limitToClassParsers, Map<Class<? extends ParserFactory>, ParserFactory> parserFactories) {
        Class<? extends ParserFactory> parserFactoryClass = ParserFactory.getParserFactoryClass(annotation);

        if (parserFactoryClass == null)
            return Map.of();

        ParserFactory parserFactory = parserFactories.computeIfAbsent(
                parserFactoryClass,
                clazz -> ParserFactory.createFromAnnotation(annotation, limitToClassParsers)
        );

        if (parserFactory == null)
            return Map.of();

        Collection<ParserFactory.ParserResult> parserResults = parserFactory.getArgumentParser(node, annotation, obj);

        if (parserResults.isEmpty())
            return Map.of();

        Map<String, ArgumentParser<?>> result = new HashMap<>();

        for (ParserFactory.ParserResult parseResult : parserResults) {
            if (!parseResult.scope().isClass() && limitToClassParsers)
                continue;

            result.put(parseResult.parser().getKeyword(), parseResult.parser());
        }

        return result;
    }

    /**
     * Get the class of the parser factory from an annotation
     * @param annotation The annotation that has a CommandParserFactory
     * @return The class of the parser factory
     */
    @ApiStatus.AvailableSince("0.0.37")
    static Class<? extends ParserFactory> getParserFactoryClass(Annotation annotation) {
        CommandParserFactory commandParserFactory = annotation.annotationType().getAnnotation(CommandParserFactory.class);
        return commandParserFactory == null ? null : commandParserFactory.value();
    }

    /**
     * Create a parser factory from an annotation
     * @param annotation The annotation that has a CommandParserFactory
     * @param onlyClassParser Whether to only create parsers for classes
     * @return The created parser factory
     */
    @ApiStatus.AvailableSince("0.0.37")
    static ParserFactory createFromAnnotation(Annotation annotation, boolean onlyClassParser) {
        if (annotation == null)
            return null;

        CommandParserFactory commandParserFactory = annotation.annotationType().getAnnotation(CommandParserFactory.class);
        if (commandParserFactory == null)
            return null;

        if (!commandParserFactory.scope().isClass() && onlyClassParser)
            return null;

        try {
            return commandParserFactory.value().getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException("Failed to instantiate parser factory: " + commandParserFactory.value(), e);
        }
    }

    /**
     * Construct argument parsers for the given annotation
     * @param node The node of the command / compound parser
     * @param annotation The annotation to construct the argument parser for
     * @param obj The annotated element, either a method or a class
     * @return A collection of argument parsers to be registered to the scope
     */
    @NotNull
    Collection<ParserResult> getArgumentParser(GenericNode node, Annotation annotation, GenericDeclaration obj);

    /**
     * Represents a parser to be registered
     * @param parser parser to register
     * @param scope scope to register the parser to
     */
    record ParserResult(ArgumentParser<?> parser, Scope scope) {

    }

}
