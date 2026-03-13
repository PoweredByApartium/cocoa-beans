package net.apartium.cocoabeans.state.numberic;

import net.apartium.cocoabeans.state.Observer;
import org.jetbrains.annotations.ApiStatus;

/**
 * @hidden
 */
@ApiStatus.Internal
public class LongObservableImpl implements LongObservable {

    private final long value;

    public LongObservableImpl(long value) {
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
    public void observe(Observer observer) {
        // no observes for immutable
    }

    @Override
    public boolean removeObserver(Observer observer) {
        return false; // no observes for immutable
    }
}
