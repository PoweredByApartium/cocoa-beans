package net.apartium.cocoabeans.spigot.visibility;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class VisibilityManager {

    private final Set<VisibilityGroup> groups = new HashSet<>();
    private final Map<UUID, VisibilityPlayer> players = new HashMap<>();
    private final JavaPlugin plugin;
    private VisibilityListener visibilityListener;

    public VisibilityManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void registerListener() {
        if (visibilityListener != null)
            return;

        visibilityListener = new VisibilityListener(this);
        plugin.getServer().getPluginManager().registerEvents(visibilityListener, plugin);
    }

    public void unregisterListener() {
        if (visibilityListener == null)
            return;

        HandlerList.unregisterAll(visibilityListener);
        visibilityListener = null;
    }

    public void handlePlayerJoin(Player joinPlayer) {
        for (Player target : Bukkit.getOnlinePlayers()) {
            if (target == joinPlayer)
                continue;

            target.hidePlayer(plugin, joinPlayer);
        }

        updateVisiblityForPlayer(joinPlayer);

        Set<Player> playerToShow = new HashSet<>(Bukkit.getOnlinePlayers());

        for (VisibilityGroup visibilityGroup : groups) {
            boolean playerInGroup = visibilityGroup.hasPlayer(joinPlayer);
            boolean playerInVisibleGroup = visibilityGroup.getVisibleGroups().stream().anyMatch(group -> group.hasPlayer(joinPlayer));
            if (!playerInGroup && !playerInVisibleGroup)
                continue;

            boolean playerInHiddenGroup = visibilityGroup.getHiddenGroups().stream().anyMatch(group -> group.hasPlayer(joinPlayer));
            if (playerInHiddenGroup)
                continue;


            for (VisibilityPlayer player : visibilityGroup.getPlayers()) {
                Player targetPlayer = player.getPlayer();
                if (targetPlayer == null)
                    continue;

                playerToShow.add(targetPlayer);
            }
        }

        for (VisibilityGroup visibleGroup : getPlayer(joinPlayer).getVisibleGroups()) {
            for (VisibilityPlayer player : visibleGroup.getPlayers()) {
                Player targetPlayer = player.getPlayer();
                if (targetPlayer == null)
                    continue;

                playerToShow.remove(targetPlayer);
            }

        }

        for (Player target : playerToShow) {
            target.showPlayer(plugin, joinPlayer);
        }

    }

    public VisibilityPlayer getPlayer(UUID uuid) {
        return players.computeIfAbsent(uuid, (key) -> new VisibilityPlayer(this, uuid));
    }

    public VisibilityPlayer getPlayer(Player player) {
        return players.computeIfAbsent(player.getUniqueId(), (key) -> new VisibilityPlayer(this, player));
    }

    public boolean deletePlayer(UUID uuid) {
        return players.remove(uuid) != null;
    }

    public void updateVisiblityForPlayer(Player player) {
        for (Player target : Bukkit.getOnlinePlayers()) {
            if (target == player)
                continue;

            player.hidePlayer(plugin, target);
        }

        Set<Player> playersToShow = new HashSet<>();
        VisibilityPlayer visibilityPlayer = getPlayer(player);

        for (VisibilityGroup group : visibilityPlayer.getVisibleGroups()) {
            if (!group.hasPlayer(player))
                continue;

            for (VisibilityPlayer target : group.getPlayers()) {
                Player targetPlayer = target.getPlayer();
                if (targetPlayer == null)
                    continue;

                if (group.getHiddenGroups().stream().anyMatch(targetGroup -> targetGroup.hasPlayer(targetPlayer)))
                    continue;

                playersToShow.add(targetPlayer);
            }
        }

        for (Player target : playersToShow)
            player.showPlayer(plugin, target);

    }

    public VisibilityGroup createGroup(String name) {
        VisibilityGroup group = createInstance(this, name);
        groups.add(group);
        return group;
    }

    public boolean deleteGroup(VisibilityGroup group) {
        if (!groups.remove(group))
            return false;

        for (VisibilityPlayer visibilityPlayer : players.values()) {
            Player player = visibilityPlayer.getPlayer();
            if (player == null)
                continue;

            updateVisiblityForPlayer(player);
        }

        return true;
    }

    public Set<VisibilityGroup> getGroups() {
        return groups;
    }


    protected VisibilityGroup createInstance(VisibilityManager visibilityManager, String name) {
        return new VisibilityGroup(visibilityManager, name);
    }

}
