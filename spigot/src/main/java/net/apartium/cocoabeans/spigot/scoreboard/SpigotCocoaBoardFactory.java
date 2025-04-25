package net.apartium.cocoabeans.spigot.scoreboard;

import net.apartium.cocoabeans.state.Observable;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;

import static net.apartium.cocoabeans.scoreboard.CocoaBoard.toObservable;

/**
 * @hidden
 */
@ApiStatus.Internal
public interface SpigotCocoaBoardFactory {

    SpigotCocoaBoard create(Player player, String objectiveId, Observable<Component> title);

    default SpigotCocoaBoard create(Player player, String objectiveId, Component title) {
        return create(player, objectiveId, toObservable(title));
    }

    default SpigotCocoaBoard create(Player player, String objectiveId) {
        return create(player, objectiveId, toObservable(Component.text("CocoaBoard")));
    }

    default SpigotCocoaBoard create(Player player, Observable<Component> title) {
        return create(player, "cocoa-" + player.getUniqueId(), title);
    }

    default SpigotCocoaBoard create(Player player, Component title) {
        return create(player, toObservable(title));
    }

    default SpigotCocoaBoard create(Player player) {
        return create(player, "cocoa-" + player.getUniqueId(), toObservable(Component.text("CocoaBoard")));
    }

}
