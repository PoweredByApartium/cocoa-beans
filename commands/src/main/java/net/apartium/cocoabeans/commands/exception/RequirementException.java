package net.apartium.cocoabeans.commands.exception;

import net.apartium.cocoabeans.commands.requirements.UnmetRequirementResponse;

public class RequirementException extends CommandException {

    public RequirementException(UnmetRequirementResponse response) {
        super(response);
    }
}