package net.apartium.cocoabeans.state;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class FlatMapElementObservableTest {

    @Test
    void flatMapEachShouldContainsDupe() {
        GamePlayer kfir = new GamePlayer(UUID.randomUUID(), Observable.mutable("Kfir"));
        GamePlayer apartium = new GamePlayer(UUID.randomUUID(), Observable.mutable("Apartium"));

        ListObservable<GamePlayer> players = Observable.list(new ArrayList<>(List.of(kfir, apartium)));

        ListObservable<String> names = players.flatMapEach(GamePlayer::name);

        assertEquals(List.of("Kfir", "Apartium"), names.get());

        apartium.name().set("Kfir");

        assertEquals(List.of("Kfir", "Kfir"), names.get());

        kfir.name().set("Apartium");

        assertEquals(List.of("Apartium", "Kfir"), names.get());
    }

    @Test
    void flatMapEacMultiDupe() {
        GamePlayer kfir = new GamePlayer(UUID.randomUUID(), Observable.mutable("Kfir"));
        GamePlayer apartium = new GamePlayer(UUID.randomUUID(), Observable.mutable("Apartium"));

        ListObservable<GamePlayer> players = Observable.list(new ArrayList<>(List.of(kfir, apartium, kfir, kfir, apartium)));

        ListObservable<String> names = players.flatMapEach(GamePlayer::name);

        assertEquals(List.of("Kfir", "Apartium", "Kfir", "Kfir", "Apartium"), names.get());

        players.remove(4);

        assertEquals(List.of("Kfir", "Apartium", "Kfir", "Kfir"), names.get());

        players.remove(3);
        kfir.name().set("Kfir2");

        assertEquals(List.of("Kfir2", "Apartium", "Kfir2"), names.get());

        try {
            Field observers = MutableObservableImpl.class.getDeclaredField("observers");
            observers.setAccessible(true);

            Set<Observer> kfirObservers = (Set<Observer>) observers.get(kfir.name);

            assertEquals(1, kfirObservers.size());

            players.removeIf(player -> kfir == player);

            assertEquals(List.of("Apartium"), names.get());
            assertEquals(0, kfirObservers.size());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail(e);
        }

    }

    @Test
    void flatMapEachShouldRemoveDupeOnSet() {
        GamePlayer kfir = new GamePlayer(UUID.randomUUID(), Observable.mutable("Kfir"));
        GamePlayer apartium = new GamePlayer(UUID.randomUUID(), Observable.mutable("Apartium"));

        SetObservable<GamePlayer> players = Observable.set(new HashSet<>(Set.of(kfir, apartium)));

        SetObservable<String> names = players.flatMapEach(GamePlayer::name);

        assertEquals(Set.of("Kfir", "Apartium"), names.get());

        apartium.name().set("Kfir");

        assertEquals(Set.of("Kfir"), names.get());

        kfir.name().set("Apartium");

        assertEquals(Set.of("Apartium", "Kfir"), names.get());
    }

    @Test
    void flatMapEachShouldMapExistingElements() {
        GamePlayer kfir = new GamePlayer(UUID.randomUUID(), Observable.mutable("Kfir"));
        GamePlayer apartium = new GamePlayer(UUID.randomUUID(), Observable.mutable("Apartium"));

        ListObservable<GamePlayer> players = Observable.list(new ArrayList<>(List.of(kfir, apartium)));

        ListObservable<String> names = players.flatMapEach(GamePlayer::name);

        assertEquals(List.of("Kfir", "Apartium"), names.get());
    }

    @Test
    void flatMapEachShouldUpdateWhenInnerObservableChanges() {
        MutableObservable<String> name = Observable.mutable("Kfir");

        GamePlayer player = new GamePlayer(UUID.randomUUID(), name);

        ListObservable<GamePlayer> players = Observable.list(new ArrayList<>(List.of(player)));

        ListObservable<String> names = players.flatMapEach(GamePlayer::name);

        assertEquals(List.of("Kfir"), names.get());

        name.set("Apartium");

        assertEquals(List.of("Apartium"), names.get());
    }

    @Test
    void flatMapEachShouldUpdateWhenElementIsAdded() {
        GamePlayer kfir = new GamePlayer(UUID.randomUUID(), Observable.mutable("Kfir"));
        GamePlayer apartium = new GamePlayer(UUID.randomUUID(), Observable.mutable("Apartium"));

        ListObservable<GamePlayer> players = Observable.list(new ArrayList<>(List.of(kfir)));

        ListObservable<String> names = players.flatMapEach(GamePlayer::name);

        assertEquals(List.of("Kfir"), names.get());

        players.add(apartium);

        assertEquals(List.of("Kfir", "Apartium"), names.get());
    }

    @Test
    void flatMapEachShouldUpdateWhenElementIsRemoved() {
        MutableObservable<String> removedName = Observable.mutable("Kfir");

        GamePlayer kfir = new GamePlayer(UUID.randomUUID(), removedName);
        GamePlayer apartium = new GamePlayer(UUID.randomUUID(), Observable.mutable("Apartium"));

        ListObservable<GamePlayer> players = Observable.list(new ArrayList<>(List.of(kfir, apartium)));

        ListObservable<String> names = players.flatMapEach(GamePlayer::name);

        assertEquals(List.of("Kfir", "Apartium"), names.get());

        players.remove(kfir);

        assertEquals(List.of("Apartium"), names.get());

        removedName.set("Kfir2");

        assertEquals(List.of("Apartium"), names.get());
    }

    @Test
    void flatMapEachShouldUpdateWhenCollectionIsCleared() {
        MutableObservable<String> name = Observable.mutable("Kfir");

        GamePlayer player = new GamePlayer(UUID.randomUUID(), name);

        ListObservable<GamePlayer> players = Observable.list(new ArrayList<>(List.of(player)));

        ListObservable<String> names = players.flatMapEach(GamePlayer::name);

        assertEquals(List.of("Kfir"), names.get());

        players.clear();

        assertEquals(List.of(), names.get());

        name.set("Kfir2");

        assertEquals(List.of(), names.get());
    }

    @Test
    void flatMapEachShouldCacheUntilBaseOrInnerChanges() {
        MutableObservable<String> name = Observable.mutable("Kfir");

        GamePlayer player = new GamePlayer(UUID.randomUUID(), name);

        ListObservable<GamePlayer> players = Observable.list(new ArrayList<>(List.of(player)));

        AtomicInteger mapperCalls = new AtomicInteger();

        ListObservable<String> names = players.flatMapEach(gamePlayer -> {
            mapperCalls.incrementAndGet();
            return gamePlayer.name();
        });

        assertEquals(List.of("Kfir"), names.get());
        assertEquals(List.of("Kfir"), names.get());
        assertEquals(List.of("Kfir"), names.get());

        assertEquals(1, mapperCalls.get());

        name.set("Kfir2");

        assertEquals(List.of("Kfir2"), names.get());

        assertEquals(1, mapperCalls.get());

        players.add(new GamePlayer(UUID.randomUUID(), Observable.mutable("Apartium")));

        assertEquals(List.of("Kfir2", "Apartium"), names.get());

        assertEquals(2, mapperCalls.get());

        assertEquals(List.of("Kfir2", "Apartium"), names.get());
        assertEquals(List.of("Kfir2", "Apartium"), names.get());
        assertEquals(List.of("Kfir2", "Apartium"), names.get());

        assertEquals(2, mapperCalls.get());
    }

    @Test
    void flatMapEachShouldExposeMappedSize() {
        GamePlayer kfir = new GamePlayer(UUID.randomUUID(), Observable.mutable("Kfir"));
        GamePlayer apartium = new GamePlayer(UUID.randomUUID(), Observable.mutable("Apartium"));

        ListObservable<GamePlayer> players = Observable.list(new ArrayList<>(List.of(kfir)));

        ListObservable<String> names = players.flatMapEach(GamePlayer::name);

        assertEquals(1, names.size().get());

        players.add(apartium);

        assertEquals(2, names.size().get());

        players.clear();

        assertEquals(0, names.size().get());
    }

    @Test
    void flatMapEachShouldNotifyObserversWhenInnerObservableChanges() {
        MutableObservable<String> name = Observable.mutable("Kfir");

        GamePlayer player = new GamePlayer(UUID.randomUUID(), name);

        ListObservable<GamePlayer> players = Observable.list(new ArrayList<>(List.of(player)));

        ListObservable<String> names = players.flatMapEach(GamePlayer::name);

        assertEquals(List.of("Kfir"), names.get());

        TestObserver observer = new TestObserver();
        names.observe(observer);

        name.set("Kfir2");

        assertTrue(observer.dirty);
        assertSame(names, observer.observable);

        assertEquals(List.of("Kfir2"), names.get());
    }

    @Test
    void flatMapEachShouldWorkAfterFilter() {
        GamePlayer kfir = new GamePlayer(UUID.randomUUID(), Observable.mutable("Kfir"), true);
        GamePlayer apartium = new GamePlayer(UUID.randomUUID(), Observable.mutable("Apartium"), false);

        ListObservable<GamePlayer> players = Observable.list(new ArrayList<>(List.of(kfir, apartium)));

        ListObservable<String> aliveNames = players
                .filter(player -> Observable.immutable(player.alive()))
                .flatMapEach(GamePlayer::name);

        assertEquals(List.of("Kfir"), aliveNames.get());

        kfir.name().set("Kfir2");

        assertEquals(List.of("Kfir2"), aliveNames.get());
    }

    @Test
    void flatMapEachShouldWorkBeforeFilter() {
        GamePlayer kfir = new GamePlayer(UUID.randomUUID(), Observable.mutable("Kfir"));
        GamePlayer apartium = new GamePlayer(UUID.randomUUID(), Observable.mutable("Apartium"));

        ListObservable<GamePlayer> players = Observable.list(new ArrayList<>(List.of(kfir, apartium)));

        ListObservable<String> namesStartingWithA = players
                .flatMapEach(GamePlayer::name)
                .filter(name -> Observable.immutable(name.startsWith("A")));

        assertEquals(List.of("Apartium"), namesStartingWithA.get());

        kfir.name().set("AlsoKfir");

        assertEquals(List.of("AlsoKfir", "Apartium"), namesStartingWithA.get());
    }

    @Test
    void flatMapEachShouldSupportChaining() {
        MutableObservable<String> name = Observable.mutable("Kfir");

        GamePlayer player = new GamePlayer(UUID.randomUUID(), name);

        ListObservable<GamePlayer> players = Observable.list(new ArrayList<>(List.of(player)));

        ListObservable<Integer> nameLengths = players
                .flatMapEach(GamePlayer::name)
                .mapEach(String::length);

        assertEquals(List.of(4), nameLengths.get());

        name.set("Apartium");

        assertEquals(List.of(8), nameLengths.get());
    }

    private record GamePlayer(UUID uuid, MutableObservable<String> name, boolean alive) {

        private GamePlayer(UUID uuid, MutableObservable<String> name) {
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