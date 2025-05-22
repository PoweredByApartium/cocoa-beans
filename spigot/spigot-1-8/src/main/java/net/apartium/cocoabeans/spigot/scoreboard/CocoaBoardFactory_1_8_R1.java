package net.apartium.cocoabeans.spigot.scoreboard;

import net.apartium.cocoabeans.scoreboard.spigot.SpigotCocoaBoard;
import net.apartium.cocoabeans.scoreboard.spigot.SpigotCocoaBoardFactory;
import net.apartium.cocoabeans.state.Observable;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class CocoaBoardFactory_1_8_R1 implements SpigotCocoaBoardFactory {

    @Override
    public SpigotCocoaBoard create(Player player, String objectiveId, Observable<Component> title) {
        return new SpigotCocoaBoard_1_8_R1(player, objectiveId, title);
    }

}
