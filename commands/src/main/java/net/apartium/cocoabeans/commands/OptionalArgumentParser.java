package net.apartium.cocoabeans.commands;

import net.apartium.cocoabeans.commands.parsers.ArgumentParser;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;

/* package-private */ class OptionalArgumentParser<T> extends ArgumentParser<T> {

    private final ArgumentParser<T> parser;
    private final boolean optionalNotMatch;

    OptionalArgumentParser(ArgumentParser<T> parser, boolean optionalNotMatch) {
        super(parser.getKeyword(), parser.getArgumentType(), parser.getPriority());
        this.parser = parser;
        this.optionalNotMatch = optionalNotMatch;
    }

    public ArgumentParser<T> parser() {
        return parser;
    }

    public boolean optionalNotMatch() {
        return optionalNotMatch;
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
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OptionalArgumentParser<?> that = (OptionalArgumentParser<?>) o;
        return optionalNotMatch == that.optionalNotMatch && Objects.equals(parser, that.parser);
    }

    @Override
    public int hashCode() {
        return Objects.hash(parser, optionalNotMatch);
    }
}
