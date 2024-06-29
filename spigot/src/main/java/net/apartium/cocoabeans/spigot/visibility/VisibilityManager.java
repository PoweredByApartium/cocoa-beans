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
    private VisibilityPlayerRemoveType removeType;
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

    public void handlePlayerJoin(Player joinPlayer) {
        for (Player target : Bukkit.getOnlinePlayers()) {
            if (target == joinPlayer)
                continue;

            target.hidePlayer(plugin, joinPlayer);
        }

        updateVisiblityForPlayer(joinPlayer);

        Set<Player> playerToShow = new HashSet<>(Bukkit.getOnlinePlayers());

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

    public void removePlayer(UUID uuid) {
        VisibilityPlayer remove = players.remove(uuid);

        if (remove == null)
            return;

        for (VisibilityGroup group : remove.getVisibleGroups()) {
            group.removePlayer(remove);
        }

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

    public void updateVisiblityForPlayer(VisibilityPlayer visibilityPlayer, VisibilityGroup group) {
        Player player = visibilityPlayer.getPlayer();
        if (player == null)
            return;

        for (VisibilityPlayer target : group.getPlayers()) {
            Player targetPlayer = target.getPlayer();
            if (targetPlayer == null)
                continue;

            if (group.getHiddenGroups().stream().anyMatch(targetGroup -> targetGroup.hasPlayer(targetPlayer)))
                continue;

            player.showPlayer(plugin, targetPlayer);
        }
    }

    public VisibilityGroup createGroup(String name) {
        if (groups.containsKey(name))
            return groups.get(name);

        VisibilityGroup group = createInstance(this, name);
        groups.put(name, group);
        return group;
    }

    public boolean deleteGroup(String name) {
        VisibilityGroup remove = groups.remove(name);
        if (remove == null)
            return false;

        if (remove.getPlayers().isEmpty())
            return true;

        for (VisibilityPlayer visibilityPlayer : players.values()) {
            Player player = visibilityPlayer.getPlayer();
            if (player == null)
                continue;

            updateVisiblityForPlayer(player);
        }

        return true;
    }

    public VisibilityGroup getGroup(String name) {
        return groups.get(name);
    }

    public Collection<VisibilityGroup> getGroups() {
        return groups.values();
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }

    public void setRemoveType(VisibilityPlayerRemoveType removeType) {
        this.removeType = removeType;
    }

    public VisibilityPlayerRemoveType getRemoveType() {
        return removeType;
    }

    protected VisibilityGroup createInstance(VisibilityManager visibilityManager, String name) {
        return new VisibilityGroup(visibilityManager, name);
    }


    public enum VisibilityPlayerRemoveType {
        NEVER,
        ON_LEAVE
    }

}
