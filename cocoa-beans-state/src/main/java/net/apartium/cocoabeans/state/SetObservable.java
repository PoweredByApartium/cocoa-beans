package net.apartium.cocoabeans.state;

import org.jetbrains.annotations.ApiStatus;

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
    default SetObservable<E> filter(Function<E, Observable<Boolean>> filter) {
        throw new UnsupportedOperationException();
    }

    @Override
    default <T> SetObservable<E> filter(Function<E, Observable<T>> mapper, Predicate<T> filter) {
        throw new UnsupportedOperationException();
    }

}
