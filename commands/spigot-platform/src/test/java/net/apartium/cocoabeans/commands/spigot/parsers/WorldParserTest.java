package net.apartium.cocoabeans.commands.spigot.parsers;

import net.apartium.cocoabeans.commands.parsers.ArgumentParser;
import net.apartium.cocoabeans.commands.parsers.ParserAssertions;
import net.apartium.cocoabeans.commands.spigot.CommandsSpigotTestBase;
import org.bukkit.WorldCreator;
import org.junit.jupiter.api.Test;

import java.util.OptionalInt;
import java.util.Set;

public class WorldParserTest extends CommandsSpigotTestBase {

    @Test
    void parse() {
        server.createWorld(new WorldCreator("test"));

        WorldParser parser = new WorldParser(0);

        ParserAssertions.assertParserResult(parser, null, null, args("test"), new ArgumentParser.ParseResult<>(server.getWorld("test"), 1));
    }

    @Test
    void tryParse() {
        server.createWorld(new WorldCreator("test"));

        WorldParser parser = new WorldParser(0);

        ParserAssertions.assertTryParseResult(parser, null, null, args("test"), OptionalInt.of(1));
    }

    @Test
    void tabComplete() {
        server.createWorld(new WorldCreator("test"));

        WorldParser parser = new WorldParser(0);

        ParserAssertions.assertParserTabCompletion(parser, null, null, new String[0], 0, Set.of("test"), 1);
        ParserAssertions.assertParserTabCompletion(parser, null, null, args(""), 0, Set.of("test"), 1);
        ParserAssertions.assertParserTabCompletion(parser, null, null, args("t"), 0, Set.of("test"), 1);
        ParserAssertions.assertParserTabCompletion(parser, null, null, args("te"), 0, Set.of("test"), 1);
        ParserAssertions.assertParserTabCompletion(parser, null, null, args("tes"), 0, Set.of("test"), 1);
        ParserAssertions.assertParserTabCompletion(parser, null, null, args("test"), 0, Set.of("test"), 1);
        ParserAssertions.assertParserTabCompletion(parser, null, null, args("test"), 0, Set.of("test"), 1);

        server.createWorld(new WorldCreator("world"));

        ParserAssertions.assertParserTabCompletion(parser, null, null, args(""), 0, Set.of("test", "world"), 1);

        ParserAssertions.assertParserTabCompletion(parser, null, null, args("t"), 0, Set.of("test"), 1);
        ParserAssertions.assertParserTabCompletion(parser, null, null, args("te"), 0, Set.of("test"), 1);
        ParserAssertions.assertParserTabCompletion(parser, null, null, args("tes"), 0, Set.of("test"), 1);
        ParserAssertions.assertParserTabCompletion(parser, null, null, args("test"), 0, Set.of("test"), 1);
        ParserAssertions.assertParserTabCompletion(parser, null, null, args("test"), 0, Set.of("test"), 1);

        ParserAssertions.assertParserTabCompletion(parser, null, null, args("w"), 0, Set.of("world"), 1);
        ParserAssertions.assertParserTabCompletion(parser, null, null, args("wo"), 0, Set.of("world"), 1);
        ParserAssertions.assertParserTabCompletion(parser, null, null, args("wor"), 0, Set.of("world"), 1);
        ParserAssertions.assertParserTabCompletion(parser, null, null, args("worl"), 0, Set.of("world"), 1);
        ParserAssertions.assertParserTabCompletion(parser, null, null, args("world"), 0, Set.of("world"), 1);
    }

    String[] args(String s) {
        return s.split("\\s+");
    }

}
