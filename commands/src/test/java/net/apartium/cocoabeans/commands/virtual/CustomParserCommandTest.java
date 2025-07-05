package net.apartium.cocoabeans.commands.virtual;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import net.apartium.cocoabeans.CollectionHelpers;
import net.apartium.cocoabeans.commands.TestCommandManager;
import net.apartium.cocoabeans.commands.lexer.SimpleArgumentParserToken;
import net.apartium.cocoabeans.commands.parsers.DummyParser;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
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

        LoggerContext contextLog = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger logger = (Logger) LoggerFactory.getLogger(SimpleArgumentParserToken.class);
        TestLogHandler handler = new TestLogHandler();

        handler.setContext(contextLog);
        handler.start();

        logger.addAppender(handler);
        logger.setAdditive(false);

        commandManager.addVirtualCommand(definition, context -> true, new DummyParser());
        ILoggingEvent[] logs = Stream.of(handler.next(), handler.next(), handler.next())
                .sorted(Comparator.comparing(a -> ((String) a.getArgumentArray()[0])))
                .toList()
                .toArray(new ILoggingEvent[0]);

        handler.assertNoMoreRecords();

        ILoggingEvent log = logs[0];
        assertEquals(Level.WARN, log.getLevel());
        assertEquals("Parser not found for: {} & fallback to: {}", log.getMessage());
        assertTrue(CollectionHelpers.equalsArray(new String[]{"potion", "DummyParser"}, log.getArgumentArray()));

        log = logs[1];
        assertEquals(Level.WARN, log.getLevel());
        assertEquals("Parser not found for: {} & fallback to: {}", log.getMessage());
        assertTrue(CollectionHelpers.equalsArray(new String[]{"spell", "DummyParser"}, log.getArgumentArray()));

        log = logs[2];
        assertEquals(Level.WARN, log.getLevel());
        assertEquals("Parser not found for: {} & fallback to: {}", log.getMessage());
        assertTrue(CollectionHelpers.equalsArray(new String[]{"wizard", "DummyParser"}, log.getArgumentArray()));
    }

    static class TestLogHandler extends AppenderBase<ILoggingEvent> {
        final List<ILoggingEvent> records = new ArrayList<>();


        public ILoggingEvent next() {
            if (records.isEmpty())
                fail("No more records");

            return records.remove(0);
        }

        public void assertNoMoreRecords() {
            assertTrue(records.isEmpty());
        }

        @Override
        protected void append(ILoggingEvent iLoggingEvent) {
            records.add(iLoggingEvent);
        }
    }

}
