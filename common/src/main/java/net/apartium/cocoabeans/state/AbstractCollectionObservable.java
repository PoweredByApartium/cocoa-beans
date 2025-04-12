package net.apartium.cocoabeans.state;

import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.function.Predicate;

@ApiStatus.Internal
/* package-private */ abstract class AbstractCollectionObservable<E, C extends Collection<E>> implements CollectionObservable<E, C> {


    protected final C collection;
    private final Set<Observer> observers = Collections.newSetFromMap(new WeakHashMap<>());

    protected AbstractCollectionObservable(C collection) {
        this.collection = collection;
    }


    @Override
    public boolean add(E element) {
        boolean result = this.collection.add(element);
        if (!result)
            return false;

        notifyObservers();

        return true;
    }

    @Override
    public boolean remove(E element) {
        boolean result = this.collection.remove(element);

        if (!result)
            return false;

        notifyObservers();

        return true;    }

    @Override
    public boolean addAll(Collection<? extends E> collection) {
        boolean result = this.collection.addAll(collection);

        if (!result)
            return false;

        notifyObservers();

        return true;
    }

    @Override
    public boolean removeAll(Collection<? extends E> collection) {
        boolean result = this.collection.removeAll(collection);

        if (!result)
            return false;

        notifyObservers();

        return true;
    }

    @Override
    public boolean removeIf(Predicate<? super E> filter) {
        boolean result = this.collection.removeIf(filter);

        if (!result)
            return false;

        notifyObservers();

        return true;
    }

    @Override
    public boolean retainAll(Collection<? extends E> collection) {
        boolean result = this.collection.retainAll(collection);

        if (!result)
            return false;

        notifyObservers();

        return true;
    }

    @Override
    public void clear() {
        boolean empty = this.collection.isEmpty();
        this.collection.clear();

        if (empty)
            return;


        notifyObservers();
    }

    @Override
    public void observe(Observer observer) {
        observers.add(observer);
    }

    protected void notifyObservers() {
        for (Observer observer : observers)
            observer.flagAsDirty(this);
    }
}
