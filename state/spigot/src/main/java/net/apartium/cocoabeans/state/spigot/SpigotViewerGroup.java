package net.apartium.cocoabeans.state.spigot;

import net.apartium.cocoabeans.state.ViewerGroup;
import net.apartium.cocoabeans.state.Observable;
import net.apartium.cocoabeans.state.SetObservable;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * @see ViewerGroup
 */
@ApiStatus.AvailableSince("0.0.41")
public class SpigotViewerGroup implements ViewerGroup<Player> {

    private final SetObservable<Player> playerSetObservable;
    private final Set<Player> players;

    public SpigotViewerGroup(Set<Player> players) {
        this.players = players;
        this.playerSetObservable = Observable.set(players);
    }

    @Override
    public void add(Player player) {
        playerSetObservable.add(player);
    }

    @Override
    public void addAll(Collection<Player> players) {
        playerSetObservable.addAll(players);
    }

    @Override
    public void remove(Player player) {
        playerSetObservable.remove(player);
    }

    @Override
    public void removeAll(Collection<Player> players) {
        playerSetObservable.removeAll(players);
    }

    @Override
    public void clear() {
        playerSetObservable.clear();
    }

    @Override
    public SetObservable<Player> observePlayers() {
        return playerSetObservable;
    }

    @Override
    public Set<Player> players() {
        return Collections.unmodifiableSet(players);
    }
}
