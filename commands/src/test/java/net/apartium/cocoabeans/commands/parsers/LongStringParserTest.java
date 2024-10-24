package net.apartium.cocoabeans.commands.parsers;

import net.apartium.cocoabeans.commands.exception.BadCommandResponse;
import org.junit.jupiter.api.Test;

import static net.apartium.cocoabeans.commands.parsers.ParserAssertions.assertParserResult;
import static net.apartium.cocoabeans.commands.parsers.ParserAssertions.assertParserThrowsReport;

public class LongStringParserTest {

    @Test
    void testNotParagraph() {
        QuotedStringParser parser = new QuotedStringParser(0);

        assertParserResult(parser, null, null, args("hello"), new ArgumentParser.ParseResult<>("hello", 1));
        assertParserResult(parser, null, null, args("just a test \"wow very cool\""), 3, new ArgumentParser.ParseResult<>("wow very cool", 6));
        assertParserResult(parser, null, null, args("just a test \"wow very cool\" wtf this is a test"), 3, new ArgumentParser.ParseResult<>("wow very cool", 6));
        assertParserResult(parser, null, null, args("hello WORLD"), new ArgumentParser.ParseResult<>("hello", 1));
        assertParserResult(parser, null, null, args("\"new test a very new test\""), new ArgumentParser.ParseResult<>("new test a very new test", 6));
        assertParserResult(parser, null, null, args("\"hello\" WORLD wtf"), new ArgumentParser.ParseResult<>("hello", 1));
        assertParserResult(parser, null, null, args("\"hello WORLD\" wtf"), new ArgumentParser.ParseResult<>("hello WORLD", 2));
        assertParserResult(parser, null, null, args("\"hello WORLD\""), new ArgumentParser.ParseResult<>("hello WORLD", 2));
        assertParserResult(parser, null, null, args("\"hello \\\"WORLD\""), new ArgumentParser.ParseResult<>("hello \"WORLD", 2));
        assertParserResult(parser, null, null, args("\"hello \\\"WO\\\\RLD\""), new ArgumentParser.ParseResult<>("hello \"WO\\RLD", 2));
        assertParserResult(parser, null, null, args("hello\\\"very-cool\\\\"), new ArgumentParser.ParseResult<>("hello\"very-cool\\", 1));

        assertParserThrowsReport(parser, null, null, args("\"Hello world"), BadCommandResponse.class);
        assertParserThrowsReport(parser, null, null, args("Hello\"world"), BadCommandResponse.class);
        assertParserThrowsReport(parser, null, null, args("\"Hello wor\"ld"), BadCommandResponse.class);
        assertParserThrowsReport(parser, null, null, args("\"Hello wor\\nld"), BadCommandResponse.class);
    }

    @Test
    void testWithParagraph() {
        ParagraphParser parser = new ParagraphParser(0);

        assertParserResult(parser, null, null, args("hello"), new ArgumentParser.ParseResult<>("hello", 1));
        assertParserResult(parser, null, null, args("hello WORLD"), new ArgumentParser.ParseResult<>("hello", 1));
        assertParserResult(parser, null, null, args("\"hello WORLD\""), new ArgumentParser.ParseResult<>("hello WORLD", 2));
        assertParserResult(parser, null, null, args("\"hello \\\"WORLD\""), new ArgumentParser.ParseResult<>("hello \"WORLD", 2));
        assertParserResult(parser, null, null, args("\"hello \\\"WO\\\\RLD\""), new ArgumentParser.ParseResult<>("hello \"WO\\RLD", 2));
        assertParserResult(parser, null, null, args("hello\\\"very-cool\\\\") , new ArgumentParser.ParseResult<>("hello\"very-cool\\", 1));
        assertParserResult(parser, null, null, args("\"Welcome to my game\\n This game is about blocks\"") , new ArgumentParser.ParseResult<>("Welcome to my game\nThis game is about blocks", 9));
        assertParserResult(parser, null, null, args("\"Hello\\\\n world\"") , new ArgumentParser.ParseResult<>("Hello\\n world", 2));
        assertParserResult(parser, null, null, args("\"test cool\\n\"") , new ArgumentParser.ParseResult<>("test cool\n", 2));
        assertParserResult(parser, null, null, args("\"te\\nst cool\\n\"") , new ArgumentParser.ParseResult<>("te\nst cool\n", 2));
        assertParserResult(parser, null, null, args("\"te\\nst \\ncool\\n\"") , new ArgumentParser.ParseResult<>("te\nst \ncool\n", 2));
        assertParserResult(parser, null, null, args("\"te\\nst\\n cool\\n\"") , new ArgumentParser.ParseResult<>("te\nst\ncool\n", 2));
        assertParserResult(parser, null, null, args("\"\\n test very cool\\n\"") , new ArgumentParser.ParseResult<>("\ntest very cool\n", 4));
        assertParserResult(parser, null, null, args("\\n") , new ArgumentParser.ParseResult<>("\n", 1));
        assertParserResult(parser, null, null, args("\"\\n\"") , new ArgumentParser.ParseResult<>("\n", 1));
        assertParserResult(parser, null, null, args("n"), new ArgumentParser.ParseResult<>("n", 1));

        assertParserResult(parser, null, null, args("") , new ArgumentParser.ParseResult<>("", 1));
        assertParserThrowsReport(parser, null, null, args("\"Hello world"), BadCommandResponse.class);
        assertParserThrowsReport(parser, null, null, args("Hello\"world"), BadCommandResponse.class);
        assertParserThrowsReport(parser, null, null, args("\"Hello wor\"ld"), BadCommandResponse.class);
        assertParserThrowsReport(parser, null, null, args("\"Hello\\\" wor\"ld"), BadCommandResponse.class);
        assertParserThrowsReport(parser, null, null, args("\"Hello\\\" w\"orld\""), BadCommandResponse.class);
    }

    public static String[] args(String args) {
        return args.split("\\s+");
    }

}
