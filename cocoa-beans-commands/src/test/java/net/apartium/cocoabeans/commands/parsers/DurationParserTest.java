package net.apartium.cocoabeans.commands.parsers;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Map;
import java.util.Set;

import static net.apartium.cocoabeans.commands.parsers.ParserAssertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;


class DurationParserTest {

    @Test
    void simple() {
        DurationParser parser = new DurationParser();

        for (int i = 1; i <= 24; i++)
            assertParserResult(parser, null, null, args(i + "h"), new ArgumentParser.ParseResult<>(Duration.ofHours(i), 1));

        for (int i = 1; i <= 60; i++) {
            assertParserResult(parser, null, null, args(i + "m"), new ArgumentParser.ParseResult<>(Duration.ofMinutes(i), 1));
            assertParserResult(parser, null, null, args(i + "s"), new ArgumentParser.ParseResult<>(Duration.ofSeconds(i), 1));
        }

        for (int i = 1; i <= 365; i++)
            assertParserResult(parser, null, null, args(i + "d"), new ArgumentParser.ParseResult<>(Duration.ofDays(i), 1));

        for (int i = 1; i <= 10; i++) {
            assertParserResult(parser, null, null, args(i + "y"), new ArgumentParser.ParseResult<>(Duration.ofDays(i * 365), 1));
            assertParserResult(parser, null, null, args(i + "w"), new ArgumentParser.ParseResult<>(Duration.ofDays(i * 7), 1));
        }
    }

    @Test
    void moreComplexStuff() {
        DurationParser parser = new DurationParser();

        assertParserResult(parser, null, null, args("10m 5s"), new ArgumentParser.ParseResult<>(Duration.ofSeconds(605), 2));
        assertParserResult(parser, null, null, args("2h 10m 5s"), new ArgumentParser.ParseResult<>(Duration.ofSeconds(7805), 3));
        assertParserResult(parser, null, null, args("10m 2h 5s"), new ArgumentParser.ParseResult<>(Duration.ofSeconds(7805), 3));
        assertParserResult(parser, null, null, args("1d 2h 10m 5s"), new ArgumentParser.ParseResult<>(Duration.ofSeconds(94205), 4));
        assertParserResult(parser, null, null, args("10m 2h 1d 5s"), new ArgumentParser.ParseResult<>(Duration.ofSeconds(94205), 4));
        assertParserResult(parser, null, null, args("1w 2d 2h 10m 5s"), new ArgumentParser.ParseResult<>(
                Duration.ofDays(7)
                        .plus(Duration.ofDays(2))
                        .plus(Duration.ofHours(2))
                        .plus(Duration.ofMinutes(10))
                        .plus(Duration.ofSeconds(5)),
                5)
        );
        assertParserResult(parser, null, null, args("1y 1w 2d 2h 10m 5s"), new ArgumentParser.ParseResult<>(
                Duration.ofDays(7)
                        .plus(Duration.ofDays(365))
                        .plus(Duration.ofDays(2))
                        .plus(Duration.ofHours(2))
                        .plus(Duration.ofMinutes(10))
                        .plus(Duration.ofSeconds(5)),
                6)
        );
        assertParserResult(parser, null, null, args("1y 1w 2d 2h 10m 5s 10s"), new ArgumentParser.ParseResult<>(
                Duration.ofDays(7)
                        .plus(Duration.ofDays(365))
                        .plus(Duration.ofDays(2))
                        .plus(Duration.ofHours(2))
                        .plus(Duration.ofMinutes(10))
                        .plus(Duration.ofSeconds(5))
                        .plus(Duration.ofSeconds(10)),
                7)
        );
    }

    @Test
    void withRandomData() {
        DurationParser parser = new DurationParser();

        assertParserResult(parser, null, null, args("2h 10m 7meow"), new ArgumentParser.ParseResult<>(
                Duration.ofHours(2)
                        .plus(Duration.ofMinutes(10)),
                2)
        );

        assertParserResult(parser, null, null, args("2h 10m 6lol"), new ArgumentParser.ParseResult<>(
                Duration.ofHours(2)
                        .plus(Duration.ofMinutes(10)),
                2)
        );

        assertParserResult(parser, null, null, args("2h 10m 21453"), new ArgumentParser.ParseResult<>(
                Duration.ofHours(2)
                        .plus(Duration.ofMinutes(10)),
                2)
        );

        assertParserResult(parser, null, null, args("2h 10m 2i"), new ArgumentParser.ParseResult<>(
                Duration.ofHours(2)
                        .plus(Duration.ofMinutes(10)),
                2)
        );

        assertParserResult(parser, null, null, args("wow this is 6cool 2h 10m 6lol"), 4, new ArgumentParser.ParseResult<>(
                Duration.ofHours(2)
                        .plus(Duration.ofMinutes(10)),
                6)
        );
    }

    @Test
    void invalidDuration() {
        DurationParser parser = new DurationParser();

        assertNotParserResult(parser, null, null, args("meow"), null);
        assertNotParserResult(parser, null, null, new String[0], null);
        assertNotParserResult(new DurationParser("meow", 0, Map.of("lo", Duration.ofMinutes(5))), null, null, args("12a"), null);
        assertNotParserResult(new DurationParser("meow", 0, Map.of("lo", Duration.ofMinutes(5))), null, null, args("12aasd"), null);

        assertThrows(IllegalArgumentException.class, () -> new DurationParser("a", 0, Map.of()));
        assertThrows(IllegalArgumentException.class, () -> new DurationParser("a", 0, null));
    }

    @Test
    void tabCompletion() {
        DurationParser parser = new DurationParser();

        assertParserTabCompletion(parser, null, null, args(""), 0, Set.of("1", "2", "3", "4", "5", "6", "7", "8", "9"), 1);
        assertParserTabCompletion(parser, null, null, args("2"), 0, Set.of("20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "2y", "2w", "2d", "2h", "2m", "2s"), 1);
        assertParserTabCompletion(parser, null, null, args("2m"), 0, Set.of("2m"), 1);

        parser = new DurationParser(DurationParser.DEFAULT_KEYWORD, 0, Map.of(
                "months", Duration.ofDays(30),
                "minutes", Duration.ofMinutes(1),
                "mills", Duration.ofMillis(1),
                "s", Duration.ofSeconds(1)
        ));

        assertParserTabCompletion(parser, null, null, args(""), 0, Set.of("1", "2", "3", "4", "5", "6", "7", "8", "9"), 1);
        assertParserTabCompletion(parser, null, null, args("2"), 0, Set.of("20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "2months", "2minutes", "2mills", "2s"), 1);
        assertParserTabCompletion(parser, null, null, args("2m"), 0, Set.of("2months", "2minutes", "2mills"), 1);
        assertParserTabCompletion(parser, null, null, args("2mo"), 0, Set.of("2months"), 1);
        assertParserTabCompletion(parser, null, null, args("2mi"), 0, Set.of("2minutes", "2mills"), 1);
    }

}
