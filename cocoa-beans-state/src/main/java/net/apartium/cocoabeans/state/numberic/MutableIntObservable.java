package net.apartium.cocoabeans.state.numberic;

import net.apartium.cocoabeans.state.Observable;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.IntSupplier;

/**
 * A mutable observable that holds a primitive {@code int} value.
 * Extends {@link IntObservable} with write operations that notify observers on change.
 *
 * @see IntObservable
 * @see Observable#mutableNumeric(int)
 */
@ApiStatus.AvailableSince("0.0.47")
public interface MutableIntObservable extends IntObservable {

    /**
     * Sets the value of the observable and notifies observers
     * @param value new value
     */
    void set(int value);

    /**
     * Increments the value by {@code 1} and notifies observers
     */
    void increment();

    /**
     * Decrements the value by {@code 1} and notifies observers
     */
    void decrement();

    /**
     * Adds the given value to the current value and notifies observers
     * @param value value to add
     */
    void add(int value);

    /**
     * Subtracts the given value from the current value and notifies observers
     * @param value value to subtract
     */
    void subtract(int value);

    /**
     * Multiplies the current value by the given value and notifies observers
     * @param value value to multiply by
     */
    void multiply(int value);

    /**
     * Divides the current value by the given value and notifies observers
     * @param value value to divide by
     */
    void divide(int value);

}
