package net.apartium.cocoabeans.commands.spigot.game.commands;

import net.apartium.cocoabeans.commands.Command;
import net.apartium.cocoabeans.commands.CommandNode;
import net.apartium.cocoabeans.commands.SubCommand;
import net.apartium.cocoabeans.commands.spigot.SenderType;
import net.apartium.cocoabeans.commands.spigot.game.Game;
import net.apartium.cocoabeans.commands.spigot.game.GamePlayer;
import net.apartium.cocoabeans.commands.spigot.game.PlayerManager;
import net.apartium.cocoabeans.commands.spigot.game.Stage;
import net.apartium.cocoabeans.commands.spigot.game.commands.utils.InGame;
import net.apartium.cocoabeans.commands.spigot.requirements.SenderLimit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;


@Command("game")
public class GameCommand implements CommandNode {

    @InGame
    @SubCommand("start")
    public void startGame(Player sender, GamePlayer gamePlayer, Game game) {
        sender.sendMessage("gamePlayer: " + gamePlayer.getUniqueId());
        sender.sendMessage("game: " + game.getUniqueId());

        game.setStage(Stage.COUNTDOWN);
        sender.sendMessage("Game started in 10 seconds");
    }

    @InGame
    @SubCommand("am i playing")
    public void amIPlaying(CommandSender sender) {
        sender.sendMessage("Yes");
    }

    @InGame(invert = true)
    @SenderLimit(SenderType.PLAYER)
    @SubCommand("create")
    public void createGame(Player player) {
        GamePlayer gamePlayer = PlayerManager.getInstance().createGamePlayer(player.getUniqueId());
        if (gamePlayer.getGame() != null)
            player.sendMessage("You are already in a game");
        else
            gamePlayer.setGame(new Game(UUID.randomUUID()));

        player.sendMessage("Created game");
        player.sendMessage(gamePlayer.getGame().getUniqueId().toString());

    }

}
