package net.apartium.cocoabeans.spigot.visibility;

import net.apartium.cocoabeans.spigot.VersionedImplInstantiator;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.stream.Stream;

/**
 * Manager class for cocoa beans hide API
 * @see VisibilityGroup
 * @see VisibilityPlayer
 * Note: There can only be a single instance of this class.
 */
public class VisibilityManager {

    private final Map<String, VisibilityGroup> groups = new HashMap<>();
    private final Map<UUID, VisibilityPlayer> players = new HashMap<>();
    private final PlayerVisibilityController playerVisibilityController;
    private final JavaPlugin plugin;
    private VisibilityListener visibilityListener;

    /**
     * Create a new instance of Visibility manager
     * @param plugin plugin to associate this instance with
     */
    public VisibilityManager(JavaPlugin plugin) {
        this(plugin, VersionedImplInstantiator.createPlayerVisibilityController());
    }

    public VisibilityManager(JavaPlugin plugin, PlayerVisibilityController playerVisibilityController) {
        this.plugin = plugin;
        this.playerVisibilityController = playerVisibilityController;
    }

    /**
     * Register listener to automatically handle player join and quit
     * @param removeType See {@link VisibilityPlayerRemoveType}
     *
     * If this method is not called the registering plugin is responsible for triggering the following methods:
     * @see VisibilityManager#handlePlayerJoin(Player)
     * @see VisibilityManager#removePlayer(UUID)
     */
    public void registerListener(VisibilityPlayerRemoveType removeType) {
        if (visibilityListener != null)
            return;

        visibilityListener = new VisibilityListener(this, removeType);
        plugin.getServer().getPluginManager().registerEvents(visibilityListener, plugin);
    }

    /**
     * Register listener to automatically handle player join and quit with {@link VisibilityPlayerRemoveType#ON_LEAVE}
     * If this method is not called the registering plugin is responsible for triggering the following methods:
     * @see VisibilityManager#handlePlayerJoin(Player)
     * @see VisibilityManager#removePlayer(UUID)
     */
    public void registerListener() {
        registerListener(VisibilityPlayerRemoveType.ON_LEAVE);
    }

    /**
     * Unregisters the listener.
     * @see VisibilityManager#registerListener(VisibilityPlayerRemoveType)
     */
    public void unregisterListener() {
        if (visibilityListener == null)
            return;

        HandlerList.unregisterAll(visibilityListener);
        visibilityListener = null;
    }

    /**
     * Reload visibility for all online players
     */
    public void reloadVisibility() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            updateVisiblityForPlayer(player);
        }
    }

    /**
     * Handle player joining the server
     * @param joinPlayer player who joined
     */
    public void handlePlayerJoin(Player joinPlayer) {
        VisibilityPlayer visibilityPlayer = getPlayer(joinPlayer);

        if (visibilityPlayer.getVisibleGroups().isEmpty()) {
            for (Player target : plugin.getServer().getOnlinePlayers()) {
                if (target == joinPlayer)
                    continue;

                if (getPlayer(target).getVisibleGroups().isEmpty()) {
                    playerVisibilityController.showPlayer(plugin, target, joinPlayer);
                    continue;
                }

                playerVisibilityController.hidePlayer(plugin, joinPlayer, target);
                playerVisibilityController.hidePlayer(plugin, target, joinPlayer);
            }

            return;
        }

        for (Player target : plugin.getServer().getOnlinePlayers()) {
            if (target == joinPlayer)
                continue;

            if (getPlayer(target).getVisibleGroups().isEmpty())
                continue;

            playerVisibilityController.hidePlayer(plugin, target, joinPlayer);
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
                Player targetPlayer = player.getPlayer().orElse(null);
                if (targetPlayer == null)
                    continue;

                if (targetPlayer == joinPlayer)
                    continue;

                playerToShow.add(targetPlayer);
            }
        }

        for (Player target : playerToShow) {
            playerVisibilityController.showPlayer(plugin, target, joinPlayer);
        }

    }

    /**
     * Get player instance associated with this manager for given player uuid
     * @param uuid player uuid, no checks are performed on this argument
     * @return visibility player instance, cannot be null
     */
    public VisibilityPlayer getPlayer(UUID uuid) {
        return players.computeIfAbsent(uuid, (key) -> new VisibilityPlayer(this, uuid));
    }

    /**
     * Get player instance associated with this manager for given player
     * @param player player, cannot be null
     * @return visibility player instance, cannot be null
     */
    public VisibilityPlayer getPlayer(Player player) {
        return players.computeIfAbsent(player.getUniqueId(), (key) -> new VisibilityPlayer(this, player));
    }

    /**
     * Remove player from the in-memory state, including dis-associating with all its groups
     * @param uuid player uuid
     */
    public void removePlayer(UUID uuid) {
        VisibilityPlayer remove = players.remove(uuid);

        if (remove == null)
            return;

        for (VisibilityGroup group : remove.getVisibleGroups()) {
            group.removePlayer(remove);
        }

    }

    /* package-private */ void updateVisiblityForPlayer(Player player) {
        VisibilityPlayer visibilityPlayer = getPlayer(player);

        if (visibilityPlayer.getVisibleGroups().isEmpty()) {
            for (Player target : plugin.getServer().getOnlinePlayers()) {
                if (target == player)
                    continue;

                if (!getPlayer(target).getVisibleGroups().isEmpty()) {
                    playerVisibilityController.hidePlayer(plugin, player, target);
                    playerVisibilityController.hidePlayer(plugin, target, player);
                    continue;
                }

                playerVisibilityController.showPlayer(plugin, player, target);
                playerVisibilityController.showPlayer(plugin, target, player);
            }

            return;
        }

        for (Player target : plugin.getServer().getOnlinePlayers()) {
            if (target == player)
                continue;

            if (getPlayer(target).getVisibleGroups().isEmpty()) {
                playerVisibilityController.hidePlayer(plugin, player, target);
                playerVisibilityController.hidePlayer(plugin, target, player);
                continue;
            }

            playerVisibilityController.hidePlayer(plugin, player, target);
        }

        Set<Player> playersToShow = new HashSet<>();

        for (VisibilityGroup group : visibilityPlayer.getVisibleGroups()) {
            if (!group.hasPlayer(player))
                continue;

            for (VisibilityGroup targetGroup : group.getVisibleGroups()) {
                for (VisibilityPlayer target : targetGroup.getPlayers()) {
                    Player targetPlayer = target.getPlayer().orElse(null);
                    if (targetPlayer == null)
                        continue;

                    if (player == targetPlayer)
                        continue;

                    if (playersToShow.contains(targetPlayer))
                        continue;

                    if (group.getHiddenGroups().stream().anyMatch(tg -> tg.hasPlayer(targetPlayer)))
                        continue;

                    playersToShow.add(targetPlayer);
                }
            }

            for (VisibilityPlayer target : group.getPlayers()) {
                Player targetPlayer = target.getPlayer().orElse(null);
                if (targetPlayer == null)
                    continue;

                if (player == targetPlayer)
                    continue;

                if (playersToShow.contains(targetPlayer))
                    continue;

                if (group.getHiddenGroups().stream().anyMatch(targetGroup -> targetGroup.hasPlayer(targetPlayer)))
                    continue;

                playersToShow.add(targetPlayer);
            }
        }

        for (Player target : playersToShow)
            playerVisibilityController.showPlayer(plugin, player, target);

    }

    /**
     * Get or create a visiblity group by name.
     * @param name unique group name
     * @return a group associated with given name, cannot be null
     */
    public VisibilityGroup getOrCreateGroup(String name) {
        return groups.computeIfAbsent(name, n -> new VisibilityGroup(this, n));
    }

    /**
     * Deletes a group from in-memory state and updating relevant players accordingly
     * @param name group name
     * @return true if the group exists, else false
     */
    public boolean deleteGroup(String name) {
        VisibilityGroup remove = groups.remove(name);
        if (remove == null)
            return false;

        if (remove.getPlayers().isEmpty())
            return true;

        for (VisibilityPlayer visibilityPlayer : players.values()) {
            visibilityPlayer.removeVisibleGroup(remove);

            Player player = visibilityPlayer.getPlayer().orElse(null);
            if (player == null)
                continue;

            updateVisiblityForPlayer(player);
        }

        return true;
    }

    /**
     * Checks whether 2 players can see each other.
     * @param player player
     * @param target target player
     * @return if can see each other - true, else false
     */
    public boolean canSee(Player player, Player target) {
        VisibilityPlayer visibilityPlayer = getPlayer(player);
        VisibilityPlayer visibilityTarget = getPlayer(target);

        if (visibilityPlayer.getVisibleGroups().isEmpty() && visibilityTarget.getVisibleGroups().isEmpty())
            return true;

        if (visibilityPlayer.getVisibleGroups().isEmpty() || visibilityTarget.getVisibleGroups().isEmpty())
            return false;

        for (VisibilityGroup group : visibilityPlayer.getVisibleGroups()) {
            if (!group.hasPlayer(target) && group.getVisibleGroups().stream().noneMatch(targetGroup -> targetGroup.hasPlayer(target)))
                continue;

            if (group.getHiddenGroups().stream().anyMatch(targetGroup -> targetGroup.hasPlayer(target)))
                continue;

            return true;
        }

        return false;
    }

    /**
     * Get all visibility groups currently registered in memory
     * @return all visibility groups currently registered in memory
     */
    public Collection<VisibilityGroup> getGroups() {
        return Collections.unmodifiableCollection(groups.values());
    }

    /**
     * Gets all players registered with the in-memory state
     * @see VisibilityPlayer
     * @return all visibility players currently registered
     */
    public Stream<VisibilityPlayer> getPlayers() {
        return players.values().stream();
    }

}
