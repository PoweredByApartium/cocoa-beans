package net.apartium.cocoabeans.commands.requirements;

import net.apartium.cocoabeans.commands.exception.BadCommandResponse;

public class RequirementResult {

    private final BadCommandResponse error;
    private final boolean meetRequirement;

    private RequirementResult(BadCommandResponse error, boolean meetRequirement) {
        this.error = error;
        this.meetRequirement = meetRequirement;
    }

    public BadCommandResponse getError() {
        return error;
    }

    public boolean hasError() {
        return error != null;
    }

    public boolean meetRequirement() {
        return meetRequirement;
    }

    public static RequirementResult error(BadCommandResponse error) {
        return new RequirementResult(error, false);
    }

    public static RequirementResult meet() {
        return new RequirementResult(null, true);
    }

}
