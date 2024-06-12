package net.apartium.cocoabeans.commands.requirements;

import net.apartium.cocoabeans.commands.exception.BadCommandResponse;
import net.apartium.cocoabeans.commands.exception.CommandException;
import net.apartium.cocoabeans.commands.exception.RequirementException;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.AvailableSince("0.0.22")
public class UnmetRequirementResponse extends BadCommandResponse {

    protected final Requirement requirement;

    public UnmetRequirementResponse(Requirement requirement, String commandName, String[] args, int depth, String message) {
        super(commandName, args, depth, message);
        this.requirement = requirement;
    }

    public UnmetRequirementResponse(Requirement requirement, RequirementEvaluationContext context, String message) {
        super(context.commandName(), context.args(), context.depth(), message);
        this.requirement = requirement;
    }

    public Requirement getRequirement() {
        return requirement;
    }

    @Override
    public Exception getError() {
        return new RequirementException(this);
    }

}
