package net.apartium.cocoabeans.spigot.listener;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.Assertions;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class MyListener implements Listener {

    public MyListener(JavaPlugin plugin, MyManager manager) {
        assertNotNull(plugin);
        assertNotNull(manager);

        manager.updateHasBeenCreated();
    }

}
