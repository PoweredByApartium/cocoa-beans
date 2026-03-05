package net.apartium.cocoabeans.state;

import net.apartium.cocoabeans.structs.Entry;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class FlatMapObservableTest {

    record PlayerRank(String name, MutableObservable<String> prefix, MutableObservable<String> suffix) {
        PlayerRank(String name, String prefix, String suffix) {
            this(name, Observable.mutable(prefix), Observable.mutable(suffix));
        }
    }

    @Test
    void basicGet() {
        MutableObservable<String> inner = Observable.mutable("hello");
        MutableObservable<Observable<String>> outer = Observable.mutable(inner);

        Observable<String> flat = outer.flatMap(o -> o);

        assertEquals("hello", flat.get());
    }

    @Test
    void innerUpdatePropagates() {
        MutableObservable<String> inner = Observable.mutable("hello");
        MutableObservable<Observable<String>> outer = Observable.mutable(inner);

        Observable<String> flat = outer.flatMap(o -> o);

        assertEquals("hello", flat.get());

        inner.set("world");

        assertEquals("world", flat.get());
    }

    @Test
    void outerChangeSwitchesInner() {
        MutableObservable<String> innerA = Observable.mutable("[A]");
        MutableObservable<String> innerB = Observable.mutable("[B]");

        MutableObservable<MutableObservable<String>> outer = Observable.mutable(innerA);

        Observable<String> flat = outer.flatMap(o -> o);

        assertEquals("[A]", flat.get());

        outer.set(innerB);

        assertEquals("[B]", flat.get());
    }

    @Test
    void oldInnerNoLongerPropagatesAfterSwitch() {
        MutableObservable<String> innerA = Observable.mutable("[A]");
        MutableObservable<String> innerB = Observable.mutable("[B]");

        MutableObservable<MutableObservable<String>> outer = Observable.mutable(innerA);

        Observable<String> flat = outer.flatMap(o -> o);
        flat.get();

        outer.set(innerB);
        assertEquals("[B]", flat.get());

        innerA.set("[A-changed]");
        assertEquals("[B]", flat.get());
    }

    @Test
    void newInnerPropagatesAfterSwitch() {
        MutableObservable<String> innerA = Observable.mutable("[A]");
        MutableObservable<String> innerB = Observable.mutable("[B]");

        MutableObservable<MutableObservable<String>> outer = Observable.mutable(innerA);

        Observable<String> flat = outer.flatMap(o -> o);
        flat.get();

        outer.set(innerB);
        innerB.set("[B+]");

        assertEquals("[B+]", flat.get());
    }

    @Test
    void cachedWhenClean() {
        MutableObservable<String> inner = Observable.mutable("hello");
        MutableObservable<Observable<String>> outer = Observable.mutable(inner);

        AtomicInteger mapCount = new AtomicInteger();

        Observable<String> flat = outer.flatMap(o -> {
            mapCount.incrementAndGet();
            return o;
        });

        assertEquals("hello", flat.get());
        assertEquals(1, mapCount.get());

        assertEquals("hello", flat.get());
        assertEquals(1, mapCount.get());
    }

    @Test
    void dirtyAfterInnerUpdate() {
        MutableObservable<String> inner = Observable.mutable("hello");
        MutableObservable<Observable<String>> outer = Observable.mutable(inner);

        Observable<String> flat = outer.flatMap(o -> o);
        LazyWatcher<String> watcher = flat.lazyWatch();

        assertTrue(watcher.isDirty());
        watcher.getOrUpdate();
        assertFalse(watcher.isDirty());

        inner.set("world");

        assertTrue(watcher.isDirty());
        assertEquals("world", watcher.getOrUpdate().key());
        assertFalse(watcher.isDirty());
    }

    @Test
    void dirtyAfterOuterSwitch() {
        MutableObservable<String> innerA = Observable.mutable("[A]");
        MutableObservable<String> innerB = Observable.mutable("[B]");

        MutableObservable<MutableObservable<String>> outer = Observable.mutable(innerA);

        Observable<String> flat = outer.flatMap(o -> o);
        LazyWatcher<String> watcher = flat.lazyWatch();

        watcher.getOrUpdate();
        assertFalse(watcher.isDirty());

        outer.set(innerB);

        assertTrue(watcher.isDirty());
        Entry<String, Boolean> result = watcher.getOrUpdate();
        assertTrue(result.value());
        assertEquals("[B]", result.key());
    }

    @Test
    void observerNotifiedOnInnerChange() {
        MutableObservable<String> inner = Observable.mutable("v1");
        MutableObservable<Observable<String>> outer = Observable.mutable(inner);

        Observable<String> flat = outer.flatMap(o -> o);
        flat.get();

        AtomicInteger notifyCount = new AtomicInteger();
        flat.observe(obs -> notifyCount.incrementAndGet());

        inner.set("v2");
        assertEquals(1, notifyCount.get());

        inner.set("v3");
        assertEquals(2, notifyCount.get());
    }

    @Test
    void observerNotifiedOnOuterSwitch() {
        MutableObservable<String> innerA = Observable.mutable("[A]");
        MutableObservable<String> innerB = Observable.mutable("[B]");

        MutableObservable<MutableObservable<String>> outer = Observable.mutable(innerA);

        Observable<String> flat = outer.flatMap(o -> o);
        flat.get();

        AtomicInteger notifyCount = new AtomicInteger();
        flat.observe(obs -> notifyCount.incrementAndGet());

        outer.set(innerB);
        assertEquals(1, notifyCount.get());
    }

    @Test
    void removeObserver() {
        MutableObservable<String> inner = Observable.mutable("v1");
        MutableObservable<Observable<String>> outer = Observable.mutable(inner);

        Observable<String> flat = outer.flatMap(o -> o);
        flat.get();

        AtomicInteger notifyCount = new AtomicInteger();
        Observer observer = obs -> notifyCount.incrementAndGet();

        flat.observe(observer);
        inner.set("v2");
        assertEquals(1, notifyCount.get());

        assertTrue(flat.removeObserver(observer));
        inner.set("v3");
        assertEquals(1, notifyCount.get());
    }

    @Test
    void removeNonExistentObserver() {
        MutableObservable<String> inner = Observable.mutable("v1");
        MutableObservable<Observable<String>> outer = Observable.mutable(inner);

        Observable<String> flat = outer.flatMap(o -> o);

        assertFalse(flat.removeObserver(obs -> {}));
    }

    @Test
    void flatMapWithMapper() {
        PlayerRank rankA = new PlayerRank("A", "[A]", "!A!");
        PlayerRank rankB = new PlayerRank("B", "[B]", "!B!");

        MutableObservable<PlayerRank> rank = Observable.mutable(rankA);

        Observable<String> prefix = rank.flatMap(PlayerRank::prefix);
        Observable<String> suffix = rank.flatMap(PlayerRank::suffix);

        assertEquals("[A]", prefix.get());
        assertEquals("!A!", suffix.get());

        rankA.prefix().set("[A+]");
        assertEquals("[A+]", prefix.get());

        rank.set(rankB);
        assertEquals("[B]", prefix.get());
        assertEquals("!B!", suffix.get());

        rankB.suffix().set("!B+!");
        assertEquals("!B+!", suffix.get());
        assertEquals("[B]", prefix.get());

        rank.set(rankA);
        rankA.prefix().set("[A-]");
        assertEquals("[A-]", prefix.get());
        assertEquals("!A!", suffix.get());
    }

    @Test
    void chainedFlatMapAndMap() {
        PlayerRank rankA = new PlayerRank("A", "admin", "suffix");
        PlayerRank rankB = new PlayerRank("B", "mod", "suffix");

        MutableObservable<PlayerRank> rank = Observable.mutable(rankA);

        Observable<String> upperPrefix = rank.flatMap(PlayerRank::prefix).map(String::toUpperCase);

        assertEquals("ADMIN", upperPrefix.get());

        rank.set(rankB);
        assertEquals("MOD", upperPrefix.get());

        rankB.prefix().set("owner");
        assertEquals("OWNER", upperPrefix.get());
    }

    @Test
    void sameInnerObservableReturnedNoDoubleSubscribe() {
        MutableObservable<String> inner = Observable.mutable("same");
        MutableObservable<String> key = Observable.mutable("k1");

        Observable<String> flat = key.flatMap(k -> inner);

        assertEquals("same", flat.get());

        inner.set("updated");
        assertEquals("updated", flat.get());

        key.set("k2");
        assertEquals("updated", flat.get());

        inner.set("again");
        assertEquals("again", flat.get());
    }

    @Test
    void baseChangesButValueUnchangedNoUnnecessaryMapperCall() {
        MutableObservable<String> innerA = Observable.mutable("value");

        MutableObservable<String> key = Observable.mutable("same");

        AtomicInteger mapperCount = new AtomicInteger();

        Observable<String> flat = key.flatMap(k -> {
            mapperCount.incrementAndGet();
            return innerA;
        });

        assertEquals("value", flat.get());
        assertEquals(1, mapperCount.get());

        key.set("same");
        assertEquals("value", flat.get());
        assertEquals(1, mapperCount.get());
    }

    @Test
    void withWatcherManager() {
        MutableObservable<String> innerA = Observable.mutable("[A]");
        MutableObservable<String> innerB = Observable.mutable("[B]");

        MutableObservable<MutableObservable<String>> outer = Observable.mutable(innerA);

        Observable<String> flat = outer.flatMap(o -> o);

        WatcherManager watcherManager = new WatcherManager();
        AtomicInteger watchCount = new AtomicInteger();

        flat.lazyWatch(watcherManager, value -> watchCount.incrementAndGet());

        watcherManager.heartbeat();
        assertEquals(1, watchCount.get());

        watcherManager.heartbeat();
        assertEquals(1, watchCount.get());

        innerA.set("[A+]");
        watcherManager.heartbeat();
        assertEquals(2, watchCount.get());

        outer.set(innerB);
        watcherManager.heartbeat();
        assertEquals(3, watchCount.get());

        innerB.set("[B+]");
        watcherManager.heartbeat();
        assertEquals(4, watchCount.get());

        innerA.set("[A-changed]");
        watcherManager.heartbeat();
        assertEquals(4, watchCount.get());
    }

    @Test
    void multipleObservers() {
        MutableObservable<String> inner = Observable.mutable("v1");
        MutableObservable<Observable<String>> outer = Observable.mutable(inner);

        Observable<String> flat = outer.flatMap(o -> o);
        flat.get();

        AtomicInteger count1 = new AtomicInteger();
        AtomicInteger count2 = new AtomicInteger();

        flat.observe(obs -> count1.incrementAndGet());
        flat.observe(obs -> count2.incrementAndGet());

        inner.set("v2");
        assertEquals(1, count1.get());
        assertEquals(1, count2.get());

        outer.set(Observable.mutable("v3"));
        assertEquals(2, count1.get());
        assertEquals(2, count2.get());
    }

    @Test
    void immutableInner() {
        Observable<String> immutable = Observable.immutable("fixed");
        MutableObservable<Observable<String>> outer = Observable.mutable(immutable);

        Observable<String> flat = outer.flatMap(o -> o);

        assertEquals("fixed", flat.get());
        assertEquals("fixed", flat.get());
    }

    @Test
    void switchToImmutableInner() {
        MutableObservable<String> mutableInner = Observable.mutable("mutable");
        MutableObservable<Observable<String>> outer = Observable.mutable(mutableInner);

        Observable<String> flat = outer.flatMap(o -> o);
        assertEquals("mutable", flat.get());

        Observable<String> immutableInner = Observable.immutable("fixed");
        outer.set(immutableInner);

        assertEquals("fixed", flat.get());

        mutableInner.set("changed");
        assertEquals("fixed", flat.get());
    }

}
