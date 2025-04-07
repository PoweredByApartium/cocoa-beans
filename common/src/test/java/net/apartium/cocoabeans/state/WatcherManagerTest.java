package net.apartium.cocoabeans.state;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class WatcherManagerTest {

    @Test
    void attachAndDetach() {
        WatcherManager watcherManager = new WatcherManager();

        MutableObservable<Integer> observable = Observable.mutable(123);
        AtomicInteger counter = new AtomicInteger(0);
        AttachedWatcher<Integer> watcher = watcherManager.watch(observable, (num) -> {
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

}
