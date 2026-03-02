package net.apartium.cocoabeans.state;

import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;
import java.util.function.BiPredicate;
import java.util.function.Function;
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

    /**
     * Filters the elements of this observable based on the provided filter function.
     * Only elements for which the provided function evaluates to an observable that emits {@code true}
     * will be included in the resulting observable.
     *
     * @param filter a function that takes an element of type {@code E} as input and returns
     *               an {@link Observable} of {@link Boolean} indicating whether the element should be included.
     * @return a new {@link CollectionObservable} containing the filtered elements.
     */
    CollectionObservable<E, C> filter(Function<E, Observable<Boolean>> filter);

    /**
     * Filters the elements of this observable based on a mapping function and a predicate.
     * The mapping function transforms each element into an {@link Observable} of type {@code T},
     * and the predicate determines whether the resulting value should be included.
     *
     * @param <T>   the type of the values produced by the mapping function.
     * @param mapper a function that takes an element of type {@code E} as input and returns an
     *               {@link Observable} of type {@code T}.
     * @param filter a predicate that determines whether the mapped value of type {@code T}
     *               should be included in the resulting observable.
     * @return a new {@link CollectionObservable} containing the elements that pass the filter criteria.
     */
    default <T> CollectionObservable<E, C> filter(Function<E, Observable<T>> mapper, Predicate<T> filter) {
        return this.filter(element -> mapper.apply(element).map(filter::test));
    }

}
