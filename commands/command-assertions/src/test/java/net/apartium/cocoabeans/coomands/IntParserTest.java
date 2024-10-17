package net.apartium.cocoabeans.coomands;

import net.apartium.cocoabeans.commands.ParserAssertions;
import net.apartium.cocoabeans.commands.parsers.ArgumentParser;
import net.apartium.cocoabeans.commands.parsers.IntParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;


public class IntParserTest {

    @Test
    public void test() {
        IntParser parser = new IntParser(0);

        ParserAssertions.assertParserResult(parser, null, "test", new String[]{"123"}, 0, new ArgumentParser.ParseResult<>(123, 1));
        Assertions.assertThrows(AssertionFailedError.class, () -> ParserAssertions.assertParserResult(parser, null, "test", new String[]{"123"}, 0, new ArgumentParser.ParseResult<>(124, 1)));
        Assertions.assertThrows(AssertionFailedError.class, () -> ParserAssertions.assertParserResult(parser, null, "test", new String[]{"123"}, 0, new ArgumentParser.ParseResult<>(123, 0)));
        Assertions.assertThrows(AssertionFailedError.class, () -> ParserAssertions.assertParserResult(parser, null, "test", new String[]{"123"}, 0, new ArgumentParser.ParseResult<>(123, 2)));
    }

}
