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
     * Attach a watcher to the current operator.
     * This method is called by {@link AttachedWatcher#attach(WatcherOperator)} to register
     * the watcher with the operator so it will be included in future heartbeat cycles.
     *
     * @param watcher the watcher to attach
     * @throws IllegalArgumentException if the watcher is not associated with this operator
     * @see AttachedWatcher#attach(WatcherOperator)
     */
    @ApiStatus.AvailableSince("0.0.52")
    void attach(AttachedWatcher<?> watcher);

    /**
     * Detach a watcher attached with the current instance
     * @param watcher watcher instance
     */
    void detach(AttachedWatcher<?> watcher);

}
