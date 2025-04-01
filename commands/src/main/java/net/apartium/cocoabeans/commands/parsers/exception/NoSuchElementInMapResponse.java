package net.apartium.cocoabeans.commands.parsers.exception;

import net.apartium.cocoabeans.commands.CommandProcessingContext;
import net.apartium.cocoabeans.commands.parsers.ArgumentParser;
import net.apartium.cocoabeans.commands.parsers.MapBasedParser;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;

/**
 * Represents a response that is reported via a parser when a mapped key is not found
 * @see net.apartium.cocoabeans.commands.parsers.MapBasedParser
 * @see net.apartium.cocoabeans.commands.parsers.SourceParser
 */
@ApiStatus.AvailableSince("0.0.39")
public class NoSuchElementInMapResponse extends InvalidParserResponse {

    private final String attempted;

    /**
     * Construct a new instance of invalid parser response
     *
     * @param context context of the command processing
     * @param parser  parser that reported the error
     * @param message error message
     * @param attempted attempted key
     */
    public NoSuchElementInMapResponse(CommandProcessingContext context, MapBasedParser<?> parser, String message, String attempted) {
        super(context, parser, message);

        this.attempted = attempted;
    }

    /**
     * The attempted key that was not found
     * @return attempted key
     */
    public String getAttempted() {
        return attempted;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NoSuchElementInMapException getError() {
        return new NoSuchElementInMapException(this);
    }

    /**
     * NoSuchElementInMapException Represents a response that is reported via a parser when a mapped key is not found
     */
    public class NoSuchElementInMapException extends InvalidParserException {

        @ApiStatus.Internal
        private NoSuchElementInMapException(NoSuchElementInMapResponse response) {
            super(response);
        }

        /**
         * The attempted key that was not found
         * @return attempted key
         */
        public String getAttempted() {
            return NoSuchElementInMapResponse.this.attempted;
        }
    }


}
