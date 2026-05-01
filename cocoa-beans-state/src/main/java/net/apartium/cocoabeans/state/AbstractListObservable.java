package net.apartium.cocoabeans.state;

import org.jetbrains.annotations.ApiStatus;

import java.util.Comparator;
import java.util.List;

/**
 * Represents an observable abstraction for operations on a list.
 * Provides methods for adding, removing, and sorting elements within the observable list.
 *
 * @param <E> the type of elements in the list
 * @see List
 */
@ApiStatus.AvailableSince("0.0.50")
public interface AbstractListObservable<E> {

    /**
     * Add an element at specified index
     * @param index index
     * @param element element
     * @see List#add(int, Object)
     */
    void add(int index, E element);

    /**
     * Remove an element at specified index
     * @param index index
     * @return removed element
     * @see List#remove(int)
     */
    E remove(int index);

    /**
     * Sort list elements by given comparator
     * @param comparator comparator
     * @see List#sort(Comparator)
     */
    void sort(Comparator<? super E> comparator);

}
