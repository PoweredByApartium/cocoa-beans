package net.apartium.cocoabeans.spigot.tab;

import net.apartium.cocoabeans.scoreboard.ViewerGroup;
import net.apartium.cocoabeans.tab.TabList;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;

import java.util.Set;

/**
 * @see net.apartium.cocoabeans.tab.TabList
 */
@ApiStatus.AvailableSince("0.0.41")
public class SpigotTabList extends TabList<Player> {

    public SpigotTabList(ViewerGroup<Player> group) {
        super(group);
    }

    @Override
    protected void sendPlayerListHeaderAndFooter(Set<Player> viewers, Component header, Component footer) {
        Audience audience = Audience.audience(viewers);
        audience.sendPlayerListHeaderAndFooter(
                header,
                footer
        );
    }

}
