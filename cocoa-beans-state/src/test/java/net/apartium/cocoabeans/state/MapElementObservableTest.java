package net.apartium.cocoabeans.state;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class MapElementObservableTest {

    @Test
    void mapEachShouldMapExistingElements() {
        ListObservable<GamePlayer> players = Observable.list(new ArrayList<>(List.of(
                new GamePlayer(UUID.randomUUID(), "Kfir"),
                new GamePlayer(UUID.randomUUID(), "Apartium")
        )));

        ListObservable<String> names = players.mapEach(GamePlayer::name);

        assertEquals(List.of("Kfir", "Apartium"), names.get());
    }

    @Test
    void mapEachShouldUpdateWhenElementIsAdded() {
        ListObservable<GamePlayer> players = Observable.list(new ArrayList<>(List.of(
                new GamePlayer(UUID.randomUUID(), "Kfir")
        )));

        ListObservable<String> names = players.mapEach(GamePlayer::name);

        assertEquals(List.of("Kfir"), names.get());

        players.add(new GamePlayer(UUID.randomUUID(), "Apartium"));

        assertEquals(List.of("Kfir", "Apartium"), names.get());
    }

    @Test
    void mapEachShouldUpdateWhenElementIsRemoved() {
        GamePlayer kfir = new GamePlayer(UUID.randomUUID(), "Kfir");
        GamePlayer apartium = new GamePlayer(UUID.randomUUID(), "Apartium");

        ListObservable<GamePlayer> players = Observable.list(new ArrayList<>(List.of(kfir, apartium)));

        ListObservable<String> names = players.mapEach(GamePlayer::name);

        assertEquals(List.of("Kfir", "Apartium"), names.get());

        players.remove(kfir);

        assertEquals(List.of("Apartium"), names.get());
    }

    @Test
    void mapEachShouldUpdateWhenCollectionIsCleared() {
        ListObservable<GamePlayer> players = Observable.list(new ArrayList<>(List.of(
                new GamePlayer(UUID.randomUUID(), "Kfir"),
                new GamePlayer(UUID.randomUUID(), "Apartium")
        )));

        ListObservable<String> names = players.mapEach(GamePlayer::name);

        assertEquals(List.of("Kfir", "Apartium"), names.get());

        players.clear();

        assertEquals(List.of(), names.get());
    }

    @Test
    void mapEachShouldCacheUntilBaseChanges() {
        ListObservable<GamePlayer> players = Observable.list(new ArrayList<>(List.of(
                new GamePlayer(UUID.randomUUID(), "Kfir")
        )));

        AtomicInteger calls = new AtomicInteger();

        ListObservable<String> names = players.mapEach(player -> {
            calls.incrementAndGet();
            return player.name();
        });

        assertEquals(List.of("Kfir"), names.get());
        assertEquals(List.of("Kfir"), names.get());
        assertEquals(List.of("Kfir"), names.get());

        assertEquals(1, calls.get());

        players.add(new GamePlayer(UUID.randomUUID(), "Apartium"));

        assertEquals(List.of("Kfir", "Apartium"), names.get());

        assertEquals(3, calls.get());
    }

    @Test
    void mapEachShouldExposeMappedSize() {
        ListObservable<GamePlayer> players = Observable.list(new ArrayList<>(List.of(
                new GamePlayer(UUID.randomUUID(), "Kfir")
        )));

        ListObservable<String> names = players.mapEach(GamePlayer::name);

        assertEquals(1, names.size().get());

        players.add(new GamePlayer(UUID.randomUUID(), "Apartium"));

        assertEquals(2, names.size().get());

        players.clear();

        assertEquals(0, names.size().get());
    }

    @Test
    void mapEachShouldNotifyObserversWhenBaseChanges() {
        ListObservable<GamePlayer> players = Observable.list(new ArrayList<>(List.of(
                new GamePlayer(UUID.randomUUID(), "Kfir")
        )));

        ListObservable<String> names = players.mapEach(GamePlayer::name);

        TestObserver observer = new TestObserver();
        names.observe(observer);

        players.add(new GamePlayer(UUID.randomUUID(), "Apartium"));

        assertTrue(observer.dirty);
        assertSame(names, observer.observable);
    }

    @Test
    void mapEachShouldWorkAfterFilter() {
        GamePlayer kfir = new GamePlayer(UUID.randomUUID(), "Kfir", true);
        GamePlayer apartium = new GamePlayer(UUID.randomUUID(), "Apartium", false);

        ListObservable<GamePlayer> players = Observable.list(new ArrayList<>(List.of(kfir, apartium)));

        ListObservable<String> aliveNames = players
                .filter(player -> Observable.immutable(player.alive()))
                .mapEach(GamePlayer::name);

        assertEquals(List.of("Kfir"), aliveNames.get());
    }

    @Test
    void mapEachShouldWorkBeforeFilter() {
        ListObservable<GamePlayer> players = Observable.list(new ArrayList<>(List.of(
                new GamePlayer(UUID.randomUUID(), "Kfir"),
                new GamePlayer(UUID.randomUUID(), "Apartium")
        )));

        ListObservable<String> namesStartingWithA = players
                .mapEach(GamePlayer::name)
                .filter(name -> Observable.immutable(name.startsWith("A")));

        assertEquals(List.of("Apartium"), new ArrayList<>(namesStartingWithA.get()));
    }

    @Test
    void mapEachShouldSupportChaining() {
        ListObservable<GamePlayer> players = Observable.list(new ArrayList<>(List.of(
                new GamePlayer(UUID.randomUUID(), "Kfir"),
                new GamePlayer(UUID.randomUUID(), "Apartium")
        )));

        ListObservable<Integer> nameLengths = players
                .mapEach(GamePlayer::name)
                .mapEach(String::length);

        assertEquals(List.of(4, 8), nameLengths.get());
    }

    private record GamePlayer(UUID uuid, String name, boolean alive) {

        private GamePlayer(UUID uuid, String name) {
            this(uuid, name, true);
        }

    }

    private static class TestObserver implements Observer {

        private boolean dirty;
        private Observable<?> observable;

        @Override
        public void flagAsDirty(Observable<?> observable) {
            this.dirty = true;
            this.observable = observable;
        }

    }

}