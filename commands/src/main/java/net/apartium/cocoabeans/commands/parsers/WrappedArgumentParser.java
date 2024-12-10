package net.apartium.cocoabeans.commands.parsers;

import net.apartium.cocoabeans.commands.CommandProcessingContext;
import org.jetbrains.annotations.ApiStatus;

import java.util.Optional;
import java.util.OptionalInt;

/**
 *
 * @param <T>
 */
@ApiStatus.AvailableSince("0.0.38")
public class WrappedArgumentParser<T> extends ArgumentParser<T> {

    private final ArgumentParser<T> parser;

    /**
     * Wrapped argument parser constructor
     * @param parser the argument parser to wrap
     * @param priority parser priority
     * @param keyword new keyword for the parser
     */
    public WrappedArgumentParser(ArgumentParser<T> parser, int priority, String keyword) {
        super(keyword, parser.getArgumentType(), priority);

        this.parser = parser;
    }

    /**
     * Wrapped argument parser constructor
     * @param parser the argument parser to wrap
     * @param keyword new keyword for the parser
     */
    public WrappedArgumentParser(ArgumentParser<T> parser, String keyword) {
        this(parser, parser.getPriority(), keyword);
    }

    /**
     * Wrapped argument parser constructor
     * @param parser the argument parser to wrap
     * @param priority parser priority
     */
    public WrappedArgumentParser(ArgumentParser<T> parser, int priority) {
        this(parser, priority, parser.getKeyword());
    }

    /**
     * Tries to parse next argument in the context
     *
     * @param processingContext cmd processing context
     * @return empty if failed, otherwise result
     */
    @Override
    public Optional<ParseResult<T>> parse(CommandProcessingContext processingContext) {
        return parser.parse(processingContext);
    }

    /**
     * Tries to lazily parse next argument in the context
     *
     * @param processingContext cmd processing context
     * @return new index int if success, empty if not
     */
    @Override
    public OptionalInt tryParse(CommandProcessingContext processingContext) {
        return parser.tryParse(processingContext);
    }

    /**
     * Retrieves available options for tab completion of this argument
     *
     * @param processingContext cmd processing context
     * @return tab completion result if success, otherwise empty option
     */
    @Override
    public Optional<TabCompletionResult> tabCompletion(CommandProcessingContext processingContext) {
        return parser.tabCompletion(processingContext);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (obj == null || getClass() != obj.getClass()) {
            if (!(obj instanceof ArgumentParser<?> other))
                return false;

            return parser.equals(other);
        }

        if (!super.equals(obj))
            return false;

        return parser.equals(((WrappedArgumentParser<?>) obj).parser);
    }

    @Override
    public int hashCode() {
        return parser.hashCode();
    }

    @Override
    public String toString() {
        return parser.toString();
    }
}
