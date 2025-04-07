package net.apartium.cocoabeans.state;

import org.junit.jupiter.api.Test;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class SetObservableTest {

    @Test
    void add() {
        SetObservable<Integer> set = Observable.set();

        AtomicInteger counter = new AtomicInteger(0);
        Observable<String> map = set.map((values) -> {
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
        Observable<String> map = set.map((values) -> {
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
        Observable<String> map = set.map((values) -> {
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

        assertTrue(set.removeIf((num) -> num % 2 == 1));
        assertEquals("[6]", map.get());
        assertEquals(3, counter.get());

        assertFalse(set.removeIf((num) -> num % 2 == 1));
        assertEquals("[6]", map.get());
        assertEquals(3, counter.get());
    }

    @Test
    void retainAll() {
        SetObservable<Integer> set = Observable.set();
        AtomicInteger counter = new AtomicInteger(0);
        Observable<String> map = set.map((values) -> {
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
        Observable<String> map = set.map((values) -> {
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

}
