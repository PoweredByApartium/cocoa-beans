package net.apartium.cocoabeans.commands.parsers.exception;

import net.apartium.cocoabeans.commands.CommandProcessingContext;
import net.apartium.cocoabeans.commands.parsers.ArgumentParser;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collections;
import java.util.List;

@ApiStatus.AvailableSince("0.0.30")
public class AmbiguousMappedKeyResponse extends InvalidParserResponse {

    private final List<String> keys;

    public AmbiguousMappedKeyResponse(CommandProcessingContext context, ArgumentParser<?> parser, String message, List<String> keys) {
        super(context, parser, message);

        this.keys = keys;
    }

    public List<String> getKeys() {
        return Collections.unmodifiableList(keys);
    }

    @Override
    public Exception getError() {
        return new AmbiguousMappedKeyException();
    }

    public class AmbiguousMappedKeyException extends InvalidParserResponse.InvalidParserException {

        public AmbiguousMappedKeyException() {
            super(AmbiguousMappedKeyResponse.this);
        }

        public List<String> getKeys() {
            return keys;
        }

    }
}
