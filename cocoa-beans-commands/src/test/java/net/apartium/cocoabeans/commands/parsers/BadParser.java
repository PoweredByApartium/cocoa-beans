package net.apartium.cocoabeans.commands.parsers;

import net.apartium.cocoabeans.commands.CommandProcessingContext;

import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;

/* package-private */ class BadParser extends ArgumentParser<String> {

    public BadParser() {
        super("bad-parser", String.class, 0);
    }

    @Override
    public Optional<ParseResult<String>> parse(CommandProcessingContext processingContext) {
        return Optional.of(new ParseResult<>(
                "wow",
                processingContext.index()
        ));
    }

    @Override
    public OptionalInt tryParse(CommandProcessingContext processingContext) {
        return OptionalInt.of(processingContext.index());
    }

    @Override
    public Optional<TabCompletionResult> tabCompletion(CommandProcessingContext processingContext) {
        return Optional.of(new TabCompletionResult(
                Set.of("wow"),
                processingContext.index()
        ));
    }
}
