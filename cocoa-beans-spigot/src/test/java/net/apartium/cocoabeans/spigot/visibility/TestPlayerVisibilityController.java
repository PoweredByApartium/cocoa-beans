package net.apartium.cocoabeans.spigot.visibility;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class TestPlayerVisibilityController implements PlayerVisibilityController {

    @Override
    public void showPlayer(JavaPlugin plugin, Player source, Player target) {
        source.showPlayer(plugin, target);
    }

    @Override
    public void hidePlayer(JavaPlugin plugin, Player source, Player target) {
        source.hidePlayer(plugin, target);
    }

}
