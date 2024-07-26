package net.apartium.cocoabeans.commands.parsers;

import net.apartium.cocoabeans.commands.CommandProcessingContext;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;

public class CompoundParser<T> extends ArgumentParser<T> {

    private CompoundParserBranchProcessor compoundParserBranchProcessor;

    /**
     * Constructs a
     *
     * @param keyword
     * @param clazz
     * @param priority
     */
    protected CompoundParser(Class<? extends CompoundParser<T>> self, String keyword, Class<T> clazz, int priority) {
        super(keyword, clazz, priority);

        this.compoundParserBranchProcessor = new CompoundParserBranchProcessor();
    }

    private void createBranch() {

    }

    @Override
    public Optional<ParseResult<T>> parse(CommandProcessingContext processingContext) {
        return Optional.empty();
    }

    @Override
    public OptionalInt tryParse(CommandProcessingContext processingContext) {
        return OptionalInt.empty();
    }

    @Override
    public Optional<TabCompletionResult> tabCompletion(CommandProcessingContext processingContext) {
        return Optional.empty();
    }


    /* package-private */ record ParserResult (
            int newIndex,
            Map<Class<?>, List<Object>> mappedByClass
    ) {

    }

}
