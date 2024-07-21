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
        return addPlayer(manager.getPlayer(player));
    }

    public boolean addPlayer(UUID uuid) {
        return addPlayer(manager.getPlayer(uuid));
    }

    public boolean addPlayer(VisibilityPlayer player) {
        if (!players.add(player))
            return false;

        boolean empty = player.getVisibleGroups().isEmpty();

        player.addVisibleGroup(this);
        Player bukkitPlayer = player.getPlayer().orElse(null);
        if (empty && bukkitPlayer != null) {
            manager.handlePlayerJoin(bukkitPlayer);
        } else if (bukkitPlayer != null) {
            for (VisibilityPlayer visibilityPlayer : players) {
                Optional<Player> target = visibilityPlayer.getPlayer();
                if (target.isEmpty())
                    continue;

                manager.updateVisiblityForPlayer(target.get());
            }
        }

        return true;
    }

    public boolean removePlayer(Player player) {
        return removePlayer(manager.getPlayer(player));
    }

    public boolean removePlayer(VisibilityPlayer player) {
        if (!players.remove(player))
            return false;

        player.removeVisibleGroup(this);
        if (player.getPlayer().isEmpty())
            return true;

        manager.updateVisiblityForPlayer(player.getPlayer().get());
        for (VisibilityPlayer visibilityPlayer : players) {
            Player bukkitPlayer = visibilityPlayer.getPlayer().orElse(null);
            if (bukkitPlayer == null)
                continue;

            manager.updateVisiblityForPlayer(bukkitPlayer);
        }

        return true;
    }

    public boolean addGroup(VisibilityGroup group, boolean visible) {
        boolean result;
        if (visible)
            result = visibleGroups.add(group);
        else
            result = hiddenGroups.add(group);

        if (!result)
            return false;

        for (VisibilityPlayer visibilityPlayer : players) {
            Optional<Player> target = visibilityPlayer.getPlayer();
            if (target.isEmpty())
                continue;

            manager.updateVisiblityForPlayer(target.get());
        }

        return true;
    }

    public boolean removeVisibleGroup(VisibilityGroup group) {
        if (!visibleGroups.remove(group))
            return false;

        for (VisibilityPlayer visibilityPlayer : players) {
            Optional<Player> target = visibilityPlayer.getPlayer();
            if (target.isEmpty())
                continue;

            manager.updateVisiblityForPlayer(target.get());
        }

        return true;
    }

    public boolean removeHiddenGroup(VisibilityGroup group) {
        if (!hiddenGroups.remove(group))
            return false;

        for (VisibilityPlayer visibilityPlayer : players) {
            Optional<Player> target = visibilityPlayer.getPlayer();
            if (target.isEmpty())
                continue;

            manager.updateVisiblityForPlayer(target.get());
        }

        return true;
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
