package net.apartium.cocoabeans.commands.requirements;

import net.apartium.cocoabeans.commands.exception.CommandError;
import net.apartium.cocoabeans.commands.exception.CommandException;

public class RequirementError extends CommandError {

    protected final Requirement requirement;

    public RequirementError(Requirement requirement, String commandName, String[] args, int depth, String message) {
        super(commandName, args, depth, message);
        this.requirement = requirement;
    }

    public class RequirementException extends CommandException {

        public RequirementException() {
            super(RequirementError.this);
        }
    }
}
