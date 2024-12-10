package net.apartium.cocoabeans.commands.parsers;

import net.apartium.cocoabeans.commands.CommandProcessingContext;
import net.apartium.cocoabeans.commands.TestCommandProcessingContext;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WrappedArgumentParserTest {

    @Test
    void testEquals() {
        ArgumentParser<Integer> parser = new IntParser(0);
        WrappedArgumentParser<Integer> wrapped = new WrappedArgumentParser<>(parser, 7, "test");

        assertEquals(wrapped, parser);

        assertEquals(parser.getArgumentType(), wrapped.getArgumentType());
        assertEquals("test", wrapped.getKeyword());
        assertEquals("int", parser.getKeyword());
        assertEquals(7, wrapped.getPriority());
        assertEquals(0, parser.getPriority());
    }

    @Test
    void testParse() {
        ArgumentParser<Integer> parser = new IntParser(0);
        WrappedArgumentParser<Integer> wrapped = new WrappedArgumentParser<>(parser, 7, "test");

        CommandProcessingContext processingContext = new TestCommandProcessingContext(null, null, List.of("123"), 0);
        assertEquals(parser.parse(processingContext), wrapped.parse(processingContext));

        processingContext = new TestCommandProcessingContext(null, null, List.of("72.1"), 0);
        assertEquals(parser.parse(processingContext), wrapped.parse(processingContext));

    }

    @Test
    void testTryParse() {
        ArgumentParser<Integer> parser = new IntParser(0);
        WrappedArgumentParser<Integer> wrapped = new WrappedArgumentParser<>(parser, 7, "test");

        CommandProcessingContext processingContext = new TestCommandProcessingContext(null, null, List.of("123"), 0);
        assertEquals(parser.tryParse(processingContext), wrapped.tryParse(processingContext));

        processingContext = new TestCommandProcessingContext(null, null, List.of("72.1"), 0);
        assertEquals(parser.tryParse(processingContext), wrapped.tryParse(processingContext));
    }

    @Test
    void testTabCompletion() {
        ArgumentParser<Integer> parser = new IntParser(0);
        WrappedArgumentParser<Integer> wrapped = new WrappedArgumentParser<>(parser, 7, "test");

        CommandProcessingContext processingContext = new TestCommandProcessingContext(null, null, List.of("123"), 0);
        assertEquals(parser.tabCompletion(processingContext), wrapped.tabCompletion(processingContext));

        processingContext = new TestCommandProcessingContext(null, null, List.of("72.1"), 0);
        assertEquals(parser.tabCompletion(processingContext), wrapped.tabCompletion(processingContext));
    }

}
