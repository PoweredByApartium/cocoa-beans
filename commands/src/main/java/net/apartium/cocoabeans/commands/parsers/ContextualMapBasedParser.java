package net.apartium.cocoabeans.commands.parsers;

import net.apartium.cocoabeans.commands.CommandProcessingContext;
import net.apartium.cocoabeans.commands.parsers.exception.AmbiguousMappedKeyResponse;
import net.apartium.cocoabeans.commands.parsers.exception.NoSuchElementInMapResponse;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;

/**
 * Contextual Map-based parser that map of keyword to value
 * When Map entry not found report {@link NoSuchElementInMapResponse}
 * @see MapBasedParser
 * @param <T> result type
 */
@ApiStatus.AvailableSince("0.0.39")
public abstract class ContextualMapBasedParser<T> extends ArgumentParser<T> {

    private final boolean ignoreCase;
    private final boolean lax;

    /**
     * Constructor
     * @param keyword keyword
     * @param clazz result class
     * @param priority priority
     */
    protected ContextualMapBasedParser(String keyword, Class<T> clazz, int priority) {
        this(keyword, clazz, priority, false);
    }

    /**
     * Constructor with an ignoreCase
     * @param keyword keyword
     * @param clazz result class
     * @param priority priority
     * @param ignoreCase whether is it case-sensitive or not
     */
    protected ContextualMapBasedParser(String keyword, Class<T> clazz, int priority, boolean ignoreCase) {
        this(keyword, clazz, priority, ignoreCase, false);
    }

    /**
     * Constructor with an ignoreCase and lax
     * @param keyword keyword
     * @param clazz result class
     * @param priority priority
     * @param ignoreCase whether is it case-sensitive or not
     * @param lax lazy mapping
     */
    protected ContextualMapBasedParser(String keyword, Class<T> clazz, int priority, boolean ignoreCase, boolean lax) {
        super(keyword, clazz, priority);

        this.ignoreCase = ignoreCase;
        this.lax = lax;
    }

    /**
     * If you want to ignore case to be applied, then remember to lowercase the keyword
     * @param context the context that you can use to change the map for it
     * @return map of keyword to value
     */
    public abstract Map<String, T> getMap(CommandProcessingContext context);

    @Override
    public Optional<ParseResult<T>> parse(CommandProcessingContext commandProcessingContext) {
        List<String> args = commandProcessingContext.args();
        int index = commandProcessingContext.index();

        Map<String, T> map = getMap(commandProcessingContext);
        StringBuilder sb = new StringBuilder();

        boolean ambiguous = false;
        for (int i = index; i < args.size(); i++) {
            if (i != index)
                sb.append(" ");

            sb.append(convertBaseOnIgnoreCase(args.get(i)));

            T value = map.get(sb.toString());
            if (value == null) {
                if (!lax)
                    continue;

                LaxResult<T> result = getLax(commandProcessingContext, sb.toString());
                if (result == null)
                    continue;

                if (result.hasAmbiguous()) {
                    ambiguous = true;
                    continue;
                }

                value = result.value();
            }

            return Optional.of(new ParseResult<>(
                    value,
                    i + 1
            ));
        }

        if (ambiguous)
            return Optional.empty();

        commandProcessingContext.report(
                this,
                new NoSuchElementInMapResponse(commandProcessingContext, this, "No such element in map", sb.toString())
        );

        return Optional.empty();
    }

    private String convertBaseOnIgnoreCase(String s) {
        if (ignoreCase)
            return s.toLowerCase();

        return s;
    }

    private record LaxResult<T>(T value, boolean hasAmbiguous) {

        private static <T> LaxResult<T> createHasAmbiguous() {
            return new LaxResult<>(null, true);
        }

    }

    private LaxResult<T> getLax(CommandProcessingContext context, String s) {
        T result = null;
        List<String> dupeKeys = new ArrayList<>();

        for (var entry : getMap(context).entrySet()) {
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
            return LaxResult.createHasAmbiguous();
        }

        return new LaxResult<>(result, false);
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

        Set<String> keys = getMap(commandProcessingContext).keySet();

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
                index + 1
        ));

    }

}
