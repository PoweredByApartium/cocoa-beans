package net.apartium.cocoabeans.commands.parsers;

import net.apartium.cocoabeans.commands.*;
import net.apartium.cocoabeans.commands.lexer.SimpleCommandLexer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.logging.Logger;

import static net.apartium.cocoabeans.commands.parsers.ParserAssertions.assertParserResult;
import static net.apartium.cocoabeans.commands.parsers.ParserAssertions.assertParserTabCompletion;
import static org.junit.jupiter.api.Assertions.*;

class BadParserTest {

    @Test
    void parse() {
        BadParser parser = new BadParser();

        assertParserResult(parser, null, null, new String[0], new ArgumentParser.ParseResult<>("wow", 0));
        assertParserResult(parser, null, null, new String[0], 13, new ArgumentParser.ParseResult<>("wow", 13));
    }

    @Test
    void tabCompletion() {
        BadParser parser = new BadParser();

        assertParserTabCompletion(parser, null, null, new String[0], 0, Set.of("wow"), 0);
        assertParserTabCompletion(parser, null, null, new String[0], 62, Set.of("wow"), 62);
    }

    @Test
    void handleCommandManager() {
        TestSender sender = new TestSender();
        BadCommandParser command = new BadCommandParser();

        assertThrows(IllegalAccessException.class, () -> command.badParse("why????"));

        CommandManager manager = new CommandManager(
                Logger.getLogger(BadParserTest.class.getName()),
                new SimpleArgumentMapper(),
                new SimpleCommandLexer()
        ) {
            @Override
            protected void addCommand(CommandNode commandNode, Command command) {

            }
        };
        manager.registerArgumentTypeHandler(new BadParser());
        manager.addCommand(command);

        try {
            manager.handle(sender, "bad", new String[]{"why2"});
            fail("The command should not be executed");
        } catch (RuntimeException e) {
            assertEquals("There is an exception with net.apartium.cocoabeans.commands.parsers.BadParser return new index that is smaller than current index", e.getMessage());
        } catch (Throwable e) {
            fail(e);
        }

    }

}
