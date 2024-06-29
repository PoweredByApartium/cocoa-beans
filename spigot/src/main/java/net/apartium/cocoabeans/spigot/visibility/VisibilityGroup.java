package net.apartium.cocoabeans.spigot.visibility;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class VisibilityGroup {

    private final VisibilityManager manager;
    private final String name;

    private final Set<VisibilityPlayer> players;

    private final Set<VisibilityGroup> visibleGroups;
    private final Set<VisibilityGroup> hiddenGroups;

    /* package-private */ VisibilityGroup(VisibilityManager manager, String name) {
        this.manager = manager;
        this.name = name;

        this.players = new HashSet<>();

        this.visibleGroups = Collections.newSetFromMap(new WeakHashMap<>());
        this.hiddenGroups = Collections.newSetFromMap(new WeakHashMap<>());
    }

    public boolean addPlayer(Player player) {
        VisibilityPlayer visibilityPlayer = manager.getPlayer(player);

        if (!players.add(visibilityPlayer))
            return false;

        visibilityPlayer.addVisibleGroup(this);
        return true;
    }

    public boolean addPlayer(UUID uuid) {
        VisibilityPlayer visibilityPlayer = manager.getPlayer(uuid);

        if (!players.add(visibilityPlayer))
            return false;

        visibilityPlayer.addVisibleGroup(this);
        return true;
    }

    public boolean addPlayer(VisibilityPlayer player) {
        if (!players.add(player))
            return false;

        player.addVisibleGroup(this);
        return true;
    }

    public boolean removePlayer(Player player) {
        VisibilityPlayer visibilityPlayer = manager.getPlayer(player);
        if (!players.remove(visibilityPlayer))
            return false;

        visibilityPlayer.removeVisibleGroup(this);
        return true;
    }

    public boolean removePlayer(VisibilityPlayer player) {
        if (!players.remove(player))
            return false;

        player.removeVisibleGroup(this);
        return true;
    }

    public boolean addGroup(VisibilityGroup group, boolean visible) {
        if (visible)
            return visibleGroups.add(group);
        else
            return hiddenGroups.add(group);
    }

    public boolean removeVisibleGroup(VisibilityGroup group) {
        return visibleGroups.remove(group);
    }

    public boolean removeHiddenGroup(VisibilityGroup group) {
        return hiddenGroups.remove(group);
    }


    public Collection<VisibilityPlayer> getPlayers() {
        return players;
    }

    public String getName() {
        return name;
    }

    public Collection<VisibilityGroup> getVisibleGroups() {
        return visibleGroups;
    }

    public Collection<VisibilityGroup> getHiddenGroups() {
        return hiddenGroups;
    }

    public boolean hasPlayer(Player player) {
        return players.contains(manager.getPlayer(player));
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }
}
