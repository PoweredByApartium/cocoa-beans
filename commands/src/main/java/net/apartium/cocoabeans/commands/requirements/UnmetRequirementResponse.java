package net.apartium.cocoabeans.commands.requirements;

import net.apartium.cocoabeans.commands.exception.BadCommandResponse;
import net.apartium.cocoabeans.commands.exception.CommandException;
import net.apartium.cocoabeans.commands.exception.RequirementException;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;

@ApiStatus.AvailableSince("0.0.22")
public class UnmetRequirementResponse extends BadCommandResponse {

    protected final Requirement requirement;
    protected final Annotation requirementAnnotation;

    public UnmetRequirementResponse(Requirement requirement, String commandName, String[] args, int depth, String message, @Nullable Annotation requirementAnnotation) {
        super(commandName, args, depth, message);
        this.requirement = requirement;
        this.requirementAnnotation = requirementAnnotation;
    }

    public UnmetRequirementResponse(Requirement requirement, RequirementEvaluationContext context, String message, @Nullable Annotation requirementAnnotation) {
        super(context.commandName(), context.args(), context.depth(), message);
        this.requirement = requirement;
        this.requirementAnnotation = requirementAnnotation;
    }

    public Requirement getRequirement() {
        return requirement;
    }

    @Override
    public CommandException getError() {
        return new RequirementException(this, requirementAnnotation);
    }

}
