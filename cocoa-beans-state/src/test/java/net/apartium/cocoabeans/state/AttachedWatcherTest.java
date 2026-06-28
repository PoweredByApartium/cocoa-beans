package net.apartium.cocoabeans.state;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;
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
                /* ignore */
            }

            @Override
            public void detach(AttachedWatcher<?> watcher) {
                /* ignore */
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
                /* ignore */
            }

            @Override
            public void detach(AttachedWatcher<?> watcher) {
                /* ignore */
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
    void attachWithNullManager() {
        Observable<Integer> num = Observable.mutable(5);
        AttachedWatcher<Integer> watcher = new AttachedWatcher<>(num) {
            @Override
            public void onChange(Integer newValue) {
                /* ignore */
            }
        };

        assertThrowsExactly(NullPointerException.class, () -> watcher.attach(null));
        assertFalse(watcher.isAttached());
        assertNull(watcher.getManager());
    }

    @Test
    void attachRollsBackWhenOperatorAttachThrows() {
        Observable<Integer> num = Observable.mutable(5);
        AttachedWatcher<Integer> watcher = new AttachedWatcher<>(num) {
            @Override
            public void onChange(Integer newValue) {
                /* ignore */
            }
        };

        WatcherOperator failingOperator = new WatcherOperator() {
            @Override
            public void attach(AttachedWatcher<?> watcher) {
                throw new RuntimeException("operator rejected");
            }

            @Override
            public void detach(AttachedWatcher<?> watcher) {
                /* ignore */
            }
        };

        RuntimeException ex = assertThrowsExactly(RuntimeException.class, () -> watcher.attach(failingOperator));
        assertEquals("operator rejected", ex.getMessage());

        // watcher should be rolled back to unattached state
        assertFalse(watcher.isAttached());
        assertNull(watcher.getManager());

        // should still be attachable after the failed attempt
        WatcherManager manager = new WatcherManager();
        watcher.attach(manager);
        assertTrue(watcher.isAttached());
        assertEquals(manager, watcher.getManager());
        watcher.detach();
    }

    @Test
    void attachRollsBackWhenObserveThrows() {
        AtomicBoolean shouldThrow = new AtomicBoolean(false);
        AtomicBoolean detachCalled = new AtomicBoolean(false);

        Observable<Integer> throwingObservable = new Observable<>() {
            @Override
            public Integer get() {
                return 0;
            }

            @Override
            public void observe(Observer observer) {
                if (shouldThrow.get())
                    throw new RuntimeException("observe failed");
            }

            @Override
            public boolean removeObserver(Observer observer) {
                return true;
            }
        };

        AttachedWatcher<Integer> watcher = new AttachedWatcher<>(throwingObservable) {
            @Override
            public void onChange(Integer newValue) {
                /* ignore */
            }
        };

        WatcherOperator operator = new WatcherOperator() {
            @Override
            public void attach(AttachedWatcher<?> watcher) {
                /* accept */
            }

            @Override
            public void detach(AttachedWatcher<?> watcher) {
                detachCalled.set(true);
            }
        };

        // observe throws after operator.attach succeeds → should rollback by calling operator.detach
        shouldThrow.set(true);
        RuntimeException ex = assertThrowsExactly(RuntimeException.class, () -> watcher.attach(operator));
        assertEquals("observe failed", ex.getMessage());

        // operator.detach was called to rollback
        assertTrue(detachCalled.get());

        // watcher is not attached
        assertFalse(watcher.isAttached());
        assertNull(watcher.getManager());
    }

    @Test
    void attachRollbackDetachThrowsSuppressed() {
        AtomicBoolean shouldThrow = new AtomicBoolean(false);

        Observable<Integer> throwingObservable = new Observable<>() {
            @Override
            public Integer get() {
                return 0;
            }

            @Override
            public void observe(Observer observer) {
                if (shouldThrow.get())
                    throw new RuntimeException("observe failed");
            }

            @Override
            public boolean removeObserver(Observer observer) {
                return true;
            }
        };

        AttachedWatcher<Integer> watcher = new AttachedWatcher<>(throwingObservable) {
            @Override
            public void onChange(Integer newValue) {
                /* ignore */
            }
        };

        WatcherOperator operator = new WatcherOperator() {
            @Override
            public void attach(AttachedWatcher<?> watcher) {
                /* accept */
            }

            @Override
            public void detach(AttachedWatcher<?> watcher) {
                throw new RuntimeException("detach also failed");
            }
        };

        // observe throws, then rollback detach also throws → detach exception is suppressed
        shouldThrow.set(true);
        RuntimeException ex = assertThrowsExactly(RuntimeException.class, () -> watcher.attach(operator));
        assertEquals("observe failed", ex.getMessage());
        assertEquals(1, ex.getSuppressed().length);
        assertEquals("detach also failed", ex.getSuppressed()[0].getMessage());

        assertFalse(watcher.isAttached());
        assertNull(watcher.getManager());
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
