/*
 * Copyright (c) 2026, Lior Slakman (me@voigon.dev), ALL RIGHTS RESERVED
 * Do not use, copy, modify, and/or distribute this software without explicit permission from the
 * rights holder. Reselling this product is not allowed. Transfer of the source code to any person
 * or organization not explicitly approved by the rights holder via a license agreement is hereby forbidden.
 */

package net.apartium.cocoabeans.commands.spigot.parsers;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import net.apartium.cocoabeans.commands.parsers.ArgumentParser;
import net.apartium.cocoabeans.commands.spigot.CommandsSpigotTestBase;
import net.apartium.cocoabeans.commands.spigot.parsers.exception.NoSuchWorldTypeResponse;
import org.bukkit.WorldType;
import org.junit.jupiter.api.Test;

import java.util.OptionalInt;
import java.util.Set;

import static net.apartium.cocoabeans.commands.parsers.ParserAssertions.*;

class WorldTypeParserTest extends CommandsSpigotTestBase {

    @Test
    void worldTypeCommand() {
        commandManager.addCommand(new WorldTypeCommand());
        PlayerMock sender = server.addPlayer("ikfir");

        execute(sender, "worldtype normal");
        sender.assertSaid("world type: DEFAULT");

        execute(sender, "worldtype no-world");
        sender.assertSaid("No world type by the name of no-world");
    }


    @Test
    void parse() {
        WorldTypeParser parser = new WorldTypeParser(0);

        assertParserResult(parser, null, null, args("normal"), new ArgumentParser.ParseResult<>(WorldType.NORMAL, 1));
        assertParserThrowsReport(parser, null, null, args("what"), NoSuchWorldTypeResponse.class);
    }

    @Test
    void tryParse() {
        WorldTypeParser parser = new WorldTypeParser(0);

        assertTryParseResult(parser, null, null, args("normal"), OptionalInt.of(1));
    }

    @Test
    void tabComplete() {
        WorldTypeParser parser = new WorldTypeParser(0);

        assertParserTabCompletion(parser, null, null, new String[0], 0, Set.of("normal", "flat", "amplified", "large-biomes"), 1);
        assertParserTabCompletion(parser, null, null, args(""), 0, Set.of("normal", "flat", "amplified", "large-biomes"), 1);
        assertParserTabCompletion(parser, null, null, args("n"), 0, Set.of("normal"), 1);
        assertParserTabCompletion(parser, null, null, args("no"), 0, Set.of("normal"), 1);
        assertParserTabCompletion(parser, null, null, args("nor"), 0, Set.of("normal"), 1);
        assertParserTabCompletion(parser, null, null, args("norm"), 0, Set.of("normal"), 1);
        assertParserTabCompletion(parser, null, null, args("norma"), 0, Set.of("normal"), 1);


        assertParserTabCompletion(parser, null, null, args("f"), 0, Set.of("flat"), 1);
        assertParserTabCompletion(parser, null, null, args("fl"), 0, Set.of("flat"), 1);
        assertParserTabCompletion(parser, null, null, args("fla"), 0, Set.of("flat"), 1);
        assertParserTabCompletion(parser, null, null, args("flat"), 0, Set.of("flat"), 1);

        assertParserTabCompletion(parser, null, null, args("s"), 0, null, 1);
    }

    @Test
    void tabCompleteCaseSensitive() {
        WorldTypeParser parser = new WorldTypeParser("world-type", 0, true);

        assertParserTabCompletion(parser, null, null, new String[0], 0, Set.of("normal", "flat", "amplified", "large-biomes"), 1);
        assertParserTabCompletion(parser, null, null, args(""), 0, Set.of("normal", "flat", "amplified", "large-biomes"), 1);
        assertParserTabCompletion(parser, null, null, args("N"), 0, Set.of("normal"), 1);
        assertParserTabCompletion(parser, null, null, args("NO"), 0, Set.of("normal"), 1);
        assertParserTabCompletion(parser, null, null, args("NOR"), 0, Set.of("normal"), 1);
        assertParserTabCompletion(parser, null, null, args("NORM"), 0, Set.of("normal"), 1);
        assertParserTabCompletion(parser, null, null, args("NORMAL"), 0, Set.of("normal"), 1);


        assertParserTabCompletion(parser, null, null, args("F"), 0, Set.of("flat"), 1);
        assertParserTabCompletion(parser, null, null, args("FL"), 0, Set.of("flat"), 1);
        assertParserTabCompletion(parser, null, null, args("FLA"), 0, Set.of("flat"), 1);
        assertParserTabCompletion(parser, null, null, args("FLAT"), 0, Set.of("flat"), 1);

        assertParserTabCompletion(parser, null, null, args("s"), 0, null, 1);
    }

    String[] args(String s) {
        return s.split("\\s+");
    }
}
