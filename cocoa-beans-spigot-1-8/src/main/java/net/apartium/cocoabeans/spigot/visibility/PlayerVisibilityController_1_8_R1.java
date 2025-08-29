package net.apartium.cocoabeans.spigot.visibility;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerVisibilityController_1_8_R1 implements PlayerVisibilityController {

    @Override
    public void showPlayer(JavaPlugin plugin, Player source, Player target) {
        source.showPlayer(target);
    }

    @Override
    public void hidePlayer(JavaPlugin plugin, Player source, Player target) {
        source.hidePlayer(target);
    }

}
