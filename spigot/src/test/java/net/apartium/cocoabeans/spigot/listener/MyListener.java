package net.apartium.cocoabeans.spigot.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class MyListener implements Listener {

    public MyListener(JavaPlugin plugin, TheManager manager) {
        assertNotNull(plugin);
        assertNotNull(manager);

        manager.updateHasBeenCreated();
    }

    @EventHandler
    public void on(PlayerJoinEvent event) {

    }


}
