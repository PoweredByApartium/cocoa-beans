package net.apartium.cocoabeans.commands.parsers;

import net.apartium.cocoabeans.commands.CommandProcessingContext;
import net.apartium.cocoabeans.commands.RegisterArgumentParser;
import net.apartium.cocoabeans.commands.RegisteredVariant;
import net.apartium.cocoabeans.commands.SimpleCommandProcessingContext;
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
                    .computeIfAbsent(entry.key().getArgumentType(), clazz -> new ArrayList<>())
                    .add(0, parse.get().result());

            return result;
        }

        if (!registeredVariants.isEmpty())
            return Optional.of(new CompoundParser.ParserResult(Collections.unmodifiableList(registeredVariants), processingContext.index(), new HashMap<>()));

        return Optional.empty();
    }

    private Optional<ArgumentParser.TabCompletionResult> lastArgTabCompletion(CommandProcessingContext processingContext, int highestIndex, Set<String> result) {
        for (Entry<RegisterArgumentParser<?>, CompoundParserBranchProcessor<T>> entry : argumentTypeHandlerMap) {

            Optional<ArgumentParser.TabCompletionResult> tabCompletionResult = entry.key().tabCompletion(processingContext);
            if (tabCompletionResult.isEmpty())
                continue;

            highestIndex = Math.max(highestIndex, tabCompletionResult.get().newIndex());
            result.addAll(tabCompletionResult.get().result());
        }

        if (result.isEmpty())
            return Optional.empty();

        return Optional.of(new ArgumentParser.TabCompletionResult(result, highestIndex));
    }

    public Optional<ArgumentParser.TabCompletionResult> tabCompletion(CommandProcessingContext processingContext) {
        if (processingContext.args().size() <= processingContext.index())
            return Optional.empty();

        Set<String> result = new HashSet<>();
        int highestIndex = processingContext.index();

        if (processingContext.args().size() - 1 == processingContext.index())
            return lastArgTabCompletion(processingContext, highestIndex, result);

        for (Entry<RegisterArgumentParser<?>, CompoundParserBranchProcessor<T>> entry : argumentTypeHandlerMap) {
            RegisterArgumentParser<?> parser = entry.key();

            if (parser.isSupportMultipleArguments()) {
                Optional<ArgumentParser.TabCompletionResult> tabCompletionResult = parser.tabCompletion(processingContext);
                if (tabCompletionResult.isPresent()) {
                    result.addAll(tabCompletionResult.get().result());
                    highestIndex = Math.max(highestIndex, tabCompletionResult.get().newIndex());
                    continue;
                }
            }

            OptionalInt parse = parser.tryParse(processingContext);
            if (parse.isEmpty())
                continue;

            int newIndex = parse.getAsInt();
            if (newIndex <= processingContext.index())
                throw new IllegalStateException("There is an exception with " + parser.parser().getClass().getName() + " return new index that isn't bigger then current index");

            Optional<ArgumentParser.TabCompletionResult> tabCompletionResult = entry.value().tabCompletion(new SimpleCommandProcessingContext(
                    processingContext.sender(),
                    processingContext.label(),
                    processingContext.args().toArray(new String[0]), newIndex)
            );

            if (tabCompletionResult.isEmpty())
                continue;

            highestIndex = Math.max(highestIndex, tabCompletionResult.get().newIndex());
            result.addAll(tabCompletionResult.get().result());
        }

        if (result.isEmpty())
            return Optional.empty();

        return Optional.of(new ArgumentParser.TabCompletionResult(result, highestIndex));
    }

    public List<RegisteredVariant> getRegisteredVariants() { return registeredVariants; }


}
