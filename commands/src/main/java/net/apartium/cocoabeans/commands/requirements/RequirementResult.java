package net.apartium.cocoabeans.commands.requirements;

import net.apartium.cocoabeans.commands.exception.CommandError;

public class RequirementResult {

    private final CommandError error;
    private final boolean meetRequirement;

    private RequirementResult(CommandError error, boolean meetRequirement) {
        this.error = error;
        this.meetRequirement = meetRequirement;
    }

    public CommandError getError() {
        return error;
    }

    public boolean hasError() {
        return error != null;
    }

    public boolean meetRequirement() {
        return meetRequirement;
    }

    public static RequirementResult error(CommandError error) {
        return new RequirementResult(error, false);
    }

    public static RequirementResult meet() {
        return new RequirementResult(null, true);
    }

}
