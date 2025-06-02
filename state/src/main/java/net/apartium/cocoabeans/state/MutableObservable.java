package net.apartium.cocoabeans.state;

import org.jetbrains.annotations.ApiStatus;

/**
 * Represents on observable with a mutable value who can be changed directly
 * @param <T> element type
 */
@ApiStatus.AvailableSince("0.0.39")
public interface MutableObservable<T> extends Observable<T> {

    /**
     * Set the value of the observable and notify listeners
     * @param value new value
     */
    void set(T value);

}
