package net.apartium.cocoabeans.state.numberic;

import org.jetbrains.annotations.ApiStatus;

/**
 * A mutable observable that holds a primitive {@code long} value.
 * Extends {@link LongObservable} with write operations that notify observers on change.
 *
 * @see LongObservable
 * @see Observable#mutableNumeric(long)
 */
@ApiStatus.AvailableSince("0.0.47")
public interface MutableLongObservable extends LongObservable {

    /**
     * Sets the value of the observable and notifies observers
     * @param value new value
     */
    void set(long value);

    /**
     * Increments the value by {@code 1L} and notifies observers
     */
    void increment();

    /**
     * Decrements the value by {@code 1L} and notifies observers
     */
    void decrement();

    /**
     * Adds the given value to the current value and notifies observers
     * @param value value to add
     */
    void add(long value);

    /**
     * Subtracts the given value from the current value and notifies observers
     * @param value value to subtract
     */
    void subtract(long value);

    /**
     * Multiplies the current value by the given value and notifies observers
     * @param value value to multiply by
     */
    void multiply(long value);

    /**
     * Divides the current value by the given value and notifies observers
     * @param value value to divide by
     */
    void divide(long value);

}
