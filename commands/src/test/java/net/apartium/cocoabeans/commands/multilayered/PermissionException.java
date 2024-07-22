package net.apartium.cocoabeans.commands.multilayered;

import net.apartium.cocoabeans.commands.exception.RequirementException;
import net.apartium.cocoabeans.commands.requirements.UnmetRequirementResponse;

public class PermissionException extends RequirementException {

    public PermissionException(UnmetRequirementResponse response, Permission permission) {
        super(response, permission);
    }

    public Permission getPermission() {
        return (Permission) requirement;
    }
}
