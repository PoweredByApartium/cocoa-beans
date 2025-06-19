package net.apartium.cocoabeans.scoreboard.spigot;

import net.apartium.cocoabeans.scoreboard.BoardPlayerGroup;
import net.apartium.cocoabeans.state.Observable;
import net.apartium.cocoabeans.state.SetObservable;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Set;

public class SpigotBoardPlayerGroup implements BoardPlayerGroup<Player> {

    private final SetObservable<Player> playerSetObservable;
    private final Set<Player> players;

    public SpigotBoardPlayerGroup(Set<Player> players) {
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
        return players;
    }
}
