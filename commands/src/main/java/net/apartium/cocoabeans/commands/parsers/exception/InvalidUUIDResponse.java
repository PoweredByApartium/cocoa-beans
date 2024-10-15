package net.apartium.cocoabeans.commands.parsers.exception;

import net.apartium.cocoabeans.commands.CommandProcessingContext;
import net.apartium.cocoabeans.commands.parsers.ArgumentParser;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.AvailableSince("0.0.36")
public class InvalidUUIDResponse extends InvalidParserResponse {

    private final String userInput;

    /**
     * Construct a new instance of invalid parser response
     *
     * @param context context of the command processing
     * @param parser  parser that reported the error
     * @param message error message
     * @param userInput user input
     */
    public InvalidUUIDResponse(CommandProcessingContext context, ArgumentParser<?> parser, String message, String userInput) {
        super(context, parser, message);

        this.userInput = userInput;
    }

    /**
     * Get the user input
     * @return user input
     */
    public String getUserInput() {
        return userInput;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InvalidUUIDException getError() {
        return new InvalidUUIDException();
    }

    public class InvalidUUIDException extends InvalidParserResponse.InvalidParserException {

        public InvalidUUIDException() {
            super(InvalidUUIDResponse.this);
        }

        /**
         * Get the user input
         * @return user input
         * @see InvalidUUIDResponse#getUserInput()
         */
        public String getUserInput() {
            return InvalidUUIDResponse.this.userInput;
        }
    }
}
