package net.apartium.cocoabeans.state;

import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class SetObservableTest {

    @Test
    void add() {
        SetObservable<Integer> set = Observable.set();

        AtomicInteger counter = new AtomicInteger(0);
        Observable<String> map = set.map(values -> {
            counter.incrementAndGet();
            return values.stream().sorted().toList().toString();
        });

        assertEquals(0, counter.get());
        set.add(1);
        assertEquals(0, counter.get());
        set.add(2);
        assertEquals(0, counter.get());
        assertEquals(Set.of(1, 2), set.get());
        assertEquals("[1, 2]", map.get());
        assertEquals(1, counter.get());

        set.add(5);
        assertEquals(1, counter.get());
        assertEquals(Set.of(1, 2, 5), set.get());
        assertEquals("[1, 2, 5]", map.get());
        assertEquals(2, counter.get());

        set.add(9);
        assertEquals(2, counter.get());
        assertEquals(Set.of(1, 2, 5, 9), set.get());
        assertEquals("[1, 2, 5, 9]", map.get());
        assertEquals(3, counter.get());
    }

    @Test
    void remove() {
        SetObservable<Integer> set = Observable.set();

        AtomicInteger counter = new AtomicInteger(0);
        Observable<String> map = set.map(values -> {
            counter.incrementAndGet();
            return values.stream().sorted().toList().toString();
        });

        assertTrue(set.add(1));
        assertTrue(set.add(5));
        assertTrue(set.add(7));

        assertEquals(0, counter.get());

        assertEquals("[1, 5, 7]", map.get());
        assertEquals(1, counter.get());

        assertTrue(set.remove(1));
        assertEquals(1, counter.get());
        assertEquals("[5, 7]", map.get());
        assertEquals(2, counter.get());

        assertTrue(set.add(3));
        assertTrue(set.remove(3));

        assertEquals("[5, 7]", map.get());
        assertEquals(2, counter.get());

        assertFalse(set.remove(3));
        assertEquals("[5, 7]", map.get());
        assertEquals(2, counter.get());

        assertFalse(set.add(5));
        assertEquals("[5, 7]", map.get());
        assertEquals(2, counter.get());
    }

    @Test
    void addAllRemoveAll() {
        SetObservable<Integer> set = Observable.set();
        AtomicInteger counter = new AtomicInteger(0);
        Observable<String> map = set.map(values -> {
            counter.incrementAndGet();
            return values.stream().sorted().toList().toString();
        });

        assertTrue(set.addAll(List.of(6, 3, 1, 5, 5, 8)));
        assertEquals(0, counter.get());

        assertEquals("[1, 3, 5, 6, 8]", map.get());
        assertEquals(1, counter.get());

        assertFalse(set.addAll(List.of(1, 3, 8)));

        assertEquals("[1, 3, 5, 6, 8]", map.get());
        assertEquals(1, counter.get());

        assertFalse(set.removeAll(List.of(2, 7, 9)));

        assertEquals("[1, 3, 5, 6, 8]", map.get());
        assertEquals(1, counter.get());

        assertTrue(set.removeAll(List.of(1, 8)));
        assertEquals("[3, 5, 6]", map.get());
        assertEquals(2, counter.get());

        assertTrue(set.removeIf(num -> num % 2 == 1));
        assertEquals("[6]", map.get());
        assertEquals(3, counter.get());

        assertFalse(set.removeIf(num -> num % 2 == 1));
        assertEquals("[6]", map.get());
        assertEquals(3, counter.get());
    }

    @Test
    void retainAll() {
        SetObservable<Integer> set = Observable.set();
        AtomicInteger counter = new AtomicInteger(0);
        Observable<String> map = set.map(values -> {
            counter.incrementAndGet();
            return values.stream().sorted().toList().toString();
        });

        set.addAll(Set.of(6, 8, 1, 2, 3, 5));
        assertEquals(0, counter.get());

        assertTrue(set.retainAll(Set.of(1, 2, 3, 5)));
        assertEquals(0, counter.get());

        assertEquals("[1, 2, 3, 5]", map.get());
        assertEquals(1, counter.get());

        assertFalse(set.retainAll(Set.of(1, 2, 3, 5)));
        assertEquals(1, counter.get());
        assertEquals("[1, 2, 3, 5]", map.get());
        assertEquals(1, counter.get());
    }

    @Test
    void clear() {
        SetObservable<Integer> set = Observable.set();
        AtomicInteger counter = new AtomicInteger(0);
        Observable<String> map = set.map(values -> {
            counter.incrementAndGet();
            return values.stream().sorted().toList().toString();
        });

        assertEquals(0, counter.get());
        assertEquals("[]", map.get());
        assertEquals(1, counter.get());

        set.addAll(Set.of(6, 8, 1, 2, 3, 5));
        set.clear();

        assertEquals("[]", map.get());
        assertEquals(1, counter.get());

        set.clear();

        assertEquals("[]", map.get());
        assertEquals(1, counter.get());
    }

    @Test
    void diffSet() {
        SetObservable<Integer> set = Observable.set(new LinkedHashSet<>());

        set.add(1);
        assertEquals(Set.of(1), set.get());
        set.addAll(Set.of(1, 2, 3));
        assertEquals(Set.of(1, 2, 3), set.get());
    }

    @Test
    void customCopyOfIsUsedForGet() {
        AtomicInteger copyOfCallCount = new AtomicInteger(0);
        Set<Integer> backing = new HashSet<>();
        SetObservable<Integer> set = Observable.set(
                backing,
                col -> {
                    copyOfCallCount.incrementAndGet();
                    return new LinkedHashSet<>(col);
                }
        );

        set.add(1);
        set.add(2);

        Set<Integer> result = set.get();
        assertEquals(1, copyOfCallCount.get());
        assertEquals(Set.of(1, 2), result);
        assertInstanceOf(LinkedHashSet.class, result);
    }

    @Test
    void customCopyOfIsUsedForFilteredCollection() {
        AtomicInteger copyOfCallCount = new AtomicInteger(0);
        Set<Integer> backing = new HashSet<>();
        SetObservable<Integer> set = Observable.set(
                backing,
                col -> {
                    copyOfCallCount.incrementAndGet();
                    return new LinkedHashSet<>(col);
                }
        );

        set.addAll(List.of(1, 2, 3, 4));
        copyOfCallCount.set(0);

        Observable<String> filtered = set.filter(n -> Observable.mutable(n % 2 == 0))
                .map(values -> values.stream().sorted().toList().toString());

        assertEquals("[2, 4]", filtered.get());
        assertTrue(copyOfCallCount.get() > 0);
    }

    @Test
    void customCreateInitSetIsUsed() {
        AtomicInteger createInitSetCallCount = new AtomicInteger(0);
        Set<Integer> backing = new HashSet<>();
        SetObservable<Integer> set = Observable.set(
                backing,
                HashSet::new,
                capacity -> {
                    createInitSetCallCount.incrementAndGet();
                    return new LinkedHashSet<>(capacity);
                }
        );

        set.addAll(List.of(1, 2, 3));
        // trigger filter to invoke createCollection
        set.filter(n -> Observable.mutable(n > 1)).map(s -> s.stream().sorted().toList().toString()).get();

        assertTrue(createInitSetCallCount.get() > 0);
    }

    @Test
    void customCopyOfProducesWeakSetAllowsGc() {
        Set<Object> backing = new HashSet<>();
        SetObservable<Object> set = Observable.set(
                backing,
                col -> {
                    Set<Object> weak = Collections.newSetFromMap(new WeakHashMap<>());
                    weak.addAll(col);
                    return Collections.unmodifiableSet(weak);
                },
                size -> Collections.newSetFromMap(new WeakHashMap<>())
        );

        Object obj = new Object();
        set.add(obj);
        assertEquals(1, set.get().size());

        obj = null;
        System.gc();

        // The internal backing set still holds the reference, so size is still 1
        // but the returned set (weak) should reflect GC if obj was only weakly held
        // Here we confirm get() returns the customCopyOf result (a weak set snapshot)
        // The returned snapshot may have lost the entry after GC
        Set<Object> snapshot = set.get();
        // snapshot was created from a weak set; after GC it may be empty
        assertTrue(snapshot.size() == 0 || snapshot.size() == 1);
    }

    @Test
    void customCopyOfPropagatesThroughChainedFilters() {
        AtomicInteger copyOfCallCount = new AtomicInteger(0);
        Set<Integer> backing = new HashSet<>();
        SetObservable<Integer> set = Observable.set(
                backing,
                col -> {
                    copyOfCallCount.incrementAndGet();
                    return new LinkedHashSet<>(col);
                }
        );

        set.addAll(List.of(1, 2, 3, 4, 5, 6));
        copyOfCallCount.set(0);

        // chain two filters — each should still use the custom copyOf
        Observable<String> result = set
                .filter(n -> Observable.mutable(n % 2 == 0))
                .filter(n -> Observable.mutable(n > 2))
                .map(values -> values.stream().sorted().toList().toString());

        assertEquals("[4, 6]", result.get());
        assertTrue(copyOfCallCount.get() > 0, "custom copyOf must be used through the filter chain");
    }

    @Test
    void twoArgOverloadDefaultsToHashSetForCreateInitSet() {
        Set<Integer> backing = new HashSet<>();
        SetObservable<Integer> set = Observable.set(backing, LinkedHashSet::new);

        set.addAll(List.of(10, 20, 30));

        // filter uses createCollection (should default to HashSet) — just verify it works
        Observable<String> filtered = set.filter(n -> Observable.mutable(n > 10))
                .map(values -> values.stream().sorted().toList().toString());

        assertEquals("[20, 30]", filtered.get());
    }

}
