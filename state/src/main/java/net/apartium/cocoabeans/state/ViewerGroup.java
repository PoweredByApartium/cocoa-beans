package net.apartium.cocoabeans.state;

import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;
import java.util.Set;

/**
 * BoardPlayerGroup is group of player that are able to see the packets we send
 * @param <P> type of Player (Spigot/Minestom/etc...)
 */
@ApiStatus.AvailableSince("0.0.41")
public interface ViewerGroup<P> {

    /**
     * Add a player as viewer
     * @param player player that will be added
     */
    void add(P player);

    /**
     * Add a group of players as viewers
     * @param players players that will be added
     */
    void addAll(Collection<P> players);

    /**
     * Remove a player from viewers
     * @param player player that will be removed
     */
    void remove(P player);

    /**
     * Remove all specified players from viewers
     * @param players players that will be removed
     */
    void removeAll(Collection<P> players);

    /**
     * Clear the entire viewer set
     */
    void clear();

    /**
     * Observable of viewers that will be used to track who to add & who to remove
     * @return Observable of viewers
     */
    SetObservable<P> observePlayers();

    /**
     * Set of current viewers
     * @return viewers
     */
    Set<P> players();

}
