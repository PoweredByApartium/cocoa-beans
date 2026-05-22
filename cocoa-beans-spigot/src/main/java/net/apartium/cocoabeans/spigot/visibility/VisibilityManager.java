package net.apartium.cocoabeans.spigot.visibility;

import net.apartium.cocoabeans.spigot.VersionedImplInstantiator;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

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
            updateVisibilityForPlayer(player);
        }
    }

    /**
     * Handle player joining the server
     * @param joinPlayer player who joined
     */
    public void handlePlayerJoin(Player joinPlayer) {
        VisibilityPlayer visibilityPlayer = getPlayer(joinPlayer);
        visibilityPlayer.onJoin();

        updateVisibilityForPlayer(joinPlayer);

        for (Player target : plugin.getServer().getOnlinePlayers()) {
            if (target == joinPlayer)
                continue;

            if (getPlayer(target).getVisibleGroups().isEmpty())
                continue;

            boolean shouldSee = canSee(target, joinPlayer);
            boolean currentlySees = playerVisibilityController.canSeePlayer(target, joinPlayer);

            if (shouldSee && !currentlySees)
                playerVisibilityController.showPlayer(plugin, target, joinPlayer);
            else if (!shouldSee && currentlySees)
                playerVisibilityController.hidePlayer(plugin, target, joinPlayer);
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
     * Checks if a player with the specified UUID is registered in the in-memory state.
     *
     * @param uuid the unique identifier of the player to check
     * @return true if the player is registered, false otherwise
     */
    @ApiStatus.AvailableSince("0.0.50")
    public boolean hasPlayer(UUID uuid) {
        return players.containsKey(uuid);
    }

    /**
     * Checks if a player is registered in the in-memory state.
     *
     * @param player the player to check
     * @return true if the player is registered, false otherwise
     */
    @ApiStatus.AvailableSince("0.0.50")
    public boolean hasPlayer(Player player) {
        if (player == null)
            return false;

        return hasPlayer(player.getUniqueId());
    }

    /**
     * Remove player from the in-memory state, including dis-associating with all its groups
     * @param uuid player uuid
     */
    public void removePlayer(UUID uuid) {
        VisibilityPlayer remove = players.remove(uuid);

        if (remove == null)
            return;

        for (VisibilityGroup group : new ArrayList<>(remove.getVisibleGroups())) {
            group.removePlayer(remove);
        }

    }

    /* package-private */ Set<Player> computeVisiblePlayers(Player player) {
        VisibilityPlayer visibilityPlayer = getPlayer(player);
        Set<Player> result = new HashSet<>();

        if (visibilityPlayer.getVisibleGroups().isEmpty()) {
            for (Player target : plugin.getServer().getOnlinePlayers()) {
                if (target == player)
                    continue;
                if (getPlayer(target).getVisibleGroups().isEmpty())
                    result.add(target);
            }
            return result;
        }

        for (VisibilityGroup group : visibilityPlayer.getVisibleGroups()) {
            if (!group.hasPlayer(player))
                continue;

            for (VisibilityGroup targetGroup : group.getVisibleGroups()) {
                addVisibleGroupPlayers(player, group, targetGroup, result);
            }

            addVisibleGroupPlayers(player, group, group, result);
        }

        return result;
    }

    private void addVisibleGroupPlayers(Player player, VisibilityGroup sourceGroup, VisibilityGroup targetGroup, Set<Player> result) {
        for (VisibilityPlayer target : targetGroup.getPlayers()) {
            Player targetPlayer = target.getPlayer().orElse(null);
            if (targetPlayer == null || player == targetPlayer)
                continue;

            if (sourceGroup.getHiddenGroups().stream().anyMatch(tg -> tg.hasPlayer(targetPlayer)))
                continue;

            result.add(targetPlayer);
        }
    }

    /* package-private */ void updateVisibilityForPlayer(Player player) {
        Set<Player> shouldBeVisible = computeVisiblePlayers(player);

        for (Player target : plugin.getServer().getOnlinePlayers()) {
            if (target == player)
                continue;

            boolean shouldSee = shouldBeVisible.contains(target);
            boolean currentlySees = playerVisibilityController.canSeePlayer(player, target);

            if (shouldSee && !currentlySees) {
                playerVisibilityController.showPlayer(plugin, player, target);
            } else if (!shouldSee && currentlySees) {
                playerVisibilityController.hidePlayer(plugin, player, target);
            }

            updateReverseDirection(player, target, shouldSee);
        }
    }

    private void updateReverseDirection(Player player, Player target, boolean shouldSee) {
        if (getPlayer(target).getVisibleGroups().isEmpty()) {
            boolean targetCanSee = playerVisibilityController.canSeePlayer(target, player);

            if (shouldSee && !targetCanSee) {
                playerVisibilityController.showPlayer(plugin, target, player);
            } else if (!shouldSee && targetCanSee) {
                playerVisibilityController.hidePlayer(plugin, target, player);
            }
        }
    }

    /**
     * Get or create a visibility group by name.
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

            updateVisibilityForPlayer(player);
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

    public void onQuit(@NotNull Player player) {
        VisibilityPlayer visibilityPlayer = players.get(player.getUniqueId());
        if (visibilityPlayer == null)
            return;

        visibilityPlayer.onQuit();
    }
}
