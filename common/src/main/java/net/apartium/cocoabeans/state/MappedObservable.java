package net.apartium.cocoabeans.state;

import net.apartium.cocoabeans.CollectionHelpers;

import java.util.*;
import java.util.function.Function;

/* package-private */ class MappedObservable<F, T> implements Observable<T>, Observer {

    private final Observable<F> base;
    private F prev;

    private boolean isDirty;
    private boolean first = true;

    private T value;

    private final Function<F, T> mapper;

    private final Set<Observer> observers = Collections.newSetFromMap(new WeakHashMap<>());

    public MappedObservable(Observable<F> base, Function<F, T> mapper) {
        this.base = base;
        this.mapper = mapper;

        base.observe(this);
    }

    @Override
    public T get() {
        if (!isDirty && !first)
            return value;

        F parameter = base.get();

        boolean hadChange = !Objects.equals(parameter, prev);
        if (parameter != null && prev != null) {
            if (parameter instanceof Collection<?> a && prev instanceof Collection<?> b)
                hadChange = !CollectionHelpers.equalsCollections(a, b);
        }



        if (!first && !hadChange) {
            isDirty = false;
            return value;
        }

        prev = parameter;

        first = false;
        isDirty = false;

        T temp = mapper.apply(parameter);

        boolean hasChange = !Objects.equals(temp, value);
        value = temp;

        if (hasChange) {
            for (Observer observer : observers)
                observer.flagAsDirty(this);
        }

        return value;
    }

    @Override
    public void observe(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void flagAsDirty(Observable<?> observable) {
        if (observable != base)
            return;

        this.isDirty = true;

        for (Observer observer : observers)
            observer.flagAsDirty(this);
    }
}
