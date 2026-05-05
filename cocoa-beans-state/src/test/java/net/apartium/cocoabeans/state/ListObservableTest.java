package net.apartium.cocoabeans.state;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class ListObservableTest {

    @Test
    void size() {
        ListObservable<Integer> scores = Observable.list();
        Observable<Integer> size = scores.size();

        assertEquals(0, size.get());
        assertEquals(List.of(), scores.get());

        scores.add(901);
        assertEquals(List.of(901), scores.get());
        assertEquals(1, size.get());

        scores.remove((Integer) 102);

        assertEquals(List.of(901), scores.get());
        assertEquals(1, size.get());
    }

    @Test
    void removeObserver() {
        ListObservable<Integer> scores = Observable.list();
        Observer observer = n -> {};
        scores.observe(observer);
        scores.add(100);
        assertTrue(scores.removeObserver(observer));
    }

    // ---------------------------------------------------------------------
    // mapEach edge cases
    // ---------------------------------------------------------------------

    @Test
    void mapEachOnEmptyListReturnsEmpty() {
        ListObservable<String> list = Observable.list();
        ListObservable<Integer> lengths = list.mapEach(String::length);

        assertEquals(List.of(), lengths.get());
        assertEquals(0, lengths.size().get());
    }

    @Test
    void mapEachPreservesOrderAndDuplicates() {
        ListObservable<String> list = Observable.list(new ArrayList<>(List.of(
                "kfir", "apartium", "voigon", "kfir", "lior"
        )));

        ListObservable<Integer> lengths = list.mapEach(String::length);

        // 4, 8, 6, 4, 4 — list keeps duplicates AND insertion order
        assertEquals(List.of(4, 8, 6, 4, 4), lengths.get());
        assertEquals(5, lengths.size().get());
    }

    @Test
    void mapEachIdentityFunction() {
        ListObservable<String> list = Observable.list(new ArrayList<>(List.of("kfir", "apartium")));
        ListObservable<String> identity = list.mapEach(Function.identity());

        assertEquals(List.of("kfir", "apartium"), identity.get());
    }

    @Test
    void mapEachReflectsAddRemoveAndAddAtIndex() {
        ListObservable<String> list = Observable.list();
        list.add("kfir");
        list.add("apartium");

        ListObservable<Integer> lengths = list.mapEach(String::length);
        assertEquals(List.of(4, 8), lengths.get());

        // add(int, E) on the source must propagate
        list.add(1, "voigon");
        assertEquals(List.of("kfir", "voigon", "apartium"), list.get());
        assertEquals(List.of(4, 6, 8), lengths.get());

        // remove by value on source propagates
        assertTrue(list.remove("voigon"));
        assertEquals(List.of(4, 8), lengths.get());

        // remove(int) on source propagates
        list.remove(0);
        assertEquals(List.of(8), lengths.get());
    }

    @Test
    void mapEachReflectsClear() {
        ListObservable<String> list = Observable.list(new ArrayList<>(List.of("kfir", "apartium")));
        ListObservable<Integer> lengths = list.mapEach(String::length);
        assertEquals(List.of(4, 8), lengths.get());

        list.clear();
        assertEquals(List.of(), lengths.get());

        // clearing an already-empty list should not produce a notification
        CountingObserver downstream = new CountingObserver();
        lengths.observe(downstream);
        list.clear();
        assertEquals(0, downstream.count, "clearing an already-empty list must not notify");
    }

    @Test
    void mapEachReflectsSort() {
        ListObservable<Integer> list = Observable.list(new ArrayList<>(List.of(3, 1, 4, 1, 5, 9)));
        ListObservable<Integer> doubled = list.mapEach(n -> n * 2);

        assertEquals(List.of(6, 2, 8, 2, 10, 18), doubled.get());

        list.sort(Comparator.naturalOrder());
        assertEquals(List.of(1, 1, 3, 4, 5, 9), list.get());
        assertEquals(List.of(2, 2, 6, 8, 10, 18), doubled.get());
    }

    @Test
    void mapEachCachesResultUntilBaseChanges() {
        ListObservable<String> list = Observable.list(new ArrayList<>(List.of("kfir", "lior")));

        AtomicInteger calls = new AtomicInteger();
        ListObservable<Integer> lengths = list.mapEach(s -> {
            calls.incrementAndGet();
            return s.length();
        });

        assertEquals(List.of(4, 4), lengths.get());
        assertEquals(List.of(4, 4), lengths.get());
        assertEquals(List.of(4, 4), lengths.get());
        assertEquals(2, calls.get(), "mapper should run once per element on the first get only");

        list.add("voigon");
        assertEquals(List.of(4, 4, 6), lengths.get());
        assertEquals(5, calls.get());
    }

    @Test
    void mapEachIgnoresFlagAsDirtyFromUnrelatedObservable() {
        ListObservable<String> list = Observable.list(new ArrayList<>(List.of("kfir")));

        AtomicInteger calls = new AtomicInteger();
        ListObservable<Integer> lengths = list.mapEach(name -> {
            calls.incrementAndGet();
            return name.length();
        });

        assertEquals(List.of(4), lengths.get());
        assertEquals(1, calls.get());

        CountingObserver downstream = new CountingObserver();
        lengths.observe(downstream);

        Observer asObserver = (Observer) lengths;
        MutableObservable<String> unrelated = Observable.mutable("notro");
        asObserver.flagAsDirty(unrelated);

        assertEquals(0, downstream.count, "unrelated flagAsDirty must not propagate notification");
        assertEquals(List.of(4), lengths.get());
        assertEquals(1, calls.get(), "unrelated flagAsDirty must not invalidate the cache");
    }

    @Test
    void mapEachNotifiesObserversOnBaseChange() {
        ListObservable<String> list = Observable.list();
        list.add("kfir");

        ListObservable<Integer> lengths = list.mapEach(String::length);
        lengths.get();

        CountingObserver downstream = new CountingObserver();
        lengths.observe(downstream);

        list.add("apartium");
        assertEquals(1, downstream.count);
        assertSame(lengths, downstream.last);

        list.remove("apartium");
        assertEquals(2, downstream.count);

        assertTrue(lengths.removeObserver(downstream));
        assertFalse(lengths.removeObserver(downstream));
    }

    @Test
    void mapEachChainedMapEach() {
        ListObservable<String> list = Observable.list(new ArrayList<>(List.of("kfir", "apartium", "voigon")));

        ListObservable<Integer> doubledLengths = list
                .mapEach(String::length)
                .mapEach(n -> n * 2);

        assertEquals(List.of(8, 16, 12), doubledLengths.get());
    }

    @Test
    void mapEachThenFilterReturnsListObservable() {
        ListObservable<String> list = Observable.list(new ArrayList<>(List.of(
                "kfir", "apartium", "voigon", "lior"
        )));

        ListObservable<Integer> longLengths = list
                .mapEach(String::length)
                .filter(n -> Observable.immutable(n >= 6));

        assertEquals(List.of(8, 6), longLengths.get());
    }

    @Test
    void mapEachThenFlatMapEach() {
        MutableObservable<String> kfirSuffix = Observable.mutable("a");
        MutableObservable<String> liorSuffix = Observable.mutable("b");

        ListObservable<MutableObservable<String>> list = Observable.list(new ArrayList<>(List.of(
                kfirSuffix, liorSuffix
        )));

        ListObservable<String> values = list
                .mapEach(Function.identity())
                .flatMapEach(o -> o);

        assertEquals(List.of("a", "b"), values.get());

        kfirSuffix.set("c");
        assertEquals(List.of("c", "b"), values.get());
    }

    @Test
    void mapEachUnsupportedListMutationsThrow() {
        ListObservable<Integer> mapped = Observable.<Integer>list().mapEach(n -> n);

        // inherited from MapElementObservable
        assertThrows(UnsupportedOperationException.class, () -> mapped.add(1));
        assertThrows(UnsupportedOperationException.class, () -> mapped.remove((Integer) 1));

        List<Integer> collection = List.of();

        assertThrows(UnsupportedOperationException.class, () -> mapped.addAll(collection));
        assertThrows(UnsupportedOperationException.class, () -> mapped.removeAll(collection));
        assertThrows(UnsupportedOperationException.class, () -> mapped.removeIf(n -> true));
        assertThrows(UnsupportedOperationException.class, () -> mapped.retainAll(collection));
        assertThrows(UnsupportedOperationException.class, mapped::clear);

        // list-specific overrides
        assertThrows(UnsupportedOperationException.class, () -> mapped.add(0, 1));
        assertThrows(UnsupportedOperationException.class, () -> mapped.remove(0));

        Comparator<Integer> comparator = Comparator.naturalOrder();

        assertThrows(UnsupportedOperationException.class, () -> mapped.sort(comparator));
    }

    @Test
    void mapEachWithNullTolerantListAllowsNullMapper() {
        // The default ListObservable's get() uses List.copyOf which rejects nulls, so we cannot
        // verify the inverse path with the default implementation. We at least verify NPE is raised.
        ListObservable<String> list = Observable.list();
        list.add("kfir");

        ListObservable<String> nulls = list.mapEach(name -> null);
        assertThrows(NullPointerException.class, nulls::get);
    }

    // ---------------------------------------------------------------------
    // flatMapEach edge cases
    // ---------------------------------------------------------------------

    @Test
    void flatMapEachOnEmptyListReturnsEmpty() {
        ListObservable<Member> list = Observable.list();
        ListObservable<String> names = list.flatMapEach(Member::displayName);

        assertEquals(List.of(), names.get());
        assertEquals(0, names.size().get());
    }

    @Test
    void flatMapEachReflectsInnerObservableChange() {
        Member kfir = new Member("kfir");
        ListObservable<Member> list = Observable.list(new ArrayList<>(List.of(kfir)));

        ListObservable<String> names = list.flatMapEach(Member::displayName);
        assertEquals(List.of("kfir"), names.get());

        kfir.displayName().set("Kfir Notro");
        assertEquals(List.of("Kfir Notro"), names.get());
    }

    @Test
    void flatMapEachPreservesDuplicateOccurrencesInList() {
        Member kfir = new Member("kfir");
        Member apartium = new Member("apartium");

        // The same element instance appearing multiple times in the source list must
        // produce one mapped entry per occurrence. Subscription bookkeeping is still
        // deduped — the inner observable is observed once per unique element.
        ListObservable<Member> list = Observable.list(new ArrayList<>(List.of(kfir, apartium, kfir)));

        ListObservable<String> names = list.flatMapEach(Member::displayName);
        assertEquals(List.of("kfir", "apartium", "kfir"), names.get());
        assertEquals(3, names.size().get());

        // a single inner change reflects in every occurrence of the element
        kfir.displayName().set("Kfir Notro");
        assertEquals(List.of("Kfir Notro", "apartium", "Kfir Notro"), names.get());
    }

    @Test
    void flatMapEachPreservesEqualButDistinctOccurrencesInList() {
        // Two records that compare equal (same components) still produce one
        // mapped entry per occurrence in the source list.
        MutableObservable<String> sharedName = Observable.mutable("kfir");
        MutableObservable<Boolean> sharedActive = Observable.mutable(true);
        Member kfir1 = new Member("kfir", sharedName, sharedActive);
        Member kfir2 = new Member("kfir", sharedName, sharedActive);

        assertEquals(kfir1, kfir2, "record equality holds when all components are equal");

        ListObservable<Member> list = Observable.list(new ArrayList<>(List.of(kfir1, kfir2)));
        ListObservable<String> names = list.flatMapEach(Member::displayName);

        assertEquals(List.of("kfir", "kfir"), names.get());

        sharedName.set("Kfir Notro");
        assertEquals(List.of("Kfir Notro", "Kfir Notro"), names.get());
    }

    @Test
    void flatMapEachEqualButDistinctElementsEachReceiveOwnMapperInvocation() {
        // Two value-equal source elements that are distinct references must each
        // get their own mapper invocation. With identity-keyed bookkeeping, the
        // mapper can return a different observable per occurrence and both are honored.
        record Token(String id) {}

        Token first = new Token("kfir");
        Token second = new Token("kfir");

        assertEquals(first, second, "records compare equal by component values");
        assertNotSame(first, second);

        Map<Token, MutableObservable<String>> backing = new IdentityHashMap<>();
        backing.put(first, Observable.mutable("first"));
        backing.put(second, Observable.mutable("second"));

        ListObservable<Token> list = Observable.list(new ArrayList<>(List.of(first, second)));
        ListObservable<String> names = (ListObservable<String>) list.flatMapEach(backing::get);

        assertEquals(List.of("first", "second"), names.get());

        backing.get(first).set("first-updated");
        assertEquals(List.of("first-updated", "second"), names.get(),
                "updating one element's inner observable must not affect the other");

        backing.get(second).set("second-updated");
        assertEquals(List.of("first-updated", "second-updated"), names.get());
    }

    @Test
    void flatMapEachReflectsAddAndRemove() {
        Member kfir = new Member("kfir");
        Member apartium = new Member("apartium");
        ListObservable<Member> list = Observable.list(new ArrayList<>(List.of(kfir)));

        ListObservable<String> names = list.flatMapEach(Member::displayName);
        assertEquals(List.of("kfir"), names.get());

        list.add(apartium);
        assertEquals(List.of("kfir", "apartium"), names.get());

        list.remove(kfir);
        assertEquals(List.of("apartium"), names.get());
    }

    @Test
    void flatMapEachInnerChangeAfterRemoveIsIgnored() {
        Member kfir = new Member("kfir");
        Member lior = new Member("lior");
        ListObservable<Member> list = Observable.list(new ArrayList<>(List.of(kfir, lior)));

        ListObservable<String> names = list.flatMapEach(Member::displayName);
        assertEquals(List.of("kfir", "lior"), names.get());

        list.remove(kfir);
        assertEquals(List.of("lior"), names.get());

        kfir.displayName().set("ghost");
        assertEquals(List.of("lior"), names.get(), "inner change for a removed element must not propagate");
    }

    @Test
    void flatMapEachReflectsClear() {
        Member kfir = new Member("kfir");
        ListObservable<Member> list = Observable.list(new ArrayList<>(List.of(kfir)));

        ListObservable<String> names = list.flatMapEach(Member::displayName);
        assertEquals(List.of("kfir"), names.get());

        list.clear();
        assertEquals(List.of(), names.get());

        kfir.displayName().set("ghost");
        assertEquals(List.of(), names.get());
    }

    @Test
    void flatMapEachWithSharedInnerObservableTracksAllElements() {
        MutableObservable<String> shared = Observable.mutable("apartium");

        Member kfir = new Member("kfir", shared);
        Member lior = new Member("lior", shared);

        // list keeps duplicates; the same shared observable is referenced by two distinct elements
        ListObservable<Member> list = Observable.list(new ArrayList<>(List.of(kfir, lior)));
        ListObservable<String> names = list.flatMapEach(Member::displayName);

        // both members share the same inner -> two entries with the same value
        assertEquals(List.of("apartium", "apartium"), names.get());

        shared.set("voigon");
        assertEquals(List.of("voigon", "voigon"), names.get());
    }

    @Test
    void flatMapEachWithSharedInnerKeepsObservingWhenOnlyOneElementRemoved() {
        MutableObservable<String> shared = Observable.mutable("apartium");

        Member kfir = new Member("kfir", shared);
        Member lior = new Member("lior", shared);

        ListObservable<Member> list = Observable.list(new ArrayList<>(List.of(kfir, lior)));
        ListObservable<String> names = list.flatMapEach(Member::displayName);
        assertEquals(List.of("apartium", "apartium"), names.get());

        list.remove(kfir);
        assertEquals(List.of("apartium"), names.get());

        shared.set("voigon");
        assertEquals(List.of("voigon"), names.get(), "shared inner must still update while any element references it");
    }

    @Test
    void flatMapEachIsCachedUntilBaseOrInnerChanges() {
        Member kfir = new Member("kfir");
        ListObservable<Member> list = Observable.list(new ArrayList<>(List.of(kfir)));

        AtomicInteger mapperCalls = new AtomicInteger();
        ListObservable<String> names = list.flatMapEach(member -> {
            mapperCalls.incrementAndGet();
            return member.displayName();
        });

        assertEquals(List.of("kfir"), names.get());
        assertEquals(List.of("kfir"), names.get());
        assertEquals(1, mapperCalls.get());

        kfir.displayName().set("Kfir Notro");
        assertEquals(List.of("Kfir Notro"), names.get());
        assertEquals(1, mapperCalls.get(), "inner change must not re-invoke the element mapper");

        list.add(new Member("voigon"));
        assertEquals(List.of("Kfir Notro", "voigon"), names.get());
        assertEquals(2, mapperCalls.get());
    }

    @Test
    void flatMapEachIgnoresFlagAsDirtyFromUnrelatedObservable() {
        Member kfir = new Member("kfir");
        ListObservable<Member> list = Observable.list(new ArrayList<>(List.of(kfir)));

        ListObservable<String> names = list.flatMapEach(Member::displayName);
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
        ListObservable<Member> list = Observable.list(new ArrayList<>(List.of(kfir)));
        ListObservable<String> names = list.flatMapEach(Member::displayName);
        names.get();

        CountingObserver downstream = new CountingObserver();
        names.observe(downstream);

        kfir.displayName().set("Kfir Notro");
        assertEquals(1, downstream.count);
        assertSame(names, downstream.last);
        assertEquals(List.of("Kfir Notro"), names.get());

        assertTrue(names.removeObserver(downstream));
        kfir.displayName().set("Kfir2");
        assertEquals(1, downstream.count, "removed observer must not be notified");
    }

    @Test
    void flatMapEachThenMapEach() {
        Member kfir = new Member("kfir");
        Member apartium = new Member("apartium");
        ListObservable<Member> list = Observable.list(new ArrayList<>(List.of(kfir, apartium)));

        ListObservable<Integer> nameLengths = list
                .flatMapEach(Member::displayName)
                .mapEach(String::length);

        assertEquals(List.of(4, 8), nameLengths.get());

        kfir.displayName().set("Kfir Notro");
        assertEquals(List.of(10, 8), nameLengths.get());
    }

    @Test
    void flatMapEachThenFlatMapEach() {
        MutableObservable<MutableObservable<String>> nested =
                Observable.mutable(Observable.mutable("kfir"));

        ListObservable<MutableObservable<MutableObservable<String>>> list =
                Observable.list(new ArrayList<>(List.of(nested)));

        ListObservable<String> values = list
                .flatMapEach(o -> o)
                .flatMapEach(o -> o);

        assertEquals(List.of("kfir"), values.get());

        nested.get().set("Kfir Notro");
        assertEquals(List.of("Kfir Notro"), values.get());

        nested.set(Observable.mutable("apartium"));
        assertEquals(List.of("apartium"), values.get());
    }

    @Test
    void flatMapEachThenFilter() {
        Member kfir = new Member("kfir");
        Member apartium = new Member("apartium");
        ListObservable<Member> list = Observable.list(new ArrayList<>(List.of(kfir, apartium)));

        ListObservable<String> startsWithA = list
                .flatMapEach(Member::displayName)
                .filter(name -> Observable.immutable(name.startsWith("a")));

        assertEquals(List.of("apartium"), startsWithA.get());

        kfir.displayName().set("aaaa-kfir");
        assertEquals(List.of("aaaa-kfir", "apartium"), startsWithA.get());
    }

    @Test
    void flatMapEachUnsupportedListMutationsThrow() {
        ListObservable<Member> list = Observable.list();
        ListObservable<String> mapped = list.flatMapEach(Member::displayName);

        // inherited from FlatMapElementObservable
        assertThrows(UnsupportedOperationException.class, () -> mapped.add("x"));
        assertThrows(UnsupportedOperationException.class, () -> mapped.remove("x"));

        List<String> collection = List.of();

        assertThrows(UnsupportedOperationException.class, () -> mapped.addAll(collection));
        assertThrows(UnsupportedOperationException.class, () -> mapped.removeAll(collection));
        assertThrows(UnsupportedOperationException.class, () -> mapped.removeIf(n -> true));
        assertThrows(UnsupportedOperationException.class, () -> mapped.retainAll(collection));
        assertThrows(UnsupportedOperationException.class, mapped::clear);

        // list-specific overrides
        assertThrows(UnsupportedOperationException.class, () -> mapped.add(0, "x"));
        assertThrows(UnsupportedOperationException.class, () -> mapped.remove(0));

        Comparator<String> comparator = Comparator.naturalOrder();

        assertThrows(UnsupportedOperationException.class, () -> mapped.sort(comparator));
    }

    @Test
    void flatMapEachReAddingElementAfterClearWorks() {
        Member kfir = new Member("kfir");
        ListObservable<Member> list = Observable.list(new ArrayList<>(List.of(kfir)));

        ListObservable<String> names = list.flatMapEach(Member::displayName);
        assertEquals(List.of("kfir"), names.get());

        list.clear();
        assertEquals(List.of(), names.get());

        list.add(kfir);
        assertEquals(List.of("kfir"), names.get());

        kfir.displayName().set("Kfir Notro");
        assertEquals(List.of("Kfir Notro"), names.get());
    }

    @Test
    void flatMapEachWithNullMapperResultThrowsBecauseListCopyOfRejectsNull() {
        Member kfir = new Member("kfir");
        ListObservable<Member> list = Observable.list(new ArrayList<>(List.of(kfir)));

        // The default ListObservableImpl uses List.copyOf, which does not allow nulls. The
        // FlatMapElementObservable adds null to the staging collection when the inner is null,
        // so this exercises the `inner == null` branch and the fact that the standard list
        // chain rejects nulls at copyOf time.
        ListObservable<String> names = list.flatMapEach(member -> null);
        assertThrows(NullPointerException.class, names::get);
    }

    // ---------------------------------------------------------------------
    // filter edge cases
    // ---------------------------------------------------------------------

    @Test
    void filterOnEmptyList() {
        ListObservable<String> list = Observable.list();
        ListObservable<String> filtered = list.filter(s -> Observable.immutable(true));

        assertEquals(List.of(), filtered.get());
        assertEquals(0, filtered.size().get());
    }

    @Test
    void filterAllTrueReturnsEverythingInOrder() {
        ListObservable<String> list = Observable.list(new ArrayList<>(List.of("kfir", "apartium", "voigon")));
        ListObservable<String> filtered = list.filter(s -> Observable.immutable(true));

        assertEquals(List.of("kfir", "apartium", "voigon"), filtered.get());
    }

    @Test
    void filterAllFalseReturnsEmpty() {
        ListObservable<String> list = Observable.list(new ArrayList<>(List.of("kfir", "apartium")));
        ListObservable<String> filtered = list.filter(s -> Observable.immutable(false));

        assertEquals(List.of(), filtered.get());
    }

    @Test
    void filterPreservesOrderAndDuplicates() {
        ListObservable<Integer> list = Observable.list(new ArrayList<>(List.of(1, 2, 3, 4, 5, 6, 4, 2)));
        ListObservable<Integer> evens = list.filter(n -> Observable.immutable(n % 2 == 0));

        assertEquals(List.of(2, 4, 6, 4, 2), evens.get());
    }

    @Test
    void filterTogglingPredicateAddsAndRemovesElement() {
        Member kfir = new Member("kfir");
        Member lior = new Member("lior");
        lior.active().set(false);

        ListObservable<Member> list = Observable.list(new ArrayList<>(List.of(kfir, lior)));
        ListObservable<Member> active = list.filter(Member::active);

        assertEquals(List.of(kfir), active.get());

        lior.active().set(true);
        assertEquals(List.of(kfir, lior), active.get());

        kfir.active().set(false);
        assertEquals(List.of(lior), active.get());
    }

    @Test
    void filterAddingElementWithTruePredicateIncludesIt() {
        ListObservable<Member> list = Observable.list();
        ListObservable<Member> active = list.filter(Member::active);

        assertEquals(List.of(), active.get());

        Member kfir = new Member("kfir");
        list.add(kfir);
        assertEquals(List.of(kfir), active.get());
    }

    @Test
    void filterAddingElementWithFalsePredicateExcludesIt() {
        ListObservable<Member> list = Observable.list();
        ListObservable<Member> active = list.filter(Member::active);

        Member kfir = new Member("kfir");
        kfir.active().set(false);
        list.add(kfir);
        assertEquals(List.of(), active.get());

        kfir.active().set(true);
        assertEquals(List.of(kfir), active.get());
    }

    @Test
    void filterRemovingElementRemovesItFromResult() {
        Member kfir = new Member("kfir");
        Member apartium = new Member("apartium");
        ListObservable<Member> list = Observable.list(new ArrayList<>(List.of(kfir, apartium)));

        ListObservable<Member> active = list.filter(Member::active);
        assertEquals(List.of(kfir, apartium), active.get());

        list.remove(kfir);
        assertEquals(List.of(apartium), active.get());
    }

    @Test
    void filterChainedFilter() {
        ListObservable<Integer> list = Observable.list(new ArrayList<>(List.of(1, 2, 3, 4, 5, 6, 7, 8)));

        ListObservable<Integer> evenAndLarge = list
                .filter(n -> Observable.immutable(n % 2 == 0))
                .filter(n -> Observable.immutable(n >= 4));

        assertEquals(List.of(4, 6, 8), evenAndLarge.get());
    }

    @Test
    void filterThenMapEach() {
        Member kfir = new Member("kfir");
        Member apartium = new Member("apartium");
        ListObservable<Member> list = Observable.list(new ArrayList<>(List.of(kfir, apartium)));

        ListObservable<String> activeIds = list
                .filter(Member::active)
                .mapEach(Member::id);

        assertEquals(List.of("kfir", "apartium"), activeIds.get());
    }

    @Test
    void filterThenFlatMapEach() {
        Member kfir = new Member("kfir");
        Member apartium = new Member("apartium");
        apartium.active().set(false);

        ListObservable<Member> list = Observable.list(new ArrayList<>(List.of(kfir, apartium)));

        ListObservable<String> activeNames = list
                .filter(Member::active)
                .flatMapEach(Member::displayName);

        assertEquals(List.of("kfir"), activeNames.get());

        kfir.displayName().set("Kfir Notro");
        assertEquals(List.of("Kfir Notro"), activeNames.get());

        apartium.active().set(true);
        assertEquals(List.of("Kfir Notro", "apartium"), activeNames.get());
    }

    @Test
    void filterCachedWhenNoNetChange() {
        Member kfir = new Member("kfir");
        Member apartium = new Member("apartium");
        ListObservable<Member> list = Observable.list(new ArrayList<>(List.of(kfir, apartium)));

        ListObservable<Member> active = list.filter(Member::active);
        List<Member> initial = active.get();
        assertEquals(List.of(kfir, apartium), initial);

        kfir.active().set(false);
        kfir.active().set(true);
        List<Member> after = active.get();
        assertEquals(initial, after, "net-zero toggle must produce the same membership");

        assertSame(after, active.get(), "second get() with no further changes must return the same cached snapshot");
    }

    @Test
    void filterIgnoresFlagAsDirtyFromUnrelatedObservable() {
        Member kfir = new Member("kfir");
        ListObservable<Member> list = Observable.list(new ArrayList<>(List.of(kfir)));
        ListObservable<Member> active = list.filter(Member::active);
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

        ListObservable<Member> list = Observable.list(new ArrayList<>(List.of(kfir, apartium, voigon)));
        ListObservable<Member> active = list.filter(Member::active);

        assertEquals(2, active.size().get());

        voigon.active().set(true);
        assertEquals(3, active.size().get());

        list.clear();
        assertEquals(0, active.size().get());
    }

    @Test
    void filterTwoArgOverloadMapsThenTests() {
        Member kfir = new Member("kfir");
        Member lior = new Member("lior");
        Member apartium = new Member("apartium");

        ListObservable<Member> list = Observable.list(new ArrayList<>(List.of(kfir, lior, apartium)));

        ListObservable<Member> startsWithVowel = list.filter(
                Member::displayName,
                name -> "aeiou".indexOf(name.charAt(0)) >= 0
        );

        assertEquals(List.of(apartium), startsWithVowel.get());

        kfir.displayName().set("orion");
        assertEquals(List.of(kfir, apartium), startsWithVowel.get());
    }

    @Test
    void filterUnsupportedListMutationsThrow() {
        ListObservable<Integer> filtered = Observable.<Integer>list().filter(n -> Observable.immutable(true));

        // inherited from FilterObservable
        assertThrows(UnsupportedOperationException.class, () -> filtered.add(1));
        assertThrows(UnsupportedOperationException.class, () -> filtered.remove((Integer) 1));

        List<Integer> collection = List.of();

        assertThrows(UnsupportedOperationException.class, () -> filtered.addAll(collection));
        assertThrows(UnsupportedOperationException.class, () -> filtered.removeAll(collection));
        assertThrows(UnsupportedOperationException.class, () -> filtered.removeIf(n -> true));
        assertThrows(UnsupportedOperationException.class, () -> filtered.retainAll(collection));
        assertThrows(UnsupportedOperationException.class, filtered::clear);

        // list-specific overrides on ListFilterObservable
        assertThrows(UnsupportedOperationException.class, () -> filtered.add(0, 1));
        assertThrows(UnsupportedOperationException.class, () -> filtered.remove(0));

        Comparator<Integer> comparator = Comparator.naturalOrder();

        assertThrows(UnsupportedOperationException.class, () -> filtered.sort(comparator));
    }

    @Test
    void filterRemovingElementWhileShufflingPredicate() {
        Member kfir = new Member("kfir");
        Member apartium = new Member("apartium");
        Member voigon = new Member("voigon");

        ListObservable<Member> list = Observable.list(new ArrayList<>(List.of(kfir, apartium, voigon)));
        ListObservable<Member> active = list.filter(Member::active);

        assertEquals(List.of(kfir, apartium, voigon), active.get());

        // toggle a flag, then change the base before the next get()
        voigon.active().set(false);
        list.remove(apartium);

        // checkAndUpdateBase should run because base changed; flagged state must be reconciled
        assertEquals(List.of(kfir), active.get());
    }

    @Test
    void filterUpdatePathHandlesIncrementalToggleWithoutRebuildingBase() {
        Member kfir = new Member("kfir");
        Member apartium = new Member("apartium");
        ListObservable<Member> list = Observable.list(new ArrayList<>(List.of(kfir, apartium)));
        ListObservable<Member> active = list.filter(Member::active);

        assertEquals(List.of(kfir, apartium), active.get());

        kfir.active().set(false);
        assertEquals(List.of(apartium), active.get());

        kfir.active().set(true);
        assertEquals(List.of(kfir, apartium), active.get());
    }

    @Test
    void filterSharedFilterObservableForMultipleElements() {
        Observable<Boolean> alwaysTrue = Observable.immutable(true);

        ListObservable<String> list = Observable.list(new ArrayList<>(List.of("kfir", "apartium", "voigon")));
        ListObservable<String> filtered = list.filter(name -> alwaysTrue);

        assertEquals(List.of("kfir", "apartium", "voigon"), filtered.get());

        list.add("notro");
        assertEquals(List.of("kfir", "apartium", "voigon", "notro"), filtered.get());

        list.remove("kfir");
        assertEquals(List.of("apartium", "voigon", "notro"), filtered.get());
    }

    @Test
    void filterAfterSortReflectsNewOrder() {
        ListObservable<Integer> list = Observable.list(new ArrayList<>(List.of(5, 1, 4, 2, 3)));
        ListObservable<Integer> evens = list.filter(n -> Observable.immutable(n % 2 == 0));

        assertEquals(List.of(4, 2), evens.get());

        list.sort(Comparator.naturalOrder());
        assertEquals(List.of(2, 4), evens.get());
    }

    @Test
    void filterPreservesBaseOrderWithSharedFilterObservable() {
        Member kfir = new Member("kfir");
        Member apartium = new Member("apartium");
        Member voigon = new Member("voigon");

        Observable<Boolean> alwaysTrue = Observable.immutable(true);

        ListObservable<Member> list = Observable.list(new ArrayList<>(List.of(kfir, apartium, voigon)));
        ListObservable<Member> filtered = list.filter(member -> alwaysTrue);

        assertEquals(List.of(kfir, apartium, voigon), filtered.get());
    }

    @Test
    void filterPreservesDuplicateOccurrencesInList() {
        Member kfir = new Member("kfir");
        ListObservable<Member> list = Observable.list(new ArrayList<>(List.of(kfir, kfir)));
        ListObservable<Member> active = list.filter(Member::active);

        assertEquals(List.of(kfir, kfir), active.get());

        kfir.active().set(false);
        assertEquals(List.of(), active.get());

        kfir.active().set(true);
        assertEquals(List.of(kfir, kfir), active.get());
    }

    @Test
    void filterPreservesInterleavedDuplicatesUnderSharedObservable() {
        Member kfir = new Member("kfir");
        Member apartium = new Member("apartium");

        ListObservable<Member> list = Observable.list(new ArrayList<>(List.of(kfir, apartium, kfir)));
        ListObservable<Member> active = list.filter(Member::active);

        assertEquals(List.of(kfir, apartium, kfir), active.get());

        apartium.active().set(false);
        assertEquals(List.of(kfir, kfir), active.get());

        apartium.active().set(true);
        assertEquals(List.of(kfir, apartium, kfir), active.get());

        kfir.active().set(false);
        assertEquals(List.of(apartium), active.get());
    }

    // ---------------------------------------------------------------------
    // duplicate-focused tests (mapEach, flatMapEach, filter)
    // ---------------------------------------------------------------------

    @Test
    void mapEachAllSameElementProducesAllSameMapped() {
        ListObservable<String> list = Observable.list(new ArrayList<>(List.of(
                "kfir", "kfir", "kfir", "kfir"
        )));

        ListObservable<Integer> lengths = list.mapEach(String::length);

        assertEquals(List.of(4, 4, 4, 4), lengths.get());
        assertEquals(4, lengths.size().get());
    }

    @Test
    void mapEachAddingMoreDuplicatesGrows() {
        ListObservable<String> list = Observable.list(new ArrayList<>(List.of("kfir", "kfir")));
        ListObservable<Integer> lengths = list.mapEach(String::length);

        assertEquals(List.of(4, 4), lengths.get());

        list.add("kfir");
        list.add("kfir");
        assertEquals(List.of(4, 4, 4, 4), lengths.get());
    }

    @Test
    void mapEachRemovingOnlyOneOccurrenceOfDuplicateShrinksByOne() {
        ListObservable<String> list = Observable.list(new ArrayList<>(List.of(
                "kfir", "apartium", "kfir", "voigon", "kfir"
        )));
        ListObservable<Integer> lengths = list.mapEach(String::length);

        assertEquals(List.of(4, 8, 4, 6, 4), lengths.get());

        list.remove("kfir");
        assertEquals(List.of(8, 4, 6, 4), lengths.get(), "List.remove(Object) drops only the first occurrence");
    }

    @Test
    void mapEachClearOfDuplicatesYieldsEmpty() {
        ListObservable<String> list = Observable.list(new ArrayList<>(List.of(
                "kfir", "kfir", "kfir"
        )));
        ListObservable<Integer> lengths = list.mapEach(String::length);

        assertEquals(List.of(4, 4, 4), lengths.get());

        list.clear();
        assertEquals(List.of(), lengths.get());
        assertEquals(0, lengths.size().get());
    }

    @Test
    void mapEachInvokesMapperPerOccurrenceForDuplicates() {
        ListObservable<String> list = Observable.list(new ArrayList<>(List.of("kfir", "kfir", "kfir")));

        AtomicInteger mapperCalls = new AtomicInteger();
        ListObservable<Integer> lengths = list.mapEach(name -> {
            mapperCalls.incrementAndGet();
            return name.length();
        });

        assertEquals(List.of(4, 4, 4), lengths.get());
        assertEquals(3, mapperCalls.get(), "mapEach invokes the mapper once per source occurrence");
    }

    @Test
    void flatMapEachAllSameRefAllSameValue() {
        Member kfir = new Member("kfir");

        ListObservable<Member> list = Observable.list(new ArrayList<>(List.of(kfir, kfir, kfir)));
        ListObservable<String> names = list.flatMapEach(Member::displayName);

        assertEquals(List.of("kfir", "kfir", "kfir"), names.get());

        kfir.displayName().set("Kfir Notro");
        assertEquals(List.of("Kfir Notro", "Kfir Notro", "Kfir Notro"), names.get(),
                "a single inner change reflects in every duplicate occurrence");
    }

    @Test
    void flatMapEachInnerChangePropagatesToEveryOccurrenceWithMixedNeighbors() {
        Member kfir = new Member("kfir");
        Member apartium = new Member("apartium");
        Member voigon = new Member("voigon");

        ListObservable<Member> list = Observable.list(new ArrayList<>(List.of(
                kfir, apartium, kfir, voigon, kfir
        )));
        ListObservable<String> names = list.flatMapEach(Member::displayName);

        assertEquals(List.of("kfir", "apartium", "kfir", "voigon", "kfir"), names.get());

        kfir.displayName().set("Kfir Notro");
        assertEquals(List.of("Kfir Notro", "apartium", "Kfir Notro", "voigon", "Kfir Notro"), names.get());
    }

    @Test
    void flatMapEachAddingMoreDuplicatesAfterMaterializationKeepsSubscriptionDeduped() {
        Member kfir = new Member("kfir");
        ListObservable<Member> list = Observable.list(new ArrayList<>(List.of(kfir)));

        AtomicInteger mapperCalls = new AtomicInteger();
        ListObservable<String> names = list.flatMapEach(member -> {
            mapperCalls.incrementAndGet();
            return member.displayName();
        });

        assertEquals(List.of("kfir"), names.get());
        assertEquals(1, mapperCalls.get());

        list.add(kfir);
        list.add(kfir);
        assertEquals(List.of("kfir", "kfir", "kfir"), names.get());
        assertEquals(1, mapperCalls.get(), "flatMapEach mapper is invoked once per unique element, not per occurrence");

        kfir.displayName().set("Kfir Notro");
        assertEquals(List.of("Kfir Notro", "Kfir Notro", "Kfir Notro"), names.get());
    }

    @Test
    void flatMapEachRemovingOneOfManyOccurrencesKeepsRest() {
        Member kfir = new Member("kfir");
        Member apartium = new Member("apartium");

        ListObservable<Member> list = Observable.list(new ArrayList<>(List.of(
                kfir, kfir, apartium, kfir
        )));
        ListObservable<String> names = list.flatMapEach(Member::displayName);

        assertEquals(List.of("kfir", "kfir", "apartium", "kfir"), names.get());

        list.remove(kfir);
        assertEquals(List.of("kfir", "apartium", "kfir"), names.get());

        kfir.displayName().set("Kfir Notro");
        assertEquals(List.of("Kfir Notro", "apartium", "Kfir Notro"), names.get(),
                "remaining occurrences must still be tracked after a single occurrence is removed");
    }

    @Test
    void flatMapEachRemovingAllOccurrencesAndReAddingResubscribes() {
        Member kfir = new Member("kfir");
        ListObservable<Member> list = Observable.list(new ArrayList<>(List.of(kfir, kfir, kfir)));
        ListObservable<String> names = list.flatMapEach(Member::displayName);

        assertEquals(List.of("kfir", "kfir", "kfir"), names.get());

        list.clear();
        assertEquals(List.of(), names.get());

        // change while not present — must not propagate (no observer)
        kfir.displayName().set("Kfir Notro");
        assertEquals(List.of(), names.get());

        list.add(kfir);
        list.add(kfir);
        assertEquals(List.of("Kfir Notro", "Kfir Notro"), names.get(),
                "re-adding picks up the current inner value");

        kfir.displayName().set("Kfir Apartium");
        assertEquals(List.of("Kfir Apartium", "Kfir Apartium"), names.get());
    }

    @Test
    void flatMapEachThenMapEachOverDuplicates() {
        Member kfir = new Member("kfir");
        Member apartium = new Member("apartium");

        ListObservable<Member> list = Observable.list(new ArrayList<>(List.of(kfir, apartium, kfir)));

        ListObservable<Integer> nameLengths = (ListObservable<Integer>) list
                .flatMapEach(Member::displayName)
                .mapEach(String::length);

        assertEquals(List.of(4, 8, 4), nameLengths.get());

        kfir.displayName().set("Kfir Notro");
        assertEquals(List.of(10, 8, 10), nameLengths.get());
    }

    @Test
    void filterAllDuplicatesPredicateFlipTogglesAllAtOnce() {
        Member kfir = new Member("kfir");
        ListObservable<Member> list = Observable.list(new ArrayList<>(List.of(kfir, kfir, kfir, kfir)));
        ListObservable<Member> active = list.filter(Member::active);

        assertEquals(List.of(kfir, kfir, kfir, kfir), active.get());
        assertEquals(4, active.size().get());

        kfir.active().set(false);
        assertEquals(List.of(), active.get());
        assertEquals(0, active.size().get());

        kfir.active().set(true);
        assertEquals(List.of(kfir, kfir, kfir, kfir), active.get());
    }

    @Test
    void filterRemovingOneOccurrenceOfDuplicateShrinksByOne() {
        Member kfir = new Member("kfir");
        Member apartium = new Member("apartium");

        ListObservable<Member> list = Observable.list(new ArrayList<>(List.of(kfir, apartium, kfir, kfir)));
        ListObservable<Member> active = list.filter(Member::active);

        assertEquals(List.of(kfir, apartium, kfir, kfir), active.get());

        list.remove(kfir);
        assertEquals(List.of(apartium, kfir, kfir), active.get());

        kfir.active().set(false);
        assertEquals(List.of(apartium), active.get(),
                "predicate flip after removing one occurrence still excludes every remaining occurrence");
    }

    @Test
    void filterThenMapEachOverDuplicates() {
        Member kfir = new Member("kfir");
        Member apartium = new Member("apartium");
        apartium.active().set(false);

        ListObservable<Member> list = Observable.list(new ArrayList<>(List.of(
                kfir, apartium, kfir, apartium, kfir
        )));

        ListObservable<String> ids = (ListObservable<String>) list
                .filter(Member::active)
                .mapEach(Member::id);

        assertEquals(List.of("kfir", "kfir", "kfir"), ids.get());

        apartium.active().set(true);
        assertEquals(List.of("kfir", "apartium", "kfir", "apartium", "kfir"), ids.get());
    }

    @Test
    void filterThenFlatMapEachOverDuplicates() {
        Member kfir = new Member("kfir");
        Member apartium = new Member("apartium");

        ListObservable<Member> list = Observable.list(new ArrayList<>(List.of(
                kfir, apartium, kfir, kfir
        )));

        ListObservable<String> activeNames = (ListObservable<String>) list
                .filter(Member::active)
                .flatMapEach(Member::displayName);

        assertEquals(List.of("kfir", "apartium", "kfir", "kfir"), activeNames.get());

        kfir.displayName().set("Kfir Notro");
        assertEquals(List.of("Kfir Notro", "apartium", "Kfir Notro", "Kfir Notro"), activeNames.get());

        kfir.active().set(false);
        assertEquals(List.of("apartium"), activeNames.get());

        kfir.active().set(true);
        assertEquals(List.of("Kfir Notro", "apartium", "Kfir Notro", "Kfir Notro"), activeNames.get());
    }

    @Test
    void mapEachThenFilterOverDuplicates() {
        ListObservable<String> list = Observable.list(new ArrayList<>(List.of(
                "kfir", "apartium", "kfir", "voigon", "kfir"
        )));

        // mapEach then filter (filter elements with length == 4)
        ListObservable<Integer> shortLengths = (ListObservable<Integer>) list
                .mapEach(String::length)
                .filter(n -> Observable.immutable(n == 4));

        assertEquals(List.of(4, 4, 4), shortLengths.get());
    }

    @Test
    void filterChainedFilterOverDuplicatesPreservesEachOccurrence() {
        ListObservable<Integer> list = Observable.list(new ArrayList<>(List.of(
                1, 2, 2, 3, 4, 4, 4, 5, 6, 6
        )));

        ListObservable<Integer> evenAndLarge = list
                .filter(n -> Observable.immutable(n % 2 == 0))
                .filter(n -> Observable.immutable(n >= 4));

        assertEquals(List.of(4, 4, 4, 6, 6), evenAndLarge.get());
    }

    @Test
    void filterSharedFilterObservableTogglesAllOccurrences() {
        MutableObservable<Boolean> shared = Observable.mutable(true);

        Member kfir = new Member("kfir", Observable.mutable("kfir"), shared);
        Member lior = new Member("lior", Observable.mutable("lior"), shared);

        ListObservable<Member> list = Observable.list(new ArrayList<>(List.of(
                kfir, lior, kfir, lior, kfir
        )));
        ListObservable<Member> active = list.filter(Member::active);

        assertEquals(List.of(kfir, lior, kfir, lior, kfir), active.get());

        shared.set(false);
        assertEquals(List.of(), active.get(),
                "single shared predicate flip excludes every occurrence");

        shared.set(true);
        assertEquals(List.of(kfir, lior, kfir, lior, kfir), active.get());
    }

    @Test
    void filterDeduplicatesSubscriptionForRepeatedElement() {
        Member kfir = new Member("kfir");

        AtomicInteger mapperCalls = new AtomicInteger();
        ListObservable<Member> list = Observable.list(new ArrayList<>(List.of(kfir, kfir, kfir, kfir)));

        ListObservable<Member> active = list.filter(member -> {
            mapperCalls.incrementAndGet();
            return member.active();
        });

        assertEquals(List.of(kfir, kfir, kfir, kfir), active.get());
        assertEquals(1, mapperCalls.get(),
                "filter mapper is invoked once per unique element, not per occurrence");
    }

    // ---------------------------------------------------------------------
    // counter-driven dup tests — verify no unnecessary work
    // ---------------------------------------------------------------------

    @Test
    void flatMapEachRefDupesInvokeMapperOnceAndSubscribeOnceOnInner() {
        Member kfir = new Member("kfir");
        CountingObservable<String> innerName = new CountingObservable<>(kfir.displayName());

        AtomicInteger mapperCalls = new AtomicInteger();
        ListObservable<Member> list = Observable.list(new ArrayList<>(List.of(kfir, kfir, kfir, kfir, kfir)));
        ListObservable<String> names = list.flatMapEach(member -> {
            mapperCalls.incrementAndGet();
            return innerName;
        });

        assertEquals(List.of("kfir", "kfir", "kfir", "kfir", "kfir"), names.get());
        assertEquals(1, mapperCalls.get(), "mapper invoked once per unique source ref");
        assertEquals(1, innerName.observes, "inner observable subscribed exactly once for all duplicates");
    }

    @Test
    void flatMapEachEqualButDistinctInvokesMapperPerOccurrence() {
        record Token(String id) {}

        Token a = new Token("kfir");
        Token b = new Token("kfir");
        Token c = new Token("kfir");

        assertEquals(a, b);
        assertEquals(a, c);

        AtomicInteger mapperCalls = new AtomicInteger();
        ListObservable<Token> list = Observable.list(new ArrayList<>(List.of(a, b, c)));
        ListObservable<String> names = (ListObservable<String>) list.flatMapEach(token -> {
            mapperCalls.incrementAndGet();
            return Observable.immutable(token.id());
        });

        assertEquals(List.of("kfir", "kfir", "kfir"), names.get());
        assertEquals(3, mapperCalls.get(),
                "identity-based bookkeeping invokes mapper once per distinct ref even when refs are .equals");
    }

    @Test
    void flatMapEachAddingMoreRefDupesDoesNotReSubscribeOrReInvokeMapper() {
        Member kfir = new Member("kfir");
        CountingObservable<String> innerName = new CountingObservable<>(kfir.displayName());

        AtomicInteger mapperCalls = new AtomicInteger();
        ListObservable<Member> list = Observable.list(new ArrayList<>(List.of(kfir)));
        ListObservable<String> names = list.flatMapEach(member -> {
            mapperCalls.incrementAndGet();
            return innerName;
        });

        names.get();
        int observesAfterInit = innerName.observes;
        assertEquals(1, mapperCalls.get());
        assertEquals(1, observesAfterInit);

        list.add(kfir);
        list.add(kfir);
        list.add(kfir);
        names.get();

        assertEquals(1, mapperCalls.get(), "mapper not re-invoked for additional duplicates of an existing ref");
        assertEquals(observesAfterInit, innerName.observes, "no new subscribe for additional duplicates");
    }

    @Test
    void flatMapEachStableGetReturnsCachedWithoutCallingInnerGet() {
        Member kfir = new Member("kfir");
        CountingObservable<String> innerName = new CountingObservable<>(kfir.displayName());

        ListObservable<Member> list = Observable.list(new ArrayList<>(List.of(kfir, kfir, kfir)));
        ListObservable<String> names = list.flatMapEach(member -> innerName);

        names.get();
        int innerGetsAfterInit = innerName.gets;

        // multiple stable gets — nothing changed, must hit cached collection
        List<String> a = names.get();
        List<String> b = names.get();
        List<String> c = names.get();

        assertSame(a, b);
        assertSame(b, c);
        assertEquals(innerGetsAfterInit, innerName.gets, "stable get() must not re-fetch inner observable");
    }

    @Test
    void flatMapEachInnerChangeCallsInnerGetOnceForAllDupes() {
        Member kfir = new Member("kfir");
        CountingObservable<String> innerName = new CountingObservable<>(kfir.displayName());

        ListObservable<Member> list = Observable.list(new ArrayList<>(List.of(kfir, kfir, kfir, kfir, kfir)));
        ListObservable<String> names = list.flatMapEach(member -> innerName);

        names.get();
        int innerGetsAfterInit = innerName.gets;

        kfir.displayName().set("Kfir Notro");
        names.get();

        // exactly one fresh inner.get() despite five duplicate occurrences
        assertEquals(innerGetsAfterInit + 1, innerName.gets,
                "inner observable.get() invoked once per change, regardless of how many duplicates reference it");
    }

    @Test
    void flatMapEachRemovingOneOfManyDupesDoesNotUnsubscribe() {
        Member kfir = new Member("kfir");
        CountingObservable<String> innerName = new CountingObservable<>(kfir.displayName());

        ListObservable<Member> list = Observable.list(new ArrayList<>(List.of(kfir, kfir, kfir)));
        ListObservable<String> names = list.flatMapEach(member -> innerName);

        names.get();
        assertEquals(0, innerName.removes);

        list.remove(kfir);
        names.get();

        assertEquals(0, innerName.removes,
                "removing one occurrence must not unsubscribe — other occurrences still reference the inner observable");
    }

    @Test
    void flatMapEachRemovingAllDupesUnsubscribesOnce() {
        Member kfir = new Member("kfir");
        CountingObservable<String> innerName = new CountingObservable<>(kfir.displayName());

        ListObservable<Member> list = Observable.list(new ArrayList<>(List.of(kfir, kfir, kfir, kfir)));
        ListObservable<String> names = list.flatMapEach(member -> innerName);

        names.get();
        assertEquals(0, innerName.removes);

        list.clear();
        names.get();

        assertEquals(1, innerName.removes,
                "after every reference is gone, the inner observable is unsubscribed exactly once");
    }

    @Test
    void flatMapEachReAddingAfterFullRemovalReSubscribesOnce() {
        Member kfir = new Member("kfir");
        CountingObservable<String> innerName = new CountingObservable<>(kfir.displayName());

        ListObservable<Member> list = Observable.list(new ArrayList<>(List.of(kfir, kfir)));
        ListObservable<String> names = list.flatMapEach(member -> innerName);

        names.get();
        assertEquals(1, innerName.observes);

        list.clear();
        names.get();
        assertEquals(1, innerName.removes);

        list.add(kfir);
        list.add(kfir);
        list.add(kfir);
        names.get();

        assertEquals(2, innerName.observes,
                "re-adding triggers a single fresh subscribe, not one per occurrence");
    }

    @Test
    void mapEachStableGetReturnsCachedWithoutReInvokingMapper() {
        ListObservable<String> list = Observable.list(new ArrayList<>(List.of(
                "kfir", "kfir", "apartium", "kfir"
        )));

        AtomicInteger mapperCalls = new AtomicInteger();
        ListObservable<Integer> lengths = list.mapEach(name -> {
            mapperCalls.incrementAndGet();
            return name.length();
        });

        lengths.get();
        assertEquals(4, mapperCalls.get(), "first materialization invokes mapper once per occurrence");

        // stable gets — no source change, mapper must not be re-invoked
        lengths.get();
        lengths.get();
        lengths.get();
        assertEquals(4, mapperCalls.get(), "stable get() must hit cache and not re-invoke the mapper");
    }

    @Test
    void mapEachReInvokesMapperPerOccurrenceOnBaseChange() {
        ListObservable<String> list = Observable.list(new ArrayList<>(List.of("kfir", "kfir")));

        AtomicInteger mapperCalls = new AtomicInteger();
        ListObservable<Integer> lengths = list.mapEach(name -> {
            mapperCalls.incrementAndGet();
            return name.length();
        });

        lengths.get();
        assertEquals(2, mapperCalls.get());

        list.add("kfir");
        lengths.get();

        // mapEach has no per-element caching: every occurrence is re-mapped after a base change
        assertEquals(5, mapperCalls.get(),
                "mapEach re-runs the mapper per occurrence after a base change (no per-element caching)");
    }

    @Test
    void filterRefDupesInvokeMapperOnceAndPredicateGetOnce() {
        Member kfir = new Member("kfir");
        CountingObservable<Boolean> activePredicate = new CountingObservable<>(kfir.active());

        AtomicInteger mapperCalls = new AtomicInteger();
        ListObservable<Member> list = Observable.list(new ArrayList<>(List.of(kfir, kfir, kfir, kfir, kfir)));
        ListObservable<Member> active = list.filter(member -> {
            mapperCalls.incrementAndGet();
            return activePredicate;
        });

        active.get();

        assertEquals(1, mapperCalls.get(), "predicate mapper invoked once per unique source ref");
        assertEquals(1, activePredicate.observes, "predicate observable subscribed exactly once across all duplicates");
        assertEquals(1, activePredicate.gets,
                "predicate.get() invoked once during materialization regardless of duplicate count");
    }

    @Test
    void filterEqualButDistinctInvokesPredicateMapperPerOccurrence() {
        record Token(String id) {}

        Token a = new Token("kfir");
        Token b = new Token("kfir");
        Token c = new Token("kfir");

        AtomicInteger mapperCalls = new AtomicInteger();
        ListObservable<Token> list = Observable.list(new ArrayList<>(List.of(a, b, c)));
        ListObservable<Token> filtered = list.filter(token -> {
            mapperCalls.incrementAndGet();
            return Observable.immutable(true);
        });

        filtered.get();

        assertEquals(3, mapperCalls.get(),
                "identity-based bookkeeping invokes the predicate mapper once per distinct ref");
    }

    @Test
    void filterStableGetDoesNotReInvokeMapperOrPredicateGet() {
        Member kfir = new Member("kfir");
        CountingObservable<Boolean> activePredicate = new CountingObservable<>(kfir.active());

        AtomicInteger mapperCalls = new AtomicInteger();
        ListObservable<Member> list = Observable.list(new ArrayList<>(List.of(kfir, kfir, kfir)));
        ListObservable<Member> active = list.filter(member -> {
            mapperCalls.incrementAndGet();
            return activePredicate;
        });

        active.get();
        int gets1 = activePredicate.gets;
        int mapper1 = mapperCalls.get();

        active.get();
        active.get();
        active.get();

        assertEquals(mapper1, mapperCalls.get(), "predicate mapper not re-invoked across stable gets");
        assertEquals(gets1, activePredicate.gets, "predicate.get() not re-invoked across stable gets");
    }

    @Test
    void filterPredicateChangeCallsPredicateGetOnceForAllDupes() {
        Member kfir = new Member("kfir");
        CountingObservable<Boolean> activePredicate = new CountingObservable<>(kfir.active());

        ListObservable<Member> list = Observable.list(new ArrayList<>(List.of(kfir, kfir, kfir, kfir, kfir)));
        ListObservable<Member> active = list.filter(member -> activePredicate);

        active.get();
        int getsAfterInit = activePredicate.gets;

        kfir.active().set(false);
        active.get();

        assertEquals(getsAfterInit + 1, activePredicate.gets,
                "predicate.get() invoked once per change regardless of how many duplicates share it");
    }

    @Test
    void filterAddingMoreRefDupesDoesNotReInvokeMapperOrReSubscribe() {
        Member kfir = new Member("kfir");
        CountingObservable<Boolean> activePredicate = new CountingObservable<>(kfir.active());

        AtomicInteger mapperCalls = new AtomicInteger();
        ListObservable<Member> list = Observable.list(new ArrayList<>(List.of(kfir)));
        ListObservable<Member> active = list.filter(member -> {
            mapperCalls.incrementAndGet();
            return activePredicate;
        });

        active.get();
        int observesAfterInit = activePredicate.observes;
        assertEquals(1, mapperCalls.get());
        assertEquals(1, observesAfterInit);

        list.add(kfir);
        list.add(kfir);
        active.get();

        assertEquals(1, mapperCalls.get(), "mapper not re-invoked for new duplicates of an existing ref");
        assertEquals(observesAfterInit, activePredicate.observes, "no extra subscribe for new duplicates");
    }

    @Test
    void filterRemovingOneDupeDoesNotUnsubscribe() {
        Member kfir = new Member("kfir");
        CountingObservable<Boolean> activePredicate = new CountingObservable<>(kfir.active());

        ListObservable<Member> list = Observable.list(new ArrayList<>(List.of(kfir, kfir, kfir)));
        ListObservable<Member> active = list.filter(member -> activePredicate);

        active.get();
        assertEquals(0, activePredicate.removes);

        list.remove(kfir);
        active.get();

        assertEquals(0, activePredicate.removes,
                "filter must not unsubscribe while any occurrence still references the predicate observable");
    }

    @Test
    void filterRemovingAllDupesUnsubscribesOnce() {
        Member kfir = new Member("kfir");
        CountingObservable<Boolean> activePredicate = new CountingObservable<>(kfir.active());

        ListObservable<Member> list = Observable.list(new ArrayList<>(List.of(kfir, kfir, kfir)));
        ListObservable<Member> active = list.filter(member -> activePredicate);

        active.get();
        list.clear();
        active.get();

        assertEquals(1, activePredicate.removes,
                "filter unsubscribes exactly once when the last occurrence is removed");
    }

    @Test
    void flatMapEachDownstreamFiresOnceWhenSharedInnerChangesEvenWithDupes() {
        MutableObservable<String> shared = Observable.mutable("kfir");

        Member a = new Member("a", shared);
        Member b = new Member("b", shared);
        Member c = new Member("c", shared);

        ListObservable<Member> list = Observable.list(new ArrayList<>(List.of(a, b, c, a, b, c)));
        ListObservable<String> names = list.flatMapEach(Member::displayName);
        names.get();

        CountingObserver downstream = new CountingObserver();
        names.observe(downstream);

        shared.set("apartium");

        assertEquals(1, downstream.count,
                "downstream is flagged dirty exactly once per upstream change, not once per duplicate");
    }

    @Test
    void filterDownstreamFiresOnceWhenSharedPredicateChangesEvenWithDupes() {
        MutableObservable<Boolean> shared = Observable.mutable(true);

        Member a = new Member("a", Observable.mutable("a"), shared);
        Member b = new Member("b", Observable.mutable("b"), shared);

        ListObservable<Member> list = Observable.list(new ArrayList<>(List.of(a, b, a, b, a, b)));
        ListObservable<Member> active = list.filter(Member::active);
        active.get();

        CountingObserver downstream = new CountingObserver();
        active.observe(downstream);

        shared.set(false);

        assertEquals(1, downstream.count,
                "filter notifies downstream once per upstream change, regardless of duplicate count");
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

    private static final class CountingObservable<T> implements Observable<T>, Observer {

        final Observable<T> delegate;
        final java.util.LinkedHashSet<Observer> observers = new java.util.LinkedHashSet<>();
        int gets;
        int observes;
        int removes;
        boolean bridged;

        CountingObservable(Observable<T> delegate) {
            this.delegate = delegate;
        }

        @Override
        public T get() {
            gets++;
            return delegate.get();
        }

        @Override
        public void observe(Observer observer) {
            observes++;
            if (!bridged) {
                delegate.observe(this);
                bridged = true;
            }
            observers.add(observer);
        }

        @Override
        public boolean removeObserver(Observer observer) {
            removes++;
            return observers.remove(observer);
        }

        @Override
        public void flagAsDirty(Observable<?> observable) {
            for (Observer observer : observers)
                observer.flagAsDirty(this);
        }

    }

}
