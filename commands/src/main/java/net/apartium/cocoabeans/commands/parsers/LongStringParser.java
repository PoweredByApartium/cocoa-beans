package net.apartium.cocoabeans.commands.parsers;

import net.apartium.cocoabeans.commands.CommandProcessingContext;
import net.apartium.cocoabeans.commands.exception.BadCommandResponse;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;

/**
 * Long string parser
 * Parsers that let you press string that is more than one word and has a limit by using quotation marks
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

        int lastIndex = index;
        if (index >= args.size() || args.get(index).isEmpty())
            return Optional.empty();

        StringBuilder sb = new StringBuilder();
        boolean quoted = args.get(index).charAt(0) == '"';
        Deque<Character> characters = new ArrayDeque<>();

        outerLoop: for (int i = index; i < args.size(); i++) {
            if (!quoted && i != index)
                break;

            String s = args.get(i);
            lastIndex = i;

            for (int j = 0; j < s.length(); j++) {
                char c = s.charAt(j);
                if (c == '"' && i == index && j == 0) {
                    characters.addLast('"');
                    continue;
                }

                if (c == '"') {
                    if (characters.isEmpty()) {
                        processingContext.report(
                                this,
                                new BadCommandResponse(processingContext.label(), processingContext.args().toArray(new String[0]), index, "Invalid quoted string")
                        );

                        return Optional.empty();
                    }

                    if (characters.peekLast() == '"') {
                        characters.removeLast();
                        if (!characters.isEmpty()) {
                            processingContext.report(
                                    this,
                                    new BadCommandResponse(processingContext.label(), processingContext.args().toArray(new String[0]), index, "Invalid quoted string")
                            );
                            return Optional.empty();
                        }

                        if ((s.length() - 1) != j) {
                            processingContext.report(
                                    this,
                                    new BadCommandResponse(processingContext.label(), processingContext.args().toArray(new String[0]), index, "Invalid quoted string in the middle")
                            );
                            return Optional.empty();
                        }

                        break;
                    }

                    if (characters.peekLast() == '\\') {
                        characters.removeLast();
                        sb.append(c);
                        continue;
                    }

                    processingContext.report(
                            this,
                            new BadCommandResponse(processingContext.label(), processingContext.args().toArray(new String[0]), index, "Invalid quoted string\nCharacter: " + c)
                    );
                    return Optional.empty();

                }

                if (c == '\\') {
                    if (!characters.isEmpty() && characters.peekLast() == '\\') {
                        characters.removeLast();
                        sb.append(c);
                        continue;
                    }

                    characters.addLast(c);
                    continue;
                }

                if (paragraphMode && c == 'n') {
                    if (!characters.isEmpty() && characters.peekLast() == '\\') {
                        characters.removeLast();
                        sb.append('\n');
                        if (j == (s.length() - 1))
                            continue outerLoop;

                        continue;
                    }
                }

                sb.append(c);
            }

            sb.append(' ');
        }

        if (!characters.isEmpty()) {
            processingContext.report(
                    this,
                    new BadCommandResponse(processingContext.label(), processingContext.args().toArray(new String[0]), index, "Invalid quoted string")
            );
            return Optional.empty();
        }

        return Optional.of(new ParseResult<>(
            sb.substring(0, sb.length() - 1),
            lastIndex + 1
        ));
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
