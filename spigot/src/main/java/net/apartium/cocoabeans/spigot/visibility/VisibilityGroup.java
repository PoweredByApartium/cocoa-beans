package net.apartium.cocoabeans.spigot.visibility;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

/**
 * Represents a group of players who can see each other, and can or cannot see different visibility group
 * Each visibility group is identified by a unique name and belongs to a {@link VisibilityManager} instance
 */
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

    /**
     * Add a member to this group.
     * @param player player to add
     * @return false if already a member of this group, else false
     */
    public boolean addPlayer(Player player) {
        return addPlayer(manager.getPlayer(player));
    }

    /**
     * Add a member to this group.
     * @param uuid unique id of player to add
     * @return false if already a member of this group, else false
     */
    public boolean addPlayer(UUID uuid) {
        return addPlayer(manager.getPlayer(uuid));
    }

    /**
     * Add a member to this group.
     * @param player player to add
     * @return false if already a member of this group, else false
     */
    public boolean addPlayer(VisibilityPlayer player) {
        if (!players.add(player))
            return false;

        boolean empty = player.getVisibleGroups().isEmpty();

        player.addVisibleGroup(this);
        Player bukkitPlayer = player.getPlayer().orElse(null);
        if (empty && bukkitPlayer != null) {
            manager.handlePlayerJoin(bukkitPlayer);
        } else if (bukkitPlayer != null) {
            Set<VisibilityPlayer> visited = new HashSet<>();

            for (VisibilityGroup visibilityGroup : player.getVisibleGroups()) {
                for (VisibilityPlayer visibilityPlayer : visibilityGroup.getPlayers()) {
                    if (!visited.add(visibilityPlayer))
                        continue;

                    Optional<Player> target = visibilityPlayer.getPlayer();
                    if (target.isEmpty())
                        continue;

                    manager.updateVisiblityForPlayer(target.get());
                }
            }
        }

        return true;
    }

    /**
     * Removes a player from the group
     * @param player player to remove
     * @return true if was part of the group, else false
     */
    public boolean removePlayer(Player player) {
        return removePlayer(manager.getPlayer(player));
    }

    /**
     * Removes a player from the group
     * @param player player to remove
     * @return true if was part of the group, else false
     */
    public boolean removePlayer(VisibilityPlayer player) {
        if (!players.remove(player))
            return false;

        player.removeVisibleGroup(this);
        if (player.getPlayer().isEmpty())
            return true;

        Set<VisibilityPlayer> visited = new HashSet<>();

        visited.add(player);
        manager.updateVisiblityForPlayer(player.getPlayer().get());

        for (VisibilityPlayer visibilityPlayer : getPlayers()) {
            if (!visited.add(visibilityPlayer))
                continue;

            Optional<Player> target = visibilityPlayer.getPlayer();
            if (target.isEmpty())
                continue;

            manager.updateVisiblityForPlayer(target.get());
        }

        for (VisibilityGroup visibilityGroup : player.getVisibleGroups()) {
            for (VisibilityPlayer visibilityPlayer : visibilityGroup.getPlayers()) {
                if (!visited.add(visibilityPlayer))
                    continue;

                Optional<Player> target = visibilityPlayer.getPlayer();
                if (target.isEmpty())
                    continue;

                manager.updateVisiblityForPlayer(target.get());
            }
        }

        return true;
    }

    /**
     * Adds a group which members of the current group can see
     * @param group group
     * @return true if existed, false otherwise
     */
    public boolean addVisibleGroup(VisibilityGroup group) {
        return addGroup(group, true);
    }

    /**
     * Adds a group which members of the current group cannot see
     * @param group group
     * @return true if existed, false otherwise
     */
    public boolean addHiddenGroup(VisibilityGroup group) {
        return addGroup(group, false);
    }

    private boolean addGroup(VisibilityGroup group, boolean visible) {
        if (group == this)
            return false;

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

    /**
     * Remove visible group
     * @param group group
     * @return true if removed, false if doesn't exist
     * @see VisibilityGroup#addVisibleGroup(VisibilityGroup)
     */
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

    /**
     * Remove hidden group
     * @param group group
     * @return true if removed, false if doesn't exist
     * @see VisibilityGroup#addHiddenGroup(VisibilityGroup)
     */
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

    /**
     * Get all players who are direct members of this group
     * @return unmodifiable viwe of backing collection
     */
    public Collection<VisibilityPlayer> getPlayers() {
        return Collections.unmodifiableCollection(players);
    }

    /**
     * Get name of visibility group
     * @return visibility group name
     */
    public String getName() {
        return name;
    }

    /**
     * Get all groups who are visible to current group
     * @return unmodifiable view of backing collection
     */
    public Collection<VisibilityGroup> getVisibleGroups() {
        return Collections.unmodifiableCollection(visibleGroups);
    }

    /**
     * Get all groups who are hidden from current group
     * @return unmodifiable view of backing collection
     */
    public Collection<VisibilityGroup> getHiddenGroups() {
        return Collections.unmodifiableCollection(hiddenGroups);
    }

    /**
     * Checks whether group contains a player
     * @param player player
     * @return true if contains given player, else false
     */
    public boolean hasPlayer(Player player) {
        return players.contains(manager.getPlayer(player));
    }

}
