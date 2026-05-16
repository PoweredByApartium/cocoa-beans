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

class ObservableCollectionTypeTest {

    @Test
    void toListCreatesDerivedListObservable() {
        ListObservable<Integer> base = Observable.list();
        base.addAll(List.of(1, 2));

        ListObservable<Integer> list = base.as(ObservableCollectionType.toList());
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

        SetObservable<Integer> set = base.as(ObservableCollectionType.toSet());
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

        ListObservable<Integer> list = base.as(ObservableCollectionType.toList(
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

        SetObservable<Integer> set = base.as(ObservableCollectionType.toSet(
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

    @Test
    void toListDefaultUsesListCopyOfAndArrayList() {
        ListObservable<Integer> base = Observable.list();
        base.addAll(List.of(1, 2, 3));

        ListObservable<Integer> copy = base.as(ObservableCollectionType.toList());
        List<Integer> snapshot = copy.get();

        assertEquals(List.of(1, 2, 3), snapshot);
        // List.copyOf produces unmodifiable
        assertThrows(UnsupportedOperationException.class, () -> snapshot.add(4));
    }

    @Test
    void toSetDefaultUsesSetCopyOfAndHashSet() {
        SetObservable<String> base = Observable.set(new HashSet<>(Set.of("a", "b")));

        SetObservable<String> copy = base.as(ObservableCollectionType.toSet());
        Set<String> snapshot = copy.get();

        assertEquals(Set.of("a", "b"), snapshot);
        // Set.copyOf produces unmodifiable
        assertThrows(UnsupportedOperationException.class, () -> snapshot.add("c"));
    }

    @Test
    void customToListFactoriesUsedByFilter() {
        AtomicInteger snapshotCalls = new AtomicInteger();
        AtomicInteger collectionCalls = new AtomicInteger();

        ListObservable<Integer> base = Observable.list(new ArrayList<>(List.of(1, 2, 3, 4)));

        ListObservable<Integer> list = base.as(ObservableCollectionType.toList(
                col -> {
                    snapshotCalls.incrementAndGet();
                    return new ArrayList<>(col);
                },
                cap -> {
                    collectionCalls.incrementAndGet();
                    return new ArrayList<>(cap);
                }
        ));

        ListObservable<Integer> evens = list.filter(n -> Observable.immutable(n % 2 == 0));
        assertEquals(List.of(2, 4), evens.get());
        assertTrue(snapshotCalls.get() > 0);
        assertTrue(collectionCalls.get() > 0);
    }

    @Test
    void customToListFactoriesUsedByFlatMapEach() {
        AtomicInteger snapshotCalls = new AtomicInteger();

        ListObservable<Integer> base = Observable.list(new ArrayList<>(List.of(1, 2)));

        ListObservable<Integer> list = base.as(ObservableCollectionType.toList(
                col -> {
                    snapshotCalls.incrementAndGet();
                    return new ArrayList<>(col);
                },
                ArrayList::new
        ));

        ListObservable<String> flat = list.flatMapEach(n -> Observable.immutable("v" + n));
        assertEquals(List.of("v1", "v2"), flat.get());
        assertTrue(snapshotCalls.get() > 0);
    }

    @Test
    void customToSetFactoriesUsedByFilter() {
        AtomicInteger snapshotCalls = new AtomicInteger();
        AtomicInteger collectionCalls = new AtomicInteger();

        SetObservable<Integer> base = Observable.set(new HashSet<>(Set.of(1, 2, 3, 4)));

        SetObservable<Integer> set = base.as(ObservableCollectionType.toSet(
                col -> {
                    snapshotCalls.incrementAndGet();
                    return new LinkedHashSet<>(col);
                },
                cap -> {
                    collectionCalls.incrementAndGet();
                    return new HashSet<>(cap);
                }
        ));

        SetObservable<Integer> evens = set.filter(n -> Observable.immutable(n % 2 == 0));
        Set<Integer> result = evens.get();
        assertEquals(Set.of(2, 4), result);
        assertTrue(snapshotCalls.get() > 0);
        assertTrue(collectionCalls.get() > 0);
    }

    @Test
    void customToSetFactoriesUsedByFlatMapEach() {
        AtomicInteger snapshotCalls = new AtomicInteger();

        SetObservable<Integer> base = Observable.set(new HashSet<>(Set.of(1, 2)));

        SetObservable<Integer> set = base.as(ObservableCollectionType.toSet(
                col -> {
                    snapshotCalls.incrementAndGet();
                    return new LinkedHashSet<>(col);
                },
                LinkedHashSet::new
        ));

        SetObservable<String> flat = set.flatMapEach(n -> Observable.immutable("v" + n));
        assertEquals(2, flat.get().size());
        assertTrue(snapshotCalls.get() > 0);
    }

    @Test
    void toSetFromSetBase() {
        SetObservable<Integer> base = Observable.set(new HashSet<>(Set.of(10, 20, 30)));
        SetObservable<Integer> copy = base.as(ObservableCollectionType.toSet());

        assertEquals(Set.of(10, 20, 30), copy.get());
        assertThrows(UnsupportedOperationException.class, () -> copy.add(40));

        base.add(40);
        assertEquals(Set.of(10, 20, 30, 40), copy.get());

        base.remove(10);
        assertEquals(Set.of(20, 30, 40), copy.get());
    }

    @Test
    void toListFromSetBase() {
        SetObservable<Integer> base = Observable.set(new HashSet<>(Set.of(1, 2, 3)));
        ListObservable<Integer> list = base.as(ObservableCollectionType.toList());

        List<Integer> result = list.get();
        assertEquals(3, result.size());
        assertTrue(result.containsAll(List.of(1, 2, 3)));
        assertThrows(UnsupportedOperationException.class, () -> list.add(4));
    }
}
