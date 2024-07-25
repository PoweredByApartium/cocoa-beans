package net.apartium.cocoabeans.commands.requirements;

import net.apartium.cocoabeans.commands.Sender;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.AvailableSince("0.0.22")
public record RequirementEvaluationContext(
        Sender sender,
        String commandName,
        String[] args,
        int depth
) {

}
