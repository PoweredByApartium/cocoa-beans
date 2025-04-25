package net.apartium.cocoabeans.state;

import org.jetbrains.annotations.ApiStatus;

import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;

/**
 * A reference implementation of {@link WatcherOperator}, referencing watchers via an identity hash set.
 * This implementation is thread safe.
 * NOTE: To make an instance do anything, its {@link WatcherManager#heartbeat()} method has to be triggered externally.
 * @see WatcherOperator
 */
@ApiStatus.AvailableSince("0.0.39")
public class WatcherManager implements WatcherOperator {

    private final Set<Watcher<?>> watchers = Collections.newSetFromMap(new IdentityHashMap<>());

    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    /**
     * Triggers heartbeat on all the attached watchers
     * @see Watcher#heartbeat()
     */
    public void heartbeat() {
        lock.readLock().lock();
        Set<Watcher<?>> tempWatchers;
        try {
            tempWatchers = new HashSet<>(this.watchers);
        } finally {
            lock.readLock().unlock();
        }

        for (Watcher<?> watcher : tempWatchers)
            watcher.heartbeat();

    }

    /**
     * Creates a watcher instance attached to the current instance
     * @param depends observable to depend on
     * @param consumer action to run on change of the observable
     * @return a new attached watcher instance
     * @param <T> state element type
     * @see Watcher
     */
    public <T> AttachedWatcher<T> watch(Observable<T> depends, Consumer<T> consumer) {
        AttachedWatcher<T> watcher = new AttachedWatcher<>(depends, consumer);
        lock.writeLock().lock();
        try {
            watchers.add(watcher);
        } finally {
            lock.writeLock().unlock();
        }
        watcher.attach(this);
        return watcher;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void detach(AttachedWatcher<?> watcher) {
        lock.writeLock().lock();
        try {
            watchers.remove(watcher);
        } finally {
            lock.writeLock().unlock();
        }
    }

}
