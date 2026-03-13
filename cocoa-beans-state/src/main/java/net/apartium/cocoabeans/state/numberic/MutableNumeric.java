package net.apartium.cocoabeans.state.numberic;

import net.apartium.cocoabeans.state.Observable;
import net.apartium.cocoabeans.state.Observer;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * @hidden
 */
@ApiStatus.Internal
/* package-private */ abstract class MutableNumeric<T extends Number> implements Observable<T> {

    private final Set<Observer> observers = Collections.newSetFromMap(new WeakHashMap<>());

    protected void flagAsDirty() {
        for (Observer observer : observers)
            observer.flagAsDirty(this);
    }

    @Override
    public void observe(Observer observer) {
        observers.add(observer);
    }

    @Override
    public boolean removeObserver(Observer observer) {
        return observers.remove(observer);
    }
}
