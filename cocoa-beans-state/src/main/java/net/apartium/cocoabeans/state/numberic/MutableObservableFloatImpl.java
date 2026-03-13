package net.apartium.cocoabeans.state.numberic;

import org.jetbrains.annotations.ApiStatus;

/**
 * @hidden
 */
@ApiStatus.Internal
public class MutableObservableFloatImpl extends MutableNumeric<Float> implements MutableFloatObservable {

    private float value;

    public MutableObservableFloatImpl(float value) {
        this.value = value;
    }

    @Override
    public float getAsFloat() {
        return value;
    }

    @Override
    public Float get() {
        return getAsFloat();
    }

    @Override
    public void set(float value) {
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
    public void add(float value) {
        set(this.value + value);
    }

    @Override
    public void subtract(float value) {
        set(this.value - value);
    }

    @Override
    public void multiply(float value) {
        set(this.value * value);
    }

    @Override
    public void divide(float value) {
        set(this.value / value);
    }

}
