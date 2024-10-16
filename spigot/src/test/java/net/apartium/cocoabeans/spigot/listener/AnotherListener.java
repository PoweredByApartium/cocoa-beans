package net.apartium.cocoabeans.spigot.listener;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

public class AnotherListener implements Listener {

    public AnotherListener(JavaPlugin plugin) {
        if (plugin == null)
            throw new NullPointerException("plugin cannot be null");
    }

}
