package net.apartium.cocoabeans.commands.parsers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Map;

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

}
