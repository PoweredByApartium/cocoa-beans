package net.apartium.cocoabeans.commands.parsers.compound;

import net.apartium.cocoabeans.commands.Sender;
import net.apartium.cocoabeans.commands.TestSender;
import net.apartium.cocoabeans.commands.parsers.ArgumentParser;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.OptionalInt;
import java.util.Set;

import static net.apartium.cocoabeans.commands.parsers.ParserAssertions.*;

class RecipeParserTest {

    @Test
    void createRecipe() {
        RecipeParser parser = new RecipeParser();
        Sender sender = new TestSender();

        assertParserResult(
                parser, sender, "command", args("rice 16m"), 0,
                new ArgumentParser.ParseResult<>(new RecipeParser.Recipe("rice", Duration.ofMinutes(16), OptionalInt.empty()), 2)
        );

        assertParserResult(
                parser, sender, "command", args("rice 16m 5"), 0,
                new ArgumentParser.ParseResult<>(new RecipeParser.Recipe("rice", Duration.ofMinutes(16), OptionalInt.of(5)), 3)
        );

        assertParserResult(
                parser, sender, "command", args("rice 16m 30s 5"), 0,
                new ArgumentParser.ParseResult<>(new RecipeParser.Recipe("rice", Duration.ofSeconds(990), OptionalInt.of(5)), 4)
        );

        assertParserResult(
                parser, sender, "command", args("test1 rice 16m 5 test2 test3"), 1,
                new ArgumentParser.ParseResult<>(new RecipeParser.Recipe("rice", Duration.ofMinutes(16), OptionalInt.of(5)), 4)
        );
    }

    @Test
    void tabCompletionRecipe() {
        RecipeParser parser = new RecipeParser();
        Sender sender = new TestSender();

        assertParserTabCompletion(
                parser, sender, "command", new String[]{"rice", ""}, 0,
                Set.of("1", "2", "3", "4", "5", "6", "7", "8", "9"), 2
        );

        assertParserTabCompletion(
                parser, sender, "command", args("rice 5"), 0,
                Set.of("50", "51", "52", "53", "54", "55", "56", "57", "58", "59", "5s", "5m", "5h", "5d", "5w", "5y"), 2
        );

        assertParserTabCompletion(
                parser, sender, "command", args("rice 5h 3"), 0,
                Set.of("30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "3s", "3m", "3h", "3d", "3w", "3y"), 3
        );
    }

}
