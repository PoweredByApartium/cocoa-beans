package net.apartium.cocoabeans.commands;

import net.apartium.cocoabeans.commands.parsers.ArgumentParser;
import org.jetbrains.annotations.ApiStatus;

import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;

/**
 * A register argument parser is a wrapper for an argument parser
 * Used for registering argument parsers
 * because we need extra information for register argument parser
 * @see ArgumentParser
 *
 * @param <T> result type of the parser
 */
@ApiStatus.AvailableSince("0.0.37")
public class RegisterArgumentParser<T> extends ArgumentParser<T> {

    private final ArgumentParser<T> parser;
    private final boolean optionalNotMatch;
    private final boolean isOptional;
    private final Optional<String> parameterName;

    /**
     * A constructor for a register argument parser
     * @param parser argument parser
     * @param optionalNotMatch return optional if not match the parser
     * @param isOptional could we don't use the parser and return optional
     * @param parameterName parameter name will be used for better parameter control
     */
    public RegisterArgumentParser(ArgumentParser<T> parser, boolean optionalNotMatch, boolean isOptional, Optional<String> parameterName) {
        super(parser.getKeyword(), parser.getArgumentType(), parser.getPriority());
        this.parser = parser;
        this.optionalNotMatch = optionalNotMatch;
        this.isOptional = isOptional;
        this.parameterName = parameterName;
    }

    /**
     * Get the argument parser that this register argument parser wraps
     * @return the argument parser
     */
    public ArgumentParser<T> parser() {
        return parser;
    }

    /**
     * Get if the argument parser accept optional but does not match
     * @return if the argument parser accept optional but does not match
     */
    public boolean optionalNotMatch() {
        return optionalNotMatch;
    }

    /**
     * Get if the argument parser accept no argument
     * @return if the argument parser accept no argument
     */
    public boolean isOptional() {
        return isOptional;
    }

    /**
     * Get the parameter name
     * @return the parameter name
     */
    public Optional<String> getParameterName() {
        return parameterName;
    }

    /**
     * Tries to parse next argument in the context
     * @param processingContext cmd processing context
     * @return empty if failed, otherwise result
     */
    @Override
    public Optional<ParseResult<T>> parse(CommandProcessingContext processingContext) {
        return parser.parse(processingContext);
    }

    /**
     * Tries to lazily parse next argument in the context
     * @param processingContext cmd processing context
     * @return new index int if success, empty if not
     */
    @Override
    public OptionalInt tryParse(CommandProcessingContext processingContext) {
        return parser.tryParse(processingContext);
    }

    /**
     * Retrieves available options for tab completion of this argument
     * @param processingContext cmd processing context
     * @return tab completion result if success, otherwise empty option
     */
    @Override
    public Optional<TabCompletionResult> tabCompletion(CommandProcessingContext processingContext) {
        return parser.tabCompletion(processingContext);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null)
            return false;

        if (getClass() != o.getClass())
            return parser.equals(o);

        RegisterArgumentParser<?> other = (RegisterArgumentParser<?>) o;

        return this.isOptional == other.isOptional
                && this.optionalNotMatch == other.optionalNotMatch
                && this.parameterName.equals(other.parameterName)
                && this.parser.equals(other.parser);
    }

    @Override
    public int hashCode() {
        return Objects.hash(parser, optionalNotMatch, isOptional, parameterName);
    }

}
