package net.apartium.cocoabeans.state.numberic;

import net.apartium.cocoabeans.state.Observable;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.IntSupplier;

@ApiStatus.AvailableSince("0.0.47")
public interface MutableIntObservable extends IntObservable {

    void set(int value);
    void increment();
    void decrement();

    void add(int value);
    void subtract(int value);
    void multiply(int value);
    void divide(int value);

}
