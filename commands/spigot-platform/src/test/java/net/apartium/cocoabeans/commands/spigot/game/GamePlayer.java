package net.apartium.cocoabeans.commands.spigot.game;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.ref.WeakReference;
import java.util.Optional;
import java.util.UUID;

public class GamePlayer {

    private final UUID uuid;
    private Game game;
    private WeakReference<Player> player = new WeakReference<>(null);

    public GamePlayer(UUID uuid) {
        this.uuid = uuid;
    }

    public Optional<Player> getPlayer() {
        Player player = this.player.get();
        if (player != null)
            return Optional.of(player);

        player = Bukkit.getPlayer(uuid);
        if (player == null)
            return Optional.empty();

        this.player = new WeakReference<>(player);
        return Optional.of(player);
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Game getGame() {
        return game;
    }
}
