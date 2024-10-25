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
    public static final char ESCAPE_CHARACTER = '\\';
    public static final char QUOTATION_CHARACTER = '"';

    public static final char NEW_LINE_CHARACTER = 'n';

    public static final String INVALID_QUOTATION_MESSAGE = "Invalid quoted string";

    private final boolean paragraphMode;

    /**
     * Creates new LongStringParser
     * @param priority priority of the parser
     * @param keyword keyword that should be used
     * @param paragraphMode paragraph mode is whether it should have next line or not
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


    /**
     * Tries to parse next argument in the context
     * @param processingContext cmd processing context
     * @return empty if failed, otherwise result
     */
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
        boolean quoted = firstArg.charAt(0) == QUOTATION_CHARACTER;

        Deque<Character> escapeCharacters = new ArrayDeque<>();


        if (quoted) {
            escapeCharacters.addLast(QUOTATION_CHARACTER);
            firstArg = firstArg.substring(1);
        }

        int length = !quoted ? index + 1 : args.size();
        for (int i = index; i < length; i++) {
            String arg = i == index ? firstArg : args.get(i);
            lastIndex = i;

            ProcessResult processResult = processArg(processingContext, arg, resultBuilder, escapeCharacters);
            if (processResult == ProcessResult.FAILED)
                return Optional.empty();

            if (processResult == ProcessResult.FINISHED)
                break;
        }

        if (!escapeCharacters.isEmpty()) {
            reportError(processingContext, INVALID_QUOTATION_MESSAGE);
            return Optional.empty();
        }


        return Optional.of(new ParseResult<>(
                toString(resultBuilder),
            lastIndex + 1
        ));
    }

    @ApiStatus.Internal
    private String toString(StringBuilder resultBuilder) {
        if (resultBuilder.isEmpty())
            return "";

        if (resultBuilder.charAt(resultBuilder.length() - 1) == ' ')
            return resultBuilder.substring(0, resultBuilder.length() - 1);

        return resultBuilder.toString();
    }


    @ApiStatus.Internal
    private ProcessResult processArg(CommandProcessingContext processingContext, String arg, StringBuilder resultBuilder, Deque<Character> escapeCharacters) {
        for (int i = 0; i < arg.length(); i++) {
            char currentChar = arg.charAt(i);

            if (currentChar == QUOTATION_CHARACTER) {
                ProcessResult processResult = handleQuote(processingContext, resultBuilder, escapeCharacters, arg, i);
                if (processResult == ProcessResult.CONTINUE)
                    continue;

                return processResult;
            }

            if (currentChar == ESCAPE_CHARACTER) {
                handleBackSlash(resultBuilder, escapeCharacters);
                continue;
            }

            if (paragraphMode && currentChar == NEW_LINE_CHARACTER) {
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

    @ApiStatus.Internal
    private ProcessResult handleQuote(CommandProcessingContext processingContext, StringBuilder resultBuilder, Deque<Character> escapeCharacters, String arg, int currentIndex) {
        if (escapeCharacters.isEmpty()) {
            reportError(processingContext, INVALID_QUOTATION_MESSAGE);
            return ProcessResult.FAILED;
        }

        return switch (escapeCharacters.peekLast()) {
            case QUOTATION_CHARACTER -> {
                escapeCharacters.removeLast();
                if (!escapeCharacters.isEmpty()) {
                    reportError(processingContext, INVALID_QUOTATION_MESSAGE);
                    yield ProcessResult.FAILED;
                }

                if ((arg.length() - 1) != currentIndex) {
                    reportError(processingContext, "Invalid quoted string in the middle");
                    yield ProcessResult.FAILED;
                }

                resultBuilder.append(" ");
                yield ProcessResult.FINISHED;
            }

            case ESCAPE_CHARACTER -> {
                escapeCharacters.removeLast();
                resultBuilder.append(QUOTATION_CHARACTER);
                yield ProcessResult.CONTINUE;
            }

            default -> {
                reportError(processingContext, INVALID_QUOTATION_MESSAGE);
                yield ProcessResult.FAILED;
            }
        };
    }

    @ApiStatus.Internal
    private void handleBackSlash(StringBuilder resultBuilder, Deque<Character> escapeCharacters) {
        if (!escapeCharacters.isEmpty() && escapeCharacters.peekLast() == ESCAPE_CHARACTER) {
            escapeCharacters.removeLast();
            resultBuilder.append(ESCAPE_CHARACTER);
            return;
        }

        escapeCharacters.addLast(ESCAPE_CHARACTER);
    }

    @ApiStatus.Internal
    private ProcessResult handleNewLine(StringBuilder resultBuilder, Deque<Character> escapeCharacters) {
        if (!escapeCharacters.isEmpty() && escapeCharacters.peekLast() == ESCAPE_CHARACTER) {
            escapeCharacters.removeLast();
            resultBuilder.append("\n");
            return ProcessResult.CONTINUE;
        }

        return ProcessResult.IGNORE;
    }

    @ApiStatus.Internal
    private void reportError(CommandProcessingContext processingContext, String message) {
        processingContext.report(
                this,
                new BadCommandResponse(processingContext.label(), processingContext.args().toArray(new String[0]), processingContext.index(), message)
        );
    }

    @ApiStatus.Internal
    private enum ProcessResult {
        FAILED,
        IGNORE,
        CONTINUE,
        FINISHED
    }

    /**
     * Tries to lazily parse next argument in the context
     * @param processingContext cmd processing context
     * @return new index int if success, empty if not
     */
    @Override
    public OptionalInt tryParse(CommandProcessingContext processingContext) {
        return parse(processingContext)
                .map(ParseResult::newIndex)
                .map(OptionalInt::of)
                .orElse(OptionalInt.empty());
    }

    /**
     * Retrieves available options for tab completion of this argument
     * @param processingContext cmd processing context
     * @return tab completion result if success, otherwise empty option
     */
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
