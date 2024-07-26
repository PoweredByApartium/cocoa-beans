package net.apartium.cocoabeans.commands.parsers;

import net.apartium.cocoabeans.commands.CommandProcessingContext;
import net.apartium.cocoabeans.commands.requirements.RequirementEvaluationContext;
import net.apartium.cocoabeans.commands.requirements.RequirementSet;
import net.apartium.cocoabeans.structs.Entry;

import java.util.*;

/* package-private */ class CompoundParserBranchProcessor<T> {

    final List<Entry<RequirementSet, CompoundParserOption<T>>> objectMap = new ArrayList<>();

    public Optional<CompoundParser.ParserResult> parse(CommandProcessingContext processingContext) {
        RequirementEvaluationContext requirementEvaluationContext = new RequirementEvaluationContext(processingContext.sender(), processingContext.label(), processingContext.args().toArray(new String[0]), processingContext.index());

        for (Entry<RequirementSet, CompoundParserOption<T>> entry : objectMap) {
            if (!entry.key().meetsRequirements(requirementEvaluationContext).meetRequirement())
                continue;

            Optional<CompoundParser.ParserResult> result = entry.value().parse(processingContext);
            if (result.isEmpty())
                continue;

            return result;
        }

        return Optional.empty();
    }

    public OptionalInt tryParse(CommandProcessingContext processingContext) {
        RequirementEvaluationContext requirementEvaluationContext = new RequirementEvaluationContext(processingContext.sender(), processingContext.label(), processingContext.args().toArray(new String[0]), processingContext.index());

        for (Entry<RequirementSet, CompoundParserOption<T>> entry : objectMap) {
            if (!entry.key().meetsRequirements(requirementEvaluationContext).meetRequirement())
                continue;

            OptionalInt result = entry.value().tryParse(processingContext);
            if (result.isEmpty())
                continue;

            return result;
        }

        return OptionalInt.empty();
    }
    public Optional<ArgumentParser.TabCompletionResult> tabCompletion(CommandProcessingContext processingContext) {
        RequirementEvaluationContext requirementEvaluationContext = new RequirementEvaluationContext(processingContext.sender(), processingContext.label(), processingContext.args().toArray(new String[0]), processingContext.index());

        Set<String> result = new HashSet<>();
        int highestIndex = -1;

        for (Entry<RequirementSet, CompoundParserOption<T>> entry : objectMap) {
            if (!entry.key().meetsRequirements(requirementEvaluationContext).meetRequirement())
                continue;

            Optional<ArgumentParser.TabCompletionResult> tabCompletion = entry.value().tabCompletion(processingContext);
            if (tabCompletion.isEmpty())
                continue;

            highestIndex = Math.max(highestIndex, tabCompletion.get().newIndex());
            result.addAll(tabCompletion.get().result());
        }

        if (result.isEmpty())
            return Optional.empty();

        return Optional.of(new ArgumentParser.TabCompletionResult(result, highestIndex));
    }


}
