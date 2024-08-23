package net.apartium.cocoabeans.commands.parsers;

import net.apartium.cocoabeans.commands.CommandNode;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.GenericDeclaration;
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
