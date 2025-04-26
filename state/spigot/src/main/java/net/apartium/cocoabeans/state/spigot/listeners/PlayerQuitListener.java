package net.apartium.cocoabeans.state.spigot.listeners;

import net.apartium.cocoabeans.state.SetObservable;
import net.apartium.cocoabeans.state.spigot.SpigotProvidedState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

    private final SpigotProvidedState providedState;

    public PlayerQuitListener(SpigotProvidedState providedState) {
        this.providedState = providedState;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        ((SetObservable<Player>) this.providedState.getOnlinePlayersObservable()).remove(event.getPlayer());
    }

}
