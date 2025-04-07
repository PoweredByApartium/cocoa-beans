package net.apartium.cocoabeans.state;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class CodeSnippets {

    @Test
    void mutable() {
        MutableObservable<Integer> observable = Observable.mutable(123);

        assertEquals(123, observable.get());

        observable.set(456); // flag his observers dirty

        assertEquals(456, observable.get());
    }

    @Test
    void immutable() {
        Observable<Integer> observable = Observable.immutable(123);

        assertEquals(123, observable.get()); // Always return 123 and not flag anyone dirty
    }

    @Test
    void empty() {
        Observable<Integer> observable = Observable.empty();

        assertEquals(Observable.empty(), observable);
        assertNull(observable.get());
    }

    @Test
    void compound() {
        MutableObservable<String> name = Observable.mutable("Kfir");
        MutableObservable<Integer> num = Observable.mutable(123);

        Observable<CompoundRecords.RecordOf2<String, Integer>> compound = Observable.compound(name, num);

        assertEquals("Kfir", compound.get().arg0());
        assertEquals(123, compound.get().arg1());
    }

    @Test
    void mapped() {
        MutableObservable<Integer> number = Observable.mutable(123);
        Observable<Boolean> isEven = number.map((num) -> num % 2 == 0);

        assertFalse(isEven.get()); // because 123 is odd

        number.set(8);
        assertTrue(isEven.get()); // because 8 is even

        number.set(11);
        number.set(8);
        assertTrue(isEven.get()); // because 8 is even and won't recompute because same value

        Observable<String> isEvenParity = isEven.map(
                (b) -> b
                        ? "even"
                        : "odd"
        );

        assertEquals("even", isEvenParity.get());

        number.set(16);

        assertEquals("even", isEvenParity.get()); // Output "even" and isEvenParity won't be recompute because isEven at come up with same value
    }

    @Test
    void list() {
        ListObservable<String> names = Observable.list(); // An ArrayList<String>

        names.add("Kfir");
        assertEquals(List.of("Kfir"), names.get());

        names.add("Lior");
        assertEquals(List.of("Kfir", "Lior"), names.get());

        names.addAll(List.of("Kfir", "Tom"));
        assertEquals(List.of("Kfir", "Lior", "Kfir", "Tom"), names.get());

        names.remove("Kfir");
        assertEquals(List.of("Lior", "Kfir", "Tom"), names.get());
    }

    public class MyObserver<T> implements Observer {

        private final Observable<T> target;
        private boolean isDirty = false;

        private MyObserver(Observable<T> target) {
            this.target = target;
        }

        @Override
        public void flagAsDirty(Observable<?> observable) {
            if (observable != target)
                return;

            isDirty = true;
            // Using heartbeat or other way to get
            // the value after it has been flagged as dirty
            // this is simple example no changes check
        }

    }

    @Test
    void watch() {
        MutableObservable<Integer> observable = Observable.mutable(1);
        AtomicInteger count = new AtomicInteger();

        WatcherManager watcherManager = new WatcherManager();
        AttachedWatcher<Integer> watcher = observable.watch(watcherManager, (num) -> {
            System.out.println(num);

            switch (count.get()) {
                case 0 -> assertEquals(1, num);
                case 1 -> assertEquals(8, num);
                case 2 -> assertEquals(67, num);
                default -> fail();
            }

            count.incrementAndGet();
        });

        watcherManager.heartbeat(); // 1
        watcherManager.heartbeat(); // No new data
        watcherManager.heartbeat(); // No new data

        observable.set(8);
        watcherManager.heartbeat(); // 8
        watcherManager.heartbeat(); // No new data

        observable.set(4);
        observable.set(67);
        watcherManager.heartbeat(); // 67

        observable.set(8);
        observable.set(67);
        watcherManager.heartbeat(); // No new data

        watcher.detach(); // Detach watcher from manager
        assertFalse(watcher.isAttached()); // Make sure it detach
    }

    @Test
    void example() {
        MutableObservable<Integer> num = Observable.mutable(0);
        Observable<Boolean> isEven = num.map(n -> n % 2 == 0);
        Observable<String> parity = Observable.compound(num, isEven)
                .map((args) -> args.arg0() + ": " + args.arg1());

        assertEquals("0: true", parity.get());

        num.set(9); // Flag isEven and parity as dirty but don't recompute yet

        assertEquals("9: false", parity.get());

        num.set(42); // Flag isEven and parity as dirty but don't recompute yet

        assertEquals("42: true", parity.get());

        num.set(21); // Flag isEven and parity as dirty but don't recompute yet
        num.set(42); // Flag isEven and parity as dirty but don't recompute yet

        assertEquals("42: true", parity.get()); // output "42: true" but didn't need to recompute
    }

    @Test
    void listExample() {
        ListObservable<String> names = Observable.list(); // Create an ArrayList<String>

        assertEquals(List.of(), names.get());

        names.add("Kfir");

        assertEquals(List.of("Kfir"), names.get());

        names.addAll(List.of("Lior", "Tom"));

        assertEquals(List.of("Kfir", "Lior", "Tom"), names.get());

        names.remove("Tom");

        assertEquals(List.of("Kfir", "Lior"),names.get());

        Observable<List<String>> namesLength = names.map((list) ->
                list.stream()
                        .map(name -> name + ": " + name.length())
                        .toList()
        );

        assertEquals(List.of("Kfir: 4", "Lior: 4"), namesLength.get());

        names.add("Elion");
        assertEquals(List.of("Kfir: 4", "Lior: 4", "Elion: 5"), namesLength.get());
    }

    @Test
    void setExample() {
        SetObservable<String> names = Observable.set();

        assertTrue(names.add("ikfir"));
        assertFalse(names.add("ikfir"));

        assertEquals(Set.of("ikfir"), names.get());

        assertTrue(names.get().contains("ikfir"));
    }

}
