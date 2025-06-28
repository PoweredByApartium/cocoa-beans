package net.apartium.cocoabeans.state;

import net.apartium.cocoabeans.structs.Entry;
import org.jetbrains.annotations.ApiStatus;

import java.util.Objects;

/**
 * Should only have one instance
 * @param <T> T as type
 */
@ApiStatus.AvailableSince("0.0.39")
public class DirtyWatcher<T> implements Observer {

    private final Observable<T> dependsOn;
    private T value;
    private boolean first = true;
    private boolean isDirty = true;

    private DirtyWatcher(Observable<T> dependsOn) {
        this.dependsOn = dependsOn;
    }

    public static <T> DirtyWatcher<T> create(Observable<T> observable) {
        if (observable == null)
            observable = Observable.empty();

        DirtyWatcher<T> dirtyWatcher = new DirtyWatcher<>(observable);
        observable.observe(dirtyWatcher);
        return dirtyWatcher;
    }

    @Override
    public void flagAsDirty(Observable<?> observable) {
        if (dependsOn != observable)
            return;

        isDirty = true;
    }

    public boolean isDirty() {
        return isDirty;
    }

    public T getCache() {
        return value;
    }

    /**
     * Get current value
     * @return entry of value and if it had changed
     */
    public Entry<T, Boolean> get() {
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
