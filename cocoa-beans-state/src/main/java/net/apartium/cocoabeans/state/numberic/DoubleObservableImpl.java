package net.apartium.cocoabeans.state.numberic;

import net.apartium.cocoabeans.state.Observer;
import org.jetbrains.annotations.ApiStatus;

/**
 * @hidden
 */
@ApiStatus.Internal
public class DoubleObservableImpl implements DoubleObservable {

    private final double value;

    public DoubleObservableImpl(double value) {
        this.value = value;
    }

    @Override
    public double getAsDouble() {
        return value;
    }

    @Override
    public Double get() {
        return getAsDouble();
    }

    @Override
    public void observe(Observer observer) {
        // no observes for immutable
    }

    @Override
    public boolean removeObserver(Observer observer) {
        return false; // no observes for immutable
    }
}
