package net.apartium.cocoabeans.state;

import org.jetbrains.annotations.ApiStatus;

/**
 * Manages watchers attached to it
 * @see WatcherManager reference implementation
 * @see AttachedWatcher
 */
@ApiStatus.AvailableSince("0.0.39")
public interface WatcherOperator {

    /**
     * Cancels a watcher attached with the current instance
     * @param watcher watcher instance
     */
    void cancel(AttachedWatcher<?> watcher);

}
