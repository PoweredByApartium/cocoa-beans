package net.apartium.cocoabeans.spigot.visibility;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;

/**
 * Interface for controlling player visibility.
 * Provides methods to show or hide one player from another.
 */
@ApiStatus.AvailableSince("0.0.30")
public interface PlayerVisibilityController {

    /**
     * Makes the target player visible to the source player.
     *
     * @param plugin The plugin instance making the request to show the player.
     * @param source The player who will see the target player.
     * @param target The player who will be made visible to the source player.
     */
    void showPlayer(JavaPlugin plugin, Player source, Player target);

    /**
     * Hides the target player from the source player.
     *
     * @param plugin The plugin instance making the request to hide the player.
     * @param source The player who will no longer see the target player.
     * @param target The player who will be hidden from the source player.
     */
    void hidePlayer(JavaPlugin plugin, Player source, Player target);

}
