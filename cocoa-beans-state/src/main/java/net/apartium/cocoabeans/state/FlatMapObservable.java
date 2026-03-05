package net.apartium.cocoabeans.state;

import java.util.*;
import java.util.function.Function;

/* package-private */ class FlatMapObservable<T, R> implements Observable<R>, Observer {

    private final Observable<T> base;
    private T prev;

    private boolean baseDirty = true;
    private boolean flatDirty = true;

    private Observable<R> currentFlat;
    private R currentValue;

    private final Function<T, Observable<R>> mapper;

    private final Set<Observer> observers = Collections.newSetFromMap(new WeakHashMap<>());

    public FlatMapObservable(Observable<T> base, Function<T, Observable<R>> mapper) {
        this.base = base;
        this.base.observe(this);

        this.mapper = mapper;
    }

    private void updateBase() {
        if (!baseDirty)
            return;

        baseDirty = false;

        T parameter = base.get();
        boolean same = Objects.equals(parameter, prev);
        if (same)
            return;

        prev = parameter;

        Observable<R> flat = mapper.apply(parameter);
        if (flat == currentFlat)
            return;

        flatDirty = true;
        if (currentFlat != null)
            currentFlat.removeObserver(this);

        currentFlat = flat;
        currentFlat.observe(this);
    }

    @Override
    public R get() {
        if (!baseDirty && !flatDirty)
            return currentValue;

        updateBase();

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
