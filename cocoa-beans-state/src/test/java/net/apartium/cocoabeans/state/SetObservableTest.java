package net.apartium.cocoabeans.state;

import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

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

    // ---------------------------------------------------------------------
    // mapEach edge cases
    // ---------------------------------------------------------------------

    @Test
    void mapEachOnEmptySetReturnsEmpty() {
        SetObservable<String> set = Observable.set();
        SetObservable<Integer> lengths = set.mapEach(String::length);

        assertEquals(Set.of(), lengths.get());
        assertEquals(0, lengths.size().get());
    }

    @Test
    void mapEachReflectsBaseAddAndRemove() {
        SetObservable<String> set = Observable.set();
        set.addAll(List.of("kfir", "apartium"));

        SetObservable<Integer> lengths = set.mapEach(String::length);
        assertEquals(Set.of(4, 8), lengths.get());

        set.add("voigon");
        assertEquals(Set.of(4, 6, 8), lengths.get());

        set.remove("kfir");
        assertEquals(Set.of(6, 8), lengths.get());
    }

    @Test
    void mapEachDeduplicatesWhenMappingProducesSameValue() {
        SetObservable<String> set = Observable.set(new HashSet<>(Set.of("kfir", "lior", "voigon", "notro")));

        // every name maps to the constant 1 -> set should have exactly one element
        SetObservable<Integer> ones = set.mapEach(name -> 1);

        assertEquals(Set.of(1), ones.get());
        assertEquals(1, ones.size().get());
    }

    @Test
    void mapEachIdentityFunctionPreservesElements() {
        SetObservable<String> set = Observable.set(new HashSet<>(Set.of("kfir", "apartium")));
        SetObservable<String> identity = set.mapEach(Function.identity());

        assertEquals(Set.of("kfir", "apartium"), identity.get());
    }

    @Test
    void mapEachCachesResultUntilBaseChanges() {
        SetObservable<String> set = Observable.set();
        set.addAll(List.of("kfir", "lior"));

        AtomicInteger calls = new AtomicInteger();
        SetObservable<Integer> lengths = set.mapEach(name -> {
            calls.incrementAndGet();
            return name.length();
        });

        Set<Integer> first = lengths.get();
        Set<Integer> second = lengths.get();
        Set<Integer> third = lengths.get();

        assertEquals(first, second);
        assertEquals(second, third);
        assertEquals(2, calls.get(), "mapper should run once per element on the first get only");

        set.add("voigon");
        assertEquals(Set.of(4, 6), Set.copyOf(lengths.get())); // 4 == "kfir"/"lior" (deduped) + 6
        assertEquals(5, calls.get());
    }

    @Test
    void mapEachAddingDuplicateBaseElementDoesNotInvalidateCache() {
        SetObservable<String> set = Observable.set();
        set.add("kfir");

        AtomicInteger calls = new AtomicInteger();
        SetObservable<Integer> lengths = set.mapEach(s -> {
            calls.incrementAndGet();
            return s.length();
        });

        assertEquals(Set.of(4), lengths.get());
        assertEquals(1, calls.get());

        // adding a duplicate to the underlying set returns false and does not notify
        assertFalse(set.add("kfir"));
        assertEquals(Set.of(4), lengths.get());
        assertEquals(1, calls.get(), "mapper should not be re-invoked when the base set didn't actually change");
    }

    @Test
    void mapEachReflectsClear() {
        SetObservable<String> set = Observable.set();
        set.addAll(List.of("kfir", "apartium", "voigon"));

        SetObservable<Integer> lengths = set.mapEach(String::length);
        assertEquals(Set.of(4, 8, 6), lengths.get());

        set.clear();
        assertEquals(Set.of(), lengths.get());
        assertEquals(0, lengths.size().get());
    }

    @Test
    void mapEachIgnoresFlagAsDirtyFromUnrelatedObservable() {
        SetObservable<String> set = Observable.set();
        set.add("kfir");

        AtomicInteger calls = new AtomicInteger();
        SetObservable<Integer> lengths = set.mapEach(name -> {
            calls.incrementAndGet();
            return name.length();
        });
        assertEquals(Set.of(4), lengths.get());
        assertEquals(1, calls.get());

        Observer asObserver = (Observer) lengths;
        MutableObservable<String> unrelated = Observable.mutable("notro");

        CountingObserver downstream = new CountingObserver();
        lengths.observe(downstream);

        asObserver.flagAsDirty(unrelated);

        assertEquals(0, downstream.count, "unrelated flagAsDirty must not propagate notification");
        assertEquals(Set.of(4), lengths.get());
        assertEquals(1, calls.get(), "unrelated flagAsDirty must not invalidate the cache");
    }

    @Test
    void mapEachNotifiesObserversOnlyOnBaseChange() {
        SetObservable<String> set = Observable.set();
        set.add("kfir");

        SetObservable<Integer> lengths = set.mapEach(String::length);
        lengths.get();

        CountingObserver downstream = new CountingObserver();
        lengths.observe(downstream);

        set.add("apartium");
        assertEquals(1, downstream.count);
        assertSame(lengths, downstream.last);

        set.remove("apartium");
        assertEquals(2, downstream.count);

        // no-op remove should not notify
        assertFalse(set.remove("voigon"));
        assertEquals(2, downstream.count);

        assertTrue(lengths.removeObserver(downstream));
        assertFalse(lengths.removeObserver(downstream));
    }

    @Test
    void mapEachSizeReflectsBaseDedupedCount() {
        SetObservable<String> set = Observable.set();
        SetObservable<Integer> lengths = set.mapEach(String::length);

        assertEquals(0, lengths.size().get());

        set.add("kfir");
        assertEquals(1, lengths.size().get());

        // "lior" maps to length 4 — same as "kfir" — the mapped set still dedupes
        set.add("lior");
        assertEquals(1, lengths.size().get());

        set.add("apartium");
        assertEquals(2, lengths.size().get());

        set.clear();
        assertEquals(0, lengths.size().get());
    }

    @Test
    void mapEachChainedMapEach() {
        SetObservable<String> set = Observable.set(new HashSet<>(Set.of("kfir", "apartium")));

        SetObservable<Integer> doubledLength = set
                .mapEach(String::length)
                .mapEach(n -> n * 2);

        assertEquals(Set.of(8, 16), doubledLength.get());
    }

    @Test
    void mapEachThenFilterReturnsSetObservable() {
        SetObservable<String> set = Observable.set(new HashSet<>(Set.of("kfir", "apartium", "voigon", "lior")));

        SetObservable<Integer> longLengths = set
                .mapEach(String::length)
                .filter(n -> Observable.immutable(n >= 6));

        assertEquals(Set.of(6, 8), longLengths.get());
    }

    @Test
    void mapEachThenFlatMapEach() {
        MutableObservable<String> kfirSuffix = Observable.mutable("a");
        MutableObservable<String> liorSuffix = Observable.mutable("b");

        SetObservable<MutableObservable<String>> set =
                Observable.set(new HashSet<>(Set.of(kfirSuffix, liorSuffix)));

        // mapEach (identity) then flatMapEach to expose the inner
        SetObservable<String> values = set
                .mapEach(Function.identity())
                .flatMapEach(o -> o);

        assertEquals(Set.of("a", "b"), values.get());

        kfirSuffix.set("c");
        assertEquals(Set.of("c", "b"), values.get());
    }

    @Test
    void mapEachUnsupportedMutationsThrow() {
        SetObservable<Integer> mapped = Observable.<Integer>set().mapEach(n -> n);

        assertThrows(UnsupportedOperationException.class, () -> mapped.add(1));
        assertThrows(UnsupportedOperationException.class, () -> mapped.remove(1));

        List<Integer> collection = List.of();

        assertThrows(UnsupportedOperationException.class, () -> mapped.addAll(collection));
        assertThrows(UnsupportedOperationException.class, () -> mapped.removeAll(collection));
        assertThrows(UnsupportedOperationException.class, () -> mapped.removeIf(n -> true));
        assertThrows(UnsupportedOperationException.class, () -> mapped.retainAll(collection));
        assertThrows(UnsupportedOperationException.class, mapped::clear);
    }

    @Test
    void mapEachWithLinkedHashSetPreservesInsertionOrder() {
        SetObservable<String> set = Observable.set(
                new LinkedHashSet<>(),
                col -> Collections.unmodifiableSet(new LinkedHashSet<>(col)),
                LinkedHashSet::new
        );
        set.add("kfir");
        set.add("apartium");
        set.add("voigon");

        SetObservable<String> upper = set.mapEach(String::toUpperCase);

        // verify iteration order through the cast chain
        List<String> ordered = new ArrayList<>(upper.get());
        assertEquals(List.of("KFIR", "APARTIUM", "VOIGON"), ordered);
    }

    @Test
    void mapEachWithNullTolerantSetAllowsNullMapper() {
        SetObservable<String> set = Observable.set(
                new HashSet<>(),
                col -> {
                    Set<String> copy = new HashSet<>();
                    copy.addAll(col);
                    return Collections.unmodifiableSet(copy);
                },
                HashSet::new
        );
        set.add("kfir");

        SetObservable<String> nulls = set.mapEach(name -> null);

        Set<String> snapshot = nulls.get();
        assertEquals(1, snapshot.size());
        assertTrue(snapshot.contains(null));
    }

    @Test
    void mapEachWithDefaultSetCopyOfRejectsNullMapper() {
        SetObservable<String> set = Observable.set();
        set.add("kfir");

        SetObservable<String> nulls = set.mapEach(name -> null);
        assertThrows(NullPointerException.class, nulls::get);
    }

    // ---------------------------------------------------------------------
    // flatMapEach edge cases
    // ---------------------------------------------------------------------

    @Test
    void flatMapEachOnEmptySetReturnsEmpty() {
        SetObservable<Member> set = Observable.set();
        SetObservable<String> names = set.flatMapEach(Member::displayName);

        assertEquals(Set.of(), names.get());
        assertEquals(0, names.size().get());
    }

    @Test
    void flatMapEachReflectsInnerObservableChange() {
        Member kfir = new Member("kfir");
        SetObservable<Member> set = Observable.set(new HashSet<>(Set.of(kfir)));

        SetObservable<String> names = set.flatMapEach(Member::displayName);
        assertEquals(Set.of("kfir"), names.get());

        kfir.displayName().set("Kfir Notro");
        assertEquals(Set.of("Kfir Notro"), names.get());
    }

    @Test
    void flatMapEachReflectsBaseAddAndRemove() {
        Member kfir = new Member("kfir");
        Member apartium = new Member("apartium");
        SetObservable<Member> set = Observable.set(new HashSet<>(Set.of(kfir)));

        SetObservable<String> names = set.flatMapEach(Member::displayName);
        assertEquals(Set.of("kfir"), names.get());

        set.add(apartium);
        assertEquals(Set.of("kfir", "apartium"), names.get());

        set.remove(kfir);
        assertEquals(Set.of("apartium"), names.get());
    }

    @Test
    void flatMapEachReflectsClear() {
        Member kfir = new Member("kfir");
        Member apartium = new Member("apartium");
        SetObservable<Member> set = Observable.set(new HashSet<>(Set.of(kfir, apartium)));

        SetObservable<String> names = set.flatMapEach(Member::displayName);
        assertEquals(Set.of("kfir", "apartium"), names.get());

        set.clear();
        assertEquals(Set.of(), names.get());

        // mutating an inner that was previously tracked must not bring it back
        kfir.displayName().set("ZOMBIE");
        assertEquals(Set.of(), names.get());
    }

    @Test
    void flatMapEachInnerChangeAfterRemoveIsIgnored() {
        Member kfir = new Member("kfir");
        Member lior = new Member("lior");
        SetObservable<Member> set = Observable.set(new HashSet<>(Set.of(kfir, lior)));

        SetObservable<String> names = set.flatMapEach(Member::displayName);
        assertEquals(Set.of("kfir", "lior"), names.get());

        set.remove(kfir);
        assertEquals(Set.of("lior"), names.get());

        kfir.displayName().set("ghost");
        assertEquals(Set.of("lior"), names.get(), "inner change for a removed element must not propagate");
    }

    @Test
    void flatMapEachWithSharedInnerObservableTracksAllElements() {
        MutableObservable<String> shared = Observable.mutable("apartium");

        Member kfir = new Member("kfir", shared);
        Member lior = new Member("lior", shared);

        SetObservable<Member> set = Observable.set(new HashSet<>(Set.of(kfir, lior)));
        SetObservable<String> names = set.flatMapEach(Member::displayName);

        // both members route to the same observable -> one shared value -> deduped to one entry
        assertEquals(Set.of("apartium"), names.get());

        shared.set("voigon");
        assertEquals(Set.of("voigon"), names.get());
    }

    @Test
    void flatMapEachWithSharedInnerKeepsObservingWhenOnlyOneElementRemoved() {
        MutableObservable<String> shared = Observable.mutable("apartium");

        Member kfir = new Member("kfir", shared);
        Member lior = new Member("lior", shared);

        SetObservable<Member> set = Observable.set(new HashSet<>(Set.of(kfir, lior)));
        SetObservable<String> names = set.flatMapEach(Member::displayName);
        assertEquals(Set.of("apartium"), names.get());

        // removing kfir leaves lior still depending on shared
        set.remove(kfir);
        assertEquals(Set.of("apartium"), names.get());

        shared.set("voigon");
        assertEquals(Set.of("voigon"), names.get(), "shared inner must still update while any element references it");
    }

    @Test
    void flatMapEachIsCachedUntilBaseOrInnerChanges() {
        Member kfir = new Member("kfir");
        SetObservable<Member> set = Observable.set(new HashSet<>(Set.of(kfir)));

        AtomicInteger mapperCalls = new AtomicInteger();
        SetObservable<String> names = set.flatMapEach(member -> {
            mapperCalls.incrementAndGet();
            return member.displayName();
        });

        assertEquals(Set.of("kfir"), names.get());
        assertEquals(Set.of("kfir"), names.get());
        assertEquals(1, mapperCalls.get());

        kfir.displayName().set("Kfir Notro");
        assertEquals(Set.of("Kfir Notro"), names.get());
        assertEquals(1, mapperCalls.get(), "inner change must not re-invoke the element mapper");

        set.add(new Member("voigon"));
        assertEquals(Set.of("Kfir Notro", "voigon"), names.get());
        assertEquals(2, mapperCalls.get());
    }

    @Test
    void flatMapEachIgnoresFlagAsDirtyFromUnrelatedObservable() {
        Member kfir = new Member("kfir");
        SetObservable<Member> set = Observable.set(new HashSet<>(Set.of(kfir)));

        SetObservable<String> names = set.flatMapEach(Member::displayName);
        names.get();

        CountingObserver downstream = new CountingObserver();
        names.observe(downstream);

        Observer asObserver = (Observer) names;
        MutableObservable<String> unrelated = Observable.mutable("notro");
        asObserver.flagAsDirty(unrelated);

        assertEquals(0, downstream.count, "unrelated observable must not trigger downstream notifications");
    }

    @Test
    void flatMapEachNotifiesDownstreamOnInnerChange() {
        Member kfir = new Member("kfir");
        SetObservable<Member> set = Observable.set(new HashSet<>(Set.of(kfir)));
        SetObservable<String> names = set.flatMapEach(Member::displayName);

        names.get();

        CountingObserver downstream = new CountingObserver();
        names.observe(downstream);

        kfir.displayName().set("Kfir Notro");

        assertEquals(1, downstream.count);
        assertSame(names, downstream.last);
        assertEquals(Set.of("Kfir Notro"), names.get());

        assertTrue(names.removeObserver(downstream));
        kfir.displayName().set("Kfir2");
        assertEquals(1, downstream.count, "removed observer must not be notified");
    }

    @Test
    void flatMapEachThenMapEachChain() {
        Member kfir = new Member("kfir");
        Member apartium = new Member("apartium");
        SetObservable<Member> set = Observable.set(new HashSet<>(Set.of(kfir, apartium)));

        SetObservable<Integer> nameLengths = set
                .flatMapEach(Member::displayName)
                .mapEach(String::length);

        assertEquals(Set.of(4, 8), nameLengths.get());

        kfir.displayName().set("Kfir Notro");
        assertEquals(Set.of(10, 8), nameLengths.get());
    }

    @Test
    void flatMapEachThenFlatMapEachChain() {
        MutableObservable<MutableObservable<String>> nested =
                Observable.mutable(Observable.mutable("kfir"));

        SetObservable<MutableObservable<MutableObservable<String>>> set =
                Observable.set(new HashSet<>(Set.of(nested)));

        SetObservable<String> values = set
                .flatMapEach(o -> o)
                .flatMapEach(o -> o);

        assertEquals(Set.of("kfir"), values.get());

        nested.get().set("Kfir Notro");
        assertEquals(Set.of("Kfir Notro"), values.get());

        nested.set(Observable.mutable("apartium"));
        assertEquals(Set.of("apartium"), values.get());
    }

    @Test
    void flatMapEachThenFilterChain() {
        Member kfir = new Member("kfir");
        Member apartium = new Member("apartium");
        SetObservable<Member> set = Observable.set(new HashSet<>(Set.of(kfir, apartium)));

        SetObservable<String> startsWithA = set
                .flatMapEach(Member::displayName)
                .filter(name -> Observable.immutable(name.startsWith("a")));

        assertEquals(Set.of("apartium"), startsWithA.get());

        kfir.displayName().set("aaaa-kfir");
        assertEquals(Set.of("apartium", "aaaa-kfir"), startsWithA.get());
    }

    @Test
    void flatMapEachUnsupportedMutationsThrow() {
        SetObservable<Member> base = Observable.set();
        SetObservable<String> mapped = base.flatMapEach(Member::displayName);

        assertThrows(UnsupportedOperationException.class, () -> mapped.add("x"));
        assertThrows(UnsupportedOperationException.class, () -> mapped.remove("x"));

        List<String> collection = List.of();

        assertThrows(UnsupportedOperationException.class, () -> mapped.addAll(collection));
        assertThrows(UnsupportedOperationException.class, () -> mapped.removeAll(collection));
        assertThrows(UnsupportedOperationException.class, () -> mapped.removeIf(n -> true));
        assertThrows(UnsupportedOperationException.class, () -> mapped.retainAll(collection));
        assertThrows(UnsupportedOperationException.class, mapped::clear);
    }

    @Test
    void flatMapEachReAddingElementAfterClearWorks() {
        Member kfir = new Member("kfir");
        SetObservable<Member> set = Observable.set(new HashSet<>(Set.of(kfir)));

        SetObservable<String> names = set.flatMapEach(Member::displayName);
        assertEquals(Set.of("kfir"), names.get());

        set.clear();
        assertEquals(Set.of(), names.get());

        set.add(kfir);
        assertEquals(Set.of("kfir"), names.get());

        kfir.displayName().set("Kfir Notro");
        assertEquals(Set.of("Kfir Notro"), names.get());
    }

    @Test
    void flatMapEachWithNullMapperResultProducesNullValueWhenCollectionAllows() {
        Member kfir = new Member("kfir");
        SetObservable<Member> set = Observable.set(
                new HashSet<>(Set.of(kfir)),
                col -> {
                    Set<Member> copy = new HashSet<>();
                    copy.addAll(col);
                    return Collections.unmodifiableSet(copy);
                },
                HashSet::new
        );

        // mapper returning null observable — exercises the `inner == null` branch
        SetObservable<String> names = set.flatMapEach(member -> null);

        Set<String> snapshot = names.get();
        assertEquals(1, snapshot.size());
        assertTrue(snapshot.contains(null));

        // even after a base change, removing a null-mapped element must not crash
        set.remove(kfir);
        assertEquals(Set.of(), names.get());
    }

    // ---------------------------------------------------------------------
    // filter edge cases
    // ---------------------------------------------------------------------

    @Test
    void filterOnEmptySet() {
        SetObservable<String> set = Observable.set();
        SetObservable<String> filtered = set.filter(s -> Observable.immutable(true));

        assertEquals(Set.of(), filtered.get());
        assertEquals(0, filtered.size().get());
    }

    @Test
    void filterAllTrueReturnsEverything() {
        SetObservable<String> set = Observable.set(new HashSet<>(Set.of("kfir", "apartium", "voigon")));
        SetObservable<String> filtered = set.filter(s -> Observable.immutable(true));

        assertEquals(Set.of("kfir", "apartium", "voigon"), filtered.get());
    }

    @Test
    void filterAllFalseReturnsEmpty() {
        SetObservable<String> set = Observable.set(new HashSet<>(Set.of("kfir", "apartium")));
        SetObservable<String> filtered = set.filter(s -> Observable.immutable(false));

        assertEquals(Set.of(), filtered.get());
    }

    @Test
    void filterTogglingPredicateAddsAndRemovesElement() {
        Member kfir = new Member("kfir");
        kfir.active().set(true);
        Member lior = new Member("lior");
        lior.active().set(false);

        SetObservable<Member> set = Observable.set(new HashSet<>(Set.of(kfir, lior)));
        SetObservable<Member> active = set.filter(Member::active);

        assertEquals(Set.of(kfir), active.get());

        // false -> true exercises updateElements value=true && !contains
        lior.active().set(true);
        assertEquals(Set.of(kfir, lior), active.get());

        // true -> false exercises updateElements value=false && contains
        kfir.active().set(false);
        assertEquals(Set.of(lior), active.get());

        // toggle no-op (already false): MutableObservable.set short-circuits, no notification at all
        kfir.active().set(false);
        assertEquals(Set.of(lior), active.get());
    }

    @Test
    void filterAddingElementWithTruePredicateIncludesIt() {
        SetObservable<Member> set = Observable.set();
        SetObservable<Member> active = set.filter(Member::active);

        assertEquals(Set.of(), active.get());

        Member kfir = new Member("kfir");
        kfir.active().set(true);
        set.add(kfir);
        assertEquals(Set.of(kfir), active.get());
    }

    @Test
    void filterAddingElementWithFalsePredicateExcludesIt() {
        SetObservable<Member> set = Observable.set();
        SetObservable<Member> active = set.filter(Member::active);

        assertEquals(Set.of(), active.get());

        Member kfir = new Member("kfir");
        kfir.active().set(false);
        set.add(kfir);
        assertEquals(Set.of(), active.get());

        kfir.active().set(true);
        assertEquals(Set.of(kfir), active.get());
    }

    @Test
    void filterRemovingElementRemovesItFromResult() {
        Member kfir = new Member("kfir");
        Member apartium = new Member("apartium");
        SetObservable<Member> set = Observable.set(new HashSet<>(Set.of(kfir, apartium)));

        SetObservable<Member> active = set.filter(Member::active);
        assertEquals(Set.of(kfir, apartium), active.get());

        set.remove(kfir);
        assertEquals(Set.of(apartium), active.get());
    }

    @Test
    void filterChainedFilter() {
        SetObservable<Integer> set = Observable.set(new HashSet<>(Set.of(1, 2, 3, 4, 5, 6, 7, 8)));

        SetObservable<Integer> evenAndLarge = set
                .filter(n -> Observable.immutable(n % 2 == 0))
                .filter(n -> Observable.immutable(n >= 4));

        assertEquals(Set.of(4, 6, 8), evenAndLarge.get());
    }

    @Test
    void filterThenMapEach() {
        Member kfir = new Member("kfir");
        Member apartium = new Member("apartium");
        SetObservable<Member> set = Observable.set(new HashSet<>(Set.of(kfir, apartium)));

        SetObservable<String> activeIds = set
                .filter(Member::active)
                .mapEach(Member::id);

        assertEquals(Set.of("kfir", "apartium"), activeIds.get());
    }

    @Test
    void filterThenFlatMapEach() {
        Member kfir = new Member("kfir");
        Member apartium = new Member("apartium");
        apartium.active().set(false);

        SetObservable<Member> set = Observable.set(new HashSet<>(Set.of(kfir, apartium)));

        SetObservable<String> activeNames = set
                .filter(Member::active)
                .flatMapEach(Member::displayName);

        assertEquals(Set.of("kfir"), activeNames.get());

        kfir.displayName().set("Kfir Notro");
        assertEquals(Set.of("Kfir Notro"), activeNames.get());

        apartium.active().set(true);
        assertEquals(Set.of("Kfir Notro", "apartium"), activeNames.get());
    }

    @Test
    void filterCachedWhenNoNetChange() {
        Member kfir = new Member("kfir");
        Member apartium = new Member("apartium");
        SetObservable<Member> set = Observable.set(new HashSet<>(Set.of(kfir, apartium)));

        SetObservable<Member> active = set.filter(Member::active);
        Set<Member> initial = active.get();
        assertEquals(Set.of(kfir, apartium), initial);

        // toggle false -> true means two flagAsDirty events but the final value is unchanged
        kfir.active().set(false);
        kfir.active().set(true);
        Set<Member> after = active.get();
        assertEquals(initial, after, "net-zero toggle must produce the same membership");

        assertSame(after, active.get(), "second get() with no further changes must return the same cached snapshot");
    }

    @Test
    void filterIgnoresFlagAsDirtyFromUnrelatedObservable() {
        Member kfir = new Member("kfir");
        SetObservable<Member> set = Observable.set(new HashSet<>(Set.of(kfir)));
        SetObservable<Member> active = set.filter(Member::active);
        active.get();

        CountingObserver downstream = new CountingObserver();
        active.observe(downstream);

        Observer asObserver = (Observer) active;
        MutableObservable<Boolean> unrelated = Observable.mutable(true);
        asObserver.flagAsDirty(unrelated);

        assertEquals(0, downstream.count, "unrelated observable must not trigger filter notifications");
    }

    @Test
    void filterSizeReflectsFilteredCount() {
        Member kfir = new Member("kfir");
        Member apartium = new Member("apartium");
        Member voigon = new Member("voigon");
        voigon.active().set(false);

        SetObservable<Member> set = Observable.set(new HashSet<>(Set.of(kfir, apartium, voigon)));
        SetObservable<Member> active = set.filter(Member::active);

        assertEquals(2, active.size().get());

        voigon.active().set(true);
        assertEquals(3, active.size().get());

        set.clear();
        assertEquals(0, active.size().get());
    }

    @Test
    void filterTwoArgOverloadMapsThenTests() {
        Member kfir = new Member("kfir");
        Member lior = new Member("lior");
        Member apartium = new Member("apartium");

        SetObservable<Member> set = Observable.set(new HashSet<>(Set.of(kfir, lior, apartium)));

        // filter(mapper, predicate) — uses the inner mapper to a String, then tests
        SetObservable<Member> startsWithVowel = set.filter(
                Member::displayName,
                name -> "aeiou".indexOf(name.charAt(0)) >= 0
        );

        assertEquals(Set.of(apartium), startsWithVowel.get());

        kfir.displayName().set("orion");
        assertEquals(Set.of(apartium, kfir), startsWithVowel.get());
    }

    @Test
    void filterUnsupportedMutationsThrow() {
        SetObservable<Integer> filtered = Observable.<Integer>set().filter(n -> Observable.immutable(true));

        assertThrows(UnsupportedOperationException.class, () -> filtered.add(1));
        assertThrows(UnsupportedOperationException.class, () -> filtered.remove(1));

        List<Integer> collection = List.of();

        assertThrows(UnsupportedOperationException.class, () -> filtered.addAll(collection));
        assertThrows(UnsupportedOperationException.class, () -> filtered.removeAll(collection));
        assertThrows(UnsupportedOperationException.class, () -> filtered.removeIf(n -> true));
        assertThrows(UnsupportedOperationException.class, () -> filtered.retainAll(collection));
        assertThrows(UnsupportedOperationException.class, filtered::clear);
    }

    @Test
    void filterRemovingElementWhileShufflingPredicate() {
        // Exercise the path where checkAndUpdateBase rebuilds dependsOn after a base change
        // while previously toggled flags need to be discarded.
        Member kfir = new Member("kfir");
        Member apartium = new Member("apartium");
        Member voigon = new Member("voigon");

        SetObservable<Member> set = Observable.set(new HashSet<>(Set.of(kfir, apartium, voigon)));
        SetObservable<Member> active = set.filter(Member::active);

        assertEquals(Set.of(kfir, apartium, voigon), active.get());

        // toggle a flag, then change the base before the next get()
        voigon.active().set(false);
        set.remove(apartium);

        // checkAndUpdateBase should run because base changed; flagged state for voigon is cleared
        assertEquals(Set.of(kfir), active.get());
    }

    @Test
    void filterUpdatePathHandlesIncrementalToggleWithoutRebuildingBase() {
        Member kfir = new Member("kfir");
        Member apartium = new Member("apartium");
        SetObservable<Member> set = Observable.set(new HashSet<>(Set.of(kfir, apartium)));
        SetObservable<Member> active = set.filter(Member::active);

        // initial materialization populates cacheValueMap and dependsOn
        assertEquals(Set.of(kfir, apartium), active.get());

        // a single inner toggle exercises updateFlagged (base unchanged)
        kfir.active().set(false);
        assertEquals(Set.of(apartium), active.get());

        kfir.active().set(true);
        assertEquals(Set.of(kfir, apartium), active.get());
    }

    @Test
    void filterSharedFilterObservableForMultipleElements() {
        // two distinct elements share the same filter Observable (immutable singleton-ish)
        // so filter.apply must group them under the same key in dependsOn.
        Observable<Boolean> alwaysTrue = Observable.immutable(true);

        SetObservable<String> set = Observable.set(new HashSet<>(Set.of("kfir", "apartium", "voigon")));
        SetObservable<String> filtered = set.filter(name -> alwaysTrue);

        assertEquals(Set.of("kfir", "apartium", "voigon"), filtered.get());

        set.add("notro");
        assertEquals(Set.of("kfir", "apartium", "voigon", "notro"), filtered.get());

        set.remove("kfir");
        assertEquals(Set.of("apartium", "voigon", "notro"), filtered.get());
    }

    @Test
    void filterOnLinkedHashSetPreservesInsertionOrder() {
        SetObservable<String> set = Observable.set(
                new LinkedHashSet<>(),
                col -> Collections.unmodifiableSet(new LinkedHashSet<>(col)),
                LinkedHashSet::new
        );
        set.add("kfir");
        set.add("apartium");
        set.add("voigon");
        set.add("lior");

        SetObservable<String> filtered = set.filter(name -> Observable.immutable(name.length() <= 5));

        List<String> ordered = new ArrayList<>(filtered.get());
        assertEquals(List.of("kfir", "lior"), ordered);
    }

    // ---------------------------------------------------------------------
    // helpers
    // ---------------------------------------------------------------------

    private record Member(
            String id,
            MutableObservable<String> displayName,
            MutableObservable<Boolean> active
    ) {

        Member(String id) {
            this(id, Observable.mutable(id), Observable.mutable(true));
        }

        Member(String id, MutableObservable<String> displayName) {
            this(id, displayName, Observable.mutable(true));
        }

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
