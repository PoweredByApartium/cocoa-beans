package net.apartium.cocoabeans.state;

import java.util.*;
import java.util.function.Predicate;

/* package-private */ class SetObservableImpl<E> implements SetObservable<E> {

    private final Set<E> set;
    private final Set<Observer> observers = Collections.newSetFromMap(new WeakHashMap<>());

    public SetObservableImpl() {
        this(new HashSet<>());
    }

    public SetObservableImpl(Set<E> set) {
        this.set = set;
    }

    @Override
    public boolean add(E element) {
        boolean result = set.add(element);
        if (!result)
            return false;

        notifyObservers();

        return true;
    }

    @Override
    public boolean remove(E element) {
        boolean result = set.remove(element);

        if (!result)
            return false;

        notifyObservers();

        return true;
    }

    @Override
    public boolean addAll(Collection<? extends E> collection) {
        boolean result = set.addAll(collection);

        if (!result)
            return false;

        notifyObservers();

        return true;
    }

    @Override
    public boolean removeAll(Collection<? extends E> collection) {
        boolean result = set.removeAll(collection);

        if (!result)
            return false;

        notifyObservers();

        return true;
    }

    @Override
    public boolean removeIf(Predicate<? super E> filter) {
        boolean result = set.removeIf(filter);

        if (!result)
            return false;

        notifyObservers();

        return true;
    }

    @Override
    public boolean retainAll(Collection<? extends E> collection) {
        boolean result = set.retainAll(collection);

        if (!result)
            return false;

        notifyObservers();

        return true;
    }

    @Override
    public void clear() {
        boolean empty = set.isEmpty();
        set.clear();

        if (empty)
            return;


        notifyObservers();
    }

    @Override
    public Set<E> get() {
        return Set.copyOf(set);
    }

    @Override
    public void observe(Observer observer) {
        observers.add(observer);
    }

    private void notifyObservers() {
        for (Observer observer : observers)
            observer.flagAsDirty(this);
    }
}
