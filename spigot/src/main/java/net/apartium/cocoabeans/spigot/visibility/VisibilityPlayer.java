package net.apartium.cocoabeans.spigot.visibility;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.*;

public class VisibilityPlayer {

    private final VisibilityManager manager;
    private final UUID uuid;
    private WeakReference<Player> playerRef;
    private final Set<VisibilityGroup> visibleGroups;

    public VisibilityPlayer(VisibilityManager manager, UUID uuid) {
        this.manager = manager;
        this.uuid = uuid;
        this.playerRef = new WeakReference<>(null);
        this.visibleGroups = Collections.newSetFromMap(new WeakHashMap<>());

    }

    public VisibilityPlayer(VisibilityManager manager, Player player) {
        this.manager = manager;
        this.uuid = player.getUniqueId();
        this.playerRef = new WeakReference<>(player);
        this.visibleGroups = Collections.newSetFromMap(new WeakHashMap<>());
    }

    public VisibilityManager getManager() {
        return manager;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public @Nullable Player getPlayer() {
        Player player = playerRef.get();

        if (player != null)
            return player;

        player = Bukkit.getPlayer(uuid);
        playerRef = new WeakReference<>(player);

        return player;
    }

    public Collection<VisibilityGroup> getVisibleGroups() {
        return visibleGroups;
    }

    /* package-private */ void addVisibleGroup(VisibilityGroup group) {
        visibleGroups.add(group);

        Player player = getPlayer();
        if (player != null)
            manager.updateVisiblityForPlayer(this, group);
    }

    /* package-private */ void removeVisibleGroup(VisibilityGroup group) {
        visibleGroups.remove(group);

        Player player = getPlayer();
        if (player != null)
            manager.updateVisiblityForPlayer(player);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof VisibilityPlayer && uuid.equals(((VisibilityPlayer) obj).uuid);
    }
}
