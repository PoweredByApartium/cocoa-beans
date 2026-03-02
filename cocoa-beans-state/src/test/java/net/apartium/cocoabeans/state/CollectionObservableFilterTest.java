package net.apartium.cocoabeans.state;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CollectionObservableFilterTest {

    private static final class TrackedBool implements MutableObservable<Boolean> {
        private boolean value;
        private final Set<Observer> observers = new HashSet<>();

        TrackedBool(boolean initial) {
            this.value = initial;
        }

        @Override
        public void set(Boolean value) {
            if (Objects.equals(this.value, value))
                return;

            this.value = value;

            for (Observer o : Set.copyOf(observers)) {
                o.flagAsDirty(this);
            }
        }

        @Override
        public Boolean get() {
            return value;
        }

        @Override
        public void observe(Observer observer) {
            observers.add(observer);
        }

        @Override
        public boolean removeObserver(Observer observer) {
            return observers.remove(observer);
        }

        int observerCount() {
            return observers.size();
        }
    }

    private static final class Player {
        private final String id;
        private final TrackedBool alive;

        Player(String id, boolean alive) {
            this.id = id;
            this.alive = new TrackedBool(alive);
        }

        Observable<Boolean> alive() {
            return alive;
        }

        void setAlive(boolean value) {
            alive.set(value);
        }

        int aliveObserverCount() {
            return alive.observerCount();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Player other)) return false;
            return Objects.equals(this.id, other.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }

        @Override
        public String toString() {
            return "Player(" + id + ")";
        }
    }

    @Test
    void filter_tracksMembership_onAddRemoveAndToggle() {
        SetObservable<Player> players = Observable.set();
        CollectionObservable<Player, Set<Player>> alivePlayers = players.filter(Player::alive);

        Player a = new Player("a", true);
        Player b = new Player("b", false);
        Player c = new Player("c", true);

        players.add(a);
        players.add(b);
        players.add(c);

        assertEquals(Set.of(a, c), alivePlayers.get(), "initial filtered set should include only alive");

        b.setAlive(true);
        assertEquals(Set.of(a, b, c), alivePlayers.get(), "toggling predicate true should add element");

        a.setAlive(false);
        assertEquals(Set.of(b, c), alivePlayers.get(), "toggling predicate false should remove element");

        players.remove(c);
        assertEquals(Set.of(b), alivePlayers.get(), "removing from source should remove from filtered view");

        players.clear();
        assertEquals(Set.of(), alivePlayers.get(), "clearing source should clear filtered view");
    }

    @Test
    void filter_unsubscribesPredicateObservers_whenElementRemoved() {
        SetObservable<Player> players = Observable.set();
        CollectionObservable<Player, Set<Player>> alivePlayers = players.filter(Player::alive);

        Player a = new Player("a", true);

        players.add(a);

        assertEquals(Set.of(a), alivePlayers.get());
        assertEquals(1, a.aliveObserverCount(), "filter should observe element predicate while element is present");

        players.remove(a);

        assertEquals(Set.of(), alivePlayers.get());
        assertEquals(0, a.aliveObserverCount(), "filter must detach predicate observer when element is removed");

        a.setAlive(false);
        assertEquals(Set.of(), alivePlayers.get());
        a.setAlive(true);
        assertEquals(Set.of(), alivePlayers.get());
    }

    @Test
    void filter_resultIsSnapshot_andImmutableLikeOtherCollectionObservables() {
        SetObservable<Player> players = Observable.set();
        CollectionObservable<Player, Set<Player>> alivePlayers = players.filter(Player::alive);

        Player a = new Player("a", true);
        players.add(a);

        Set<Player> snapshot = alivePlayers.get();
        assertEquals(Set.of(a), snapshot);

        Player x = new Player("x", true);
        assertThrows(UnsupportedOperationException.class, () -> snapshot.add(x));
    }
}