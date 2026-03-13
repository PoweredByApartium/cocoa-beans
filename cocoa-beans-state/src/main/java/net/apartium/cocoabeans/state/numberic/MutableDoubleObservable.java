package net.apartium.cocoabeans.state.numberic;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.AvailableSince("0.0.47")
public interface MutableDoubleObservable extends DoubleObservable {

    void set(double value);
    void increment();
    void decrement();

    void add(double value);
    void subtract(double value);
    void multiply(double value);
    void divide(double value);

}
