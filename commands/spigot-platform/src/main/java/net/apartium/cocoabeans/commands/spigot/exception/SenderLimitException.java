package net.apartium.cocoabeans.commands.spigot.exception;

import net.apartium.cocoabeans.commands.exception.RequirementException;
import net.apartium.cocoabeans.commands.requirements.UnmetRequirementResponse;
import net.apartium.cocoabeans.commands.spigot.requirements.SenderLimit;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.AvailableSince("0.0.26")
public class SenderLimitException extends RequirementException {

    private final SenderLimit senderLimit;

    public SenderLimitException(UnmetRequirementResponse response, SenderLimit senderLimit) {
        super(response);
        this.senderLimit = senderLimit;
    }

    public SenderLimit getSenderLimit() {
        return senderLimit;
    }

}
