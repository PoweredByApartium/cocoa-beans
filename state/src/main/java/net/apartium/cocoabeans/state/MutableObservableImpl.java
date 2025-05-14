package net.apartium.cocoabeans.state;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.WeakHashMap;

/* package-private */ class MutableObservableImpl<T> implements MutableObservable<T> {

    private T value;
    private final Set<Observer> observers = Collections.newSetFromMap(new WeakHashMap<>());

    /* package-private */ MutableObservableImpl(T value) {
        this.value = value;
    }

    @Override
    public void set(T value) {
        if (Objects.equals(this.value, value))
            return;

        this.value = value;

        for (Observer observer : observers)
            observer.flagAsDirty(this);
    }

    @Override
    public T get() {
        return this.value;
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
