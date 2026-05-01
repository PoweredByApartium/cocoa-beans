package net.apartium.cocoabeans.state;

import net.apartium.cocoabeans.CollectionHelpers;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

/**
 * A concrete implementation of an observable linked list that allows observing changes
 * such as additions, removals, and structural modifications. This class extends
 * {@code AbstractCollectionObservable} and implements {@code AbstractListObservable}
 * and {@code QueueObservable}.
 *
 * @param <E> the type of elements in this collection
 * @see AbstractCollectionObservable
 * @see AbstractListObservable
 * @see QueueObservable
 */
@ApiStatus.AvailableSince("0.0.50")
public class LinkedListObservable<E> extends AbstractCollectionObservable<E, LinkedList<E>> implements AbstractListObservable<E>, QueueObservable<E, LinkedList<E>> {

    public LinkedListObservable() {
        this(new LinkedList<>());
    }

    public LinkedListObservable(LinkedList<E> collection) {
        super(collection);
    }

    @Override
    protected LinkedList<E> createFilteredCollection(Collection<E> elements) {
        return new LinkedList<>(elements);
    }

    @Override
    protected LinkedList<E> createCollection(int initialCapacity) {
        return new LinkedList<>();
    }

    @Override
    public void add(int index, E element) {
        int prevSize = collection.size();
        collection.add(index, element);

        if (prevSize == collection.size())
            return;

        notifyObservers();
    }

    @Override
    public E remove(int index) {
        int prevSize = collection.size();
        E element = collection.remove(index);

        if (prevSize == collection.size())
            return element;

        notifyObservers();

        return element;
    }

    @Override
    public void sort(Comparator<? super E> comparator) {
        if (comparator == null)
            throw new NullPointerException();

        if (CollectionHelpers.isSorted(collection, comparator))
            return;

        collection.sort(comparator);
        notifyObservers();
    }

    @Override
    public boolean offer(E element) {
        int prevSize = collection.size();
        if (!collection.offer(element))
            return false;

        if (prevSize == collection.size())
            return false;

        notifyObservers();
        return true;
    }

    @Override
    public E remove() {
        if (collection.isEmpty())
            throw new NoSuchElementException("No elements in the queue");

        E element = collection.remove();
        notifyObservers();
        return element;
    }

    @Override
    public E poll() {
        if (collection.isEmpty())
            return null;

        E element = collection.poll();
        notifyObservers();
        return element;
    }

    @Override
    public E element() {
        return collection.element();
    }

    @Override
    public E peek() {
        return collection.peek();
    }

    @Override
    public LinkedList<E> get() {
        return new LinkedList<>(collection);
    }

}