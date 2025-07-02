package net.apartium.cocoabeans.scoreboard.fixture;

import net.apartium.cocoabeans.scoreboard.ViewerGroup;
import net.apartium.cocoabeans.state.Observable;
import net.apartium.cocoabeans.state.SetObservable;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

public class TestViewerGroup implements ViewerGroup<MockPlayer> {

    private final SetObservable<MockPlayer> playerSetObservable;
    private final Set<MockPlayer> players;


    public TestViewerGroup(Set<MockPlayer> players) {
        this.players = players;
        this.playerSetObservable = Observable.set(players);
    }

    @Override
    public void add(MockPlayer player) {
        playerSetObservable.add(player);
    }

    @Override
    public void addAll(Collection<MockPlayer> players) {
        playerSetObservable.addAll(players);
    }

    @Override
    public void remove(MockPlayer player) {
        playerSetObservable.remove(player);
    }

    @Override
    public void removeAll(Collection<MockPlayer> players) {
        playerSetObservable.removeAll(players);
    }

    @Override
    public void clear() {
        playerSetObservable.clear();
    }

    @Override
    public SetObservable<MockPlayer> observePlayers() {
        return playerSetObservable;
    }

    @Override
    public Set<MockPlayer> players() {
        return Collections.unmodifiableSet(players);
    }
}
