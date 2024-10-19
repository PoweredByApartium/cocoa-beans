package net.apartium.cocoabeans.commands.parsers;

import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

import static net.apartium.cocoabeans.commands.parsers.ParserAssertions.assertParserResult;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class IntParserTest {

    @Test
    public void test() {
        IntParser parser = new IntParser(0);

        assertParserResult(parser, null, "test", new String[]{"123"}, 0, new ArgumentParser.ParseResult<>(123, 1));
        assertThrows(AssertionFailedError.class, () -> assertParserResult(parser, null, "test", new String[]{"123"}, 0, new ArgumentParser.ParseResult<>(124, 1)));
        assertThrows(AssertionFailedError.class, () -> assertParserResult(parser, null, "test", new String[]{"123"}, 0, new ArgumentParser.ParseResult<>(123, 0)));
        assertThrows(AssertionFailedError.class, () -> assertParserResult(parser, null, "test", new String[]{"123"}, 0, new ArgumentParser.ParseResult<>(123, 2)));
    }

}
