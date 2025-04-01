package net.apartium.cocoabeans.state;

import net.apartium.cocoabeans.CollectionHelpers;

import java.util.*;
import java.util.function.Predicate;

/* package-private */ class ListObservableImpl<E> implements ListObservable<E> {

    private final List<E> list;
    private final Set<Observer> observers = Collections.newSetFromMap(new WeakHashMap<>());

    /**
     * Creates a new StateList with an empty list
     * @apiNote it would use ArrayList as is list implementation
     */
    public ListObservableImpl() {
        this(new ArrayList<>());
    }

    /**
     * Creates a new StateList with the given list
     * @param list should be modifiable
     */
    public ListObservableImpl(List<E> list) {
        this.list = list;
    }

    @Override
    public boolean add(E element) {
        boolean result = list.add(element);
        if (!result)
            return false;

        notfiyObservers();

        return true;
    }

    private void notfiyObservers() {
        for (Observer observer : observers)
            observer.flagAsDirty(this);
    }

    @Override
    public boolean remove(E element) {
        boolean result = list.remove(element);

        if (!result)
            return false;

        notfiyObservers();

        return true;
    }

    @Override
    public void add(int index, E element) {
        int prevSize = list.size();
        list.add(index, element);

        if (prevSize == list.size())
            return;

        notfiyObservers();
    }

    @Override
    public E remove(int index) {
        E element = list.remove(index);

        if (element == null)
            return null;

        notfiyObservers();

        return element;
    }

    @Override
    public void sort(Comparator<? super E> comparator) {
        if (comparator == null)
            throw new NullPointerException();

        if (CollectionHelpers.isSorted(list, comparator))
            return;

        list.sort(comparator);
        notfiyObservers();
    }

    @Override
    public boolean addAll(Collection<? extends E> collection) {
        boolean result = list.addAll(collection);

        if (!result)
            return false;

        notfiyObservers();

        return true;
    }

    @Override
    public boolean removeAll(Collection<? extends E> collection) {
        boolean result = list.removeAll(collection);

        if (!result)
            return false;

        notfiyObservers();

        return true;
    }

    @Override
    public boolean removeIf(Predicate<? super E> filter) {
        boolean result = list.removeIf(filter);

        if (!result)
            return false;

        notfiyObservers();

        return true;
    }

    @Override
    public boolean retainAll(Collection<? extends E> collection) {
        boolean result = list.retainAll(collection);

        if (!result)
            return false;

        notfiyObservers();

        return true;
    }

    @Override
    public void clear() {
        boolean result = list.isEmpty();

        list.clear();;

        if (result)
            return;

        notfiyObservers();
    }

    /**
     * Will return the value of the current state
     *
     * @return value of the current state
     */

    @Override
    public List<E> get() {
        return List.copyOf(list);
    }


    @Override
    public void observe(Observer observer) {
        observers.add(observer);
    }

}
