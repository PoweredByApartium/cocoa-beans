package net.apartium.cocoabeans.state.numberic;

import org.jetbrains.annotations.ApiStatus;

/**
 * @hidden
 */
@ApiStatus.Internal
public class MutableObservableLongImpl extends MutableNumeric<Long> implements MutableLongObservable {

    private long value;

    public MutableObservableLongImpl(long value) {
        this.value = value;
    }

    @Override
    public long getAsLong() {
        return value;
    }

    @Override
    public Long get() {
        return getAsLong();
    }

    @Override
    public void set(long value) {
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
    public void add(long value) {
        set(this.value + value);
    }

    @Override
    public void subtract(long value) {
        set(this.value - value);
    }

    @Override
    public void multiply(long value) {
        set(this.value * value);
    }

    @Override
    public void divide(long value) {
        set(this.value / value);
    }

}
