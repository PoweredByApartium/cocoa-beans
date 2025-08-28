package net.apartium.cocoabeans.commands.spigot.exception;

import net.apartium.cocoabeans.commands.exception.RequirementException;
import net.apartium.cocoabeans.commands.requirements.UnmetRequirementResponse;
import net.apartium.cocoabeans.commands.spigot.requirements.Whitelist;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.AvailableSince("0.0.26")
public class WhitelistException extends RequirementException {

    public WhitelistException(UnmetRequirementResponse response, Whitelist whitelist) {
        super(response, whitelist);
    }

    public Whitelist getWhitelist() {
        return (Whitelist) requirement;
    }
}
