package net.apartium.cocoabeans.scoreboard;

import net.apartium.cocoabeans.state.SetObservable;

import java.util.Collection;
import java.util.Set;

public interface BoardPlayerGroup<P> {

    void add(P player);
    void addAll(Collection<P> players);

    void remove(P player);
    void removeAll(Collection<P> players);

    void clear();

    SetObservable<P> observePlayers();
    Set<P> players();

}
