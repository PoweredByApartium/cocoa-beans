package net.apartium.cocoabeans.state;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class AttachedWatcherTest {

    @Test
    void attachAndDetach() {
        Observable<Integer> num = Observable.mutable(5);
        AttachedWatcher<Integer> watcher = new AttachedWatcher<>(num) {
            @Override
            public void onChange(Integer newValue) {
                System.out.println(newValue);
            }
        };

        WatcherOperator operator = new WatcherOperator() {
            @Override
            public void attach(AttachedWatcher<?> watcher) {

            }

            @Override
            public void detach(AttachedWatcher<?> watcher) {

            }
        };

        watcher.attach(operator);

        assertTrue(watcher.isAttached());
        assertEquals(operator, watcher.getManager());

        watcher.detach();

        assertFalse(watcher.isAttached());
    }

    @Test
    void attachToAlreadyAttached() {
        Observable<Integer> num = Observable.mutable(5);
        AttachedWatcher<Integer> watcher = new AttachedWatcher<>(num) {
            @Override
            public void onChange(Integer newValue) {
                System.out.println(newValue);
            }
        };

        WatcherOperator operator = new WatcherOperator() {
            @Override
            public void attach(AttachedWatcher<?> watcher) {

            }

            @Override
            public void detach(AttachedWatcher<?> watcher) {

            }
        };

        watcher.attach(operator);

        assertTrue(watcher.isAttached());
        assertEquals(operator, watcher.getManager());

        assertThrowsExactly(IllegalArgumentException.class, () -> watcher.attach(operator));
    }

    @Test
    void detachFromNotAttached() {
        Observable<Integer> num = Observable.mutable(5);
        AttachedWatcher<Integer> watcher = new AttachedWatcher<>(num) {
            @Override
            public void onChange(Integer newValue) {
                System.out.println(newValue);
            }
        };
        assertThrowsExactly(IllegalArgumentException.class, watcher::detach);
    }

    @Test
    void reAttachToDifferentOperator() {
        MutableObservable<Integer> num = Observable.mutable(5);
        AtomicInteger callCount = new AtomicInteger();
        AttachedWatcher<Integer> watcher = new AttachedWatcher<>(num) {
            @Override
            public void onChange(Integer newValue) {
                callCount.incrementAndGet();
            }
        };

        WatcherManager firstManager = new WatcherManager();
        WatcherManager secondManager = new WatcherManager();

        watcher.attach(firstManager);
        assertTrue(watcher.isAttached());
        assertEquals(firstManager, watcher.getManager());

        // heartbeat on first manager triggers the watcher
        firstManager.heartbeat();
        assertEquals(1, callCount.get());

        num.set(10);
        firstManager.heartbeat();
        assertEquals(2, callCount.get());

        // detach from first manager
        watcher.detach();
        assertFalse(watcher.isAttached());
        assertNull(watcher.getManager());

        // heartbeat on first manager should no longer trigger
        num.set(20);
        firstManager.heartbeat();
        assertEquals(2, callCount.get());

        // re-attach to second manager
        watcher.attach(secondManager);
        assertTrue(watcher.isAttached());
        assertEquals(secondManager, watcher.getManager());

        // changes while detached are not observed, so set a new value
        num.set(25);
        secondManager.heartbeat();
        assertEquals(3, callCount.get());

        num.set(30);
        secondManager.heartbeat();
        assertEquals(4, callCount.get());

        // first manager still does not trigger
        num.set(40);
        firstManager.heartbeat();
        assertEquals(4, callCount.get());

        watcher.detach();
        assertFalse(watcher.isAttached());
    }

    @Test
    void reAttachToSameOperator() {
        MutableObservable<Integer> num = Observable.mutable(1);
        AtomicInteger callCount = new AtomicInteger();
        AttachedWatcher<Integer> watcher = new AttachedWatcher<>(num) {
            @Override
            public void onChange(Integer newValue) {
                callCount.incrementAndGet();
            }
        };

        WatcherManager manager = new WatcherManager();

        watcher.attach(manager);
        manager.heartbeat();
        assertEquals(1, callCount.get());

        watcher.detach();

        // re-attach to the same manager
        watcher.attach(manager);
        assertTrue(watcher.isAttached());
        assertEquals(manager, watcher.getManager());

        num.set(42);
        manager.heartbeat();
        assertEquals(2, callCount.get());

        watcher.detach();
    }

}
