package net.apartium.cocoabeans.state.spigot.listeners;

import net.apartium.cocoabeans.state.SetObservable;
import net.apartium.cocoabeans.state.spigot.SpigotProvidedState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private final SetObservable<Player> players;

    public PlayerJoinListener(SetObservable<Player> players) {
        this.players = players;
    }

    @EventHandler
    public void on(PlayerJoinEvent event) {
        players.add(event.getPlayer());
    }
}
