package net.apartium.cocoabeans.spigot.visibility;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class VisibilityManager {

    private final Map<String, VisibilityGroup> groups = new HashMap<>();
    private final Map<UUID, VisibilityPlayer> players = new HashMap<>();
    private final JavaPlugin plugin;
    private final VisibilityPlayerRemoveType removeType;
    private VisibilityListener visibilityListener;

    public VisibilityManager(JavaPlugin plugin) {
        this(plugin, VisibilityPlayerRemoveType.NEVER);
    }

    public VisibilityManager(JavaPlugin plugin, VisibilityPlayerRemoveType removeType) {
        this.plugin = plugin;
        this.removeType = removeType;
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

    public void reloadVisibility() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            updateVisiblityForPlayer(player);
        }
    }

    public void handlePlayerJoin(Player joinPlayer) {
        VisibilityPlayer visibilityPlayer = getPlayer(joinPlayer);

        if (visibilityPlayer.getVisibleGroups().isEmpty()) {
            for (Player target : Bukkit.getOnlinePlayers()) {
                if (target == joinPlayer)
                    continue;

                if (getPlayer(target).getVisibleGroups().isEmpty()) {
                    target.showPlayer(plugin, joinPlayer);
                    continue;
                }

                joinPlayer.hidePlayer(plugin, target);
                target.hidePlayer(plugin, joinPlayer);
            }

            return;
        }

        for (Player target : Bukkit.getOnlinePlayers()) {
            if (target == joinPlayer)
                continue;

            if (getPlayer(target).getVisibleGroups().isEmpty())
                continue;

            target.hidePlayer(plugin, joinPlayer);
        }

        updateVisiblityForPlayer(joinPlayer);

        Set<Player> playerToShow = new HashSet<>();

        for (VisibilityGroup visibilityGroup : groups.values()) {
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

                if (targetPlayer == joinPlayer)
                    continue;

                playerToShow.add(targetPlayer);
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

    public void removePlayer(UUID uuid) {
        VisibilityPlayer remove = players.remove(uuid);

        if (remove == null)
            return;

        for (VisibilityGroup group : remove.getVisibleGroups()) {
            group.removePlayer(remove);
        }

    }

    public void updateVisiblityForPlayer(Player player) {
        VisibilityPlayer visibilityPlayer = getPlayer(player);

        if (visibilityPlayer.getVisibleGroups().isEmpty()) {
            for (Player target : Bukkit.getOnlinePlayers()) {
                if (target == player)
                    continue;

                if (!getPlayer(target).getVisibleGroups().isEmpty()) {
                    player.hidePlayer(plugin, target);
                    target.hidePlayer(plugin, player);
                    continue;
                }

                player.showPlayer(plugin, target);
                target.showPlayer(plugin, player);
            }

            return;
        }

        for (Player target : Bukkit.getOnlinePlayers()) {
            if (target == player)
                continue;

            if (getPlayer(target).getVisibleGroups().isEmpty()) {
                player.hidePlayer(plugin, target);
                target.hidePlayer(plugin, player);
                continue;
            }

            player.hidePlayer(plugin, target);
        }

        Set<Player> playersToShow = new HashSet<>();

        for (VisibilityGroup group : visibilityPlayer.getVisibleGroups()) {
            if (!group.hasPlayer(player))
                continue;

            for (VisibilityPlayer target : group.getPlayers()) {
                Player targetPlayer = target.getPlayer();
                if (targetPlayer == null)
                    continue;

                if (player == targetPlayer)
                    continue;

                if (group.getHiddenGroups().stream().anyMatch(targetGroup -> targetGroup.hasPlayer(targetPlayer)))
                    continue;

                playersToShow.add(targetPlayer);
            }
        }

        for (Player target : playersToShow)
            player.showPlayer(plugin, target);

    }

    public VisibilityGroup getOrCreateGroup(String name) {
        return groups.computeIfAbsent(name, this::createInstance);
    }

    public boolean deleteGroup(String name) {
        VisibilityGroup remove = groups.remove(name);
        if (remove == null)
            return false;

        if (remove.getPlayers().isEmpty())
            return true;

        for (VisibilityPlayer visibilityPlayer : players.values()) {
            visibilityPlayer.removeVisibleGroup(remove);

            Player player = visibilityPlayer.getPlayer();
            if (player == null)
                continue;

            updateVisiblityForPlayer(player);
        }

        return true;
    }

    public Collection<VisibilityGroup> getGroups() {
        return groups.values();
    }

    public VisibilityPlayerRemoveType getRemoveType() {
        return removeType;
    }

    protected VisibilityGroup createInstance(String name) {
        return new VisibilityGroup(this, name);
    }

}
