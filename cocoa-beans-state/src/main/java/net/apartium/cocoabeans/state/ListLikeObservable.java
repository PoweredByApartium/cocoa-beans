package net.apartium.cocoabeans.state;

import org.jetbrains.annotations.ApiStatus;

import java.util.Comparator;
import java.util.List;

/**
 * Represents an observable list-like structure that allows modification operations
 * such as addition, removal, and sorting while providing change notifications to observers.
 *
 * @param <E> the type of elements in the list-like structure
 * @see List
 */
@ApiStatus.AvailableSince("0.0.50")
public interface ListLikeObservable<E> {

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
