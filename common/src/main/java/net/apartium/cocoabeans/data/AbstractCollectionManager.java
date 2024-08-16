package net.apartium.cocoabeans.data;

import java.util.Collection;
import java.util.Iterator;

/**
 * An abstract manager class that provides common operations for managing a collection.
 *
 * @param <T> the type of collection managed by this manager
 * @param <E> the type of elements contained in the collection
 */
public abstract class AbstractCollectionManager<T extends Collection<E>, E> {

    protected final T collection;

    /**
     * Constructs an AbstractCollectionManager with the specified collection.
     *
     * @param collection the collection to be managed
     */
    public AbstractCollectionManager(T collection) {
        this.collection = collection;
    }

    /**
     * Adds the specified element to the collection.
     *
     * @param element the element to be added
     * @return {@code true} if the collection changed as a result of the call
     */
    public boolean add(E element) {
        return this.collection.add(element);
    }

    /**
     * Removes a single instance of the specified element from the collection, if it is present.
     *
     * @param element the element to be removed
     * @return {@code true} if the element was removed
     */
    public boolean remove(E element) {
        return this.collection.remove(element);
    }

    /**
     * Checks if the collection contains the specified element.
     *
     * @param element the element whose presence in the collection is to be tested
     * @return {@code true} if the collection contains the specified element
     */
    public boolean contains(E element) {
        return this.collection.contains(element);
    }

    /**
     * Returns the number of elements in the collection.
     *
     * @return the number of elements in the collection
     */
    public int size() {
        return this.collection.size();
    }

    /**
     * Checks if the collection is empty.
     *
     * @return {@code true} if the collection contains no elements
     */
    public boolean isEmpty() {
        return this.collection.isEmpty();
    }

    /**
     * Removes all elements from the collection.
     */
    public void clear() {
        this.collection.clear();
    }

    /**
     * Adds all elements from the specified collection to this collection.
     *
     * @param elements collection containing elements to be added
     * @return {@code true} if the collection changed as a result of the call
     */
    public boolean addAll(Collection<? extends E> elements) {
        return this.collection.addAll(elements);
    }

    /**
     * Removes all elements from the specified collection from this collection.
     *
     * @param elements collection containing elements to be removed
     * @return {@code true} if the collection changed as a result of the call
     */
    public boolean removeAll(Collection<?> elements) {
        return this.collection.removeAll(elements);
    }

    /**
     * Retains only the elements in this collection that are contained in the specified collection.
     *
     * @param elements collection containing elements to be retained
     * @return {@code true} if the collection changed as a result of the call
     */
    public boolean retainAll(Collection<?> elements) {
        return this.collection.retainAll(elements);
    }

    /**
     * Returns an iterator over the elements in this collection.
     *
     * @return an iterator over the elements in this collection
     */
    public Iterator<E> iterator() {
        return this.collection.iterator();
    }
}
