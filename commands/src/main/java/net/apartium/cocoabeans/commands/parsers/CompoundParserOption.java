package net.apartium.cocoabeans.commands.parsers;

import net.apartium.cocoabeans.commands.SimpleCommandProcessingContext;
import net.apartium.cocoabeans.commands.CommandProcessingContext;
import net.apartium.cocoabeans.commands.RegisterArgumentParser;
import net.apartium.cocoabeans.commands.RegisteredVariant;
import net.apartium.cocoabeans.structs.Entry;

import java.util.*;

/* package-private */ class CompoundParserOption<T> {

    private final List<RegisteredVariant> registeredVariants = new ArrayList<>();

    final List<Entry<RegisterArgumentParser<?>, CompoundParserBranchProcessor<T>>> argumentTypeHandlerMap = new ArrayList<>();


    public Optional<CompoundParser.ParserResult> parse(CommandProcessingContext processingContext) {
        if (processingContext.args().size() <= processingContext.index() || argumentTypeHandlerMap.isEmpty()) {
            if (!registeredVariants.isEmpty())
                return Optional.of(new CompoundParser.ParserResult(Collections.unmodifiableList(registeredVariants), processingContext.index(), new HashMap<>()));

            return Optional.empty();
        }

        for (Entry<RegisterArgumentParser<?>, CompoundParserBranchProcessor<T>> entry : argumentTypeHandlerMap) {
            Optional<? extends ArgumentParser.ParseResult<?>> parse = entry.key().parse(processingContext);

            if (parse.isEmpty())
                continue;

            Optional<CompoundParser.ParserResult> result = entry.value().parse(new SimpleCommandProcessingContext(
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

        if (!registeredVariants.isEmpty())
            return Optional.of(new CompoundParser.ParserResult(Collections.unmodifiableList(registeredVariants), processingContext.index(), new HashMap<>()));

        return Optional.empty();
    }


    public Optional<ArgumentParser.TabCompletionResult> tabCompletion(CommandProcessingContext processingContext) {
        Set<String> result = new HashSet<>();

        int highestIndex = -1;

        for (Entry<RegisterArgumentParser<?>, CompoundParserBranchProcessor<T>> entry : argumentTypeHandlerMap) {
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

    public List<RegisteredVariant> getRegisteredVariants() { return registeredVariants; }


}
