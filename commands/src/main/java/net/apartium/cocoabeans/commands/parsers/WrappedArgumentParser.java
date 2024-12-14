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
     * {@inheritDoc}
     * Using the wrapped parser to parse the context
     */
    @Override
    public Optional<ParseResult<T>> parse(CommandProcessingContext processingContext) {
        return parser.parse(processingContext);
    }

    /**
     * {@inheritDoc}
     * Using the wrapped parser to try parse the context
     */
    @Override
    public OptionalInt tryParse(CommandProcessingContext processingContext) {
        return parser.tryParse(processingContext);
    }

    /**
     * {@inheritDoc}
     * Using the wrapped parser to get tab completion
     */
    @Override
    public Optional<TabCompletionResult> tabCompletion(CommandProcessingContext processingContext) {
        return parser.tabCompletion(processingContext);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (obj == null)
            return false;

        ArgumentParser<?> other;
        Class<?> otherClass;

        if (getClass() == obj.getClass()) {
            other = ((WrappedArgumentParser<?>) obj);
            otherClass = ((WrappedArgumentParser<?>) obj).parser.getClass();
        } else if (obj instanceof ArgumentParser<?> o) {
            other = o;
            otherClass = o.getClass();
        } else return false;

        return parser.getClass().equals(otherClass)
                && other.getKeyword().equals(getKeyword())
                && other.getArgumentType().equals(getArgumentType())
                && other.getPriority() == getPriority();
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
