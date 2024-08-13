package net.apartium.cocoabeans.commands.parsers.exception;

import net.apartium.cocoabeans.commands.CommandProcessingContext;
import net.apartium.cocoabeans.commands.exception.BadCommandResponse;
import net.apartium.cocoabeans.commands.exception.CommandException;
import net.apartium.cocoabeans.commands.parsers.ArgumentParser;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.AvailableSince("0.0.30")
public class InvalidParserResponse extends BadCommandResponse {

    private final ArgumentParser<?> parser;

    public InvalidParserResponse(CommandProcessingContext context, ArgumentParser<?> parser, String message) {
        super(context.label(), context.args().toArray(new String[0]), context.index(), message);
        this.parser = parser;
    }

    public ArgumentParser<?> getParser() {
        return parser;
    }

    @Override
    public Exception getError() {
        return new InvalidParserException();
    }

    public class InvalidParserException extends CommandException {

        public InvalidParserException() {
            super(InvalidParserResponse.this);
        }

        public InvalidParserException(InvalidParserResponse response) {
            super(response);
        }

    }
}
