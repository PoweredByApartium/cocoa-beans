package net.apartium.cocoabeans.commands.exception;

import net.apartium.cocoabeans.commands.requirements.UnmetRequirementResponse;

import java.lang.annotation.Annotation;

public class RequirementException extends CommandException {

    protected final Annotation requirement;

    public RequirementException(UnmetRequirementResponse response, Annotation requirement) {
        super(response);
        this.requirement = requirement;
    }

    public Annotation getRequirement() {
        return requirement;
    }
}