package net.apartium.cocoabeans.state.numberic;

import org.jetbrains.annotations.ApiStatus;

/**
 * @hidden
 */
@ApiStatus.Internal
public class MutableObservableDoubleImpl extends MutableNumeric<Double> implements MutableDoubleObservable {

    private double value;

    public MutableObservableDoubleImpl(double value) {
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
    public void set(double value) {
        if (this.value == value)
            return;

        this.value = value;
        flagAsDirty();
    }

    @Override
    public void increment() {
        set(this.value + 1);
    }

    @Override
    public void decrement() {
        set(this.value - 1);
    }

    @Override
    public void add(double value) {
        set(this.value + value);
    }

    @Override
    public void subtract(double value) {
        set(this.value - value);
    }

    @Override
    public void multiply(double value) {
        set(this.value * value);
    }

    @Override
    public void divide(double value) {
        set(this.value / value);
    }

}
