package net.apartium.cocoabeans.commands.parsers;

import net.apartium.cocoabeans.commands.CommandProcessingContext;
import net.apartium.cocoabeans.commands.exception.BadCommandResponse;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Stack;

/**
 * Long string parser
 * Parsers that let you press string that is more than one word and has a limit by using quotation marks
 * @see QuotedStringParser
 * @see ParagraphParser
 */
@ApiStatus.AvailableSince("0.0.36")
public class LongStringParser extends ArgumentParser<String> {

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

        for (int i = index; i < args.size(); i++) {
            if (!quoted && i != index)
                break;

            Stack<Character> stack = new Stack<>();
            String s = args.get(i);
            lastIndex = i;

            for (int j = 0; j < s.length(); j++) {
                char c = s.charAt(j);
                if (c == '"' && i == index && j == 0) {
                    stack.push('"');
                    continue;
                }

                if (c == '"') {
                    if (stack.isEmpty()) {
                        processingContext.report(
                                this,
                                new BadCommandResponse(processingContext.label(), processingContext.args().toArray(new String[0]), index, "Invalid quoted string")
                        );

                        return Optional.empty();
                    }

                    if (stack.peek() == '"') {
                        stack.pop();
                        if (stack.isEmpty()) {
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

                    if (stack.peek() == '\\') {
                        stack.pop();
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
                    if (!stack.isEmpty() && stack.peek() == '\\') {
                        stack.pop();
                        sb.append(c);
                        continue;
                    }

                    stack.push(c);
                    continue;
                }

                if (paragraphMode && c == 'n') {
                    if (!stack.isEmpty() && stack.peek() == '\\') {
                        stack.pop();
                        sb.append('\n');
                        continue;
                    }
                }

                sb.append(c);
            }

            sb.append(' ');
        }

        return Optional.of(new ParseResult<>(
            sb.substring(0, sb.length() - 1),
            lastIndex + 1
        ));
    }

    @Override
    public OptionalInt tryParse(CommandProcessingContext processingContext) {
        return OptionalInt.empty();
    }

    @Override
    public Optional<TabCompletionResult> tabCompletion(CommandProcessingContext processingContext) {
        return Optional.empty();
    }
}
