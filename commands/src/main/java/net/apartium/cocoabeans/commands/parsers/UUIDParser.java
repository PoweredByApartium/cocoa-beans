package net.apartium.cocoabeans.commands.parsers;

import net.apartium.cocoabeans.commands.CommandProcessingContext;
import net.apartium.cocoabeans.commands.parsers.exception.InvalidUUIDResponse;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;

@ApiStatus.AvailableSince("0.0.36")
public class UUIDParser extends ArgumentParser<UUID> {

    /**
     * Creates new UUID parser
     * @param keyword parser keyword
     * @param priority parser priority
     */
    public UUIDParser(String keyword, int priority) {
        super(keyword, UUID.class, priority);
    }

    /**
     * Creates new UUID parser
     * @param priority parser priority
     * @see #UUIDParser(String, int)
     */
    public UUIDParser(int priority) {
        this("uuid", priority);
    }

    /**
     * Creates new UUID parser
     * @see #UUIDParser(int)
     */
    public UUIDParser() {
        this(0);
    }

    @Override
    public Optional<ParseResult<UUID>> parse(CommandProcessingContext processingContext) {
        List<String> args = processingContext.args();
        int startIndex = processingContext.index();

        if (startIndex >= args.size())
            return Optional.empty();

        try {
            return Optional.of(new ParseResult<>(
                    UUID.fromString(args.get(startIndex)),
                    startIndex + 1
            ));
        } catch (IllegalArgumentException e) {
            processingContext.report(this, new InvalidUUIDResponse(processingContext, this, e.getMessage(), args.get(startIndex)));
            return Optional.empty();
        }
    }


    @Override
    public OptionalInt tryParse(CommandProcessingContext processingContext) {
        return parse(processingContext).map(ParseResult::newIndex).map(OptionalInt::of).orElse(OptionalInt.empty());
    }

    @Override
    public Optional<TabCompletionResult> tabCompletion(CommandProcessingContext processingContext) {
        int startIndex = processingContext.index();

        return Optional.of(new TabCompletionResult(
                Set.of(),
                startIndex + 1
        ));
    }

}
