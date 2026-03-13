package net.apartium.cocoabeans.state.numberic;

import net.apartium.cocoabeans.state.Observer;
import org.jetbrains.annotations.ApiStatus;

/**
 * @hidden
 */
@ApiStatus.Internal
public class FloatObservableImpl implements FloatObservable {

    private final float value;

    public FloatObservableImpl(float value) {
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
    public void observe(Observer observer) {
        // no observes for immutable
    }

    @Override
    public boolean removeObserver(Observer observer) {
        return false; // no observes for immutable
    }
}
