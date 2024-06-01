package net.apartium.cocoabeans.commands.requirements;

import net.apartium.cocoabeans.commands.exception.CommandException;

public class RequirementException extends CommandException {

    protected final Requirement requirement;

    public RequirementException(Requirement requirement, String commandName, String[] args, int depth, String message) {
        super(commandName, args, depth, message);
        this.requirement = requirement;
    }
}
