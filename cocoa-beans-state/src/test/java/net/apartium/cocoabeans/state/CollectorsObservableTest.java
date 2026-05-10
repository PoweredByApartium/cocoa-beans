package net.apartium.cocoabeans.state;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class CollectorsObservableTest {

    @Test
    void toListCreatesDerivedListObservable() {
        ListObservable<Integer> base = Observable.list();
        base.addAll(List.of(1, 2));

        ListObservable<Integer> list = base.as(CollectorsObservable.toList());
        List<Integer> firstSnapshot = list.get();

        assertEquals(List.of(1, 2), firstSnapshot);
        assertThrows(UnsupportedOperationException.class, () -> list.add(3));

        base.add(3);

        assertEquals(List.of(1, 2), firstSnapshot);
        assertEquals(List.of(1, 2, 3), list.get());
    }

    @Test
    void toSetCreatesDerivedSetObservable() {
        ListObservable<Integer> base = Observable.list();
        base.addAll(List.of(1, 2, 2, 3));

        SetObservable<Integer> set = base.as(CollectorsObservable.toSet());
        Set<Integer> firstSnapshot = set.get();

        assertEquals(Set.of(1, 2, 3), firstSnapshot);
        assertThrows(UnsupportedOperationException.class, () -> set.add(4));

        base.add(4);

        assertEquals(Set.of(1, 2, 3), firstSnapshot);
        assertEquals(Set.of(1, 2, 3, 4), set.get());

        base.add(3);

        assertEquals(List.of(1, 2, 2, 3, 4, 3), base.get());
        assertEquals(Set.of(1, 2, 3, 4), set.get());
    }

    @Test
    void customToListFactoriesAreUsedByDerivedOperations() {
        AtomicInteger snapshotCallCount = new AtomicInteger(0);
        AtomicInteger collectionCallCount = new AtomicInteger(0);

        Function<Collection<Integer>, List<Integer>> snapshot = collection -> {
            snapshotCallCount.incrementAndGet();
            return new ArrayList<>(collection);
        };

        ListObservable<Integer> base = Observable.list();
        base.addAll(List.of(1, 2, 3));

        ListObservable<Integer> list = base.as(CollectorsObservable.toList(
                snapshot,
                initialCapacity -> {
                    collectionCallCount.incrementAndGet();
                    return new ArrayList<>(initialCapacity);
                }
        ));

        ListObservable<String> mapped = list.mapEach(String::valueOf);

        assertEquals(List.of("1", "2", "3"), mapped.get());
        assertTrue(snapshotCallCount.get() > 0);
        assertTrue(collectionCallCount.get() > 0);
    }

    @Test
    void customToSetFactoriesAreUsedByDerivedOperations() {
        AtomicInteger snapshotCallCount = new AtomicInteger(0);
        AtomicInteger collectionCallCount = new AtomicInteger(0);

        Function<Collection<Integer>, Set<Integer>> snapshot = collection -> {
            snapshotCallCount.incrementAndGet();
            return new LinkedHashSet<>(collection);
        };

        ListObservable<Integer> base = Observable.list();
        base.addAll(List.of(1, 2, 3));

        SetObservable<Integer> set = base.as(CollectorsObservable.toSet(
                snapshot,
                initialCapacity -> {
                    collectionCallCount.incrementAndGet();
                    return new HashSet<>(initialCapacity);
                }
        ));

        Observable<List<String>> mapped = set.mapEach(String::valueOf)
                .map(values -> values.stream().sorted().toList());

        assertEquals(List.of("1", "2", "3"), mapped.get());
        assertTrue(snapshotCallCount.get() > 0);
        assertTrue(collectionCallCount.get() > 0);
    }
}
