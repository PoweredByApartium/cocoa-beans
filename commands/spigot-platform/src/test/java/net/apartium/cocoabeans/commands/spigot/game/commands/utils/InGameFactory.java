package net.apartium.cocoabeans.commands.spigot.game.commands.utils;

import net.apartium.cocoabeans.commands.CommandNode;
import net.apartium.cocoabeans.commands.GenericNode;
import net.apartium.cocoabeans.commands.requirements.*;
import net.apartium.cocoabeans.commands.spigot.game.Game;
import net.apartium.cocoabeans.commands.spigot.game.GamePlayer;
import net.apartium.cocoabeans.commands.spigot.game.PlayerManager;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class InGameFactory implements RequirementFactory {

    @Override
    public @Nullable Requirement getRequirement(GenericNode node, Object obj) {
        if (!(obj instanceof InGame inGame))
            return null;

        return new InGameImpl(inGame, inGame.invert());
    }

    private static class InGameImpl implements Requirement {

        private final PlayerManager playerManager = PlayerManager.getInstance();
        private final InGame inGame;
        private final boolean invert;

        public InGameImpl(InGame inGame, boolean invert) {
            this.inGame = inGame;
            this.invert = invert;
        }

        /**
         * Check if given context meets this requirement.
         *
         * @param context context to evaluate
         * @return if sender meets requirement or else given an error
         */
        @Override
        public RequirementResult meetsRequirement(RequirementEvaluationContext context) {
            if (context.sender().getSender() == null || !(context.sender().getSender() instanceof Player player)) {
                if (invert)
                    return RequirementResult.meet();

                return RequirementResult.error(new UnmetGameRequirement(
                        this,
                        context,
                        "Sender is not a player such as he can't be in game"
                ));
            }

            GamePlayer gamePlayer = playerManager.getGamePlayer(player.getUniqueId());
            if (gamePlayer == null) {
                if (invert)
                    return RequirementResult.meet();

                return RequirementResult.error(new UnmetGameRequirement(
                        this,
                        context,
                        "Player isn't in any game"
                ));
            }

            Game game = gamePlayer.getGame();
            if (game == null) {
                if (invert)
                    return RequirementResult.meet();

                return RequirementResult.error(new UnmetGameRequirement(
                        this,
                        context,
                        "Player isn't in any game"
                ));
            }

            if (invert)
                return RequirementResult.error(new UnmetGameRequirement(this, context, "Player is in game"));

            return RequirementResult.meet(RequirementResult.valueOf(game, Game.class), RequirementResult.valueOf(gamePlayer, GamePlayer.class));
        }

        @Override
        public List<Class<?>> getTypes() {
            return List.of(Game.class, GamePlayer.class);
        }

        private class UnmetGameRequirement extends UnmetRequirementResponse<NotInGameException> {

            public UnmetGameRequirement(Requirement requirement, RequirementEvaluationContext context, String message) {
                super(requirement, context, message, inGame);
            }

            @Override
            public NotInGameException getError() {
                return new NotInGameException(this, inGame);
            }
        }
    }
}