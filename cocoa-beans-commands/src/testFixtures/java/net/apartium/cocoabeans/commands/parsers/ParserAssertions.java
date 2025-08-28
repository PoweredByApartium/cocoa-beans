package net.apartium.cocoabeans.commands.parsers;

import net.apartium.cocoabeans.CollectionHelpers;
import net.apartium.cocoabeans.commands.CommandProcessingContext;
import net.apartium.cocoabeans.commands.Sender;
import net.apartium.cocoabeans.commands.TestCommandProcessingContext;
import net.apartium.cocoabeans.commands.exception.BadCommandResponse;
import org.jetbrains.annotations.ApiStatus;
import org.junit.jupiter.api.AssertionFailureBuilder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Parser assertions is a collection of assertions for parsers
 * @see ParserAssertions#assertParserResult(ArgumentParser, Sender, String, String[], ArgumentParser.ParseResult)
 */
@ApiStatus.AvailableSince("0.0.36")
public class ParserAssertions {

    /**
     * Checks if the parser result is as expected without any report of errors parsing
     * @param parser the parser itself
     * @param sender the sender who sent the command
     * @param label the label of the command
     * @param args the args of the command
     * @param expected the expected result with expected index
     */
    public static void assertParserResult(ArgumentParser<?> parser, Sender sender, String label, String[] args, ArgumentParser.ParseResult<?> expected) {
        assertParserResult(parser, sender, label, args, 0, expected);
    }

    /**
     * Checks if the parser result is as expected without any report of errors parsing
     * @param parser the parser itself
     * @param sender the sender who sent the command
     * @param label the label of the command
     * @param args the args of the command
     * @param startIndex where the parser should start parsing
     * @param expected the expected result with expected index
     */
    public static void assertParserResult(ArgumentParser<?> parser, Sender sender, String label, String[] args, int startIndex, ArgumentParser.ParseResult<?> expected) {
        assertParserResult(parser, sender, label, args, startIndex, expected, null);
    }


    /**
     * Checks if the parser result is as expected without any report of errors parsing
     * @param parser the parser itself
     * @param sender the sender who sent the command
     * @param label the label of the command
     * @param args the args of the command
     * @param startIndex where the parser should start parsing
     * @param expected the expected result with expected index
     * @param message the message to report
     */
    public static void assertParserResult(ArgumentParser<?> parser, Sender sender, String label, String[] args, int startIndex, ArgumentParser.ParseResult<?> expected, String message) {
        assertParserResult(parser, new TestCommandProcessingContext(sender, label, List.of(args), startIndex), expected, message);
    }

    /**
     * Checks if the parser result is as expected without any report of errors parsing
     * @param parser the parser itself
     * @param processingContext the context of the command
     * @param expected the expected result with expected index
     * @param message the message to report
     */
    public static void assertParserResult(ArgumentParser<?> parser, CommandProcessingContext processingContext, ArgumentParser.ParseResult<?> expected, String message) {
        TestCommandProcessingContext context = new TestCommandProcessingContext(processingContext.sender(), processingContext.label(), processingContext.args(), processingContext.index());

        Optional<? extends ArgumentParser.ParseResult<?>> parse = parser.parse(context);

        if (parse.isEmpty()) {
            if (!context.hasAnyReports()) {
                failMessage("Parser did not return a result or report any errors", message, expected, null);
                return;
            }

            List<BadCommandResponse> reports = context.getReports();
            failMessage("Parser failed with " + reports.size() + " report" + (reports.size() == 1 ? "" : "s"), message, expected, reports);
            return;
        }

        if (context.hasAnyReports()) {
            List<BadCommandResponse> reports = context.getReports();
            failMessage("Parser produced unexpected error reports", message, expected, reports);
            return;
        }

        ArgumentParser.ParseResult<?> result = parse.get();

        if (expected.newIndex() != result.newIndex()) {
            failMessage("Expected different index", message, expected.newIndex(), result.newIndex());
            return;
        }

        Object actual = result.result();

        if (!Objects.equals(expected.result(), actual)) {
            failMessage("Expected different result", message, expected.result(), actual);
            return;
        }
    }

    /**
     * Checks if the try parse is as expected without any report of errors try parsing
     * @param parser the parser itself
     * @param sender the sender who sent the command
     * @param label the label of the command
     * @param args the args of the command
     * @param expected the expected result
     */
    public static void assertTryParseResult(ArgumentParser<?> parser, Sender sender, String label, String[] args, OptionalInt expected) {
        assertTryParseResult(parser, sender, label, args, 0, expected);
    }

    /**
     * Checks if the try parse is as expected without any report of errors try parsing
     * @param parser the parser itself
     * @param sender the sender who sent the command
     * @param label the label of the command
     * @param args the args of the command
     * @param startIndex where the parser should start trying to parse
     * @param expected the expected result
     */
    public static void assertTryParseResult(ArgumentParser<?> parser, Sender sender, String label, String[] args, int startIndex, OptionalInt expected) {
        assertTryParseResult(parser, sender, label, args, startIndex, expected, null);
    }

    /**
     * Checks if the try parse is as expected without any report of errors try parsing
     * @param parser the parser itself
     * @param sender the sender who sent the command
     * @param label the label of the command
     * @param args the args of the command
     * @param startIndex where the parser should start trying to parse
     * @param expected the expected result
     * @param message the message to report
     */
    public static void assertTryParseResult(ArgumentParser<?> parser, Sender sender, String label, String[] args, int startIndex, OptionalInt expected, String message) {
        assertTryParseResult(parser, new TestCommandProcessingContext(sender, label, List.of(args), startIndex), expected, message);
    }

    /**
     * Check if the try parse is as expected without any report of errors try parsing
     * @param parser the parser itself
     * @param processingContext the context of the command
     * @param expected the expected result
     * @param message the message to report
     */
    public static void assertTryParseResult(ArgumentParser<?> parser, CommandProcessingContext processingContext, OptionalInt expected, String message) {
        TestCommandProcessingContext context;
        if (!(processingContext instanceof TestCommandProcessingContext)) {
            context = new TestCommandProcessingContext(processingContext.sender(), processingContext.label(), processingContext.args(), processingContext.index());
        } else {
            context = (TestCommandProcessingContext) processingContext;
        }

        OptionalInt result = parser.tryParse(processingContext);

        if (result.isEmpty()) {
            if (expected.isEmpty())
                return; // success

            List<BadCommandResponse> reports = context.getReports();
            if (reports.isEmpty()) {
                failMessage("Parser did not return a result or report any errors", message, expected, null);
                return;
            }

            failMessage("Parser failed with " + reports.size() + " report" + (reports.size() == 1 ? "" : "s"), message, expected, reports);
            return;
        }

        if (expected.isEmpty()) {
            List<BadCommandResponse> reports = context.getReports();
            if (!reports.isEmpty()) {
                failMessage("Parser produced unexpected error reports", message, expected, reports);
                return;
            }

            failMessage("Parser returned unexpected result", message, expected, result);
            return;
        }

        if (expected.getAsInt() != result.getAsInt()) {
            failMessage("Expected different result", message, expected.getAsInt(), result.getAsInt());
            return;
        }
    }

    /**
     * Checks if the parser result is as not expected without any report of errors parsing
     * @param parser the parser itself
     * @param sender the sender who sent the command
     * @param label the label of the command
     * @param args the args of the command
     * @param expected the expected result with expected index
     */
    public static void assertNotParserResult(ArgumentParser<?> parser, Sender sender, String label, String[] args, ArgumentParser.ParseResult<?> expected) {
        assertNotParserResult(parser, sender, label, args, 0, expected);
    }

    /**
     * Checks if the parser result is as not expected without any report of errors parsing
     * @param parser the parser itself
     * @param sender the sender who sent the command
     * @param label the label of the command
     * @param args the args of the command
     * @param startIndex where the parser should start parsing
     * @param expected the expected result with expected index
     */
    public static void assertNotParserResult(ArgumentParser<?> parser, Sender sender, String label, String[] args, int startIndex,  ArgumentParser.ParseResult<?> expected) {
        assertNotParserResult(parser, sender, label, args, startIndex, expected, null);
    }

    /**
     * Checks if the parser result is as not expected without any report of errors parsing
     * @param parser the parser itself
     * @param sender the sender who sent the command
     * @param label the label of the command
     * @param args the args of the command
     * @param startIndex where the parser should start parsing
     * @param expected the expected result with expected index
     * @param message the message to report
     */
    public static void assertNotParserResult(ArgumentParser<?> parser, Sender sender, String label, String[] args, int startIndex, ArgumentParser.ParseResult<?> expected, String message) {
        TestCommandProcessingContext context = new TestCommandProcessingContext(sender, label, List.of(args), startIndex);

        Optional<? extends ArgumentParser.ParseResult<?>> parse = parser.parse(context);

        if (parse.isPresent()) {
            if (context.hasAnyReports()) {
                List<BadCommandResponse> reports = context.getReports();
                failMessage("Parser produced unexpected error reports", message, expected, reports);
                return;
            }

            ArgumentParser.ParseResult<?> result = parse.get();
            if (expected.newIndex() == result.newIndex() && Objects.equals(expected.result(), result.result())) {
                failMessage("Expected different result", message, expected.result(), result.result());
                return;
            }

            return;
        }

        if (context.hasAnyReports()) {
            List<BadCommandResponse> reports = context.getReports();
            failMessage("Parser unexpectedly failed with " + reports.size() + " report" + (reports.size() == 1 ? "" : "s"), message, expected, reports);
            return;
        }
    }

    /**
     * Checks if the parser throws the expected exception
     * @param parser the parser
     * @param sender the sender
     * @param label the label
     * @param args the args
     * @param expected the expected exception
     */
    public static void assertParserThrowsReport(ArgumentParser<?> parser, Sender sender, String label, String[] args, Class<? extends BadCommandResponse> expected) {
        assertParserThrowsReport(parser, sender, label, args, 0, expected);
    }

    /**
     * Checks if the parser throws the expected exception
     * @param parser the parser
     * @param sender the sender
     * @param label the label
     * @param args the args
     * @param startIndex where the parser should start parsing
     * @param expected the expected exception
     */
    public static void assertParserThrowsReport(ArgumentParser<?> parser, Sender sender, String label, String[] args, int startIndex, Class<? extends BadCommandResponse> expected) {
        assertParserThrowsReport(parser, sender, label, args, startIndex, expected, null);
    }

    /**
     * Checks if the parser throws the expected exception
     * @param parser the parser
     * @param sender the sender
     * @param label the label
     * @param args the args
     * @param startIndex where the parser should start parsing
     * @param expected the expected exception
     * @param message the message
     */
    public static void assertParserThrowsReport(ArgumentParser<?> parser, Sender sender, String label, String[] args, int startIndex, Class<? extends BadCommandResponse> expected, String message) {
        assertParserThrowsReport(parser, sender, label, args, startIndex, List.of(expected), message);
    }

    public static void assertParserThrowsReport(ArgumentParser<?> parser, Sender sender, String label, String[] args, int startIndex, List<Class<? extends BadCommandResponse>> expected, String message) {
        TestCommandProcessingContext context = new TestCommandProcessingContext(sender, label, List.of(args), startIndex);
        parser.parse(context);

        if (!context.hasAnyReports()) {
            failMessage("Expected parser to fail but no error report has been provided", message, expected, null);
            return;
        }

        List<BadCommandResponse> reports = context.getReports();

        if (reports.size() != expected.size()) {
            failMessage("Reports aren't the same as expected", message, expected, reports);
            return;
        }

        if (CollectionHelpers.equalsList(reports.stream().map(BadCommandResponse::getClass).toList(), expected))
            return;

        failMessage("Reports aren't the same as expected", message, expected, reports);
    }

    /**
     *
     * @param parser
     * @param sender
     * @param label
     * @param args
     * @param startIndex
     * @param expected
     */
    @ApiStatus.AvailableSince("0.0.37")
    public static void assertParserTabCompletion(ArgumentParser<?> parser, Sender sender, String label, String[] args, int startIndex, Set<String> expected, int expectedIndex) {
        assertParserTabCompletion(parser, sender, label, args, startIndex, expected, expectedIndex, null);
    }

    /**
     *
     * @param parser
     * @param sender
     * @param label
     * @param args
     * @param startIndex
     * @param expected
     * @param message
     */
    @ApiStatus.AvailableSince("0.0.37")
    public static void assertParserTabCompletion(ArgumentParser<?> parser, Sender sender, String label, String[] args, int startIndex, Set<String> expected, int expectedIndex, String message) {
        TestCommandProcessingContext context = new TestCommandProcessingContext(sender, label, List.of(args), startIndex);
        Optional<ArgumentParser.TabCompletionResult> tabCompletionResult = parser.tabCompletion(context);

        if (context.hasAnyReports()) {
            List<BadCommandResponse> reports = context.getReports();
            failMessage("Parser produced unexpected error reports", message, expected, reports);
            return;
        }

        if (expected == null) {
            if (tabCompletionResult.isPresent())
                failMessage("Expected no tab completion", message, null, tabCompletionResult.get().result());
            return;
        }

        if (tabCompletionResult.isEmpty()) {
            failMessage("Expected tab completion", message, expected, null);
            return;
        }

        Set<String> result = tabCompletionResult.get().result();
        assertEquals(expected, result, message);
        assertEquals(expectedIndex, tabCompletionResult.get().newIndex(), message);
    }

    @ApiStatus.Internal
    private static void failMessage(String reason, String message, Object expected, Object actual) {
        AssertionFailureBuilder.assertionFailure().reason(reason).message(message).expected(expected).actual(actual).buildAndThrow();
    }

    /**
     * Convert string to command args
     * @param args the string to convert
     * @return command args output
     */
    @ApiStatus.AvailableSince("0.0.39")
    public static String[] args(String args) {
        return args.split("\\s+");
    }


}
