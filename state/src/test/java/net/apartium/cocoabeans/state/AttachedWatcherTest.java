package net.apartium.cocoabeans.state;

import org.junit.jupiter.api.Test;

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

        WatcherOperator operator = w -> {};

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

        WatcherOperator operator = w -> {};

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

}
