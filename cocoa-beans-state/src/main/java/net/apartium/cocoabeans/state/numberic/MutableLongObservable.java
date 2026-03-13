package net.apartium.cocoabeans.state.numberic;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.AvailableSince("0.0.47")
public interface MutableLongObservable extends LongObservable {

    void set(long value);
    void increment();
    void decrement();

    void add(long value);
    void subtract(long value);
    void multiply(long value);
    void divide(long value);

}
