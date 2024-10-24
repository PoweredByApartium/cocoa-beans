package net.apartium.cocoabeans.commands.parsers;

import net.apartium.cocoabeans.commands.CommandProcessingContext;
import net.apartium.cocoabeans.commands.exception.BadCommandResponse;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;

/**
 * Long string parser
 * Parsers that let you press strings that are more than one word and are limited by using quotation marks
 * @see QuotedStringParser
 * @see ParagraphParser
 */
@ApiStatus.AvailableSince("0.0.36")
/* package-private */ class LongStringParser extends ArgumentParser<String> {

    public static final String DEFAULT_KEYWORD = "long-string";

    private final boolean paragraphMode;

    /**
     * Creates new LongStringParser
     * @param priority priority of the parser
     * @param keyword keyword that should be used
     * @param paragraphMode paragraph mode is whether should string have next line or not
     */
    public LongStringParser(int priority, String keyword, boolean paragraphMode) {
        super(keyword, String.class, priority);
        this.paragraphMode = paragraphMode;
    }

    /**
     * Creates new LongStringParser
     * @param priority priority of the parser
     * @param keyword keyword that should be used
     */
    public LongStringParser(int priority, String keyword) {
        this(priority, keyword, false);
    }

    /**
     * Creates new LongStringParser
     * @param priority priority of the parser
     * @param paragraphMode paragraph mode is whether should string have next line or not
     */
    public LongStringParser(int priority, boolean paragraphMode) {
        this(priority, DEFAULT_KEYWORD, paragraphMode);
    }

    /**
     * Creates new LongStringParser
     * @param priority priority of the parser
     */
    public LongStringParser(int priority) {
        this(priority, DEFAULT_KEYWORD);
    }


    @Override
    public Optional<ParseResult<String>> parse(CommandProcessingContext processingContext) {
        List<String> args = processingContext.args();
        int index = processingContext.index();

        if (index >= args.size())
            return Optional.empty();


        if (args.get(index).isEmpty())
            return Optional.of(new ParseResult<>(
                    "",
                    index + 1
            ));

        int lastIndex = index;

        StringBuilder resultBuilder = new StringBuilder();
        String firstArg = args.get(index);
        boolean quoted = firstArg.charAt(0) == '"';

        Deque<Character> escapeCharacters = new ArrayDeque<>();


        if (quoted) {
            escapeCharacters.addLast('"');
            firstArg = firstArg.substring(1);
        }

        for (int i = index; i < args.size(); i++) {
            if (!quoted && i != index)
                break;

            String arg = i == index ? firstArg : args.get(i);
            lastIndex = i;

            ProcessResult processResult = processArg(processingContext, arg, resultBuilder, escapeCharacters);
            if (processResult == ProcessResult.FAILED)
                return Optional.empty();

            if (processResult == ProcessResult.FINISHED)
                break;
        }

        if (!escapeCharacters.isEmpty()) {
            reportError(processingContext, "Invalid quoted string");
            return Optional.empty();
        }

        String result;
        if (resultBuilder.isEmpty())
            result = "";
        else if (resultBuilder.charAt(resultBuilder.length() - 1) == ' ')
            result = resultBuilder.substring(0, resultBuilder.length() - 1);
        else
            result = resultBuilder.toString();

        return Optional.of(new ParseResult<>(
                result,
            lastIndex + 1
        ));
    }

    private ProcessResult processArg(CommandProcessingContext processingContext, String arg, StringBuilder resultBuilder, Deque<Character> escapeCharacters) {
        for (int i = 0; i < arg.length(); i++) {
            char currentChar = arg.charAt(i);

            if (currentChar == '"') {
                ProcessResult processResult = handleQuote(processingContext, resultBuilder, escapeCharacters, arg, i);
                if (processResult == ProcessResult.CONTINUE)
                    continue;

                return processResult;
            }

            if (currentChar == '\\') {
                handleBackSlash(resultBuilder, escapeCharacters);
                continue;
            }

            if (paragraphMode && currentChar == 'n') {
                ProcessResult processResult = handleNewLine(resultBuilder, escapeCharacters);
                if (processResult != ProcessResult.IGNORE) {
                    if ((arg.length() - 1) == i)
                        return ProcessResult.CONTINUE;

                    continue;
                }
            }

            resultBuilder.append(currentChar);
        }

        resultBuilder.append(" ");
        return ProcessResult.CONTINUE;
    }

    private ProcessResult handleQuote(CommandProcessingContext processingContext, StringBuilder resultBuilder, Deque<Character> escapeCharacters, String arg, int currentIndex) {
        if (escapeCharacters.isEmpty()) {
            reportError(processingContext, "Invalid quoted string");
            return ProcessResult.FAILED;
        }

        return switch (escapeCharacters.peekLast()) {
            case '"' -> {
                escapeCharacters.removeLast();
                if (!escapeCharacters.isEmpty()) {
                    reportError(processingContext, "Invalid quoted string");
                    yield ProcessResult.FAILED;
                }

                if ((arg.length() - 1) != currentIndex) {
                    reportError(processingContext, "Invalid quoted string in the middle");
                    yield ProcessResult.FAILED;
                }

                resultBuilder.append(" ");
                yield ProcessResult.FINISHED;
            }

            case '\\' -> {
                escapeCharacters.removeLast();
                resultBuilder.append('"');
                yield ProcessResult.CONTINUE;
            }

            default -> {
                reportError(processingContext, "Invalid quoted string");
                yield ProcessResult.FAILED;
            }
        };
    }

    private void handleBackSlash(StringBuilder resultBuilder, Deque<Character> escapeCharacters) {
        if (!escapeCharacters.isEmpty() && escapeCharacters.peekLast() == '\\') {
            escapeCharacters.removeLast();
            resultBuilder.append('\\');
            return;
        }

        escapeCharacters.addLast('\\');
    }

    private ProcessResult handleNewLine(StringBuilder resultBuilder, Deque<Character> escapeCharacters) {
        if (!escapeCharacters.isEmpty() && escapeCharacters.peekLast() == '\\') {
            escapeCharacters.removeLast();
            resultBuilder.append("\n");
            return ProcessResult.CONTINUE;
        }

        return ProcessResult.IGNORE;
    }

    private void reportError(CommandProcessingContext processingContext, String message) {
        processingContext.report(
                this,
                new BadCommandResponse(processingContext.label(), processingContext.args().toArray(new String[0]), processingContext.index(), message)
        );
    }

    private enum ProcessResult {
        FAILED,
        IGNORE,
        CONTINUE,
        FINISHED
    }

    @Override
    public OptionalInt tryParse(CommandProcessingContext processingContext) {
        return parse(processingContext)
                .map(ParseResult::newIndex)
                .map(OptionalInt::of)
                .orElse(OptionalInt.empty());
    }

    @Override
    public Optional<TabCompletionResult> tabCompletion(CommandProcessingContext processingContext) {
        OptionalInt optionalInt = tryParse(processingContext);

        if (optionalInt.isEmpty())
            return Optional.empty();

        return Optional.of(new TabCompletionResult(
                Set.of(),
                optionalInt.getAsInt()
        ));
    }
}
