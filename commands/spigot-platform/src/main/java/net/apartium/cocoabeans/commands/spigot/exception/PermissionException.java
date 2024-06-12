package net.apartium.cocoabeans.commands.spigot.exception;

import net.apartium.cocoabeans.commands.exception.RequirementException;
import net.apartium.cocoabeans.commands.requirements.UnmetRequirementResponse;
import net.apartium.cocoabeans.commands.spigot.requirements.Permission;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.AvailableSince("0.0.26")
public class PermissionException extends RequirementException {

    private final Permission permission;

    public PermissionException(UnmetRequirementResponse response, Permission permission) {
        super(response);
        this.permission = permission;
    }

    public Permission getPermission() {
        return permission;
    }
}
