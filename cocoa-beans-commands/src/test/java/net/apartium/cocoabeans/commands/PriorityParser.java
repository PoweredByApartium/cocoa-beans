package net.apartium.cocoabeans.commands;

import net.apartium.cocoabeans.commands.parsers.ArgumentParser;

import java.util.*;

public class PriorityParser extends ArgumentParser<String> {

    public static final String DEFAULT_KEYWORD = "priority";

    private final Set<String> completions;
    private final int tabCompletionPriority;

    public PriorityParser(int parserPriority, int tabCompletionPriority, Set<String> completions) {
        this(DEFAULT_KEYWORD, parserPriority, tabCompletionPriority, completions);
    }

    public PriorityParser(String keyword, int parserPriority, int tabCompletionPriority, Set<String> completions) {
        super(keyword, String.class, parserPriority);
        this.tabCompletionPriority = tabCompletionPriority;
        this.completions = completions;
    }

    public PriorityParser(int parserPriority) {
        this(parserPriority, 0, Set.of());
    }

    public PriorityParser(String keyword, int parserPriority) {
        this(keyword, parserPriority, 0, Set.of());
    }

    @Override
    public Optional<ParseResult<String>> parse(CommandProcessingContext processingContext) {
        List<String> args = processingContext.args();
        int index = processingContext.index();

        if (index >= args.size())
            return Optional.empty();

        String arg = args.get(index);
        if (completions.contains(arg))
            return Optional.of(new ParseResult<>(arg, index + 1));

        return Optional.empty();
    }

    @Override
    public OptionalInt tryParse(CommandProcessingContext processingContext) {
        List<String> args = processingContext.args();
        int index = processingContext.index();

        if (index >= args.size())
            return OptionalInt.empty();

        if (completions.contains(args.get(index)))
            return OptionalInt.of(index + 1);

        return OptionalInt.empty();
    }

    @Override
    public Optional<TabCompletionResult> tabCompletion(CommandProcessingContext processingContext) {
        List<String> args = processingContext.args();
        int index = processingContext.index();

        if (index >= args.size())
            return Optional.empty();

        String prefix = args.get(index);
        Set<String> matching = new HashSet<>();
        for (String completion : completions) {
            if (completion.startsWith(prefix))
                matching.add(completion);
        }

        if (matching.isEmpty())
            return Optional.empty();

        return Optional.of(new TabCompletionResult(
                matching,
                index + 1,
                tabCompletionPriority
        ));
    }
}