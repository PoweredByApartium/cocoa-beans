package net.apartium.cocoabeans.state;

import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;
import java.util.function.Predicate;

/**
 * Represents an observable containing multiple values
 * @param <E> Element type
 * @param <C> Collection type
 * @see Observable
 */
@ApiStatus.AvailableSince("0.0.39")
public interface CollectionObservable<E, C extends Collection<E>> extends Observable<C> {

    /**
     * Add an element to this observable
     * @param element element to add
     * @return true if successful, else false
     * @see Collection#add(Object)
     */
    boolean add(E element);

    /**
     * Removes an element from this observable
     * @param element element to remove
     * @return true if successful, else false
     * @see Collection#remove(Object)
     */
    boolean remove(E element);

    /**
     * Add multiple elements to this observable
     * @param collection elements to add
     * @return true if successful, else false
     * @see Collection#addAll(Collection)
     */
    boolean addAll(Collection<? extends E> collection);

    /**
     * Remove multiple elements from this observable
     * @param collection elements to remove
     * @return true if successful, else false
     * @see Collection#removeAll(Collection)
     */
    boolean removeAll(Collection<? extends E> collection);

    /**
     * Remove all elements matching given predicate
     * @param filter predicate
     * @return true if successful, else false
     * @see Collection#removeIf(Predicate)
     */
    boolean removeIf(Predicate<? super E> filter);

    /**
     * Remove all elements not in given collection
     * @param collection collection
     * @return true if successful, else false
     * @see Collection#retainAll(Collection)
     */
    boolean retainAll(Collection<? extends E> collection);

    /**
     * Clear all elements from this observable
     * @see Collection#clear()
     */
    void clear();

    /**
     * Returns an observable representing the size of the observable.
     * When the size of the current observable changes, the size observable will emit the new size.
     * @return size observable
     */
    Observable<Integer> size();

}
