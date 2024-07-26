package net.apartium.cocoabeans.commands.parsers;

import net.apartium.cocoabeans.commands.AbstractCommandProcessingContext;
import net.apartium.cocoabeans.commands.CommandProcessingContext;
import net.apartium.cocoabeans.structs.Entry;

import java.util.*;

/* package-private */ class CompoundParserOption<T> {

    final List<Entry<ArgumentParser<?>, CompoundParserBranchProcessor<T>>> argumentTypeHandlerMap = new ArrayList<>();


    public Optional<CompoundParser.ParserResult> parse(CommandProcessingContext processingContext) {
        for (Entry<ArgumentParser<?>, CompoundParserBranchProcessor<T>> entry : argumentTypeHandlerMap) {
            Optional<? extends ArgumentParser.ParseResult<?>> parse = entry.key().parse(processingContext);

            if (parse.isEmpty())
                continue;

            Optional<CompoundParser.ParserResult> result = entry.value().parse(new AbstractCommandProcessingContext(
                    processingContext.sender(),
                    processingContext.label(),
                    processingContext.args().toArray(new String[0]),
                    parse.get().newIndex()
            ));

            if (result.isEmpty())
                continue;

            result.get().mappedByClass()
                    .computeIfAbsent(entry.key().getArgumentType(), (clazz) -> new ArrayList<>())
                    .add(0, parse.get().result());

            return result;
        }

        return Optional.empty();
    }

    public OptionalInt tryParse(CommandProcessingContext processingContext) {
        for (Entry<ArgumentParser<?>, CompoundParserBranchProcessor<T>> entry : argumentTypeHandlerMap) {
            OptionalInt parse = entry.key().tryParse(processingContext);

            if (parse.isEmpty())
                continue;

            OptionalInt result = entry.value().tryParse(new AbstractCommandProcessingContext(
                    processingContext.sender(),
                    processingContext.label(),
                    processingContext.args().toArray(new String[0]),
                    parse.getAsInt()
            ));

            if (result.isEmpty())
                continue;

            return result;
        }

        return OptionalInt.empty();
    }

    public Optional<ArgumentParser.TabCompletionResult> tabCompletion(CommandProcessingContext processingContext) {
        Set<String> result = new HashSet<>();

        int highestIndex = -1;

        for (Entry<ArgumentParser<?>, CompoundParserBranchProcessor<T>> entry : argumentTypeHandlerMap) {
            Optional<ArgumentParser.TabCompletionResult> parse = entry.key().tabCompletion(processingContext);

            if (parse.isEmpty())
                continue;

            highestIndex = Math.max(highestIndex, parse.get().newIndex());
            result.addAll(parse.get().result());
        }

        if (result.isEmpty())
            return Optional.empty();

        return Optional.of(new ArgumentParser.TabCompletionResult(result, highestIndex));
    }


}
