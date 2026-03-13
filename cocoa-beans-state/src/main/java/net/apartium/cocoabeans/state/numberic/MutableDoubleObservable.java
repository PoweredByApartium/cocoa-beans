package net.apartium.cocoabeans.state.numberic;

import org.jetbrains.annotations.ApiStatus;

/**
 * A mutable observable that holds a primitive {@code double} value.
 * Extends {@link DoubleObservable} with write operations that notify observers on change.
 *
 * @see DoubleObservable
 * @see Observable#mutableNumeric(double)
 */
@ApiStatus.AvailableSince("0.0.47")
public interface MutableDoubleObservable extends DoubleObservable {

    /**
     * Sets the value of the observable and notifies observers
     * @param value new value
     */
    void set(double value);

    /**
     * Increments the value by {@code 1.0} and notifies observers
     */
    void increment();

    /**
     * Decrements the value by {@code 1.0} and notifies observers
     */
    void decrement();

    /**
     * Adds the given value to the current value and notifies observers
     * @param value value to add
     */
    void add(double value);

    /**
     * Subtracts the given value from the current value and notifies observers
     * @param value value to subtract
     */
    void subtract(double value);

    /**
     * Multiplies the current value by the given value and notifies observers
     * @param value value to multiply by
     */
    void multiply(double value);

    /**
     * Divides the current value by the given value and notifies observers
     * @param value value to divide by
     */
    void divide(double value);

}
