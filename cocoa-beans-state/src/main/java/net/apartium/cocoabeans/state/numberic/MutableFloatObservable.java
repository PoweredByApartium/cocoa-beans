package net.apartium.cocoabeans.state.numberic;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.AvailableSince("0.0.47")
public interface MutableFloatObservable extends FloatObservable {

    void set(float value);
    void increment();
    void decrement();

    void add(float value);
    void subtract(float value);
    void multiply(float value);
    void divide(float value);

}
