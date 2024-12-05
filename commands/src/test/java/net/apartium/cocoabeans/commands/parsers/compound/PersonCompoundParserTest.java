package net.apartium.cocoabeans.commands.parsers.compound;

import net.apartium.cocoabeans.commands.exception.BadCommandResponse;
import net.apartium.cocoabeans.commands.parsers.ArgumentParser;
import net.apartium.cocoabeans.commands.parsers.ParserAssertions;
import org.junit.jupiter.api.Test;

class PersonCompoundParserTest {

    @Test
    void testingParser() {
        PersonCompoundParser personParser = new PersonCompoundParser(0);

        ParserAssertions.assertParserResult(
                personParser,
                null,
                null,
                args("kfir 18"),
                new ArgumentParser.ParseResult<>(new PersonCompoundParser.Person("kfir", 18), 2)
        );

        ParserAssertions.assertParserResult(
                personParser,
                null,
                null,
                args("tom 26"),
                new ArgumentParser.ParseResult<>(new PersonCompoundParser.Person("tom", 26), 2)
        );

        ParserAssertions.assertParserThrowsReport(
                personParser,
                null,
                null,
                args("no 152"),
                BadCommandResponse.class
        );
    }

    static String[] args(String args) {
        return args.split("\\s+");
    }

}
