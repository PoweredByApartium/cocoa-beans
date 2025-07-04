package net.apartium.cocoabeans.spigot.tab;

import net.apartium.cocoabeans.scoreboard.ViewerGroup;
import net.apartium.cocoabeans.tab.TabList;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;

import java.util.Optional;
import java.util.Set;

/**
 * @see net.apartium.cocoabeans.tab.TabList
 */
@ApiStatus.AvailableSince("0.0.41")
public class SpigotTabList extends TabList<Player> {

    private static final boolean HAS_NATIVE_SUPPORT_ADVENTURE_API = Audience.class.isAssignableFrom(Player.class);

    public SpigotTabList(ViewerGroup<Player> group) {
        super(group);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void sendPlayerListHeaderAndFooter(Set<Player> viewers, Component header, Component footer) {
        if (viewers.isEmpty())
            return;

        if (HAS_NATIVE_SUPPORT_ADVENTURE_API) {
            Audience audience = Audience.audience(viewers);
            audience.sendPlayerListHeaderAndFooter(
                    header,
                    footer
            );
        } else {
            BungeeComponentSerializer serializer = BungeeComponentSerializer.get();
            BaseComponent[] baseHeader = Optional.ofNullable(header)
                    .map(serializer::serialize)
                    .orElse(new BaseComponent[0]);
            BaseComponent[] baseFooter = Optional.ofNullable(footer)
                    .map(serializer::serialize)
                    .orElse(new BaseComponent[0]);

            
            for (Player player : viewers)
                player.setPlayerListHeaderFooter(baseHeader, baseFooter);
        }
    }

}
