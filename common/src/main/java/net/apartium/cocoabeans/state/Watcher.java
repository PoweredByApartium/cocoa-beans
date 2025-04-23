package net.apartium.cocoabeans.state;

import org.jetbrains.annotations.ApiStatus;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * An observer implementation attaching an action to an observable instance, performing it when its state changes and a heartbeat is triggered
 * @param <T> element type
 * @see Observable
 * @see Observer
 * @see AttachedWatcher
 */
@ApiStatus.AvailableSince("0.0.39")
public class Watcher<T> implements Observer {

    protected final Observable<T> depends;
    private final Consumer<T> consumer;
    private boolean first = true;
    private boolean isDirty = true;
    private T prevValue;

    /**
     * Constructs a new instance
     * @param depends parent state
     * @param consumer action to perform on change
     */
    public Watcher(Observable<T> depends, Consumer<T> consumer) {
        depends.observe(this);

        this.depends = depends;
        this.consumer = consumer;
    }

    /**
     * Trigger a heartbeat on this instance. If the value of the underlying observable has changed, trigger the action.
     */
    public void heartbeat() {
        if (!isDirty)
            return;

        T value = depends.get();

        if (Objects.equals(value, prevValue) && !first)
            return;

        prevValue = value;
        first = false;
        isDirty = false;

        consumer.accept(value);
    }

    /**
     * Detaches the current watcher from its target observable.
     */
    public void detach() {
        depends.removeObserver(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void flagAsDirty(Observable<?> observable) {
        isDirty = true;
    }
}
