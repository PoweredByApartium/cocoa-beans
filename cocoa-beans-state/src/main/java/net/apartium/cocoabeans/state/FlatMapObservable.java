package net.apartium.cocoabeans.state;

import net.apartium.cocoabeans.CollectionHelpers;

import java.util.*;
import java.util.function.Function;

/* package-private */ class FlatMapObservable<F, T> implements Observable<T>, Observer {

    private final Observable<F> base;
    private F prev;

    private boolean baseDirty = true;
    private boolean flatDirty = true;

    private Observable<T> currentFlat;
    private T currentValue;

    private final Function<F, Observable<T>> mapper;

    private final Set<Observer> observers = Collections.newSetFromMap(new WeakHashMap<>());

    public FlatMapObservable(Observable<F> base, Function<F, Observable<T>> mapper) {
        this.base = base;
        this.base.observe(this);

        this.mapper = mapper;
    }

    @Override
    public T get() {
        if (!baseDirty && !flatDirty)
            return currentValue;

        if (baseDirty) {
            F parameter = base.get();
            boolean hadChange = !Objects.equals(parameter, prev);
            if (parameter instanceof Collection<?>  a&& prev instanceof Collection<?> b)
                hadChange = !CollectionHelpers.equalsCollections(a, b);

            if (hadChange) {
                prev = parameter;

                Observable<T> flat = mapper.apply(parameter);
                if (flat != currentFlat) {
                    flatDirty = true;
                    if (currentFlat != null)
                        currentFlat.removeObserver(this);

                    currentFlat = flat;
                    currentFlat.observe(this);
                }
            }
        }

        if (flatDirty) {
            currentValue = currentFlat.get();
            flatDirty = false;
        }

        return currentValue;
    }

    @Override
    public void observe(Observer observer) {
        observers.add(observer);
    }

    @Override
    public boolean removeObserver(Observer observer) {
        return observers.remove(observer);
    }

    /**
     * Notifies all observers of the change.
     */
    protected void notifyObservers() {
        for (Observer observer : observers)
            observer.flagAsDirty(this);
    }

    @Override
    public void flagAsDirty(Observable<?> observable) {
        if (base == observable) {
            notifyObservers();
            baseDirty = true;
        } else if (currentFlat == observable) {
            notifyObservers();
            flatDirty = true;
        }
    }

}
