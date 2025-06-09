package net.apartium.cocoabeans.commands.spigot.parsers;

import net.apartium.cocoabeans.commands.parsers.ArgumentParser;
import net.apartium.cocoabeans.commands.parsers.ParserAssertions;
import net.apartium.cocoabeans.commands.spigot.CommandsSpigotTestBase;
import org.bukkit.Material;
import org.junit.jupiter.api.Test;

import java.util.OptionalInt;
import java.util.Set;

import static net.apartium.cocoabeans.commands.parsers.ParserAssertions.args;

public class MaterialParserTest extends CommandsSpigotTestBase {

    @Test
    void testParse() {
        MaterialParser parser = new MaterialParser(0);

        ParserAssertions.assertParserResult(parser, null, null, args("diamondsword"),
                new ArgumentParser.ParseResult<>(Material.DIAMOND_SWORD, 1));
    }

    @Test
    void testParseWithUnderscore() {
        MaterialParser parser = new MaterialParser(0);

        ParserAssertions.assertParserResult(parser, null, null, args("diamond_sword"),
                new ArgumentParser.ParseResult<>(Material.DIAMOND_SWORD, 1));
    }


    @Test
    void testTryParse() {
        MaterialParser parser = new MaterialParser(0);
        ParserAssertions.assertTryParseResult(parser, null, null, args("diamondsword"), OptionalInt.of(1));
    }

    @Test
    void testTryParseReturnsEmpty() {
        MaterialParser parser = new MaterialParser(0);
        ParserAssertions.assertTryParseResult(parser, null, null, args("something"), OptionalInt.empty());
    }

    @Test
    void testTabCompletionWithUnderscores() {
        MaterialParser parser = new MaterialParser(0);

        ParserAssertions.assertParserTabCompletion(parser, null, null, args("diamond_sw"), 0,
                Set.of("diamond_sword"), 1);
        ParserAssertions.assertParserTabCompletion(parser, null, null, args("diamond_swo"), 0,
                Set.of("diamond_sword"), 1);
        ParserAssertions.assertParserTabCompletion(parser, null, null, args("diamond_swor"), 0,
                Set.of("diamond_sword"), 1);
        ParserAssertions.assertParserTabCompletion(parser, null, null, args("diamond_sword"), 0,
                Set.of("diamond_sword"), 1);

        ParserAssertions.assertParserTabCompletion(parser, null, null, args("DIAMOND_SW"), 0,
                Set.of("diamond_sword"), 1);
        ParserAssertions.assertParserTabCompletion(parser, null, null, args("DIAMOND_SWO"), 0,
                Set.of("diamond_sword"), 1);
        ParserAssertions.assertParserTabCompletion(parser, null, null, args("DIAMOND_SWOR"), 0,
                Set.of("diamond_sword"), 1);
        ParserAssertions.assertParserTabCompletion(parser, null, null, args("DIAMOND_SWORD"), 0,
                Set.of("diamond_sword"), 1);
    }

    @Test
    void testTabCompletionWithoutUnderscores() {
        MaterialParser parser = new MaterialParser(0);

        ParserAssertions.assertParserTabCompletion(parser, null, null, args("diamondsw"), 0,
                Set.of("diamondsword"), 1);

        ParserAssertions.assertParserTabCompletion(parser, null, null, args("diamondswo"), 0,
                Set.of("diamondsword"), 1);

        ParserAssertions.assertParserTabCompletion(parser, null, null, args("diamondswor"), 0,
                Set.of("diamondsword"), 1);

        ParserAssertions.assertParserTabCompletion(parser, null, null, args("diamondsword"), 0,
                Set.of("diamondsword"), 1);


        ParserAssertions.assertParserTabCompletion(parser, null, null, args("DIAMONDSW"), 0,
                Set.of("diamondsword"), 1);

        ParserAssertions.assertParserTabCompletion(parser, null, null, args("DIAMONDSWO"), 0,
                Set.of("diamondsword"), 1);

        ParserAssertions.assertParserTabCompletion(parser, null, null, args("DIAMONDSWOR"), 0,
                Set.of("diamondsword"), 1);

        ParserAssertions.assertParserTabCompletion(parser, null, null, args("DIAMONDSWORD"), 0,
                Set.of("diamondsword"), 1);
    }

}
