package net.apartium.cocoabeans.state;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AttachedWatcherTest {

    @Test
    void attachAndDetach() {
        Observable<Integer> num = Observable.mutable(5);
        AttachedWatcher<Integer> watcher = new AttachedWatcher<>(num, System.out::println);

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
        AttachedWatcher<Integer> watcher = new AttachedWatcher<>(num, System.out::println);

        WatcherOperator operator = w -> {};

        watcher.attach(operator);

        assertTrue(watcher.isAttached());
        assertEquals(operator, watcher.getManager());

        assertThrowsExactly(IllegalArgumentException.class, () -> watcher.attach(operator));
    }

    @Test
    void detachFromNotAttached() {
        Observable<Integer> num = Observable.mutable(5);
        AttachedWatcher<Integer> watcher = new AttachedWatcher<>(num, System.out::println);
        assertThrowsExactly(IllegalArgumentException.class, watcher::detach);
    }

}
