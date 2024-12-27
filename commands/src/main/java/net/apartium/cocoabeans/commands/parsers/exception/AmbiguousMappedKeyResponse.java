package net.apartium.cocoabeans.commands.parsers.exception;

import net.apartium.cocoabeans.commands.CommandProcessingContext;
import net.apartium.cocoabeans.commands.parsers.ArgumentParser;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collections;
import java.util.List;

/**
 * Represents a response that is reported via a parser when a mapped key is ambiguous
 * @see net.apartium.cocoabeans.commands.parsers.MapBasedParser
 * @see net.apartium.cocoabeans.commands.parsers.SourceParser
 */
@ApiStatus.AvailableSince("0.0.30")
public class AmbiguousMappedKeyResponse extends InvalidParserResponse<AmbiguousMappedKeyResponse.AmbiguousMappedKeyException> {

    private final List<String> keys;

    /**
     * Construct a new instance of ambiguous mapped key response
     * @param context context of the command processing
     * @param parser parser that reported the error
     * @param message error message
     * @param keys list of ambiguous keys
     */
    public AmbiguousMappedKeyResponse(CommandProcessingContext context, ArgumentParser<?> parser, String message, List<String> keys) {
        super(context, parser, message);

        this.keys = keys;
    }

    /**
     * Get the list of ambiguous keys
     * @return list of ambiguous keys
     */
    public List<String> getKeys() {
        return Collections.unmodifiableList(keys);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AmbiguousMappedKeyException getError() {
        return new AmbiguousMappedKeyException();
    }

    public class AmbiguousMappedKeyException extends InvalidParserResponse.InvalidParserException {

        private AmbiguousMappedKeyException() {
            super(AmbiguousMappedKeyResponse.this);
        }

        /**
         * Get the list of ambiguous keys
         * @return list of ambiguous keys
         * @see AmbiguousMappedKeyResponse#getKeys()
         */
        public List<String> getKeys() {
            return AmbiguousMappedKeyResponse.this.getKeys();
        }

    }
}
