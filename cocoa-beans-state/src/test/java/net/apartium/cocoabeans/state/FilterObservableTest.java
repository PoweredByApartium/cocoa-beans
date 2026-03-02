package net.apartium.cocoabeans.state;

import net.apartium.cocoabeans.structs.Entry;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class FilterObservableTest {

    static class Player {

        private final MutableObservable<Boolean> online = Observable.mutable(false);
        private final MutableObservable<Instant> spectatorSince = Observable.mutable(null);
        private final Observable<Boolean> alive = Observable.compound(
                online,
                spectatorSince
        ).map(args -> args.arg0() && args.arg1() == null);

        private final String name;

        Player(String name) {
            this(name, false);
        }

        Player(String name, boolean online) {
            this.name = name;
            this.online.set(online);
        }

        public void setOnline(boolean online) {
            this.online.set(online);
        }

        public Observable<Boolean> getOnline() {
            return online;
        }

        public Observable<Instant> getSpectatorSince() {
            return spectatorSince;
        }

        public void setSpectatorSince(Instant since) {
            this.spectatorSince.set(since);
        }

        public void respawn() {
            this.spectatorSince.set(null);
        }

        public Observable<Boolean> getAlive() {
            return alive;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            Player player = (Player) o;
            return Objects.equals(name, player.name);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(name);
        }
    }

    @Test
    void onlinePlayer() {
        Player ikfir = new Player("ikfir", true);
        Player ikfir2 = new Player("ikfir2");
        Player ikfir3 = new Player("ikfir3", true);
        Player ikfir4 = new Player("ikfir4");
        Player ikfir5 = new Player("ikfir5");

        SetObservable<Player> players = Observable.set(new HashSet<>(Set.of(
                ikfir,
                ikfir2,
                ikfir3,
                ikfir4,
                ikfir5
        )));

        Observable<Set<Player>> onlinePlayers = players.filter(Player::getOnline);

        assertEquals(
                Set.of(
                        ikfir,
                        ikfir3
                ),

                onlinePlayers.get()
        );

        ikfir3.setOnline(false);

        assertEquals(
                Set.of(
                        ikfir
                ),

                onlinePlayers.get()
        );

        Player ikfir6 = new Player("ikfir6", true);
        players.add(ikfir6);

        assertEquals(
                Set.of(
                        ikfir,
                        ikfir6
                ),

                onlinePlayers.get()
        );

        ikfir2.setOnline(true);

        assertEquals(
                Set.of(
                        ikfir,
                        ikfir2,
                        ikfir6
                ),

                onlinePlayers.get()
        );

        ikfir2.setOnline(false);
        ikfir3.setOnline(true);
        ikfir4.setOnline(true);

        assertEquals(
                Set.of(
                        ikfir,
                        ikfir3,
                        ikfir4,
                        ikfir6
                ),

                onlinePlayers.get()
        );

        ikfir2.setOnline(true);
        players.remove(ikfir6);

        assertEquals(
                Set.of(
                        ikfir,
                        ikfir2,
                        ikfir3,
                        ikfir4
                ),

                onlinePlayers.get()
        );


        players.remove(ikfir5);

        ikfir6.setOnline(false);
        players.add(ikfir6);


        assertEquals(
                Set.of(
                        ikfir,
                        ikfir2,
                        ikfir3,
                        ikfir4
                ),

                onlinePlayers.get()
        );

        ikfir6.setOnline(true);
        ikfir6.setOnline(false);

        assertEquals(
                Set.of(
                        ikfir,
                        ikfir2,
                        ikfir3,
                        ikfir4
                ),

                onlinePlayers.get()
        );

        assertEquals(
                Set.of(
                        ikfir,
                        ikfir2,
                        ikfir3,
                        ikfir4
                ),

                onlinePlayers.get()
        );
    }

    @Test
    void alivePlayer() {
        Player ikfir = new Player("ikfir", true);
        Player ikfir2 = new Player("ikfir2");
        Player ikfir3 = new Player("ikfir3", true);
        Player ikfir4 = new Player("ikfir4", true);

        ikfir4.setSpectatorSince(Instant.now());

        SetObservable<Player> players = Observable.set(new HashSet<>(Set.of(
                ikfir,
                ikfir2,
                ikfir3,
                ikfir4
        )));

        CollectionObservable<Player, Set<Player>> alivePlayers = players.filter(Player::getAlive);
        Observable<Integer> size = alivePlayers.size();

        assertEquals(
                Set.of(
                        ikfir,
                        ikfir3
                ),

                alivePlayers.get()
        );

        assertEquals(2, size.get());

        ikfir2.setSpectatorSince(Instant.now());

        assertEquals(
                Set.of(
                        ikfir,
                        ikfir3
                ),

                alivePlayers.get()
        );

        assertEquals(2, size.get());

        ikfir3.setOnline(false);
        ikfir2.setOnline(true);
        ikfir2.respawn();

        assertEquals(2, size.get());

        assertEquals(
                Set.of(
                        ikfir,
                        ikfir2
                ),

                alivePlayers.get()
        );

        assertEquals(2, size.get());

        ikfir3.setOnline(true);

        assertEquals(3, size.get());

        assertEquals(
                Set.of(
                        ikfir,
                        ikfir2,
                        ikfir3
                ),

                alivePlayers.get()
        );

        assertEquals(3, size.get());
    }

    @Test
    void unsupportedMethods() {
        CollectionObservable<Object, Set<Object>> filtered = Observable.set().filter(element -> Observable.immutable(true));

        assertThrows(UnsupportedOperationException.class, () -> filtered.add(1));
        assertThrows(UnsupportedOperationException.class, () -> filtered.remove(1));

        List<Object> empty = List.of();

        assertThrows(UnsupportedOperationException.class, () -> filtered.addAll(empty));
        assertThrows(UnsupportedOperationException.class, () -> filtered.removeAll(empty));
        assertThrows(UnsupportedOperationException.class, () -> filtered.removeIf(e -> true));
        assertThrows(UnsupportedOperationException.class, () -> filtered.retainAll(empty));
        assertThrows(UnsupportedOperationException.class, filtered::clear);
    }

    @Test
    void withWatchers() {
        Player ikfir = new Player("ikfir", true);
        Player ikfir2 = new Player("ikfir2");
        Player ikfir3 = new Player("ikfir3", true);
        Player ikfir4 = new Player("ikfir4", true);

        ikfir4.setSpectatorSince(Instant.now());

        SetObservable<Player> players = Observable.set(new HashSet<>(Set.of(
                ikfir,
                ikfir2,
                ikfir3,
                ikfir4
        )));

        CollectionObservable<Player, Set<Player>> alivePlayers = players.filter(Player::getAlive);

        LazyWatcher<Set<Player>> watcher = alivePlayers.lazyWatch();

        assertTrue(watcher.isDirty());

        Entry<Set<Player>, Boolean> valueAndUpdateFlag = watcher.getOrUpdate();
        Set<Player> currentAlivePlayer = valueAndUpdateFlag.key();
        boolean hasUpdate = valueAndUpdateFlag.value();

        assertTrue(hasUpdate);
        assertEquals(
                Set.of(ikfir, ikfir3),
                currentAlivePlayer
        );

        assertFalse(watcher.isDirty());

        valueAndUpdateFlag = watcher.getOrUpdate();
        currentAlivePlayer = valueAndUpdateFlag.key();
        hasUpdate = valueAndUpdateFlag.value();

        assertFalse(watcher.isDirty());
        assertFalse(hasUpdate);
        assertEquals(
                Set.of(ikfir, ikfir3),
                currentAlivePlayer
        );

        ikfir2.setOnline(true);
        assertTrue(watcher.isDirty());

        valueAndUpdateFlag = watcher.getOrUpdate();
        currentAlivePlayer = valueAndUpdateFlag.key();
        hasUpdate = valueAndUpdateFlag.value();

        assertTrue(hasUpdate);
        assertEquals(
                Set.of(ikfir, ikfir2, ikfir3),
                currentAlivePlayer
        );

        assertFalse(watcher.isDirty());
    }

    @Test
    void filterImmutable() {
        Player ikfir = new Player("ikfir", true);
        Player ikfir2 = new Player("ikfir2");
        Player kfir3 = new Player("kfir3", true);
        Player ikfir4 = new Player("ikfir4", true);

        SetObservable<Player> players = Observable.set(new HashSet<>(Set.of(
                ikfir,
                ikfir2,
                kfir3,
                ikfir4
        )));

        CollectionObservable<Player, Set<Player>> playersStartWithI = players.filter(player -> Observable.immutable(player.name.startsWith("i")));

        assertEquals(
                Set.of(
                        ikfir,
                        ikfir2,
                        ikfir4
                ),
                playersStartWithI.get()
        );
    }

    @Test
    void rapidSwitch() {

        Player ikfir = new Player("ikfir");
        Player ikfir2 = new Player("ikfir2");
        Player ikfir3 = new Player("ikfir3");
        Player ikfir4 = new Player("ikfir4");
        Player ikfir5 = new Player("ikfir5");

        SetObservable<Player> players = Observable.set(new HashSet<>(Set.of(
                ikfir,
                ikfir2,
                ikfir3,
                ikfir4,
                ikfir5
        )));

        Observable<Set<Player>> onlinePlayers = players.filter(Player::getOnline);

        assertEquals(
                Set.of(),
                onlinePlayers.get()
        );


        boolean i1 = false;
        boolean i2 = false;
        boolean i3 = false;
        boolean i4 = false;
        boolean i5 = false;

        Random random = new Random(123456789);
        for (int i = 0; i < 100_000; i++) {
            int value = random.nextInt(5);
            switch (value) {
                case 0: {
                    i1 = !i1;
                    ikfir.setOnline(i1);
                    break;
                }
                case 1: {
                    i2 = !i2;
                    ikfir2.setOnline(i2);
                    break;
                }
                case 2: {
                    i3 = !i3;
                    ikfir3.setOnline(i3);
                    break;
                }
                case 3: {
                    i4 = !i4;
                    ikfir4.setOnline(i4);
                    break;
                }
                case 4: {
                    i5 = !i5;
                    ikfir5.setOnline(i5);
                    break;
                }

                default:
                    throw new IllegalStateException("Value was: " + value);
            }

            Set<Player> actual = new HashSet<>();

            if (i1)
                actual.add(ikfir);

            if (i2)
                actual.add(ikfir2);

            if (i3)
                actual.add(ikfir3);

            if (i4)
                actual.add(ikfir4);

            if (i5)
                actual.add(ikfir5);

            assertEquals(actual, onlinePlayers.get());
        }
    }

    @Test
    void rapidAddRemoveAndPredicateToggles() {

        Player p1 = new Player("p1");
        Player p2 = new Player("p2");
        Player p3 = new Player("p3");
        Player p4 = new Player("p4");
        Player p5 = new Player("p5");
        Player p6 = new Player("p6");
        Player p7 = new Player("p7");
        Player p8 = new Player("p8");

        Player[] pool = { p1, p2, p3, p4, p5, p6, p7, p8 };

        SetObservable<Player> players = Observable.set(new HashSet<>(Set.of(p1, p2, p3, p4)));

        Observable<Set<Player>> onlinePlayers = players.filter(Player::getOnline);

        Set<Player> inSource = new HashSet<>(Set.of(p1, p2, p3, p4));
        Set<Player> onlineExpected = new HashSet<>();

        for (Player p : pool) {
            p.setOnline(false);
        }
        assertEquals(Set.of(), onlinePlayers.get());

        Random rnd = new Random(123456789);

        // Weighted mix:
        // 0..59  => toggle online of random player (60%)
        // 60..84 => add random player (25%)
        // 85..99 => remove random player (15%)
        for (int i = 0; i < 500_000; i++) {
            int op = rnd.nextInt(100);
            Player target = pool[rnd.nextInt(pool.length)];

            if (op < 60) {
                boolean newOnline = !target.getOnline().get();
                target.setOnline(newOnline);

                if (inSource.contains(target)) {
                    if (newOnline) onlineExpected.add(target);
                    else onlineExpected.remove(target);
                }

            } else if (op < 85) {
                players.add(target);
                inSource.add(target);

                if (target.getOnline().get())
                    onlineExpected.add(target);

            } else {
                players.remove(target);
                inSource.remove(target);

                onlineExpected.remove(target);

            }

            assertEquals(onlineExpected, onlinePlayers.get(), "Mismatch at iteration " + i);
        }
    }

    @Test
    void spectatorPlayers() {
        Player ikfir = new Player("ikfir", true);
        Player ikfir2 = new Player("ikfir2");
        Player ikfir3 = new Player("ikfir3", true);
        Player ikfir4 = new Player("ikfir4", true);

        SetObservable<Player> players = Observable.set(new HashSet<>(Set.of(
                ikfir,
                ikfir2,
                ikfir3,
                ikfir4
        )));


        CollectionObservable<Player, Set<Player>> spectatorPlayers = players.filter(Player::getSpectatorSince, Objects::nonNull);
        assertEquals(
                Set.of(),
                spectatorPlayers.get()
        );

        ikfir.setSpectatorSince(Instant.now());
        ikfir3.setSpectatorSince(Instant.now());

        assertEquals(
                Set.of(ikfir, ikfir3),
                spectatorPlayers.get()
        );

        ikfir.respawn();
        ikfir2.setSpectatorSince(Instant.now());

        assertEquals(
                Set.of(ikfir2, ikfir3),
                spectatorPlayers.get()
        );

        ikfir.setSpectatorSince(Instant.now());
        ikfir4.setSpectatorSince(Instant.now());

        assertEquals(
                Set.of(ikfir, ikfir2, ikfir3, ikfir4),
                spectatorPlayers.get()
        );
    }

}
