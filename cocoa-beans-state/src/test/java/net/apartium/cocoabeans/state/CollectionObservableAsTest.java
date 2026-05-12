package net.apartium.cocoabeans.state;

import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class CollectionObservableAsTest {

    @Test
    void listToListPreservesOrderAndDuplicates() {
        ListObservable<String> base = Observable.list(new ArrayList<>(List.of("a", "b", "a", "c")));

        ListObservable<String> copy = base.as(CollectorsObservable.toList());

        assertEquals(List.of("a", "b", "a", "c"), copy.get());
    }

    @Test
    void listToListReflectsBaseChanges() {
        ListObservable<Integer> base = Observable.list();
        ListObservable<Integer> copy = base.as(CollectorsObservable.toList());

        assertEquals(List.of(), copy.get());

        base.add(1);
        base.add(2);
        assertEquals(List.of(1, 2), copy.get());

        base.remove((Integer) 1);
        assertEquals(List.of(2), copy.get());

        base.clear();
        assertEquals(List.of(), copy.get());
    }

    @Test
    void listToListSnapshotIsImmutable() {
        ListObservable<Integer> base = Observable.list(new ArrayList<>(List.of(1, 2, 3)));
        ListObservable<Integer> copy = base.as(CollectorsObservable.toList());

        List<Integer> snapshot = copy.get();
        assertThrows(UnsupportedOperationException.class, () -> snapshot.add(4));
    }

    @Test
    void listToListCachesUntilBaseChanges() {
        ListObservable<Integer> base = Observable.list(new ArrayList<>(List.of(1, 2)));
        ListObservable<Integer> copy = base.as(CollectorsObservable.toList());

        List<Integer> first = copy.get();
        List<Integer> second = copy.get();
        assertSame(first, second);

        base.add(3);
        List<Integer> third = copy.get();
        assertNotSame(first, third);
        assertEquals(List.of(1, 2, 3), third);
    }

    @Test
    void listToListSizeTracking() {
        ListObservable<Integer> base = Observable.list();
        ListObservable<Integer> copy = base.as(CollectorsObservable.toList());
        Observable<Integer> size = copy.size();

        assertEquals(0, size.get());

        base.addAll(List.of(10, 20, 30));
        assertEquals(3, size.get());

        base.clear();
        assertEquals(0, size.get());
    }

    @Test
    void listToListNotifiesObservers() {
        ListObservable<Integer> base = Observable.list(new ArrayList<>(List.of(1)));
        ListObservable<Integer> copy = base.as(CollectorsObservable.toList());
        copy.get();

        CountingObserver downstream = new CountingObserver();
        copy.observe(downstream);

        base.add(2);
        assertEquals(1, downstream.count);
        assertSame(copy, downstream.last);

        assertTrue(copy.removeObserver(downstream));
        base.add(3);
        assertEquals(1, downstream.count);
    }

    @Test
    void listToListIgnoresUnrelatedFlagAsDirty() {
        ListObservable<Integer> base = Observable.list(new ArrayList<>(List.of(1)));
        ListObservable<Integer> copy = base.as(CollectorsObservable.toList());
        copy.get();

        CountingObserver downstream = new CountingObserver();
        copy.observe(downstream);

        Observer asObserver = (Observer) copy;
        MutableObservable<Integer> unrelated = Observable.mutable(99);
        asObserver.flagAsDirty(unrelated);

        assertEquals(0, downstream.count);
        assertSame(copy.get(), copy.get());
    }

    @Test
    void listToListUnsupportedMutationsThrow() {
        ListObservable<Integer> copy = Observable.<Integer>list().as(CollectorsObservable.toList());

        assertThrows(UnsupportedOperationException.class, () -> copy.add(1));
        assertThrows(UnsupportedOperationException.class, () -> copy.remove((Integer) 1));

        List<Integer> emptyList = List.of();

        assertThrows(UnsupportedOperationException.class, () -> copy.addAll(emptyList));
        assertThrows(UnsupportedOperationException.class, () -> copy.removeAll(emptyList));
        assertThrows(UnsupportedOperationException.class, () -> copy.removeIf(n -> true));
        assertThrows(UnsupportedOperationException.class, () -> copy.retainAll(emptyList));
        assertThrows(UnsupportedOperationException.class, copy::clear);
        assertThrows(UnsupportedOperationException.class, () -> copy.add(0, 1));
        assertThrows(UnsupportedOperationException.class, () -> copy.remove(0));

        Comparator<Integer> comparator = Comparator.naturalOrder();

        assertThrows(UnsupportedOperationException.class, () -> copy.sort(comparator));
    }

    @Test
    void listToSetDeduplicates() {
        ListObservable<Integer> base = Observable.list(new ArrayList<>(List.of(1, 2, 2, 3, 3, 3)));

        SetObservable<Integer> set = base.as(CollectorsObservable.toSet());

        assertEquals(Set.of(1, 2, 3), set.get());
    }

    @Test
    void listToSetReflectsBaseChanges() {
        ListObservable<Integer> base = Observable.list();
        SetObservable<Integer> set = base.as(CollectorsObservable.toSet());

        assertEquals(Set.of(), set.get());

        base.addAll(List.of(1, 2, 3));
        assertEquals(Set.of(1, 2, 3), set.get());

        base.remove((Integer) 2);
        assertEquals(Set.of(1, 3), set.get());
    }

    @Test
    void listToSetSnapshotIsImmutable() {
        ListObservable<Integer> base = Observable.list(new ArrayList<>(List.of(1, 2, 3)));
        SetObservable<Integer> set = base.as(CollectorsObservable.toSet());

        Set<Integer> snapshot = set.get();
        assertThrows(UnsupportedOperationException.class, () -> snapshot.add(4));
    }

    @Test
    void listToSetUnsupportedMutationsThrow() {
        SetObservable<Integer> set = Observable.<Integer>list().as(CollectorsObservable.toSet());

        assertThrows(UnsupportedOperationException.class, () -> set.add(1));
        assertThrows(UnsupportedOperationException.class, () -> set.remove(1));

        List<Integer> emptyList = List.of();

        assertThrows(UnsupportedOperationException.class, () -> set.addAll(emptyList));
        assertThrows(UnsupportedOperationException.class, () -> set.removeAll(emptyList));
        assertThrows(UnsupportedOperationException.class, () -> set.removeIf(n -> true));
        assertThrows(UnsupportedOperationException.class, () -> set.retainAll(emptyList));
        assertThrows(UnsupportedOperationException.class, set::clear);
    }

    @Test
    void setToListConverts() {
        SetObservable<String> base = Observable.set(new HashSet<>(Set.of("a", "b", "c")));
        ListObservable<String> list = base.as(CollectorsObservable.toList());

        List<String> result = list.get();
        assertEquals(3, result.size());
        assertTrue(result.containsAll(List.of("a", "b", "c")));
    }

    @Test
    void setToListReflectsBaseChanges() {
        SetObservable<String> base = Observable.set();
        ListObservable<String> list = base.as(CollectorsObservable.toList());

        assertEquals(List.of(), list.get());

        base.add("x");
        assertEquals(1, list.get().size());
        assertTrue(list.get().contains("x"));

        base.add("y");
        assertEquals(2, list.get().size());

        base.clear();
        assertEquals(List.of(), list.get());
    }

    @Test
    void setToListUnsupportedMutationsThrow() {
        ListObservable<Integer> list = Observable.<Integer>set().as(CollectorsObservable.toList());

        assertThrows(UnsupportedOperationException.class, () -> list.add(1));
        assertThrows(UnsupportedOperationException.class, () -> list.remove((Integer) 1));
        assertThrows(UnsupportedOperationException.class, () -> list.add(0, 1));
        assertThrows(UnsupportedOperationException.class, () -> list.remove(0));

        Comparator<Integer> comparator = Comparator.naturalOrder();

        assertThrows(UnsupportedOperationException.class, () -> list.sort(comparator));
        assertThrows(UnsupportedOperationException.class, list::clear);
    }

    @Test
    void setToSetConverts() {
        SetObservable<Integer> base = Observable.set(new HashSet<>(Set.of(1, 2, 3)));
        SetObservable<Integer> copy = base.as(CollectorsObservable.toSet());

        assertEquals(Set.of(1, 2, 3), copy.get());
    }

    @Test
    void setToSetReflectsBaseChanges() {
        SetObservable<Integer> base = Observable.set();
        SetObservable<Integer> copy = base.as(CollectorsObservable.toSet());

        assertEquals(Set.of(), copy.get());

        base.add(10);
        assertEquals(Set.of(10), copy.get());

        base.add(20);
        assertEquals(Set.of(10, 20), copy.get());

        base.remove(10);
        assertEquals(Set.of(20), copy.get());
    }

    @Test
    void setToSetCachesUntilBaseChanges() {
        SetObservable<Integer> base = Observable.set(new HashSet<>(Set.of(1, 2)));
        SetObservable<Integer> copy = base.as(CollectorsObservable.toSet());

        Set<Integer> first = copy.get();
        Set<Integer> second = copy.get();
        assertSame(first, second);

        base.add(3);
        Set<Integer> third = copy.get();
        assertNotSame(first, third);
    }

    @Test
    void setToSetSizeTracking() {
        SetObservable<Integer> base = Observable.set();
        SetObservable<Integer> copy = base.as(CollectorsObservable.toSet());
        Observable<Integer> size = copy.size();

        assertEquals(0, size.get());

        base.addAll(Set.of(1, 2, 3));
        assertEquals(3, size.get());

        base.clear();
        assertEquals(0, size.get());
    }

    @Test
    void listToListMapEachChain() {
        ListObservable<String> base = Observable.list(new ArrayList<>(List.of("kfir", "apartium")));
        ListObservable<String> copy = base.as(CollectorsObservable.toList());

        ListObservable<Integer> lengths = copy.mapEach(String::length);
        assertEquals(List.of(4, 8), lengths.get());

        base.add("voigon");
        assertEquals(List.of(4, 8, 6), lengths.get());
    }

    @Test
    void listToListFlatMapEachChain() {
        MutableObservable<String> name = Observable.mutable("kfir");
        record Player(MutableObservable<String> name) {}

        Player p = new Player(name);
        ListObservable<Player> base = Observable.list(new ArrayList<>(List.of(p)));
        ListObservable<Player> copy = base.as(CollectorsObservable.toList());

        ListObservable<String> names = copy.flatMapEach(Player::name);
        assertEquals(List.of("kfir"), names.get());

        name.set("apartium");
        assertEquals(List.of("apartium"), names.get());
    }

    @Test
    void listToListFilterChain() {
        ListObservable<Integer> base = Observable.list(new ArrayList<>(List.of(1, 2, 3, 4, 5)));
        ListObservable<Integer> copy = base.as(CollectorsObservable.toList());

        ListObservable<Integer> evens = copy.filter(n -> Observable.immutable(n % 2 == 0));
        assertEquals(List.of(2, 4), evens.get());

        base.add(6);
        assertEquals(List.of(2, 4, 6), evens.get());
    }

    @Test
    void listToListTwoArgFilter() {
        MutableObservable<String> name1 = Observable.mutable("alice");
        MutableObservable<String> name2 = Observable.mutable("bob");

        record Entry(String id, MutableObservable<String> name) {}

        Entry e1 = new Entry("1", name1);
        Entry e2 = new Entry("2", name2);

        ListObservable<Entry> base = Observable.list(new ArrayList<>(List.of(e1, e2)));
        ListObservable<Entry> copy = base.as(CollectorsObservable.toList());

        ListObservable<Entry> startsWithA = copy.filter(
                Entry::name,
                n -> n.startsWith("a")
        );

        assertEquals(List.of(e1), startsWithA.get());

        name2.set("apartium");
        assertEquals(List.of(e1, e2), startsWithA.get());
    }

    @Test
    void listToSetMapEachChain() {
        ListObservable<String> base = Observable.list(new ArrayList<>(List.of("kfir", "apartium", "lior")));
        SetObservable<String> set = base.as(CollectorsObservable.toSet());

        SetObservable<Integer> lengths = set.mapEach(String::length);
        // "kfir" -> 4, "apartium" -> 8, "lior" -> 4, deduped in set
        assertEquals(Set.of(4, 8), lengths.get());

        base.add("voigon");
        assertEquals(Set.of(4, 6, 8), lengths.get());
    }

    @Test
    void listToSetFilterChain() {
        ListObservable<Integer> base = Observable.list(new ArrayList<>(List.of(1, 2, 2, 3, 4)));
        SetObservable<Integer> set = base.as(CollectorsObservable.toSet());

        SetObservable<Integer> evens = set.filter(n -> Observable.immutable(n % 2 == 0));
        assertEquals(Set.of(2, 4), evens.get());
    }

    @Test
    void listToSetFlatMapEachChain() {
        MutableObservable<String> name = Observable.mutable("kfir");
        record Player(MutableObservable<String> name) {}

        Player p = new Player(name);
        ListObservable<Player> base = Observable.list(new ArrayList<>(List.of(p)));
        SetObservable<Player> set = base.as(CollectorsObservable.toSet());

        SetObservable<String> names = set.flatMapEach(Player::name);
        assertEquals(Set.of("kfir"), names.get());

        name.set("apartium");
        assertEquals(Set.of("apartium"), names.get());
    }

    @Test
    void listToSetTwoArgFilter() {
        MutableObservable<String> name1 = Observable.mutable("alice");
        MutableObservable<String> name2 = Observable.mutable("bob");

        record Entry(String id, MutableObservable<String> name) {}

        Entry e1 = new Entry("1", name1);
        Entry e2 = new Entry("2", name2);

        ListObservable<Entry> base = Observable.list(new ArrayList<>(List.of(e1, e2)));
        SetObservable<Entry> set = base.as(CollectorsObservable.toSet());

        SetObservable<Entry> startsWithA = set.filter(
                Entry::name,
                n -> n.startsWith("a")
        );

        assertEquals(Set.of(e1), startsWithA.get());

        name2.set("apartium");
        assertEquals(Set.of(e1, e2), startsWithA.get());
    }

    @Test
    void setToListMapEachChain() {
        SetObservable<String> base = Observable.set(new HashSet<>(Set.of("kfir", "apartium")));
        ListObservable<String> list = base.as(CollectorsObservable.toList());

        ListObservable<Integer> lengths = list.mapEach(String::length);
        List<Integer> result = lengths.get();

        assertEquals(2, result.size());
        assertTrue(result.containsAll(List.of(4, 8)));
    }

    @Test
    void setToListFilterChain() {
        SetObservable<Integer> base = Observable.set(new HashSet<>(Set.of(1, 2, 3, 4, 5)));
        ListObservable<Integer> list = base.as(CollectorsObservable.toList());

        ListObservable<Integer> evens = list.filter(n -> Observable.immutable(n % 2 == 0));
        List<Integer> result = evens.get();

        assertTrue(result.containsAll(List.of(2, 4)));
        assertEquals(2, result.size());
    }

    @Test
    void setToSetMapEachChain() {
        SetObservable<String> base = Observable.set(new HashSet<>(Set.of("kfir", "apartium")));
        SetObservable<String> copy = base.as(CollectorsObservable.toSet());

        SetObservable<Integer> lengths = copy.mapEach(String::length);
        assertEquals(Set.of(4, 8), lengths.get());

        base.add("voigon");
        assertEquals(Set.of(4, 6, 8), lengths.get());
    }

    @Test
    void setToSetFilterChain() {
        SetObservable<Integer> base = Observable.set(new HashSet<>(Set.of(1, 2, 3, 4, 5)));
        SetObservable<Integer> copy = base.as(CollectorsObservable.toSet());

        SetObservable<Integer> evens = copy.filter(n -> Observable.immutable(n % 2 == 0));
        assertEquals(Set.of(2, 4), evens.get());

        base.add(6);
        assertEquals(Set.of(2, 4, 6), evens.get());
    }

    @Test
    void setToSetFlatMapEachChain() {
        MutableObservable<String> name = Observable.mutable("kfir");
        record Player(MutableObservable<String> name) {}

        Player p = new Player(name);
        SetObservable<Player> base = Observable.set(new HashSet<>(Set.of(p)));
        SetObservable<Player> copy = base.as(CollectorsObservable.toSet());

        SetObservable<String> names = copy.flatMapEach(Player::name);
        assertEquals(Set.of("kfir"), names.get());

        name.set("apartium");
        assertEquals(Set.of("apartium"), names.get());
    }

    @Test
    void customToListFactoriesPropagateToFilter() {
        AtomicInteger snapshotCalls = new AtomicInteger();
        AtomicInteger collectionCalls = new AtomicInteger();

        ListObservable<Integer> base = Observable.list(new ArrayList<>(List.of(1, 2, 3, 4)));

        ListObservable<Integer> list = base.as(CollectorsObservable.toList(
                col -> {
                    snapshotCalls.incrementAndGet();
                    return new ArrayList<>(col);
                },
                cap -> {
                    collectionCalls.incrementAndGet();
                    return new ArrayList<>(cap);
                }
        ));

        ListObservable<Integer> filtered = list.filter(n -> Observable.immutable(n > 2));
        assertEquals(List.of(3, 4), filtered.get());

        assertTrue(snapshotCalls.get() > 0);
        assertTrue(collectionCalls.get() > 0);
    }

    @Test
    void customToSetFactoriesPropagateToFlatMapEach() {
        AtomicInteger snapshotCalls = new AtomicInteger();

        MutableObservable<String> name = Observable.mutable("kfir");
        record Player(MutableObservable<String> name) {}

        Player p = new Player(name);
        ListObservable<Player> base = Observable.list(new ArrayList<>(List.of(p)));

        SetObservable<Player> set = base.as(CollectorsObservable.toSet(
                col -> {
                    snapshotCalls.incrementAndGet();
                    return new LinkedHashSet<>(col);
                },
                LinkedHashSet::new
        ));

        SetObservable<String> names = set.flatMapEach(Player::name);
        Set<String> result = names.get();

        assertEquals(Set.of("kfir"), result);
        assertTrue(snapshotCalls.get() > 0);
    }

    @Test
    void asOnEmptyCollection() {
        ListObservable<String> emptyList = Observable.list();
        SetObservable<String> set = emptyList.as(CollectorsObservable.toSet());
        assertEquals(Set.of(), set.get());
        assertEquals(0, set.size().get());

        SetObservable<String> emptySet = Observable.set();
        ListObservable<String> list = emptySet.as(CollectorsObservable.toList());
        assertEquals(List.of(), list.get());
        assertEquals(0, list.size().get());
    }

    @Test
    void asObserverNotificationPropagatesThroughChain() {
        ListObservable<Integer> base = Observable.list(new ArrayList<>(List.of(1, 2, 3)));
        SetObservable<Integer> set = base.as(CollectorsObservable.toSet());
        SetObservable<Integer> mapped = set.mapEach(n -> n * 10);

        CountingObserver downstream = new CountingObserver();
        mapped.observe(downstream);

        base.add(4);
        assertEquals(1, downstream.count);
        assertEquals(Set.of(10, 20, 30, 40), mapped.get());
    }

    @Test
    void setToSetNotifiesObservers() {
        SetObservable<Integer> base = Observable.set(new HashSet<>(Set.of(1)));
        SetObservable<Integer> copy = base.as(CollectorsObservable.toSet());
        copy.get();

        CountingObserver downstream = new CountingObserver();
        copy.observe(downstream);

        base.add(2);
        assertEquals(1, downstream.count);
        assertSame(copy, downstream.last);
    }

    @Test
    void setToSetIgnoresUnrelatedFlagAsDirty() {
        SetObservable<Integer> base = Observable.set(new HashSet<>(Set.of(1)));
        SetObservable<Integer> copy = base.as(CollectorsObservable.toSet());
        copy.get();

        CountingObserver downstream = new CountingObserver();
        copy.observe(downstream);

        Observer asObserver = (Observer) copy;
        MutableObservable<Integer> unrelated = Observable.mutable(99);
        asObserver.flagAsDirty(unrelated);

        assertEquals(0, downstream.count);
    }

    @Test
    void setToListTwoArgFilter() {
        MutableObservable<String> name1 = Observable.mutable("alice");
        MutableObservable<String> name2 = Observable.mutable("bob");

        record Entry(String id, MutableObservable<String> name) {}

        Entry e1 = new Entry("1", name1);
        Entry e2 = new Entry("2", name2);

        SetObservable<Entry> base = Observable.set(new HashSet<>(Set.of(e1, e2)));
        ListObservable<Entry> list = base.as(CollectorsObservable.toList());

        ListObservable<Entry> startsWithA = list.filter(
                Entry::name,
                n -> n.startsWith("a")
        );

        assertEquals(1, startsWithA.get().size());
        assertTrue(startsWithA.get().contains(e1));

        name2.set("apartium");
        assertEquals(2, startsWithA.get().size());
    }

    @Test
    void setToSetTwoArgFilter() {
        MutableObservable<String> name1 = Observable.mutable("alice");
        MutableObservable<String> name2 = Observable.mutable("bob");

        record Entry(String id, MutableObservable<String> name) {}

        Entry e1 = new Entry("1", name1);
        Entry e2 = new Entry("2", name2);

        SetObservable<Entry> base = Observable.set(new HashSet<>(Set.of(e1, e2)));
        SetObservable<Entry> copy = base.as(CollectorsObservable.toSet());

        SetObservable<Entry> startsWithA = copy.filter(
                Entry::name,
                n -> n.startsWith("a")
        );

        assertEquals(Set.of(e1), startsWithA.get());

        name2.set("apartium");
        assertEquals(Set.of(e1, e2), startsWithA.get());
    }

    @Test
    void setToListFlatMapEachChain() {
        MutableObservable<String> name = Observable.mutable("kfir");
        record Player(MutableObservable<String> name) {}

        Player p = new Player(name);
        SetObservable<Player> base = Observable.set(new HashSet<>(Set.of(p)));
        ListObservable<Player> list = base.as(CollectorsObservable.toList());

        ListObservable<String> names = list.flatMapEach(Player::name);
        assertEquals(List.of("kfir"), names.get());

        name.set("apartium");
        assertEquals(List.of("apartium"), names.get());
    }

    @Test
    void doubleConversionListToSetToList() {
        ListObservable<Integer> base = Observable.list(new ArrayList<>(List.of(1, 2, 2, 3)));

        SetObservable<Integer> set = base.as(CollectorsObservable.toSet());
        assertEquals(Set.of(1, 2, 3), set.get());

        ListObservable<Integer> backToList = set.as(CollectorsObservable.toList());
        assertEquals(3, backToList.get().size());
        assertTrue(backToList.get().containsAll(List.of(1, 2, 3)));

        base.add(4);
        assertEquals(Set.of(1, 2, 3, 4), set.get());
        assertEquals(4, backToList.get().size());
    }

    @Test
    void chainedMapEachFilterOnConvertedObservable() {
        ListObservable<Integer> base = Observable.list(new ArrayList<>(List.of(1, 2, 3, 4, 5)));
        SetObservable<Integer> set = base.as(CollectorsObservable.toSet());

        SetObservable<String> result = set
                .filter(n -> Observable.immutable(n % 2 == 0))
                .mapEach(n -> "v=" + n);

        Set<String> snapshot = result.get();
        assertEquals(Set.of("v=2", "v=4"), snapshot);

        base.add(6);
        assertEquals(Set.of("v=2", "v=4", "v=6"), result.get());
    }

    private static final class CountingObserver implements Observer {
        int count;
        Observable<?> last;

        @Override
        public void flagAsDirty(Observable<?> observable) {
            count++;
            last = observable;
        }
    }
}
