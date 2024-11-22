package net.apartium.cocoabeans.commands.parsers;

import net.apartium.cocoabeans.commands.CommandNode;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

/**
 * A factory associated with specific annotations to create argument parsers based on annotations present in the command class
 * @see WithParserFactory
 * @see SourceParserFactory
 * @see CommandParserFactory
 */
@ApiStatus.AvailableSince("0.0.30")
public interface ParserFactory {

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
        CommandParserFactory commandParserFactory = annotation.annotationType().getAnnotation(CommandParserFactory.class);
        if (commandParserFactory == null)
            return null;

        if (!commandParserFactory.scope().isClass() && onlyClassParser)
            return null;

        try {
            return commandParserFactory.value().getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            return null;
        }
    }

    /**
     * Construct argument parsers for the given annotation
     * @param commandNode The command node that the annotation is present on
     * @param annotation The annotation to construct the argument parser for
     * @param obj The annotated element, either a method or a class
     * @return A collection of argument parsers to be registered to the scope
     */
    @NotNull
    Collection<ParserResult> getArgumentParser(CommandNode commandNode, Annotation annotation, GenericDeclaration obj);

    /**
     * Represents a parser to be registered
     * @param parser parser to register
     * @param scope scope to register the parser to
     */
    record ParserResult(ArgumentParser<?> parser, Scope scope) {

    }

}
