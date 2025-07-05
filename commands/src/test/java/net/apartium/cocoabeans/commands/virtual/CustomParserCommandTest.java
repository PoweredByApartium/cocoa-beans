package net.apartium.cocoabeans.commands.virtual;

import net.apartium.cocoabeans.commands.CommandManager;
import net.apartium.cocoabeans.commands.TestCommandManager;
import net.apartium.cocoabeans.commands.parsers.DummyParser;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class CustomParserCommandTest {

    @Test
    void simpleTestOfConverting() {
        CustomParserCommand customParserCommand = new CustomParserCommand();

        VirtualCommandFactory virtualCommandFactory = new VirtualCommandFactory();
        VirtualCommandDefinition definition = virtualCommandFactory.create(customParserCommand);

        assertEquals(3, definition.variants().size());

        assertTrue(definition.variants().stream().anyMatch(v -> v.variant().equals("use <spell>")));
        assertTrue(definition.variants().stream().anyMatch(v -> v.variant().equals("kill <wizard>")));
        assertTrue(definition.variants().stream().anyMatch(v -> v.variant().equals("create <potion>")));
    }

    @Test
    void registerWithNoFallback() {
        CustomParserCommand customParserCommand = new CustomParserCommand();

        VirtualCommandFactory virtualCommandFactory = new VirtualCommandFactory();
        VirtualCommandDefinition definition = virtualCommandFactory.create(customParserCommand);

        TestCommandManager commandManager = new TestCommandManager();

        assertThrows(IllegalArgumentException.class, () -> commandManager.addVirtualCommand(definition, context -> true));
    }

    @Test
    void registerWithFallback() {
        CustomParserCommand customParserCommand = new CustomParserCommand();

        VirtualCommandFactory virtualCommandFactory = new VirtualCommandFactory();
        VirtualCommandDefinition definition = virtualCommandFactory.create(customParserCommand);

        TestCommandManager commandManager = new TestCommandManager();

        Logger logger = Logger.getLogger(CommandManager.LOGGER_NAME);
        TestLogHandler handler = new TestLogHandler();
        logger.addHandler(handler);
        logger.setUseParentHandlers(false);

        commandManager.addVirtualCommand(definition, context -> true, new DummyParser());
        LogRecord[] logs = Stream.of(handler.next(), handler.next(), handler.next())
                .sorted((a, b) -> a.getMessage().compareTo(b.getMessage()))
                .toList()
                .toArray(new LogRecord[0]);

        handler.assertNoMoreRecords();

        LogRecord log = logs[0];
        assertEquals(Level.WARNING, log.getLevel());
        assertEquals("Parser not found for: potion & fallback to: DummyParser", log.getMessage());

        log = logs[1];
        assertEquals(Level.WARNING, log.getLevel());
        assertEquals("Parser not found for: spell & fallback to: DummyParser", log.getMessage());

        log = logs[2];
        assertEquals(Level.WARNING, log.getLevel());
        assertEquals("Parser not found for: wizard & fallback to: DummyParser", log.getMessage());
    }

    static class TestLogHandler extends Handler {
        final List<LogRecord> records = new ArrayList<>();

        @Override
        public void publish(LogRecord record) {
            records.add(record);
        }

        public LogRecord next() {
            if (records.isEmpty())
                fail("No more records");

            return records.remove(0);
        }

        public void assertNoMoreRecords() {
            assertTrue(records.isEmpty());
        }

        @Override public void flush() {}
        @Override public void close() throws SecurityException {}
    }

}
