package net.apartium.cocoabeans.commands.spigot.game.commands.utils;

import net.apartium.cocoabeans.commands.exception.RequirementException;
import net.apartium.cocoabeans.commands.requirements.UnmetRequirementResponse;

public class NotInGameException extends RequirementException {
    public NotInGameException(UnmetRequirementResponse response, InGame inGame) {
        super(response, inGame);
    }

    public InGame getInGame() {
        return (InGame) getRequirement();
    }
}
