package net.apartium.cocoabeans.spigot.tab;

import net.apartium.cocoabeans.scoreboard.ViewerGroup;
import net.apartium.cocoabeans.tab.TabList;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.VisibleForTesting;

import java.util.Optional;
import java.util.Set;

/**
 * @see net.apartium.cocoabeans.tab.TabList
 */
@ApiStatus.AvailableSince("0.0.41")
public class SpigotTabList extends TabList<Player> {

    private final boolean hasNativeKyori;

    @VisibleForTesting
    /* package-private */ SpigotTabList(ViewerGroup<Player> group, boolean hasNativeKyori) {
        super(group);

        this.hasNativeKyori = hasNativeKyori;
    }

    @SuppressWarnings("ConstantConditions")
    public SpigotTabList(ViewerGroup<Player> group) {
        this(group, Audience.class.isAssignableFrom(Player.class));
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void sendPlayerListHeaderAndFooter(Set<Player> viewers, Component header, Component footer) {
        if (viewers.isEmpty())
            return;

        if (hasNativeKyori) {
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
