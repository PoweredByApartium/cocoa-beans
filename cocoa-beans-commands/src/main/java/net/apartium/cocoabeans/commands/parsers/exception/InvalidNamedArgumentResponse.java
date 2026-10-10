package net.apartium.cocoabeans.commands.parsers.exception;

import net.apartium.cocoabeans.commands.CommandProcessingContext;
import net.apartium.cocoabeans.commands.exception.BadCommandResponse;
import net.apartium.cocoabeans.commands.parsers.ArgumentParser;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

@ApiStatus.AvailableSince("0.0.54")
public class InvalidNamedArgumentResponse extends InvalidParserResponse {

    private final @Nullable BadCommandResponse reason;

    /**
     * Construct a new instance of invalid parser response
     *
     * @param context context of the command processing
     * @param parser  parser that reported the error
     * @param message error message
     */
    public InvalidNamedArgumentResponse(CommandProcessingContext context, ArgumentParser<?> parser, String message, @Nullable BadCommandResponse reason) {
        super(context, parser, message);
        this.reason = reason;
    }

    public @Nullable BadCommandResponse getReason() {
        return reason;
    }

    @Override
    public InvalidNamedArgumentException getError() {
        return new InvalidNamedArgumentException(this);
    }

    public static class InvalidNamedArgumentException extends InvalidParserException {

        private final @Nullable BadCommandResponse reason;

        public InvalidNamedArgumentException(InvalidNamedArgumentResponse response) {
            super(response);
            this.reason = response.reason;
        }

        public @Nullable BadCommandResponse getReason() {
            return reason;
        }

    }

}
