package net.apartium.cocoabeans.commands.requirements;

import net.apartium.cocoabeans.commands.Sender;

public record RequirementEvaluationContext(
        Sender sender,
        String commandName,
        String[] args,
        int depth
) {

}
