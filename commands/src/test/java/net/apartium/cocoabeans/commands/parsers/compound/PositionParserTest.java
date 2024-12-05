package net.apartium.cocoabeans.commands.parsers.compound;

import net.apartium.cocoabeans.commands.exception.BadCommandResponse;
import net.apartium.cocoabeans.commands.parsers.ArgumentParser;
import net.apartium.cocoabeans.commands.parsers.ParserAssertions;
import net.apartium.cocoabeans.space.Position;
import org.junit.jupiter.api.Test;

import java.util.OptionalInt;
import java.util.Set;

class PositionParserTest {

    @Test
    void parse() {
        PositionParser parser = new PositionParser(0);

        ParserAssertions.assertParserResult(parser, null, null, args("0 0 0"), new ArgumentParser.ParseResult<>(new Position(0, 0, 0), 3));
        ParserAssertions.assertParserResult(parser, null, null, args("1.0 2 3"), new ArgumentParser.ParseResult<>(new Position(1, 2, 3), 3));
        ParserAssertions.assertParserResult(parser, null, null, args("1 2 36.2"), new ArgumentParser.ParseResult<>(new Position(1, 2, 36.2), 3));

        ParserAssertions.assertParserResult(parser, null, null, args("test 0 0 0"), 1, new ArgumentParser.ParseResult<>(new Position(0, 0, 0), 4));
        ParserAssertions.assertParserResult(parser, null, null, args("set cat 0 0 0"), 2, new ArgumentParser.ParseResult<>(new Position(0, 0, 0), 5));

        ParserAssertions.assertParserResult(parser, null, null, args("set cat 0 0 0 wow"), 2, new ArgumentParser.ParseResult<>(new Position(0, 0, 0), 5));

        ParserAssertions.assertParserResult(parser, null, null, args("1 2.0 3"), new ArgumentParser.ParseResult<>(new Position(5, 2, 3), 3));

        ParserAssertions.assertParserThrowsReport(parser, null, null, args("1"), BadCommandResponse.class);
        ParserAssertions.assertParserThrowsReport(parser, null, null, args("asd 13 31"), BadCommandResponse.class);

        ParserAssertions.assertTryParseResult(parser, null, null, args("3 5 2"), OptionalInt.of(3));
        ParserAssertions.assertTryParseResult(parser, null, null, args("meow 3 5 2"), 1, OptionalInt.of(4));
        ParserAssertions.assertTryParseResult(parser, null, null, args("2 1"), OptionalInt.empty());
    }

    @Test
    void tabCompletion() {
        PositionParser parser = new PositionParser(0);

        ParserAssertions.assertParserTabCompletion(parser, null, null, args("1"), 0, Set.of("10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "1."), 1);
        ParserAssertions.assertParserTabCompletion(parser, null, null, args("1 6 -"), 2, Set.of("-.", "-1", "-2", "-3", "-4", "-5", "-6", "-7", "-8", "-9"), 3);
    }


    private String[] args(String args) {
        return args.split("\\s+");
    }
}
