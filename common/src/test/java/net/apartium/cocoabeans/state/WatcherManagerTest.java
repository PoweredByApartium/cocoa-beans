package net.apartium.cocoabeans.state;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class WatcherManagerTest {

    @Test
    void detachAndAttach() {
        MutableObservable<Integer> num = Observable.mutable(9);

        WatcherManager watcherManager = new WatcherManager();

        AtomicInteger counter = new AtomicInteger(0);
        AttachedWatcher<Integer> watch = num.watch(watcherManager, n -> {
            assertEquals(switch(counter.getAndIncrement()) {
                case 0 -> 9;
                case 1 -> 10;
                case 2 -> 63;
                case 3 -> 21;
                default -> throw new IllegalArgumentException();
            }, n);
        });

        watcherManager.heartbeat();

        num.set(10);
        watcherManager.heartbeat();

        watch.detach();
        num.set(600);
        watcherManager.heartbeat();

        watch.attach(watcherManager);

        num.set(63);
        watcherManager.heartbeat();

        num.set(21);
        watcherManager.heartbeat();
    }

    @Test
    void watcherDetach() {
        MutableObservable<Integer> num = Observable.mutable(9);

        WatcherManager watcherManager = new WatcherManager();

        AtomicInteger counter = new AtomicInteger(0);
        Watcher<Integer> watch = num.watch(watcherManager, n ->
            assertEquals(switch(counter.getAndIncrement()) {
                case 0 -> 9;
                case 1 -> 10;
                case 2 -> 63;
                default -> throw new IllegalArgumentException();
            }, n)
        );

        watcherManager.heartbeat();
        watcherManager.heartbeat();

        num.set(10);

        watcherManager.heartbeat();
        watcherManager.heartbeat();
        watcherManager.heartbeat();

        num.set(21); // should not print because no heartbeat
        num.set(63);
        watcherManager.heartbeat();

        watch.detach();

        num.set(16);
        watcherManager.heartbeat();
        watcherManager.heartbeat();
        watcherManager.heartbeat();

    }

    @Test
    void attachAndDetach() {
        WatcherManager watcherManager = new WatcherManager();

        MutableObservable<Integer> observable = Observable.mutable(123);
        AtomicInteger counter = new AtomicInteger(0);
        AttachedWatcher<Integer> watcher = watcherManager.watch(observable, num -> {
            if (counter.get() >= 2) {
                fail();
                return;
            }

            assertEquals(
                    counter.get() == 0
                            ? 123
                            : 456,
                    num
            );
            counter.incrementAndGet();
        });

        watcherManager.heartbeat();
        watcherManager.heartbeat();

        observable.set(2);
        observable.set(3);
        observable.set(123);

        watcherManager.heartbeat();

        observable.set(456);

        watcherManager.heartbeat();

        watcherManager.detach(watcher);

    }

    @Test
    void heartbeat() {
        WatcherManager watcherManager = new WatcherManager();

        MutableObservable<Integer> observable = Observable.mutable(null);
        AtomicInteger counter = new AtomicInteger(0);
        AttachedWatcher<Integer> watcher = watcherManager.watch(observable, num -> {
            if (counter.get() >= 2) {
                fail();
                return;
            }

            assertEquals(
                    counter.get() == 0
                            ? null
                            : 456,
                    num
            );
            counter.incrementAndGet();
        });

        watcherManager.heartbeat();
        watcherManager.heartbeat();

        observable.set(456);
        watcherManager.heartbeat();

        watcher.detach();
    }

}
