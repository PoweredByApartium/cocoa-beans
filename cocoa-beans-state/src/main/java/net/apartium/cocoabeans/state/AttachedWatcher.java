package net.apartium.cocoabeans.state;

import net.apartium.cocoabeans.Ensures;
import org.jetbrains.annotations.ApiStatus;

/**
 * Represents a watcher attached to an operator
 * @param <T>
 * @see Watcher
 * @see WatcherManager
 * @see WatcherOperator
 */
@ApiStatus.AvailableSince("0.0.39")
public abstract class AttachedWatcher<T> extends Watcher<T> {

    private WatcherOperator manager;

    /**
     * Constructs a new instance.
     * @param depends the {@link Observable<T>} state that this watcher is observing.
     */
    public AttachedWatcher(Observable<T> depends) {
        super(depends);
    }

    /**
     * Attach this instance to an operator.
     * @throws IllegalArgumentException if this watcher is already attached
     * @param manager operator to associate this instance to
     */
    public void attach(WatcherOperator manager) {
        if (this.manager != null)
            throw new IllegalArgumentException("already attached");

        Ensures.notNull(manager, "manager +-");

        this.manager = manager;
        depends.observe(this);
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
    @Override
    public void detach() {
        if (this.manager == null)
            throw new IllegalArgumentException("not attached");

        this.manager.detach(this);
        this.manager = null;

        super.detach();
    }

}
