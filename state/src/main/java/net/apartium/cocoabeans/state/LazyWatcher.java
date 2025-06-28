package net.apartium.cocoabeans.state;

import net.apartium.cocoabeans.structs.Entry;
import org.jetbrains.annotations.ApiStatus;

import java.util.Objects;

/**
 * Watcher implementation that contains the last manually updated value.
 * Updating the value occurs through the {@link #getOrUpdate()} method
 * @param <T> T as type
 */
@ApiStatus.AvailableSince("0.0.41")
public class LazyWatcher<T> implements Observer {

    private Observable<T> dependsOn;
    private T value;
    private boolean first = true;
    private boolean isDirty = true;

    private LazyWatcher(Observable<T> dependsOn) {
        this.dependsOn = dependsOn;
    }

    public static <T> LazyWatcher<T> create(Observable<T> observable) {
        if (observable == null)
            observable = Observable.empty();

        LazyWatcher<T> dirtyWatcher = new LazyWatcher<>(observable);
        observable.observe(dirtyWatcher);
        return dirtyWatcher;
    }

    @Override
    public void flagAsDirty(Observable<?> observable) {
        if (dependsOn != observable)
            return;

        isDirty = true;
    }

    public void setDependsOn(Observable<T> dependsOn) {
        if (this.dependsOn == dependsOn)
            return;

        this.dependsOn.removeObserver(this);
        this.dependsOn = dependsOn;
        this.dependsOn.observe(this);

        isDirty = true;
    }

    public boolean isDirty() {
        return isDirty;
    }

    /**
     * Get the value applied from last {@link #getOrUpdate()} call.
     * @return last applied value
     */
    public T getLastValue() {
        return value;
    }

    /**
     * If the current watcher is dirty, update it if necessary, then return the new value.
     * @return entry containing the up-to-date value (key) and if it was changed since previous call (value).
     */
    public Entry<T, Boolean> getOrUpdate() {
        if (!isDirty)
            return new Entry<>(value, false);

        T newValue = dependsOn.get();
        if (Objects.equals(value, newValue) && !first)
            return new Entry<>(value, false);

        value = newValue;
        first = false;
        isDirty = false;

        return new Entry<>(value, true);
    }

    public void delete() {
        dependsOn.removeObserver(this);
        value = null;
    }

    public Observable<T> getWatchedObservable() {
        return dependsOn;
    }
}
