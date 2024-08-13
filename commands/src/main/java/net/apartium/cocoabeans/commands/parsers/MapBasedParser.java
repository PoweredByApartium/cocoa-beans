package net.apartium.cocoabeans.commands.parsers;

import net.apartium.cocoabeans.commands.CommandProcessingContext;
import net.apartium.cocoabeans.commands.exception.BadCommandResponse;
import net.apartium.cocoabeans.commands.parsers.exception.AmbiguousMappedKeyResponse;
import net.apartium.cocoabeans.commands.parsers.exception.InvalidParserResponse;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;
import java.util.function.Supplier;

/**
 * Map based parser that map of keyword to value
 *
 * @see SourceParser
 * @param <T> result type
 */
public abstract class MapBasedParser<T> extends ArgumentParser<T> {

    private final boolean ignoreCase;
    private final boolean lax;

    /**
     * Constructor
     * @param keyword keyword
     * @param clazz result class
     * @param priority priority
     */
    public MapBasedParser(String keyword, Class<T> clazz, int priority) {
        this(keyword, clazz, priority, false);
    }

    /**
     * Constructor with ignore case
     * @param keyword keyword
     * @param clazz result class
     * @param priority priority
     * @param ignoreCase ignore case
     */
    @ApiStatus.AvailableSince("0.0.30")
    public MapBasedParser(String keyword, Class<T> clazz, int priority, boolean ignoreCase) {
        this(keyword, clazz, priority, ignoreCase, false);
    }

    /**
     * Constructor with ignore case and lax
     * @param keyword keyword
     * @param clazz result class
     * @param priority priority
     * @param ignoreCase ignore case
     * @param lax lazy mapping
     */
    @ApiStatus.AvailableSince("0.0.30")
    public MapBasedParser(String keyword, Class<T> clazz, int priority, boolean ignoreCase, boolean lax) {
        super(keyword, clazz, priority);

        this.ignoreCase = ignoreCase;
        this.lax = lax;
    }

    /**
     * If you want to ignore case to be applied don't forgot to lowercase the keyword
     * @return map of keyword to value
     */
    public abstract Map<String, T> getMap();

    @Override
    public Optional<ParseResult<T>> parse(CommandProcessingContext commandProcessingContext) {
        List<String> args = commandProcessingContext.args();
        int index = commandProcessingContext.index();

        Map<String, T> map = getMap();
        String s = "";

        for (int i = index; i < args.size(); i++) {
            if (i != index)
                s += " ";

            s += args.get(index);

            if (ignoreCase)
                s = s.toLowerCase();

            T value = map.get(s);
            if (value == null) {
                if (!lax)
                    continue;

                value = getLax(commandProcessingContext, s);
                if (value == null)
                    continue;
            }

            return Optional.of(new ParseResult<>(
                    value,
                    i + 1
            ));
        }

        return Optional.empty();
    }

    private T getLax(CommandProcessingContext context, String s) {
        T result = null;
        List<String> dupeKeys = new ArrayList<>();

        for (var entry : getMap().entrySet()) {
            if (!entry.getKey().startsWith(s))
                continue;

            if (result != null) {
                dupeKeys.add(entry.getKey());
                continue;
            }

            result = entry.getValue();
            dupeKeys.add(entry.getKey());
        }

        if (dupeKeys.isEmpty())
            return null;

        if (dupeKeys.size() != 1) {
            context.report(
                    this,
                    new AmbiguousMappedKeyResponse(
                            context,
                            this,
                            "Did you mean " + dupeKeys.get(0) + "?",
                            dupeKeys
                    )
            );
            return null;
        }

        return result;
    }

    @Override
    public OptionalInt tryParse(CommandProcessingContext commandProcessingContext) {
        return parse(commandProcessingContext)
                .map(ParseResult::newIndex)
                .map(OptionalInt::of)
                .orElse(OptionalInt.empty());
    }

    @Override
    public Optional<TabCompletionResult> tabCompletion(CommandProcessingContext commandProcessingContext) {
        List<String> args = commandProcessingContext.args();
        int index = commandProcessingContext.index();

        Set<String> keys = getMap().keySet();

        Set<String> result = new HashSet<>();

        String s = String.join(" ", args.subList(index, args.size()));

        for (String key : keys) {
            if (!key.toLowerCase().startsWith(s.toLowerCase()))
                continue;

            result.add(key);
        }

        if (result.isEmpty())
            return Optional.empty();

        return Optional.of(new TabCompletionResult(
                result,
                args.size() + 1
        ));

    }

}
