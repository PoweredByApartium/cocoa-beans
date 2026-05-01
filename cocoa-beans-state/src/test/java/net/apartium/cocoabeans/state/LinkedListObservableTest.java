package net.apartium.cocoabeans.state;

import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class LinkedListObservableTest {

    @Test
    void defaultConstructorStartsEmpty() {
        LinkedListObservable<Integer> list = Observable.linkedList();
        assertEquals(List.of(), list.get());
        assertEquals(0, list.size().get());
    }

    @Test
    void constructorWithPrePopulatedList() {
        LinkedList<String> initial = new LinkedList<>(List.of("a", "b", "c"));
        LinkedListObservable<String> list = Observable.linkedList(initial);
        assertEquals(List.of("a", "b", "c"), list.get());
        assertEquals(3, list.size().get());
    }

    @Test
    void getReturnsCopy() {
        LinkedListObservable<Integer> list = Observable.linkedList();
        list.add(1);

        LinkedList<Integer> snapshot = list.get();
        snapshot.add(99); // mutate the copy

        assertEquals(List.of(1), list.get()); // original unchanged
    }

    // -------------------------------------------------------------------------
    // add / addAll
    // -------------------------------------------------------------------------

    @Test
    void addNotifiesObservers() {
        LinkedListObservable<Integer> list = Observable.linkedList();
        AtomicInteger counter = new AtomicInteger(0);
        Observable<String> mapped = list.map(l -> {
            counter.incrementAndGet();
            return l.toString();
        });

        assertEquals(0, counter.get());

        assertTrue(list.add(1));
        assertEquals(0, counter.get()); // lazy – not yet evaluated

        assertEquals("[1]", mapped.get());
        assertEquals(1, counter.get());

        assertTrue(list.add(2));
        assertEquals("[1, 2]", mapped.get());
        assertEquals(2, counter.get());
    }

    @Test
    void addAllNotifiesOnce() {
        LinkedListObservable<Integer> list = Observable.linkedList();
        AtomicInteger counter = new AtomicInteger(0);
        Observable<String> mapped = list.map(l -> {
            counter.incrementAndGet();
            return l.toString();
        });

        assertTrue(list.addAll(List.of(1, 2, 3)));
        assertEquals("[1, 2, 3]", mapped.get());
        assertEquals(1, counter.get());
    }

    @Test
    void addAllNoChangeDoesNotNotify() {
        LinkedListObservable<Integer> list = Observable.linkedList();
        list.add(1);

        AtomicInteger counter = new AtomicInteger(0);
        Observable<String> mapped = list.map(l -> {
            counter.incrementAndGet();
            return l.toString();
        });

        assertEquals("[1]", mapped.get());
        assertEquals(1, counter.get());

        // LinkedList allows duplicates, so addAll with already-present elements still changes the list
        // This test verifies addAll with an empty collection does NOT notify
        assertFalse(list.addAll(List.of()));
        assertEquals("[1]", mapped.get());
        assertEquals(1, counter.get()); // no recomputation
    }

    // -------------------------------------------------------------------------
    // add(int index, E element) — AbstractListObservable
    // -------------------------------------------------------------------------

    @Test
    void addAtIndexHead() {
        LinkedListObservable<Integer> list = Observable.linkedList();
        list.add(2);
        list.add(3);

        list.add(0, 1); // insert at head
        assertEquals(List.of(1, 2, 3), list.get());
    }

    @Test
    void addAtIndexTail() {
        LinkedListObservable<Integer> list = Observable.linkedList();
        list.add(1);
        list.add(2);

        list.add(2, 3); // append at tail via index
        assertEquals(List.of(1, 2, 3), list.get());
    }

    @Test
    void addAtIndexMiddleNotifiesObservers() {
        LinkedListObservable<Integer> list = Observable.linkedList();
        list.add(1);
        list.add(3);

        AtomicInteger counter = new AtomicInteger(0);
        list.observe(obs -> counter.incrementAndGet());

        list.add(1, 2);
        assertEquals(1, counter.get());
        assertEquals(List.of(1, 2, 3), list.get());
    }

    // -------------------------------------------------------------------------
    // remove(E element) / remove(int index)
    // -------------------------------------------------------------------------

    @Test
    void removeByElement() {
        LinkedListObservable<Integer> list = Observable.linkedList();
        list.addAll(List.of(1, 2, 3));

        assertTrue(list.remove((Integer) 2));
        assertEquals(List.of(1, 3), list.get());
    }

    @Test
    void removeByElementNotPresentReturnsFalse() {
        LinkedListObservable<Integer> list = Observable.linkedList();
        list.add(1);

        assertFalse(list.remove((Integer) 99));
        assertEquals(List.of(1), list.get());
    }

    @Test
    void removeByIndex() {
        LinkedListObservable<Integer> list = Observable.linkedList();
        list.addAll(List.of(10, 20, 30));

        assertEquals(20, list.remove(1));
        assertEquals(List.of(10, 30), list.get());
    }

    @Test
    void removeByIndexHeadAndTail() {
        LinkedListObservable<Integer> list = Observable.linkedList();
        list.addAll(List.of(1, 2, 3));

        assertEquals(1, list.remove(0)); // head
        assertEquals(List.of(2, 3), list.get());

        assertEquals(3, list.remove(1)); // tail
        assertEquals(List.of(2), list.get());
    }

    @Test
    void removeByIndexNotifiesObservers() {
        LinkedListObservable<Integer> list = Observable.linkedList();
        list.addAll(List.of(1, 2, 3));

        AtomicInteger counter = new AtomicInteger(0);
        list.observe(obs -> counter.incrementAndGet());

        list.remove(0);
        assertEquals(1, counter.get());
    }

    // -------------------------------------------------------------------------
    // removeAll / retainAll / removeIf / clear
    // -------------------------------------------------------------------------

    @Test
    void removeAll() {
        LinkedListObservable<Integer> list = Observable.linkedList();
        list.addAll(List.of(1, 2, 3, 4, 5));

        assertTrue(list.removeAll(List.of(2, 4)));
        assertEquals(List.of(1, 3, 5), list.get());

        assertFalse(list.removeAll(List.of(99)));
        assertEquals(List.of(1, 3, 5), list.get());
    }

    @Test
    void retainAll() {
        LinkedListObservable<Integer> list = Observable.linkedList();
        list.addAll(List.of(1, 2, 3, 4, 5));

        AtomicInteger counter = new AtomicInteger(0);
        Observable<String> mapped = list.map(l -> {
            counter.incrementAndGet();
            return l.toString();
        });

        assertTrue(list.retainAll(List.of(1, 3, 5)));
        assertEquals("[1, 3, 5]", mapped.get());
        assertEquals(1, counter.get());

        assertFalse(list.retainAll(List.of(1, 3, 5)));
        assertEquals("[1, 3, 5]", mapped.get());
        assertEquals(1, counter.get()); // no recomputation
    }

    @Test
    void removeIf() {
        LinkedListObservable<Integer> list = Observable.linkedList();
        list.addAll(List.of(1, 2, 3, 4, 5));

        assertTrue(list.removeIf(n -> n % 2 == 0));
        assertEquals(List.of(1, 3, 5), list.get());

        assertFalse(list.removeIf(n -> n % 2 == 0));
        assertEquals(List.of(1, 3, 5), list.get());
    }

    @Test
    void clearNotifiesOnlyWhenNonEmpty() {
        LinkedListObservable<Integer> list = Observable.linkedList();
        AtomicInteger counter = new AtomicInteger(0);
        list.observe(obs -> counter.incrementAndGet());

        list.clear(); // already empty – no notification
        assertEquals(0, counter.get());

        list.addAll(List.of(1, 2, 3));
        counter.set(0);

        list.clear();
        assertEquals(1, counter.get());
        assertEquals(List.of(), list.get());

        list.clear(); // already empty again – no notification
        assertEquals(1, counter.get());
    }

    // -------------------------------------------------------------------------
    // sort
    // -------------------------------------------------------------------------

    @Test
    void sortNotifiesWhenOrderChanges() {
        LinkedListObservable<Integer> list = Observable.linkedList();
        list.addAll(List.of(3, 1, 2));

        AtomicInteger counter = new AtomicInteger(0);
        Observable<String> mapped = list.map(l -> {
            counter.incrementAndGet();
            return l.toString();
        });

        assertEquals("[3, 1, 2]", mapped.get());
        assertEquals(1, counter.get());

        list.sort(Integer::compareTo);
        assertEquals("[1, 2, 3]", mapped.get());
        assertEquals(2, counter.get());
    }

    @Test
    void sortDoesNotNotifyWhenAlreadySorted() {
        LinkedListObservable<Integer> list = Observable.linkedList();
        list.addAll(List.of(1, 2, 3));

        AtomicInteger counter = new AtomicInteger(0);
        Observable<String> mapped = list.map(l -> {
            counter.incrementAndGet();
            return l.toString();
        });

        assertEquals("[1, 2, 3]", mapped.get());
        assertEquals(1, counter.get());

        list.sort(Integer::compareTo); // already sorted – no notification
        assertEquals("[1, 2, 3]", mapped.get());
        assertEquals(1, counter.get()); // no recomputation
    }

    @Test
    void sortWithNullComparatorThrows() {
        LinkedListObservable<Integer> list = Observable.linkedList();
        list.add(1);
        assertThrows(NullPointerException.class, () -> list.sort(null));
    }

    // -------------------------------------------------------------------------
    // Queue operations: offer / poll / peek / element / remove (head)
    // -------------------------------------------------------------------------

    @Test
    void offerAddsToTailAndNotifies() {
        LinkedListObservable<Integer> list = Observable.linkedList();
        AtomicInteger counter = new AtomicInteger(0);
        list.observe(obs -> counter.incrementAndGet());

        assertTrue(list.offer(1));
        assertEquals(1, counter.get());
        assertTrue(list.offer(2));
        assertEquals(2, counter.get());
        assertEquals(List.of(1, 2), list.get());
    }

    @Test
    void pollRemovesHeadAndNotifies() {
        LinkedListObservable<Integer> list = Observable.linkedList();
        list.addAll(List.of(10, 20, 30));

        AtomicInteger counter = new AtomicInteger(0);
        list.observe(obs -> counter.incrementAndGet());

        assertEquals(10, list.poll());
        assertEquals(1, counter.get());
        assertEquals(List.of(20, 30), list.get());
    }

    @Test
    void pollOnEmptyListReturnsNull() {
        LinkedListObservable<Integer> list = Observable.linkedList();
        assertNull(list.poll());
    }

    @Test
    void removeHeadNotifiesOnNonEmpty() {
        LinkedListObservable<Integer> list = Observable.linkedList();
        list.addAll(List.of(5, 6, 7));

        AtomicInteger counter = new AtomicInteger(0);
        list.observe(obs -> counter.incrementAndGet());

        assertEquals(5, list.remove());
        assertEquals(1, counter.get());
        assertEquals(List.of(6, 7), list.get());
    }

    @Test
    void removeHeadOnEmptyListThrows() {
        LinkedListObservable<Integer> list = Observable.linkedList();
        assertThrows(NoSuchElementException.class, list::remove);
    }

    @Test
    void peekReturnsHeadWithoutRemoving() {
        LinkedListObservable<Integer> list = Observable.linkedList();
        list.addAll(List.of(42, 43));

        AtomicInteger counter = new AtomicInteger(0);
        list.observe(obs -> counter.incrementAndGet());

        assertEquals(42, list.peek());
        assertEquals(0, counter.get()); // no notification
        assertEquals(List.of(42, 43), list.get());
    }

    @Test
    void peekOnEmptyListReturnsNull() {
        LinkedListObservable<Integer> list = Observable.linkedList();
        assertNull(list.peek());
    }

    @Test
    void elementReturnsHeadWithoutRemoving() {
        LinkedListObservable<Integer> list = Observable.linkedList();
        list.add(99);

        AtomicInteger counter = new AtomicInteger(0);
        list.observe(obs -> counter.incrementAndGet());

        assertEquals(99, list.element());
        assertEquals(0, counter.get()); // no notification
        assertEquals(List.of(99), list.get());
    }

    // -------------------------------------------------------------------------
    // size observable
    // -------------------------------------------------------------------------

    @Test
    void sizeObservableTracksChanges() {
        LinkedListObservable<Integer> list = Observable.linkedList();
        Observable<Integer> size = list.size();

        assertEquals(0, size.get());

        list.add(1);
        assertEquals(1, size.get());

        list.add(2);
        assertEquals(2, size.get());

        list.remove((Integer) 1);
        assertEquals(1, size.get());

        list.clear();
        assertEquals(0, size.get());
    }

    // -------------------------------------------------------------------------
    // Observer / watcher management
    // -------------------------------------------------------------------------

    @Test
    void removeObserver() {
        LinkedListObservable<Integer> list = Observable.linkedList();
        AtomicInteger counter = new AtomicInteger(0);
        Observer observer = obs -> counter.incrementAndGet();

        list.observe(observer);
        list.add(1);
        assertEquals(1, counter.get());

        assertTrue(list.removeObserver(observer));
        list.add(2);
        assertEquals(1, counter.get()); // no more notifications
    }

    @Test
    void removeObserverNotAttachedReturnsFalse() {
        LinkedListObservable<Integer> list = Observable.linkedList();
        Observer observer = obs -> {};
        assertFalse(list.removeObserver(observer));
    }

    // -------------------------------------------------------------------------
    // FIFO queue semantics end-to-end
    // -------------------------------------------------------------------------

    @Test
    void fifoQueueBehavior() {
        LinkedListObservable<String> queue = Observable.linkedList();

        queue.offer("first");
        queue.offer("second");
        queue.offer("third");

        assertEquals("first", queue.poll());
        assertEquals("second", queue.poll());
        assertEquals("third", queue.poll());
        assertNull(queue.poll()); // empty
    }

    @Test
    void drainQueueWithMappedObservable() {
        LinkedListObservable<Integer> queue = Observable.linkedList();
        Observable<Integer> size = queue.size();

        for (int i = 1; i <= 5; i++) {
            queue.offer(i);
        }
        assertEquals(5, size.get());

        int sum = 0;
        while (queue.peek() != null) {
            sum += queue.poll();
        }
        assertEquals(15, sum);
        assertEquals(0, size.get());
    }

    // -------------------------------------------------------------------------
    // Edge cases with duplicates
    // -------------------------------------------------------------------------

    @Test
    void duplicateElementsAreAllowed() {
        LinkedListObservable<String> list = Observable.linkedList();
        list.add("x");
        list.add("x");
        list.add("x");

        assertEquals(List.of("x", "x", "x"), list.get());
        assertEquals(3, list.size().get());
    }

    @Test
    void removeByElementOnlyRemovesFirstOccurrence() {
        LinkedListObservable<String> list = Observable.linkedList();
        list.addAll(List.of("a", "b", "a", "c"));

        assertTrue(list.remove("a"));
        assertEquals(List.of("b", "a", "c"), list.get());
    }

    // -------------------------------------------------------------------------
    // Lazy evaluation (observers are not recomputed until get() is called)
    // -------------------------------------------------------------------------

    @Test
    void multipleMutationsBeforeGetRecomputesOnce() {
        LinkedListObservable<Integer> list = Observable.linkedList();
        AtomicInteger counter = new AtomicInteger(0);
        Observable<Integer> sum = list.map(l -> {
            counter.incrementAndGet();
            return l.stream().reduce(0, Integer::sum);
        });

        list.add(1);
        list.add(2);
        list.add(3);

        assertEquals(0, counter.get()); // none yet
        assertEquals(6, sum.get());
        assertEquals(1, counter.get()); // computed once despite 3 mutations
    }

}
