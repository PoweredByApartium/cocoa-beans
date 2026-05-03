package net.apartium.cocoabeans.state;

import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;
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
}
