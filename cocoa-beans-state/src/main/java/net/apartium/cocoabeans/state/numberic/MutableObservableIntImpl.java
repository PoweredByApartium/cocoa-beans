package net.apartium.cocoabeans.state.numberic;

import org.jetbrains.annotations.ApiStatus;

/**
 * @hidden
 */
@ApiStatus.Internal
public class MutableObservableIntImpl extends MutableNumeric<Integer> implements MutableIntObservable {

    private int value;

    public MutableObservableIntImpl(int value) {
        this.value = value;
    }

    @Override
    public int getAsInt() {
        return value;
    }

    @Override
    public Integer get() {
        return getAsInt();
    }

    @Override
    public void set(int value) {
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
    public void add(int value) {
        set(this.value + value);
    }

    @Override
    public void subtract(int value) {
        set(this.value - value);
    }

    @Override
    public void multiply(int value) {
        set(this.value * value);
    }

    @Override
    public void divide(int value) {
        set(this.value / value);
    }

}
