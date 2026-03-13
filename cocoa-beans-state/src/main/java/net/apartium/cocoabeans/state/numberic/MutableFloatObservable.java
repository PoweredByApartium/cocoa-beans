package net.apartium.cocoabeans.state.numberic;

import org.jetbrains.annotations.ApiStatus;

/**
 * A mutable observable that holds a primitive {@code float} value.
 * Extends {@link FloatObservable} with write operations that notify observers on change.
 *
 * @see FloatObservable
 * @see Observable#mutableNumeric(float)
 */
@ApiStatus.AvailableSince("0.0.47")
public interface MutableFloatObservable extends FloatObservable {

    /**
     * Sets the value of the observable and notifies observers
     * @param value new value
     */
    void set(float value);

    /**
     * Increments the value by {@code 1.0f} and notifies observers
     */
    void increment();

    /**
     * Decrements the value by {@code 1.0f} and notifies observers
     */
    void decrement();

    /**
     * Adds the given value to the current value and notifies observers
     * @param value value to add
     */
    void add(float value);

    /**
     * Subtracts the given value from the current value and notifies observers
     * @param value value to subtract
     */
    void subtract(float value);

    /**
     * Multiplies the current value by the given value and notifies observers
     * @param value value to multiply by
     */
    void multiply(float value);

    /**
     * Divides the current value by the given value and notifies observers
     * @param value value to divide by
     */
    void divide(float value);

}
