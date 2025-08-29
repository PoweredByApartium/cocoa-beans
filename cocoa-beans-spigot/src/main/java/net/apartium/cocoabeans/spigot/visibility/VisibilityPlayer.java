package net.apartium.cocoabeans.spigot.visibility;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.ref.WeakReference;
import java.util.*;

/**
 * Represents a player known to the visibility api, whether online or not
 * @see VisibilityManager
 * @see VisibilityManager#getPlayer(UUID)
 * @see VisibilityManager#getPlayer(Player)
 */
public class VisibilityPlayer {

    private final VisibilityManager manager;
    private final UUID uuid;
    private WeakReference<Player> playerRef;
    private final Set<VisibilityGroup> visibleGroups;

    /* package-private */ VisibilityPlayer(VisibilityManager manager, UUID uuid) {
        this.manager = manager;
        this.uuid = uuid;
        this.playerRef = new WeakReference<>(Bukkit.getPlayer(uuid));
        this.visibleGroups = Collections.newSetFromMap(new WeakHashMap<>());

    }

    /* package-private */ VisibilityPlayer(VisibilityManager manager, Player player) {
        this.manager = manager;
        this.uuid = player.getUniqueId();
        this.playerRef = new WeakReference<>(player);
        this.visibleGroups = Collections.newSetFromMap(new WeakHashMap<>());
    }

    /**
     * Get parent manager instance
     * @return parent manager instance
     */
    public VisibilityManager getManager() {
        return manager;
    }

    /**
     * Get player unique id
     * @return player unique id
     */
    public UUID getUniqueId() {
        return uuid;
    }

    /**
     * Attempts to retrieve bukkit player instance
     * @return player if found, empty if not online
     */
    public Optional<Player> getPlayer() {
        Player player = playerRef.get();

        if (player != null)
            return Optional.of(player);

        player = Bukkit.getPlayer(uuid);
        if (player == null)
            return Optional.empty();

        playerRef = new WeakReference<>(player);

        return Optional.of(player);
    }

    /**
     * Get groups this player is a member of
     * @return unmodifiable view of backing collection
     */
    public Collection<VisibilityGroup> getVisibleGroups() {
        return Collections.unmodifiableCollection(visibleGroups);
    }

    /* package-private */ void addVisibleGroup(VisibilityGroup group) {
        visibleGroups.add(group);
    }

    /* package-private */ void removeVisibleGroup(VisibilityGroup group) {
        visibleGroups.remove(group);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VisibilityPlayer that = (VisibilityPlayer) o;
        return Objects.equals(manager, that.manager) && Objects.equals(uuid, that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(manager, uuid);
    }
}
