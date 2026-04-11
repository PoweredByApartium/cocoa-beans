/*
 * Copyright 2024 Apartium
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
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

        for (WorldType value : WorldType.values()) {
            assertParserResult(parser, null, null, args(value.name().toLowerCase()), new ArgumentParser.ParseResult<>(value, 1));
            assertParserResult(parser, null, null, args(value.getName().toLowerCase()), new ArgumentParser.ParseResult<>(value, 1));
        }

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

        assertParserTabCompletion(parser, null, null, new String[0], 0, Set.of("amplified", "default", "flat", "large_biomes", "largebiomes", "normal"), 1);
        assertParserTabCompletion(parser, null, null, args(""), 0, Set.of("amplified", "default", "flat", "large_biomes", "largebiomes", "normal"), 1);
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
    void tabCompleteIgnoreCase() {
        WorldTypeParser parser = new WorldTypeParser("world-type", 0, true);

        assertParserTabCompletion(parser, null, null, new String[0], 0, Set.of("amplified", "default", "flat", "large_biomes", "largebiomes", "normal"), 1);
        assertParserTabCompletion(parser, null, null, args(""), 0, Set.of("amplified", "default", "flat", "large_biomes", "largebiomes", "normal"), 1);
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
