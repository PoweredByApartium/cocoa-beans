package net.apartium.cocoabeans.commands.parsers;

import net.apartium.cocoabeans.commands.CommandProcessingContext;

import java.util.*;
import java.util.function.Supplier;

public abstract class MapBasedParser<T> extends ArgumentParser<T> {

    public abstract Map<String, T> getMap();

    public MapBasedParser(String keyword, Class<T> clazz, int priority) {
        super(keyword, clazz, priority);
    }

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

            T value = map.get(s);
            if (value == null)
                continue;

            return Optional.of(new ParseResult<>(
                    value,
                    i + 1
            ));
        }

        return Optional.empty();
    }

    @Override
    public OptionalInt tryParse(CommandProcessingContext commandProcessingContext) {
        List<String> args = commandProcessingContext.args();
        int index = commandProcessingContext.index();

        Map<String, T> map = getMap();
        String s = "";
        for (int i = index; i < args.size(); i++) {
            if (i != index)
                s += " ";
            s += args.get(index);

            if (!map.containsKey(s))
                continue;

            return OptionalInt.of(i + 1);
        }

        return OptionalInt.empty();
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
