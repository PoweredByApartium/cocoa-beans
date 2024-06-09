package net.apartium.cocoabeans.commands.requirements;

import net.apartium.cocoabeans.commands.exception.CommandError;
import net.apartium.cocoabeans.commands.exception.CommandException;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.AvailableSince("0.0.22")
public class RequirementError extends CommandError {

    protected final Requirement requirement;

    public RequirementError(Requirement requirement, String commandName, String[] args, int depth, String message) {
        super(commandName, args, depth, message);
        this.requirement = requirement;
    }

    public RequirementError(Requirement requirement, RequirementEvaluationContext context, String message) {
        super(context.commandName(), context.args(), context.depth(), message);
        this.requirement = requirement;
    }


    public Requirement getRequirement() {
        return requirement;
    }

    @Override
    public Throwable getError() {
        return new RequirementException();
    }

    public class RequirementException extends CommandException {

        public RequirementException() {
            super(RequirementError.this);
        }
    }
}
