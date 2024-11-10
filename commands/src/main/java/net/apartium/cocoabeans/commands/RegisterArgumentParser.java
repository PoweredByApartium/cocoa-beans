package net.apartium.cocoabeans.commands;

import net.apartium.cocoabeans.commands.parsers.ArgumentParser;

import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;

public class RegisterArgumentParser<T> extends ArgumentParser<T> {

    private final ArgumentParser<T> parser;
    private final boolean optionalNotMatch;
    private final boolean isOptional;
    private final Optional<String> parameterName;

    public RegisterArgumentParser(ArgumentParser<T> parser, boolean optionalNotMatch, boolean isOptional, Optional<String> parameterName) {
        super(parser.getKeyword(), parser.getArgumentType(), parser.getPriority());
        this.parser = parser;
        this.optionalNotMatch = optionalNotMatch;
        this.isOptional = isOptional;
        this.parameterName = parameterName;
    }

    public ArgumentParser<T> parser() {
        return parser;
    }

    public boolean optionalNotMatch() {
        return optionalNotMatch;
    }

    public boolean isOptional() {
        return isOptional;
    }

    @Override
    public Optional<ParseResult<T>> parse(CommandProcessingContext processingContext) {
        return parser.parse(processingContext);
    }

    @Override
    public OptionalInt tryParse(CommandProcessingContext processingContext) {
        return parser.tryParse(processingContext);
    }

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
