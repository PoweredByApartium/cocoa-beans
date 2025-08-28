package net.apartium.cocoabeans.commands.parsers;

import net.apartium.cocoabeans.commands.parsers.exception.AmbiguousMappedKeyResponse;
import net.apartium.cocoabeans.commands.parsers.exception.NoSuchElementInMapResponse;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

import java.util.List;
import java.util.Map;

import static net.apartium.cocoabeans.commands.parsers.ParserAssertions.assertParserResult;
import static net.apartium.cocoabeans.commands.parsers.ParserAssertions.assertParserThrowsReport;
import static org.junit.jupiter.api.Assertions.*;

public class MapBasedParserTest {

    @Test
    public void test() {
        MapBasedParser<GameMode> parser = new MapBasedParser<>("gamemode", GameMode.class, 0, true, true) {
            private final Map<String, GameMode> map = Map.of(
                    "survival", GameMode.SURVIVAL,
                    "creative", GameMode.CREATIVE,
                    "adventure", GameMode.ADVENTURE,
                    "spectator", GameMode.SPECTATOR
            );

            @Override
            public Map<String, GameMode> getMap() {
                return map;
            }
        };

        assertParserResult(parser, null, "test", new String[]{"creative"}, new ArgumentParser.ParseResult<>(GameMode.CREATIVE, 1));
        assertParserResult(parser, null, "test", new String[]{"sur"}, 0, new ArgumentParser.ParseResult<>(GameMode.SURVIVAL, 1));

        assertThrows(AssertionFailedError.class, () -> assertParserResult(parser, null, "test", new String[]{"survival"}, 0, new ArgumentParser.ParseResult<>(GameMode.SURVIVAL, 0)));
        assertThrows(AssertionFailedError.class, () -> assertParserResult(parser, null, "test", new String[]{"survivala"}, 0, new ArgumentParser.ParseResult<>(GameMode.SURVIVAL, 1)));

        try {
            assertParserResult(parser, null, "test", new String[]{"s"}, 0, new ArgumentParser.ParseResult<>(GameMode.SURVIVAL, 1));
        } catch (AssertionFailedError e) {
            Object value = e.getActual().getEphemeralValue();
            if (!(value instanceof List<?> list)) {
                fail();
                return;
            }

            assertEquals(1, list.size());
            assertEquals(AmbiguousMappedKeyResponse.class, list.get(0).getClass());
        }

        assertParserThrowsReport(parser, null, "test", new String[]{"s"}, 0, AmbiguousMappedKeyResponse.class);

        try {
            assertParserResult(parser, null, "test", new String[]{"sa"}, 0, new ArgumentParser.ParseResult<>(GameMode.SURVIVAL, 1));
        } catch (AssertionFailedError e) {
            Object value = e.getActual().getEphemeralValue();
            if (!(value instanceof List<?> list)) {
                fail();
                return;
            }

            assertEquals(1, list.size());
            assertEquals(NoSuchElementInMapResponse.class, list.get(0).getClass());
            assertEquals("sa", ((NoSuchElementInMapResponse) list.get(0)).getAttempted());
            assertEquals("sa", ((NoSuchElementInMapResponse) list.get(0)).getError().getAttempted());
        }
    }

    public enum GameMode {
        SURVIVAL,
        CREATIVE,
        ADVENTURE,
        SPECTATOR
    }

}