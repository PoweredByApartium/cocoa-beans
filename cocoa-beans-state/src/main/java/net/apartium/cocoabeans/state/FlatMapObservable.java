package net.apartium.cocoabeans.state;

import java.util.*;
import java.util.function.Function;

/* package-private */ class FlatMapObservable<T, R> implements Observable<R>, Observer {

    private final Observable<T> base;
    private T prev;

    private boolean baseDirty = true;
    private boolean innerDirty = true;

    private Observable<R> currentInner;
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
        if (flat == currentInner)
            return;

        innerDirty = true;
        if (currentInner != null)
            currentInner.removeObserver(this);

        currentInner = flat;
        currentInner.observe(this);
    }

    @Override
    public R get() {
        if (!baseDirty && !innerDirty)
            return currentValue;

        updateBase();

        if (innerDirty) {
            currentValue = currentInner.get();
            innerDirty = false;
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
        } else if (currentInner == observable) {
            notifyObservers();
            innerDirty = true;
        }
    }

}
