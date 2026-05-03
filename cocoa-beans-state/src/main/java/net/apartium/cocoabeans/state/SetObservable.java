package net.apartium.cocoabeans.state;

import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Represents an observable containing multiple unique values
 * @param <E> element type
 * @see CollectionObservable
 * @see Observable
 */
@ApiStatus.AvailableSince("0.0.39")
public interface SetObservable<E> extends CollectionObservable<E, Set<E>> {

    @Override
    SetObservable<E> filter(Function<E, Observable<Boolean>> filter);

    @Override
    <T> SetObservable<E> filter(Function<E, Observable<T>> mapper, Predicate<T> filter);

    @ApiStatus.AvailableSince("0.0.50")
    @Override
    <R> SetObservable<R> mapEach(Function<E, R> mapper);

    @ApiStatus.AvailableSince("0.0.50")
    @Override
    <R> SetObservable<R> flatMapEach(Function<E, Observable<R>> mapper);
}
