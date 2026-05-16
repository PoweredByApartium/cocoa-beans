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
public interface ListObservable<E> extends CollectionObservable<E, List<E>>, ListLikeObservable<E> {

    @Override
    ListObservable<E> filter(Function<E, Observable<Boolean>> filter);

    @Override
    <T> ListObservable<E> filter(Function<E, Observable<T>> mapper, Predicate<T> filter);

    @ApiStatus.AvailableSince("0.0.50")
    @Override
    <R> ListObservable<R> mapEach(Function<E, R> mapper);

    @ApiStatus.AvailableSince("0.0.50")
    @Override
    <R> ListObservable<R> flatMapEach(Function<E, Observable<R>> mapper);

    /**
     * Sorts the elements of this observable based on the values obtained by applying the given mapping function
     * and comparator. The comparator is wrapped as an immutable observable, so the sorted list reacts to changes
     * in the source list and mapped observable values, but not to changes in the comparator itself.
     *
     * @param <T> the type resulting from the mapping function
     * @param mapper a function that maps each element of type {@code E} to an observable of type {@code T}
     *               whose value will be used for sorting
     * @param comparator a comparator used to determine the ordering of the mapped values of type {@code T}
     * @return a {@link ListObservable} containing the elements of this observable sorted according to the specified comparator
     */
    @ApiStatus.AvailableSince("0.0.50")
    default <T> ListObservable<E> sorted(Function<E, Observable<T>> mapper, Comparator<? super T> comparator) {
        return sorted(mapper, Observable.immutable(comparator));
    }

    /**
     * Sorts the elements of this observable based on the values obtained by applying the given mapping function
     * and the current value of the given comparator observable. The elements are transformed using the specified
     * mapper function, and the resulting values are compared using the current comparator value to determine the
     * sorted order.
     * <p>
     * The returned list reacts to changes in the source list, changes in the mapped observable values, and changes
     * in the comparator observable.
     *
     * @param <T> the type resulting from the mapping function
     * @param mapper a function that maps each element of type {@code E} to an observable of type {@code T}
     *               whose value will be used for sorting
     * @param comparator an observable comparator used to determine the ordering of the mapped values of type {@code T}
     * @return a {@link ListObservable} containing the elements of this observable sorted according to the current comparator
     */
    @ApiStatus.AvailableSince("0.0.50")
    <T> ListObservable<E> sorted(Function<E, Observable<T>> mapper, Observable<Comparator<? super T>> comparator);
}
