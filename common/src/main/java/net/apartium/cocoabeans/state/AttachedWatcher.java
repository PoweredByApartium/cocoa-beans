package net.apartium.cocoabeans.state;

import org.jetbrains.annotations.ApiStatus;

import java.util.function.Consumer;

/**
 * Represents a watcher attached to an operator
 * @param <T>
 * @see Watcher
 * @see WatcherManager
 * @see WatcherOperator
 */
@ApiStatus.AvailableSince("0.0.39")
public class AttachedWatcher<T> extends Watcher<T> {

    private WatcherOperator manager;

    /**
     * Constructs a new instance
     * @param depends parent state
     * @param consumer action to perform on change
     */
    public AttachedWatcher(Observable<T> depends, Consumer<T> consumer) {
        super(depends, consumer);
    }

    /**
     * Attach this instance to an operator.
     * @throws IllegalArgumentException if this watcher is already attached
     * @param watcherManager operator to associate this instance to
     */
    public void attach(WatcherOperator watcherManager) {
        if (this.manager != null)
            throw new IllegalArgumentException("already attached");

        this.manager = watcherManager;

    }

    /**
     * Cancels this watcher instance.
     * Cancelled instances will not trigger
     */
    public void detach() {
        if (this.manager == null)
            throw new IllegalArgumentException("not attached");

        this.manager.detach(this);
    }

}
