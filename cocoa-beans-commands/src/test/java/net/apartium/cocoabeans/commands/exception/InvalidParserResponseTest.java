package net.apartium.cocoabeans.commands.exception;

import net.apartium.cocoabeans.commands.TestCommandProcessingContext;
import net.apartium.cocoabeans.commands.parsers.exception.InvalidParserResponse;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InvalidParserResponseTest {

    @Test
    void getError() {
        InvalidParserResponse error = new InvalidParserResponse(new TestCommandProcessingContext(null, "test", List.of(), 0), null, null);

        InvalidParserResponse.InvalidParserException exception = error.getError();

        assertEquals(InvalidParserResponse.InvalidParserException.class, exception.getClass());
        assertEquals(error, exception.getBadCommandResponse());
    }

}
