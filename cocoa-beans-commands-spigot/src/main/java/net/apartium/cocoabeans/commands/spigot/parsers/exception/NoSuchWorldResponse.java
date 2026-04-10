package net.apartium.cocoabeans.commands.spigot.parsers.exception;

import net.apartium.cocoabeans.commands.CommandProcessingContext;
import net.apartium.cocoabeans.commands.parsers.ContextualMapBasedParser;
import net.apartium.cocoabeans.commands.parsers.exception.NoSuchElementInMapResponse;
import org.jetbrains.annotations.ApiStatus;

/**
 * Represents a response reported via a parser when a world is not found
 */
@ApiStatus.AvailableSince("0.0.49")
public class NoSuchWorldResponse extends NoSuchElementInMapResponse {

    /**
     * Construct a new instance of invalid parser response
     *
     * @param context context of the command processing
     * @param parser  parser that reported the error
     * @param message error message
     * @param attempted attempted key
     */
    public NoSuchWorldResponse(CommandProcessingContext context, ContextualMapBasedParser<?> parser, String message, String attempted) {
        super(context, parser, message, attempted);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NoSuchWorldException getError() {
        return new NoSuchWorldException(this);
    }

    /**
     * Thrown to indicate that a specific world could not be found during the parsing process.
     * This exception is used internally to wrap a {@link NoSuchWorldResponse} and propagate error information.
     * <p>
     * This exception extends {@link NoSuchElementInMapException}, which is raised when a key is not found
     * in a map-based parser during command processing.
     *
     * @see NoSuchWorldResponse
     * @see NoSuchElementInMapException
     * @see ContextualMapBasedParser
     */
    public class NoSuchWorldException extends NoSuchElementInMapException {

        @ApiStatus.Internal
        private NoSuchWorldException(NoSuchWorldResponse response) {
            super(response);
        }

    }

}
