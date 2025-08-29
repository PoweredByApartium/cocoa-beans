package net.apartium.cocoabeans.commands.parsers.exception;

import net.apartium.cocoabeans.commands.CommandProcessingContext;
import net.apartium.cocoabeans.commands.exception.BadCommandResponse;
import net.apartium.cocoabeans.commands.exception.CommandException;
import net.apartium.cocoabeans.commands.parsers.ArgumentParser;
import org.jetbrains.annotations.ApiStatus;

/**
 * Represents a response that is reported via a parser
 * @see CommandProcessingContext#report(Object, BadCommandResponse)
 */
@ApiStatus.AvailableSince("0.0.30")
public class InvalidParserResponse extends BadCommandResponse {

    private final ArgumentParser<?> parser;

    /**
     * Construct a new instance of invalid parser response
     * @param context context of the command processing
     * @param parser parser that reported the error
     * @param message error message
     */
    public InvalidParserResponse(CommandProcessingContext context, ArgumentParser<?> parser, String message) {
        super(context.label(), context.args().toArray(new String[0]), context.index(), message);
        this.parser = parser;
    }

    /**
     * Get the parser that reported the error
     * @return parser that reported the error
     */
    public ArgumentParser<?> getParser() {
        return parser;
    }

    /**
     * Construct a new instance of the exception to be thrown
     * @return exception to be thrown
     */
    @Override
    public InvalidParserException getError() {
        return new InvalidParserException(this);
    }

    public static class InvalidParserException extends CommandException {

        public InvalidParserException(InvalidParserResponse response) {
            super(response);
        }

    }
}
