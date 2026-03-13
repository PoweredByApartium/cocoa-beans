package net.apartium.cocoabeans.state.numberic;

import net.apartium.cocoabeans.state.Observer;
import org.jetbrains.annotations.ApiStatus;

/**
 * @hidden
 */
@ApiStatus.Internal
public class IntObservableImpl implements IntObservable {

    private final int value;

    public IntObservableImpl(int value) {
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
    public void observe(Observer observer) {
        // no observes for immutable
    }

    @Override
    public boolean removeObserver(Observer observer) {
        return false; // no observes for immutable
    }
}
