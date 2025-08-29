package net.apartium.cocoabeans.spigot.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class FailedListener implements Listener {

    public FailedListener() {
        throw new RuntimeException();
    }

    @EventHandler
    public void on(PlayerJoinEvent event) {

    }

}
