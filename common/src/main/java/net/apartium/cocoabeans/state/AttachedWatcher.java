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
     * Constructs a new instance.
     * @param depends the {@link Observable<T>} state that this watcher is observing.
     * @param consumer the action to perform when the observed state changes.
     */
    public AttachedWatcher(Observable<T> depends, Consumer<T> consumer) {
        super(depends, consumer);
    }

    /**
     * Attach this instance to an operator.
     * @throws IllegalArgumentException if this watcher is already attached
     * @param manager operator to associate this instance to
     */
    public void attach(WatcherOperator manager) {
        if (this.manager != null)
            throw new IllegalArgumentException("already attached");

        this.manager = manager;

    }


    /**
     * Checks if this watcher instance is currently attached to a {@link WatcherOperator}.
     *
     * @return {@code true} if this watcher is attached to an operator, {@code false} otherwise
     */
    public boolean isAttached() {
        return this.manager != null;
    }

    /**
     * Returns the {@link WatcherOperator} this watcher instance is attached to.
     *
     * @return the operator managing this watcher, or {@code null} if not attached
     */
    public WatcherOperator getManager() {
        return this.manager;
    }

    /**
     * Detaches this watcher instance from its associated {@link WatcherOperator}. Once detached, the watcher
     * will no longer be managed by the operator and will not trigger on state changes.
     *
     * @throws IllegalArgumentException if this watcher is not currently attached to an operator
     */
    public void detach() {
        if (this.manager == null)
            throw new IllegalArgumentException("not attached");

        this.manager.detach(this);
        this.manager = null;
    }

}
