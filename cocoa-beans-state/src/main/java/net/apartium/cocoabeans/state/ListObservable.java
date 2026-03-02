package net.apartium.cocoabeans.state;

import org.jetbrains.annotations.ApiStatus;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Represents an observable containing multiple values
 * @param <E> element type
 * @see CollectionObservable
 * @see Observable
 */
@ApiStatus.AvailableSince("0.0.39")
public interface ListObservable<E> extends CollectionObservable<E, List<E>> {

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

    @Override
    default ListObservable<E> filter(Function<E, Observable<Boolean>> filter) {
        throw new UnsupportedOperationException();
    }

    @Override
    default <T> ListObservable<E> filter(Function<E, Observable<T>> mapper, Predicate<T> filter) {
        throw new UnsupportedOperationException();
    }
}
